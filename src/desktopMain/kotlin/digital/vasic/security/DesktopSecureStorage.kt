package digital.vasic.security

import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.*
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Desktop implementation of secure storage using AES encryption with file-based storage.
 */
class DesktopSecureStorage(
    private val storageDir: File
) : SecureStorage {

    private val keyFile: File = File(storageDir, ".storage_key")
    private val dataFile: File = File(storageDir, ".secure_storage")
    private val mutex = Mutex()
    private var cache: MutableMap<String, String>? = null
    private var secretKey: SecretKey? = null
    private var lastKnownFileModified: Long = 0L
    private var lastKnownFileSize: Long = 0L

    override suspend fun store(key: String, value: String): Result<Unit> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                ensureStorageDirectory()
                val aesKey = getOrCreateSecretKey()
                val encryptedData = encryptData(value, aesKey)
                val data = loadCache().toMutableMap()
                data[key] = encryptedData
                cache = data
                persistToFile(data)
                lastKnownFileModified = if (dataFile.exists()) dataFile.lastModified() else 0L
                lastKnownFileSize = if (dataFile.exists()) dataFile.length() else 0L
                Result.success(Unit)
            } catch (e: Exception) { Result.failure(e) }
        }
    }

    override suspend fun retrieve(key: String): Result<String?> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                ensureStorageDirectory()
                val aesKey = getOrCreateSecretKey()
                val data = loadCache()
                val encryptedValue = data[key] ?: return@withLock Result.success(null)
                val decryptedValue = decryptData(encryptedValue, aesKey)
                Result.success(decryptedValue)
            } catch (e: Exception) { Result.failure(e) }
        }
    }

    override suspend fun delete(key: String): Result<Unit> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                ensureStorageDirectory()
                val data = loadCache().toMutableMap()
                data.remove(key)
                cache = data
                persistToFile(data)
                lastKnownFileModified = if (dataFile.exists()) dataFile.lastModified() else 0L
                lastKnownFileSize = if (dataFile.exists()) dataFile.length() else 0L
                Result.success(Unit)
            } catch (e: Exception) { Result.failure(e) }
        }
    }

    override suspend fun contains(key: String): Result<Boolean> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try { Result.success(loadCache().containsKey(key)) }
            catch (e: Exception) { Result.failure(e) }
        }
    }

    override suspend fun listKeys(): Result<List<String>> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try { Result.success(loadCache().keys.toList()) }
            catch (e: Exception) { Result.failure(e) }
        }
    }

    override suspend fun clear(): Result<Unit> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                cache = mutableMapOf()
                if (dataFile.exists()) dataFile.delete()
                Result.success(Unit)
            } catch (e: Exception) { Result.failure(e) }
        }
    }

    override suspend fun isSecure(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            ensureStorageDirectory()
            Cipher.getInstance("AES/GCM/NoPadding")
            KeyGenerator.getInstance("AES")
            if (!storageDir.exists()) storageDir.mkdirs()
            if (!storageDir.canWrite() || !storageDir.canRead()) return@withContext Result.success(false)
            val aesKey = getOrCreateSecretKey()
            val testData = "secure_test_${System.currentTimeMillis()}"
            val encrypted = encryptData(testData, aesKey)
            val decrypted = decryptData(encrypted, aesKey)
            Result.success(decrypted == testData)
        } catch (e: Exception) { Result.success(false) }
    }

    private fun ensureStorageDirectory() { if (!storageDir.exists()) storageDir.mkdirs() }

    private fun loadCache(): MutableMap<String, String> {
        val currentModified = if (dataFile.exists()) dataFile.lastModified() else 0L
        val currentSize = if (dataFile.exists()) dataFile.length() else 0L
        if (cache != null && currentModified == lastKnownFileModified && currentSize == lastKnownFileSize) return cache!!
        val data = readEncryptedData().toMutableMap()
        cache = data
        lastKnownFileModified = currentModified
        lastKnownFileSize = currentSize
        return data
    }

    private fun getOrCreateSecretKey(): SecretKey {
        secretKey?.let { return it }
        val key = if (keyFile.exists() && keyFile.length() > 0) readSecretKey()
        else { ensureStorageDirectory(); val newKey = generateSecretKey(); writeSecretKey(newKey); newKey }
        secretKey = key
        return key
    }

    private fun generateSecretKey(): SecretKey { val keyGen = KeyGenerator.getInstance("AES"); keyGen.init(256); return keyGen.generateKey() }
    private fun readSecretKey(): SecretKey = SecretKeySpec(keyFile.readBytes(), "AES")
    private fun writeSecretKey(key: SecretKey) {
        keyFile.writeBytes(key.encoded)
        keyFile.setReadable(true, true); keyFile.setWritable(true, true); keyFile.setExecutable(false, false)
    }

    private fun readEncryptedData(): Map<String, String> {
        if (!dataFile.exists()) return emptyMap()
        try {
            val content = dataFile.readText(StandardCharsets.UTF_8)
            if (content.isBlank()) return emptyMap()
            return content.lines().filter { it.isNotBlank() }.mapNotNull { line ->
                val separatorIndex = line.indexOf('|')
                if (separatorIndex >= 0) {
                    try { hexDecode(line.substring(0, separatorIndex)) to line.substring(separatorIndex + 1) }
                    catch (e: Exception) { null }
                } else null
            }.toMap()
        } catch (e: IOException) { return emptyMap() }
    }

    private fun persistToFile(data: Map<String, String>) {
        ensureStorageDirectory()
        val content = data.entries.joinToString("\n") { "${hexEncode(it.key)}|${it.value}" }
        dataFile.writeText(content, StandardCharsets.UTF_8)
        dataFile.setReadable(true, true); dataFile.setWritable(true, true); dataFile.setExecutable(false, false)
    }

    private fun hexEncode(str: String) = str.toByteArray(StandardCharsets.UTF_8).joinToString("") { String.format("%02x", it) }
    private fun hexDecode(hex: String): String {
        if (hex.length % 2 != 0) return ""
        return String(ByteArray(hex.length / 2) { i -> hex.substring(i * 2, i * 2 + 2).toInt(16).toByte() }, StandardCharsets.UTF_8)
    }

    private fun encryptData(data: String, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
        return java.util.Base64.getEncoder().encodeToString(combined)
    }

    private fun decryptData(encryptedData: String, key: SecretKey): String {
        val combined = java.util.Base64.getDecoder().decode(encryptedData)
        val iv = combined.sliceArray(0 until 12)
        val cipherText = combined.sliceArray(12 until combined.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return String(cipher.doFinal(cipherText), StandardCharsets.UTF_8)
    }
}
