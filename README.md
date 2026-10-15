# DOOMX FF Optimizer

Android Studio source project for a rootless black-and-red Free Fire preparation assistant.

## Build on GitHub Actions
1. Create a GitHub repository.
2. Upload the contents of this project folder to the repository root on branch `main` (including hidden `.github` folder).
3. Open the repository's **Actions** tab. If asked, enable Actions.
4. Select **Build DOOMX APK**, click **Run workflow**, choose `main`, then click **Run workflow**.
5. Wait for the green check. Open the completed run and download the artifact named `DOOMX-FF-Optimizer-debug-apk`.
6. Extract that artifact ZIP to get `app-debug.apk`.

The workflow builds a debug APK. Android may ask you to confirm installation from the source you used to download it.

## Local build
Open the folder in Android Studio, install SDK Platform 35 and JDK 17 if prompted, wait for Gradle sync, then choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

## Capabilities and limits
- Reads device model, Android version, available/total RAM, storage availability, network transport and display refresh rate.
- Launches installed Free Fire or Free Fire MAX after the user taps Boost & Play.
- Opens Android app-management, storage and wireless settings for user-controlled actions.
- `ACCESS_NETWORK_STATE` is a normal permission; no runtime prompt is required.
- It does not force-stop other apps, delete personal files, modify CPU/GPU clocks, unlock thermal limits, or measure actual in-game FPS.
- No root, accessibility service, usage access, VPN, notification access, or broad storage permission is requested.
