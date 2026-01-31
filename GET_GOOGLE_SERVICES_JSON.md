# How to Get google-services.json

## 📋 Steps to Download google-services.json

### Step 1: Go to Firebase Console
1. Open: https://console.firebase.google.com/project/android-app-data-collect
2. Click the gear icon ⚙️ next to "Project Overview"
3. Select **"Project settings"**

### Step 2: Add Android App (if not already added)
1. Scroll down to **"Your apps"** section
2. If you see an Android app, skip to Step 3
3. If no Android app, click **"Add app"** → Select **Android** icon
4. Enter:
   - **Package name**: `com.datacollector.app`
   - **App nickname** (optional): Data Collector
   - **Debug signing certificate SHA-1** (optional, skip for now)
5. Click **"Register app"**

### Step 3: Download google-services.json
1. You'll see a **"Download google-services.json"** button
2. Click it to download the file
3. Save the file

### Step 4: Place the File
1. Copy the downloaded `google-services.json` file
2. Place it in: `android-app/app/google-services.json`
3. Make sure it's in the `app/` folder (same level as `build.gradle`)

### Step 5: Verify
```bash
cd android-app/app
ls -la google-services.json
```

You should see the file listed.

## ✅ After Setup

Once `google-services.json` is in place:
1. Rebuild the APK: `cd android-app && ./gradlew assembleDebug`
2. Install on device
3. Data will be sent directly to Firebase!

## 🔍 File Structure Should Be:

```
android-app/
├── app/
│   ├── google-services.json  ← HERE
│   ├── build.gradle
│   └── src/
└── build.gradle
```

## ⚠️ Important

- The `google-services.json` file is unique to your Firebase project
- Don't share it publicly (it's in .gitignore)
- Each Firebase project has its own file
- The file connects your Android app to Firebase

## 🚀 Quick Link

Direct link to project settings:
https://console.firebase.google.com/project/android-app-data-collect/settings/general
