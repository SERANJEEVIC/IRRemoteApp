# IR Remote Offline (Kotlin) — Ready-to-build Android Studio Project

This project is a fully-offline Android IR remote app (Kotlin) that stores devices & button IR patterns locally using Room, transmits IR via the phone's IR blaster (ConsumerIrManager), and can be CI-built into a signed release APK with GitHub Actions.

> **Important**: Most phones cannot receive IR pulses natively. The app provides a manual pattern entry and instructions to use a cheap IR receiver + Arduino to capture pulses.

---

## Features
- Offline-only, local Room DB.
- Devices and Buttons (label, frequency, pattern).
- Transmit IR with the phone's IR blaster.
- Import/Export JSON to app storage.
- Pre-seeded `CeilingFan` sample with ON/OFF patterns.
- Optional IR Finder screen with manual paste and Arduino instructions.

---

## How to build locally (Android Studio)

1. Open Android Studio -> `Open` -> select project root.
2. Let Gradle sync and download dependencies.
3. Connect an Android device and Run (or Build > Build Bundle(s)/APK(s) > Build APK(s)).
4. If your phone has IR blaster, test transmit in Device screen.

---

## Generate a keystore (for release signing)

Run:
```bash
keytool -genkeypair -v \
 -keystore release.keystore \
 -storetype JKS \
 -keyalg RSA \
 -keysize 2048 \
 -validity 10000 \
 -alias myappalias
