<!-- SPDX-FileCopyrightText: 2025 Milos Vasic -->
<!-- SPDX-License-Identifier: Apache-2.0 -->

# Contributing to Security-KMP

Thank you for your interest in contributing to Security-KMP!

## Getting Started

1. Fork the repository
2. Clone your fork
3. Create a feature branch: `git checkout -b feature/my-feature`
4. Make your changes
5. Run tests: `./gradlew test`
6. Commit: `git commit -m "feat: add my feature"`
7. Push: `git push origin feature/my-feature`
8. Open a Pull Request

## Development Setup

### Prerequisites
- JDK 11+
- Gradle 8.7+

### Building
```bash
./gradlew build
```

### Testing
```bash
./gradlew test
./gradlew koverHtmlReport  # Coverage report
```

## Code Style

- Kotlin primary language
- Follow existing code conventions
- SPDX license headers required on all new files:
```kotlin
/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Brief description
 *
 *########################################################*/
```

## Module-Specific Notes

- Never log, print, or expose stored secrets, keys, or credentials in any output
- Desktop storage uses AES-256-GCM encryption; do not weaken the cipher suite
- Platform stubs (Android, iOS, Wasm) are intentionally in-memory only; production apps should replace them with platform-native secure storage
- All storage operations return `Result<T>` to handle failures gracefully
- Test with both valid and invalid/corrupted storage states

## Pull Request Process

1. Update tests for any new functionality
2. Ensure all tests pass
3. Update README.md if API changes
4. Update CHANGELOG.md with your changes

## License

By contributing, you agree that your contributions will be licensed under the Apache-2.0 License.
