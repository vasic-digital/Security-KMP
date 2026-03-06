# Security-KMP Agent Guidelines

## Testing

Tests in `src/commonTest/` and `src/desktopTest/`. Run with `./gradlew desktopTest`.

Test files:
- `SecureStorageTest.kt` - Abstract test class (20 tests): CRUD, credentials, tokens, private keys, unicode, edge cases
- `DesktopSecureStorageTest.kt` - Desktop-specific (12 tests): encryption, persistence, corruption, directory creation

## Rules

- Never remove or disable tests
- All changes must pass existing tests
- Desktop encryption must use AES/GCM/NoPadding with 256-bit keys
- Android/iOS/Wasm stubs are placeholders — replace with platform-native APIs when needed
