# Security-KMP User Guide

## Getting Started

Security-KMP provides a platform-agnostic secure storage interface with an AES-256-GCM encrypted implementation for desktop.

## Basic Operations

```kotlin
val storage = SecureStorageFactory.create().getOrThrow()

// Store and retrieve
storage.store("key", "value")
val value = storage.retrieve("key").getOrNull()

// Check and delete
val exists = storage.contains("key").getOrNull() ?: false
storage.delete("key")

// List all keys
val keys = storage.listKeys().getOrNull() ?: emptyList()

// Clear everything
storage.clear()
```

## Credential Management

```kotlin
// Store credentials (handles colons in usernames safely)
storage.storeCredentials("webdav", "domain:user", "pass:word")

// Retrieve as Pair<username, password>
val creds = storage.retrieveCredentials("webdav").getOrNull()
println("User: ${creds?.first}, Pass: ${creds?.second}")

// Delete credentials
storage.deleteCredentials("webdav")
```

## Token Storage

```kotlin
storage.storeToken("dropbox", "sl.access-token-abc123")
val token = storage.retrieveToken("dropbox").getOrNull()
storage.deleteToken("dropbox")
```

## Private Key Storage

```kotlin
val key = "-----BEGIN RSA PRIVATE KEY-----\nkey_data\n-----END RSA PRIVATE KEY-----"
storage.storePrivateKey("sftp", key)
val retrieved = storage.retrievePrivateKey("sftp").getOrNull()
storage.deletePrivateKey("sftp")
```

## Desktop Direct Construction

```kotlin
// Custom storage directory
val storage = DesktopSecureStorage(File("/custom/path"))

// Check security status
val secure = storage.isSecure().getOrNull() ?: false
```

## Security Verification

```kotlin
val isSecure = storage.isSecure().getOrNull() ?: false
// Verifies AES/GCM availability, directory permissions, and round-trip encryption
```
