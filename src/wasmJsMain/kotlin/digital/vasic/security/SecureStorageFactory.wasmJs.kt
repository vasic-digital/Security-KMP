package digital.vasic.security

/**
 * WasmJs implementation of SecureStorageFactory.
 * Uses in-memory storage as fallback. Replace with localStorage in production.
 */
actual object SecureStorageFactory {
    actual suspend fun create(): Result<SecureStorage> = Result.success(WasmInMemorySecureStorage())
    actual suspend fun isAvailable(): Boolean = true
}

private class WasmInMemorySecureStorage : SecureStorage {
    private val storage = mutableMapOf<String, String>()
    override suspend fun store(key: String, value: String): Result<Unit> { storage[key] = value; return Result.success(Unit) }
    override suspend fun retrieve(key: String): Result<String?> = Result.success(storage[key])
    override suspend fun delete(key: String): Result<Unit> { storage.remove(key); return Result.success(Unit) }
    override suspend fun contains(key: String): Result<Boolean> = Result.success(storage.containsKey(key))
    override suspend fun listKeys(): Result<List<String>> = Result.success(storage.keys.toList())
    override suspend fun clear(): Result<Unit> { storage.clear(); return Result.success(Unit) }
    override suspend fun isSecure(): Result<Boolean> = Result.success(true)
}
