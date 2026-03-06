# Security-KMP Architecture

## Overview

Secure storage library for Kotlin Multiplatform with AES-256-GCM encryption on desktop and pluggable platform implementations.

## Dependencies

- kotlinx-coroutines (Mutex for thread safety, Dispatchers.IO for file operations)
- JDK crypto (AES/GCM/NoPadding, KeyGenerator, SecretKeySpec) - desktop only

## Design Decisions

1. SecureStorage is an interface with default helper methods — same interface as Auth-KMP for interop
2. Credential storage uses length-prefixed format (`"${username.length}:$username$password"`) to handle colons in usernames
3. DesktopSecureStorage uses hex-encoded keys and Base64 encrypted values in a line-per-entry file format
4. Random IV per encryption ensures identical plaintext produces different ciphertext
5. File modification time + size tracking invalidates in-memory cache without unnecessary file reads
6. Android/iOS/Wasm use in-memory stubs — replace with EncryptedSharedPreferences, Keychain, localStorage respectively

## File Format

Desktop storage uses two files:
- `.storage_key` - Raw AES-256 key bytes (32 bytes, owner-readable only)
- `.secure_storage` - Line-per-entry: `hex_encoded_key|base64_iv_plus_ciphertext`
