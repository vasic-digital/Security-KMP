# Security-KMP Video Course Outline

## Module 1: Introduction
- What is Security-KMP and why it exists
- SecureStorage interface design
- Platform support overview

## Module 2: Desktop Encryption
- AES-256-GCM fundamentals
- Key generation and storage
- Encryption/decryption with random IV
- File format and persistence

## Module 3: Using the API
- Basic store/retrieve/delete operations
- Credential management with colons
- Token and private key storage
- Security verification

## Module 4: Platform Implementations
- Desktop: DesktopSecureStorage deep dive
- Android: EncryptedSharedPreferences integration
- iOS: Keychain Services integration
- Web: Browser storage considerations

## Module 5: Integration
- Using with Auth-KMP
- SecureStorageFactory pattern
- Testing with in-memory implementations
- Thread safety considerations
