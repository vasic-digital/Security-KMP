package digital.vasic.security

/**
 * Platform factory for creating secure storage instances.
 */
expect object SecureStorageFactory {
    suspend fun create(): Result<SecureStorage>
    suspend fun isAvailable(): Boolean
}
