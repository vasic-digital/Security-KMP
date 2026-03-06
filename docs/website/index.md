# Security-KMP

Cross-platform secure storage for Kotlin Multiplatform applications.

## Quick Start

```kotlin
val storage = SecureStorageFactory.create().getOrThrow()
storage.store("api_key", "sk-secret-value")
val key = storage.retrieve("api_key").getOrNull()
```

## Key Features

- AES-256-GCM encryption on desktop
- Thread-safe with coroutine Mutex
- Credential, token, and private key helpers
- Platform factory pattern with expect/actual
- In-memory cache with file modification tracking

## Links

- [User Guide](../user-guide.md)
- [API Reference](../api-reference.md)
- [Architecture](../architecture.md)
- [GitHub](https://github.com/vasic-digital/Security-KMP)
