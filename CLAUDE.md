# Security-KMP

## Project Overview

Kotlin Multiplatform secure storage library. Package: `digital.vasic.security`.

## Build Commands

```bash
./gradlew desktopTest    # Run tests
./gradlew build          # Build all targets
```

## Architecture

- `SecureStorage.kt` - Interface with 7 core methods + 9 helper methods for credentials/tokens/keys
- `DesktopSecureStorage.kt` - AES-256-GCM file-based encrypted storage with in-memory caching
- `SecureStorageFactory.kt` - expect/actual platform factory

## Key Patterns

- DesktopSecureStorage uses hex-encoded keys and Base64-encoded encrypted values in `.secure_storage` file
- AES key stored in `.storage_key` file with owner-only permissions
- In-memory cache invalidated by file modification time + size tracking
- Mutex-based thread safety on all public operations
- Credential storage uses length-prefixed format to handle colons in usernames

## Dependencies

- kotlinx-coroutines (Mutex, Dispatchers.IO)
- JDK crypto APIs (AES/GCM/NoPadding, KeyGenerator, SecretKeySpec) - desktop only
