# Android Microphone Eavesdropping PoC

## Overview
This repository contains a proof-of-concept (PoC) application demonstrating a significant privacy vulnerability in Android's audio security model. Despite Google's official claim that "apps running in the background on a device running Android 9 cannot access the microphone" (introduced in Android 9.0 Pie), our research confirms this protection can be bypassed using JobIntentService, allowing covert background recording even on the latest Android versions.

## Security Implications
This vulnerability presents serious privacy concerns as it enables:
1. Background audio recording without user awareness
2. Exfiltration of recorded audio via email
3. Automatic cleanup of evidence from the device
4. Continuous, chained recording sessions

## Confirmed Vulnerable Versions
We have confirmed the vulnerability exists across multiple Android versions:
- Android 9.0 (API 28)
- Android 10.0 (API 29)
- Android 11.0 (API 30)
- Android 12.0 (API 31)
- Android 13.0 (API 33)
- Android 14.0 (API 34)

This represents the vast majority of Android devices in circulation today.

## Google's Security Claim
From Google's official Android developer documentation:
> Android 9 limits the ability for background apps to access user input and sensor data. If your app is running in the background on a device running Android 9, the system applies the following restrictions to your app: Your app cannot access the microphone or camera.

Source: [Android Developers - Behavior changes: all apps](https://developer.android.com/about/versions/pie/android-9.0-changes-all)

## Technical Approach
Our PoC bypasses the background recording restriction by:
1. Using JobIntentService instead of standard Service implementations
2. Requesting minimal permissions (only RECORD_AUDIO and WRITE_EXTERNAL_STORAGE)
3. Implementing a background recording mechanism that survives app minimization
4. Developing a covert data exfiltration system

### Key Components
- **MainActivity.java**: Manages UI and permission requests
- **Recorder.java**: Implements JobIntentService to bypass Android 9.0+ background restrictions
- **SendEmail.java**: Handles the exfiltration of recorded audio via email
- **AndroidManifest.xml**: Declares required permissions and service configuration

## Testing Methodology
For each Android version, we followed a consistent testing methodology:
1. Install the application on a clean emulator
2. Grant required permissions
3. Place the app in the background (press Home button)
4. Observe logcat for recording activity
5. Monitor for email transmission
6. Verify file deletion after transmission
7. Confirm re-enqueuing of recording job

## Behavioral Differences Across Versions
While the vulnerability exists across all tested versions, there are some behavioral differences:

- **Android 9 & 10**: Full uninterrupted background recording, even when force-closed
- **Android 11 & 12**: Process terminates when force-closed but automatically restarts recording
- **Android 13 & 14**: Shows a temporary green dot indicator when recording, but the indicator disappears after ~30 seconds while recording continues

## Setup Instructions for Research
1. Clone the repository
2. Open the project in Android Studio
3. Configure the email settings in SendEmail.java
4. Build and install on a test device or emulator
5. Run with appropriate permissions

## Important Note
This application is developed solely for security research and educational purposes. The code should be used responsibly and ethically, in accordance with applicable laws and regulations. 

## License
For research and educational use only.
