# Hidden App Features

## 🔒 App Icon Hiding

The app is designed to hide its launcher icon after installation and permission setup.

### How It Works

1. **Initial Launch**: When first installed, the app icon appears in the launcher
2. **Permission Setup**: User grants all required permissions
3. **Auto-Hide**: Once permissions are granted, the app icon automatically disappears from the launcher
4. **Background Operation**: The app continues running in the background as a foreground service

### Technical Implementation

- Uses `PackageManager.setComponentEnabledSetting()` to disable the launcher activity
- MainActivity component is disabled after permissions are granted
- App can still be accessed via Settings > Apps if needed
- Service continues running independently

## 🔄 Auto-Start on Boot

The app automatically starts when the device boots:

- **BootReceiver**: Listens for `BOOT_COMPLETED` intent
- **Auto-Start**: Service starts automatically without user interaction
- **Persistent**: Continues running even after device restarts

## 📱 Running in Background

- **Foreground Service**: Runs as a foreground service with notification
- **Periodic Collection**: Collects data every 5 minutes
- **Persistent**: Service restarts automatically if killed
- **Low Battery Impact**: Optimized for minimal battery usage

## 🔍 Finding the App (If Needed)

If you need to access the app after it's hidden:

1. **Settings Method**:
   - Go to Settings > Apps
   - Find "Data Collector" in the app list
   - You can view app info, force stop, or uninstall

2. **Package Manager** (Advanced):
   ```bash
   adb shell pm enable com.datacollector.app/.MainActivity
   ```

3. **Re-enable Icon** (For testing):
   - Use ADB command above
   - Or reinstall the app

## ⚠️ Important Notes

- The app icon hiding is **permanent** after permissions are granted
- The app cannot be easily found in the app drawer
- Service notification may be visible (can be minimized)
- App can still be uninstalled via Settings > Apps

## 🛠️ Development Notes

To test the hidden icon feature:

1. Install the app
2. Grant all permissions
3. Icon will disappear automatically
4. To restore icon for testing:
   ```bash
   adb shell pm enable com.datacollector.app/.MainActivity
   ```

## 🔐 Security Considerations

- Hidden icon provides basic obfuscation
- App is still visible in Settings
- Users can still uninstall via Settings
- For production, consider additional security measures:
  - App lock/password protection
  - Root detection
  - Anti-debugging measures
  - Encrypted communication
