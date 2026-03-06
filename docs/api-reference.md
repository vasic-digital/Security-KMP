# Security-KMP API Reference

## SecureStorage (Interface)

### Core Methods
- `store(key, value): Result<Unit>` - Store key-value pair
- `retrieve(key): Result<String?>` - Get value by key (null if missing)
- `delete(key): Result<Unit>` - Remove key
- `contains(key): Result<Boolean>` - Check key existence
- `listKeys(): Result<List<String>>` - List all stored keys
- `clear(): Result<Unit>` - Remove all data
- `isSecure(): Result<Boolean>` - Verify storage security

### Credential Helpers
- `storeCredentials(service, username, password): Result<Unit>` - Length-prefixed credential storage
- `retrieveCredentials(service): Result<Pair<String, String>?>` - Retrieve as (username, password)
- `deleteCredentials(service): Result<Unit>` - Remove credentials

### Token Helpers
- `storeToken(service, token): Result<Unit>` - Store access token
- `retrieveToken(service): Result<String?>` - Retrieve token
- `deleteToken(service): Result<Unit>` - Remove token

### Private Key Helpers
- `storePrivateKey(service, privateKey): Result<Unit>` - Store SSH/RSA key
- `retrievePrivateKey(service): Result<String?>` - Retrieve key
- `deletePrivateKey(service): Result<Unit>` - Remove key

## SecureStorageFactory (expect/actual)

- `create(): Result<SecureStorage>` - Create platform-appropriate storage instance
- `isAvailable(): Boolean` - Check if secure storage is available on this platform

## DesktopSecureStorage (Desktop/JVM)

- `DesktopSecureStorage(storageDir: File)` - Create with custom storage directory
- Uses AES-256-GCM encryption with random IV per write
- Key stored in `.storage_key`, data in `.secure_storage`
- In-memory cache with file modification tracking
- Mutex-based thread safety
