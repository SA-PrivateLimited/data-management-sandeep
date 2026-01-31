# Build Instructions

## Prerequisites

1. **Android Studio** (latest version)
   - Download from: https://developer.android.com/studio
   - Install Android SDK (API 24+)

2. **Node.js** (v14 or higher)
   - Download from: https://nodejs.org/

3. **Java JDK** (11 or higher)
   - Required for Android development

## Step 1: Setup Backend Server

```bash
# Navigate to backend directory
cd backend-server

# Install dependencies
npm install

# Start the server
npm start
```

The server will run on `http://localhost:3000`

**Important**: Note your server's IP address. You'll need it for the Android app.

## Step 2: Configure Android App

1. Open Android Studio
2. Open the project: `File > Open > Select android-app folder`
3. Wait for Gradle sync to complete
4. Open `MainActivity.java`
5. Find this line:
   ```java
   private static final String SERVER_URL = "http://YOUR_SERVER_IP:3000/api/data";
   ```
6. Replace `YOUR_SERVER_IP` with your actual server IP address
   - For local testing: Use `10.0.2.2` (Android emulator) or your computer's local IP
   - For production: Use your public server IP

## Step 3: Build APK

### Option A: Using Android Studio (Recommended)

1. In Android Studio, go to: `Build > Build Bundle(s) / APK(s) > Build APK(s)`
2. Wait for build to complete
3. APK location: `android-app/app/build/outputs/apk/debug/app-debug.apk`

### Option B: Using Command Line

```bash
cd android-app
./gradlew assembleDebug
```

APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

### Option C: Build Release APK (For Distribution)

1. In Android Studio: `Build > Generate Signed Bundle / APK`
2. Select "APK"
3. Create a new keystore (first time) or use existing
4. Fill in keystore information
5. Select "release" build variant
6. Click "Finish"

Or via command line:
```bash
cd android-app
./gradlew assembleRelease
```

## Step 4: Install APK on Device

### Method 1: Direct Install
1. Transfer APK to Android device
2. Enable "Install from Unknown Sources" in device settings
3. Open APK file and install

### Method 2: Using ADB
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Method 3: Using Android Studio
1. Connect device via USB
2. Enable USB debugging
3. Click "Run" button in Android Studio

## Step 5: Grant Permissions

After installing the app:
1. Open the app
2. Grant all requested permissions:
   - Contacts
   - SMS
   - Call Logs
   - Phone
   - Storage
   - Location
3. Enable "Usage Access" if prompted (Settings > Apps > Special Access)

## Step 6: Access Admin Dashboard

1. Ensure backend server is running
2. Open browser and navigate to:
   - Local: `http://localhost:3000`
   - Network: `http://YOUR_SERVER_IP:3000`
3. You should see the admin dashboard

## Troubleshooting

### Build Errors

**Error: SDK not found**
- Open Android Studio
- Go to: `Tools > SDK Manager`
- Install Android SDK Platform 34

**Error: Gradle sync failed**
- File > Invalidate Caches / Restart
- Try: `./gradlew clean` then rebuild

### App Not Sending Data

1. Check server URL in MainActivity.java
2. Ensure device and server are on same network
3. Check server logs for errors
4. Verify all permissions are granted

### Server Connection Issues

1. Check firewall settings
2. Ensure server is listening on `0.0.0.0` (not just localhost)
3. Test with: `curl http://YOUR_SERVER_IP:3000/api/stats`

### Permission Denied

- Go to device Settings > Apps > Data Collector
- Grant all permissions manually
- For Usage Access: Settings > Apps > Special Access > Usage Access

## Network Configuration

### Finding Your Server IP

**Windows:**
```cmd
ipconfig
```
Look for IPv4 Address

**Mac/Linux:**
```bash
ifconfig
# or
ip addr show
```

### Testing Connection

From Android device browser, try:
```
http://YOUR_SERVER_IP:3000/api/stats
```

Should return JSON with statistics.

## Production Deployment

For production use:

1. **Use HTTPS**: Set up SSL certificate
2. **Add Authentication**: Implement login for dashboard
3. **Secure Database**: Encrypt sensitive data
4. **Update Server URL**: Use production server address
5. **Enable ProGuard**: Obfuscate code in release build
6. **Review Permissions**: Only request what's needed

## Support

If you encounter issues:
1. Check Android Studio logcat for errors
2. Check server console for errors
3. Verify network connectivity
4. Ensure all dependencies are installed
