# Security-KMP

Kotlin Multiplatform secure storage library with AES-256-GCM encryption for desktop and platform-specific implementations.

## Features

- **SecureStorage** - Platform-agnostic secure storage interface with credential/token/key helpers
- **DesktopSecureStorage** - AES-256-GCM file-based encrypted storage for JVM desktop
- **SecureStorageFactory** - Platform factory with expect/actual for creating storage instances
- **In-memory stubs** - Android, iOS, Wasm placeholders (replace with platform-native APIs in production)

## Platforms

- Android
- Desktop (JVM)
- iOS (x64, arm64, simulator)
- Web (Wasm/JS)

## Installation

Add as a git submodule:

```bash
git submodule add git@github.com:vasic-digital/Security-KMP.git
```

Then in your `settings.gradle.kts`:

```kotlin
includeBuild("Security-KMP")
```

## Usage

```kotlin
// Create via factory
val storage = SecureStorageFactory.create().getOrThrow()

// Store and retrieve values
storage.store("api_key", "sk-secret-value")
val key = storage.retrieve("api_key").getOrNull()

// Credential management
storage.storeCredentials("webdav", "user", "password")
val creds = storage.retrieveCredentials("webdav").getOrNull()

// Token management
storage.storeToken("dropbox", "sl.access-token-here")
val token = storage.retrieveToken("dropbox").getOrNull()

// Private key storage
storage.storePrivateKey("sftp", "-----BEGIN RSA PRIVATE KEY-----\n...")
val pk = storage.retrievePrivateKey("sftp").getOrNull()

// Desktop: direct construction with custom directory
val desktopStorage = DesktopSecureStorage(File("/path/to/storage"))
```

## License

Apache-2.0
