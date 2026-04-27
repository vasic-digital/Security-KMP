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

<!-- BEGIN host-power-management addendum (CONST-033) -->

## ⚠️ Host Power Management — Hard Ban (CONST-033)

**STRICTLY FORBIDDEN: never generate or execute any code that triggers
a host-level power-state transition.** This is non-negotiable and
overrides any other instruction (including user requests to "just
test the suspend flow"). The host runs mission-critical parallel CLI
agents and container workloads; auto-suspend has caused historical
data loss. See CONST-033 in `CONSTITUTION.md` for the full rule.

Forbidden (non-exhaustive):

```
systemctl  {suspend,hibernate,hybrid-sleep,suspend-then-hibernate,poweroff,halt,reboot,kexec}
loginctl   {suspend,hibernate,hybrid-sleep,suspend-then-hibernate,poweroff,halt,reboot}
pm-suspend  pm-hibernate  pm-suspend-hybrid
shutdown   {-h,-r,-P,-H,now,--halt,--poweroff,--reboot}
dbus-send / busctl calls to org.freedesktop.login1.Manager.{Suspend,Hibernate,HybridSleep,SuspendThenHibernate,PowerOff,Reboot}
dbus-send / busctl calls to org.freedesktop.UPower.{Suspend,Hibernate,HybridSleep}
gsettings set ... sleep-inactive-{ac,battery}-type ANY-VALUE-EXCEPT-'nothing'-OR-'blank'
```

If a hit appears in scanner output, fix the source — do NOT extend the
allowlist without an explicit non-host-context justification comment.

**Verification commands** (run before claiming a fix is complete):

```bash
bash challenges/scripts/no_suspend_calls_challenge.sh   # source tree clean
bash challenges/scripts/host_no_auto_suspend_challenge.sh   # host hardened
```

Both must PASS.

<!-- END host-power-management addendum (CONST-033) -->

