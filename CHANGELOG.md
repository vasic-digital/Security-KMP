<!-- SPDX-FileCopyrightText: 2025 Milos Vasic -->
<!-- SPDX-License-Identifier: Apache-2.0 -->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-03-06

### Added
- Initial release extracted from Yole monolith
- `SecureStorage` - Platform-agnostic secure storage interface with credential, token, and private key helpers
- `DesktopSecureStorage` - AES-256-GCM file-based encrypted storage implementation for JVM desktop
- `SecureStorageFactory` - Platform factory with expect/actual for creating platform-appropriate storage instances
- In-memory stubs for Android, iOS, and Wasm platforms (replace with platform-native APIs in production)
- Credential management (store/retrieve username-password pairs by service identifier)
- Token management (store/retrieve OAuth tokens by provider identifier)
- Private key management (store/retrieve SSH/PGP keys by identifier)
- Kotlin Multiplatform support (Android, Desktop/JVM, iOS, Wasm/JS)
- Comprehensive test suite
- CI/CD via GitHub Actions

### Infrastructure
- Gradle build with version catalog
- Kover code coverage
- SPDX license headers (Apache-2.0)
