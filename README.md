# DOOMX Adaptive Gaming Performance System

Native Kotlin + Jetpack Compose Android Studio project for package `com.doomx.optimizer`.

## Included
- Dark DOOMX dashboard with red accent styling
- Device model, Android version, available RAM, free internal storage, and battery level
- Persistent selection for SMART, BALANCED, PERFORMANCE, and THERMAL SAFE profiles
- App-owned cache size inspection and clear-cache action
- Session audit history
- Honest labels for capabilities that are not implemented or not universally available

## Build
1. Open this folder in Android Studio (recent stable version).
2. Install Android SDK Platform 35 and JDK 17 if prompted.
3. Allow Gradle sync to complete.
4. Choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. APK output is normally `app/build/outputs/apk/debug/app-debug.apk`.

Command line, from this project folder:
```bash
gradle assembleDebug
```
If Gradle wrapper files are not present, use Android Studio to generate/sync them or run `gradle wrapper`.

## Important limitations
This project is a functional starter, not a fully validated release build. It has not been compiled in this environment. Thermal reporting is currently a capability label rather than a live thermal reading. History is session-only. Root detection, allowlisted root commands, sustained-performance API integration, floating overlay, Free Fire package launch, exportable logs, cleanup confirmation dialog, and automated device tests remain to be implemented. No fake FPS is shown, and the app does not claim to boost FPS or modify game files.

Cache deletion is limited to the app's own cache directory. It can be irreversible.
