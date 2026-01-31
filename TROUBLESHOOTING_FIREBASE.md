# Troubleshooting: Unable to See Devices Collection

## 🔍 Common Issues and Solutions

### Issue 1: Collection Doesn't Exist Yet

**Problem:** Firestore collections are created automatically when the first document is written. If no data has been sent, the collection won't appear.

**Solution:**
1. Make sure your Android app is running and has sent data
2. Check server logs to see if data is being received
3. The collection will appear after the first document is written

### Issue 2: Firestore Database Not Created

**Problem:** Firestore database might not be initialized in your Firebase project.

**Solution:**
1. Go to Firebase Console: https://console.firebase.google.com/project/android-app-data-collect
2. Click "Firestore Database" in left sidebar
3. If you see "Create database" button, click it
4. Choose:
   - **Mode**: Production mode (or Test mode for development)
   - **Location**: Select closest region (e.g., us-central1)
5. Click "Enable"

### Issue 3: Wrong Project Selected

**Problem:** You might be looking at a different Firebase project.

**Solution:**
1. Verify project ID: `android-app-data-collect`
2. Check URL: `https://console.firebase.google.com/project/android-app-data-collect/firestore`
3. Make sure you're logged in with the correct Google account

### Issue 4: Server Not Running or Not Receiving Data

**Problem:** Backend server might not be running or Android app isn't sending data.

**Solution:**
1. **Start the server:**
   ```bash
   cd backend-server
   npm start
   ```

2. **Check server logs** for:
   - "Firebase Firestore initialized successfully"
   - "Data received and stored in Firebase successfully"

3. **Verify Android app:**
   - Check if app is running
   - Verify server URL is correct in MainActivity.java
   - Check app logs for network errors

### Issue 5: Permissions Issue

**Problem:** Service account might not have proper permissions.

**Solution:**
1. Go to Firebase Console > Project Settings > Service Accounts
2. Verify service account exists: `firebase-adminsdk-fbsvc@android-app-data-collect.iam.gserviceaccount.com`
3. Ensure it has "Firebase Admin SDK Administrator Service Agent" role

## 🧪 Testing Steps

### Step 1: Test Firebase Connection

Run the test script:
```bash
cd backend-server
node test-firebase.js
```

Expected output:
- ✅ Test document written successfully
- ✅ Test document read successfully
- 📊 Total devices in collection: X

### Step 2: Check Server Status

```bash
cd backend-server
npm start
```

Look for:
```
Server running on http://0.0.0.0:3000
Firebase Firestore initialized successfully
```

### Step 3: Send Test Data

Use curl to send test data:
```bash
curl -X POST http://localhost:3000/api/data \
  -H "Content-Type: application/json" \
  -d '{
    "deviceInfo": {
      "deviceId": "test-device-123",
      "model": "Test Model",
      "manufacturer": "Test Manufacturer"
    },
    "timestamp": "1234567890"
  }'
```

### Step 4: Verify in Firebase Console

1. Go to: https://console.firebase.google.com/project/android-app-data-collect/firestore
2. You should see:
   - `devices` collection
   - Document with ID: `test-device-123`

## 📋 Checklist

- [ ] Firestore Database is created in Firebase Console
- [ ] Server is running (`npm start`)
- [ ] Server shows "Firebase Firestore initialized successfully"
- [ ] Service account key file exists: `backend-server/servicesAccountsKey.json`
- [ ] Android app is configured with correct server URL
- [ ] Android app has sent data at least once
- [ ] You're viewing the correct Firebase project

## 🔧 Quick Fixes

### Create Firestore Database (if not exists)

1. Open: https://console.firebase.google.com/project/android-app-data-collect/firestore
2. Click "Create database"
3. Select mode and location
4. Click "Enable"

### Verify Service Account

```bash
cd backend-server
cat servicesAccountsKey.json | grep project_id
```

Should show: `"project_id": "android-app-data-collect"`

### Check Server Logs

```bash
cd backend-server
npm start
```

Watch for errors or success messages.

## 🆘 Still Not Working?

1. **Check Firebase Console:**
   - Go to Firestore Database
   - Look for any error messages
   - Check if database is in "locked" mode

2. **Check Server Logs:**
   - Look for Firebase initialization errors
   - Check for permission errors
   - Verify service account key is valid

3. **Test Manually:**
   ```bash
   cd backend-server
   node test-firebase.js
   ```

4. **Verify Project Access:**
   - Make sure you have access to the Firebase project
   - Check if you're the project owner or have Editor role

## 📞 Next Steps

If collection still doesn't appear:
1. Run `node test-firebase.js` to verify connection
2. Check Firebase Console for database status
3. Verify server is receiving data from Android app
4. Check server logs for any errors
