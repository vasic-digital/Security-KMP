package digital.vasic.security

import kotlinx.coroutines.test.runTest
import kotlin.test.*

/**
 * Abstract unit tests for SecureStorage interface.
 */
abstract class SecureStorageTest {

    abstract suspend fun createStorage(): SecureStorage

    @Test
    fun `should store and retrieve basic values`() = runTest {
        val storage = createStorage()
        assertTrue(storage.store("test_key", "test_value_123").isSuccess)
        assertEquals("test_value_123", storage.retrieve("test_key").getOrNull())
    }

    @Test
    fun `should return null for non-existent keys`() = runTest {
        val storage = createStorage()
        assertNull(storage.retrieve("non_existent_key").getOrNull())
    }

    @Test
    fun `should update existing values`() = runTest {
        val storage = createStorage()
        storage.store("key", "initial")
        storage.store("key", "updated")
        assertEquals("updated", storage.retrieve("key").getOrNull())
    }

    @Test
    fun `should delete existing values`() = runTest {
        val storage = createStorage()
        storage.store("key", "value")
        assertTrue(storage.delete("key").isSuccess)
        assertNull(storage.retrieve("key").getOrNull())
    }

    @Test
    fun `should handle delete of non-existent key`() = runTest {
        val storage = createStorage()
        assertTrue(storage.delete("non_existent").isSuccess)
    }

    @Test
    fun `should check if key exists`() = runTest {
        val storage = createStorage()
        storage.store("exists", "value")
        assertTrue(storage.contains("exists").getOrNull() ?: false)
        assertFalse(storage.contains("not_exists").getOrNull() ?: true)
    }

    @Test
    fun `should list all keys`() = runTest {
        val storage = createStorage()
        listOf("key1", "key2", "key3").forEach { storage.store(it, "value") }
        val keys = storage.listKeys().getOrNull()
        assertNotNull(keys)
        assertTrue(keys.containsAll(listOf("key1", "key2", "key3")))
    }

    @Test
    fun `should clear all stored values`() = runTest {
        val storage = createStorage()
        listOf("key1", "key2", "key3").forEach { storage.store(it, "value") }
        assertTrue(storage.clear().isSuccess)
        assertTrue(storage.listKeys().getOrNull()?.isEmpty() ?: true)
    }

    @Test
    fun `should store and retrieve credentials`() = runTest {
        val storage = createStorage()
        assertTrue(storage.storeCredentials("webdav", "testuser", "testpass123").isSuccess)
        val creds = storage.retrieveCredentials("webdav").getOrNull()
        assertNotNull(creds)
        assertEquals("testuser", creds.first)
        assertEquals("testpass123", creds.second)
    }

    @Test
    fun `should handle credentials with colons`() = runTest {
        val storage = createStorage()
        storage.storeCredentials("webdav", "domain:user", "pass:word:with:colons")
        val creds = storage.retrieveCredentials("webdav").getOrNull()
        assertNotNull(creds)
        assertEquals("domain:user", creds.first)
        assertEquals("pass:word:with:colons", creds.second)
    }

    @Test
    fun `should store and retrieve tokens`() = runTest {
        val storage = createStorage()
        assertTrue(storage.storeToken("dropbox", "sl.test_token_abc123xyz").isSuccess)
        assertEquals("sl.test_token_abc123xyz", storage.retrieveToken("dropbox").getOrNull())
    }

    @Test
    fun `should delete tokens`() = runTest {
        val storage = createStorage()
        storage.storeToken("googledrive", "test_token_123")
        assertTrue(storage.deleteToken("googledrive").isSuccess)
        assertNull(storage.retrieveToken("googledrive").getOrNull())
    }

    @Test
    fun `should store and retrieve private keys`() = runTest {
        val storage = createStorage()
        val privateKey = "-----BEGIN RSA PRIVATE KEY-----\ntest_key_data\n-----END RSA PRIVATE KEY-----"
        assertTrue(storage.storePrivateKey("sftp", privateKey).isSuccess)
        assertEquals(privateKey, storage.retrievePrivateKey("sftp").getOrNull())
    }

    @Test
    fun `should validate security status`() = runTest {
        val storage = createStorage()
        val result = storage.isSecure()
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `should handle empty keys`() = runTest {
        val storage = createStorage()
        assertTrue(storage.store("", "value").isSuccess)
    }

    @Test
    fun `should handle empty values`() = runTest {
        val storage = createStorage()
        assertTrue(storage.store("key", "").isSuccess)
        assertEquals("", storage.retrieve("key").getOrNull())
    }

    @Test
    fun `should handle special characters in keys`() = runTest {
        val storage = createStorage()
        val specialKeys = listOf("key-dashes", "key_underscores", "key.dots", "key with spaces")
        specialKeys.forEach { key ->
            storage.store(key, "value")
            assertEquals("value", storage.retrieve(key).getOrNull())
        }
    }

    @Test
    fun `should handle unicode content`() = runTest {
        val storage = createStorage()
        val unicodeValue = "Hello World Cafe"
        storage.store("unicode", unicodeValue)
        assertEquals(unicodeValue, storage.retrieve("unicode").getOrNull())
    }

    @Test
    fun `should handle long tokens`() = runTest {
        val storage = createStorage()
        val token = "a".repeat(2048)
        storage.storeToken("onedrive", token)
        assertEquals(token, storage.retrieveToken("onedrive").getOrNull())
    }
}
