package digital.vasic.security

/**
 * Common interface for secure storage implementations across platforms.
 * Provides secure storage of sensitive data like passwords, tokens, and keys.
 */
interface SecureStorage {
    suspend fun store(key: String, value: String): Result<Unit>
    suspend fun retrieve(key: String): Result<String?>
    suspend fun delete(key: String): Result<Unit>
    suspend fun contains(key: String): Result<Boolean>
    suspend fun listKeys(): Result<List<String>>
    suspend fun clear(): Result<Unit>
    suspend fun isSecure(): Result<Boolean>

    suspend fun storeCredentials(service: String, username: String, password: String): Result<Unit> {
        val credentialData = "${username.length}:$username$password"
        return store("${service}_credentials", credentialData)
    }

    suspend fun retrieveCredentials(service: String): Result<Pair<String, String>?> {
        return retrieve("${service}_credentials").map { credentialData ->
            credentialData?.let {
                val colonIndex = it.indexOf(':')
                if (colonIndex >= 0) {
                    try {
                        val usernameLength = it.substring(0, colonIndex).toInt()
                        val username = it.substring(colonIndex + 1, colonIndex + 1 + usernameLength)
                        val password = it.substring(colonIndex + 1 + usernameLength)
                        Pair(username, password)
                    } catch (e: Exception) { null }
                } else null
            }
        }
    }

    suspend fun deleteCredentials(service: String): Result<Unit> = delete("${service}_credentials")
    suspend fun storeToken(service: String, token: String): Result<Unit> = store("${service}_token", token)
    suspend fun retrieveToken(service: String): Result<String?> = retrieve("${service}_token")
    suspend fun deleteToken(service: String): Result<Unit> = delete("${service}_token")
    suspend fun storePrivateKey(service: String, privateKey: String): Result<Unit> = store("${service}_private_key", privateKey)
    suspend fun retrievePrivateKey(service: String): Result<String?> = retrieve("${service}_private_key")
    suspend fun deletePrivateKey(service: String): Result<Unit> = delete("${service}_private_key")
}
