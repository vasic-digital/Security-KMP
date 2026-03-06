package digital.vasic.security

import java.io.File
import java.nio.file.Paths

actual object SecureStorageFactory {
    actual suspend fun create(): Result<SecureStorage> {
        return try {
            val userHome = System.getProperty("user.home") ?: System.getenv("HOME") ?: "."
            val storageDir = File(Paths.get(userHome, ".security-kmp", "secure").toString())
            if (!storageDir.exists()) storageDir.mkdirs()
            Result.success(DesktopSecureStorage(storageDir))
        } catch (e: Exception) { Result.failure(e) }
    }

    actual suspend fun isAvailable(): Boolean {
        return try {
            val userHome = System.getProperty("user.home") ?: System.getenv("HOME") ?: "."
            val storageDir = File(Paths.get(userHome, ".security-kmp", "secure").toString())
            if (!storageDir.exists()) storageDir.mkdirs()
            storageDir.exists() && storageDir.canWrite()
        } catch (e: Exception) { false }
    }
}
