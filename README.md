# DOOMX NEXUS Optimizer 4.0

A refreshed, black/graphite and DOOMX-red Android gaming utility based on the supplied DOOMX FF Optimizer 3.2 project.

## Included features
- New DOOMX NEXUS dashboard and clearer device snapshot.
- Live Android-reported available RAM, battery level, battery temperature when exposed, and display refresh rate.
- Free Fire / Free Fire MAX launch action, with a staged preparation animation (the animation is not a benchmark or hardware overclock).
- Floating DX control panel with draggable handle, saved LOW → BALANCED → HIGH profile cycling, live supported readings, and separate panel-close vs stop-overlay actions.
- Sensitivity profile suggestions, phone/RAM selectors, optional DPI input, X/Y touch profile editor, and copy-to-clipboard actions.
- Network transport check and Android network settings shortcut.
- Storage filesystem summary and Android app/device storage settings shortcuts.
- Dark/light theme toggle and local owner key manager.
- Android 23+ project, Java/Kotlin 17, and GitHub Actions APK workflow.

## Important limits — no fake claims
Android normally does not let an ordinary non-root app force CPU/GPU clocks, clear other apps' private RAM/cache, change Free Fire's internal sensitivity, or read reliable live in-game FPS. Display Hz is not FPS. DOOMX profile modes are saved suggestions, not a hardware overclock. Network status is connection type, not measured ping.

## Access key warning
The supplied app's owner code (`doomadmin`) and generated keys are local prototype controls stored in SharedPreferences. They are not secure online licensing and can be bypassed by modifying/reinstalling the app. Do not distribute this as a secure paid-key system. Production access control needs server-side owner authentication, a protected secret, a database, and server-side validation. This package does not pretend to include that backend.

## Build a debug APK
1. Extract this ZIP.
2. Open the `DOOMX_NEXUS_4` folder in Android Studio.
3. Install Android SDK Platform 35 and JDK 17 if prompted.
4. Use **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. The APK is normally at `app/build/outputs/apk/debug/app-debug.apk`.

## Build using GitHub Actions
Upload the project contents to a GitHub repository, then open **Actions → Build DOOMX APK → Run workflow**. Download the generated artifact after the workflow completes.

## First-run notes
- The floating panel requires the user to grant Android's **Display over other apps** permission.
- Install the official Free Fire or Free Fire MAX app to use the launch action.
- Test the APK on your actual Android phone. Overlay behavior and sensor availability vary by device and Android version.
