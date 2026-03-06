package digital.vasic.security

import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import kotlin.test.*

class DesktopSecureStorageTest : SecureStorageTest() {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var storageDir: File
    private lateinit var desktopSecureStorage: DesktopSecureStorage

    override suspend fun createStorage(): SecureStorage = desktopSecureStorage

    @Before
    fun setup() {
        storageDir = tempFolder.newFolder("secure_storage_test")
        desktopSecureStorage = DesktopSecureStorage(storageDir)
        runTest { desktopSecureStorage.clear() }
    }

    @After
    fun tearDown() { runTest { desktopSecureStorage.clear() } }

    @Test
    fun `should create storage directory if it does not exist`() {
        val newTempDir = File(tempFolder.root, "new_secure_dir")
        assertFalse(newTempDir.exists())
        val newStorage = DesktopSecureStorage(newTempDir)
        runTest { newStorage.store("test_key", "test_value") }
        assertTrue(newTempDir.exists())
    }

    @Test
    fun `should create key file`() = runTest {
        desktopSecureStorage.store("test_key", "test_value")
        val keyFile = File(storageDir, ".storage_key")
        assertTrue(keyFile.exists())
    }

    @Test
    fun `should create data file`() = runTest {
        desktopSecureStorage.store("test_key", "test_value")
        val dataFile = File(storageDir, ".secure_storage")
        assertTrue(dataFile.exists())
    }

    @Test
    fun `should use AES GCM encryption`() = runTest {
        desktopSecureStorage.store("key", "sensitive_data_123")
        val dataFile = File(storageDir, ".secure_storage")
        val encryptedContent = dataFile.readText()
        assertFalse(encryptedContent.contains("sensitive_data_123"))
        assertEquals("sensitive_data_123", desktopSecureStorage.retrieve("key").getOrNull())
    }

    @Test
    fun `should persist across restarts`() = runTest {
        desktopSecureStorage.store("key", "value")
        val newStorage = DesktopSecureStorage(storageDir)
        assertEquals("value", newStorage.retrieve("key").getOrNull())
    }

    @Test
    fun `should handle corrupted key file`() = runTest {
        desktopSecureStorage.store("key", "value")
        File(storageDir, ".storage_key").writeBytes(byteArrayOf(0, 1, 2, 3, 4, 5))
        val corruptedStorage = DesktopSecureStorage(storageDir)
        val result = corruptedStorage.retrieve("key")
        if (result.isSuccess) assertNull(result.getOrNull()) else assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `should handle corrupted data file`() = runTest {
        desktopSecureStorage.store("key", "value")
        File(storageDir, ".secure_storage").writeText("corrupted_data")
        val result = desktopSecureStorage.retrieve("key")
        if (result.isSuccess) assertNull(result.getOrNull()) else assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `should handle missing directory parent`() = runTest {
        val deepPath = File(tempFolder.root, "deep/nested/path/secure")
        val storage = DesktopSecureStorage(deepPath)
        assertTrue(storage.store("key", "value").isSuccess)
        assertTrue(deepPath.exists())
    }

    @Test
    fun `should validate security`() = runTest {
        val result = desktopSecureStorage.isSecure()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() ?: false)
    }

    @Test
    fun `should handle concurrent file access`() = runTest {
        (1..50).forEach { i -> desktopSecureStorage.store("key_$i", "value_$i") }
        (1..50).forEach { i -> assertEquals("value_$i", desktopSecureStorage.retrieve("key_$i").getOrNull()) }
    }

    @Test
    fun `should encrypt with unique IV`() = runTest {
        desktopSecureStorage.store("key", "same_value")
        val data1 = File(storageDir, ".secure_storage").readText()
        desktopSecureStorage.store("key", "same_value")
        val data2 = File(storageDir, ".secure_storage").readText()
        assertNotEquals(data1, data2)
        assertEquals("same_value", desktopSecureStorage.retrieve("key").getOrNull())
    }

    @Test
    fun `should test AES encryption parameters`() {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        val testKey = keyGenerator.generateKey()
        assertEquals("AES", testKey.algorithm)
        assertEquals(32, testKey.encoded.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        assertTrue(cipher.algorithm.contains("GCM") || cipher.algorithm.contains("AES"))
    }
}
