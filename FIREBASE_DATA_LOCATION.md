# Firebase Data Storage Location

## 📍 Where Data is Stored

Your data is stored in **Firebase Firestore Database** (not Cloud Storage for files).

### Firebase Project
- **Project ID**: `android-app-data-collect`
- **Project Name**: android-app-data-collect
- **Database Type**: Firestore Database

## 🗂️ Data Structure in Firestore

### Main Collection: `devices`

```
Firestore Database
└── devices/                          (Main Collection)
    └── {deviceId}/                   (Document - e.g., "ABC123")
        ├── Device Info Fields:
        │   ├── device_id: "ABC123"
        │   ├── model: "Pixel 5"
        │   ├── manufacturer: "Google"
        │   ├── android_version: "12"
        │   ├── phone_number: "+1234567890"
        │   ├── imei: "123456789012345"
        │   ├── last_seen: Timestamp
        │   └── updated_at: Timestamp
        │
        ├── contacts/                 (Subcollection)
        │   └── {auto-generated-id}/
        │       ├── name: "John Doe"
        │       ├── phone: "+1234567890"
        │       └── collected_at: Timestamp
        │
        ├── sms/                      (Subcollection)
        │   └── {auto-generated-id}/
        │       ├── address: "+1234567890"
        │       ├── body: "Message text"
        │       ├── date: "1234567890"
        │       ├── type: "1"
        │       └── collected_at: Timestamp
        │
        ├── call_logs/               (Subcollection)
        │   └── {auto-generated-id}/
        │       ├── number: "+1234567890"
        │       ├── type: "1"
        │       ├── date: "1234567890"
        │       ├── duration: "120"
        │       └── collected_at: Timestamp
        │
        ├── installed_apps/           (Subcollection)
        │   └── {auto-generated-id}/
        │       ├── package_name: "com.example.app"
        │       ├── app_name: "Example App"
        │       ├── is_system_app: false
        │       └── collected_at: Timestamp
        │
        ├── locations/                (Subcollection)
        │   └── {auto-generated-id}/
        │       ├── latitude: 37.7749
        │       ├── longitude: -122.4194
        │       ├── accuracy: 10.5
        │       ├── timestamp: "1234567890"
        │       └── collected_at: Timestamp
        │
        ├── files/                    (Subcollection)
        │   └── {auto-generated-id}/
        │       ├── name: "document.pdf"
        │       ├── path: "/storage/emulated/0/Download/document.pdf"
        │       ├── size: 1024000
        │       ├── is_directory: false
        │       ├── last_modified: "1234567890"
        │       └── collected_at: Timestamp
        │
        └── submissions/              (Subcollection)
            └── {auto-generated-id}/
                ├── timestamp: "1234567890"
                └── collected_at: Timestamp
```

## 🔍 How to View Your Data

### Method 1: Firebase Console (Web)

1. **Go to Firebase Console:**
   ```
   https://console.firebase.google.com/
   ```

2. **Select Your Project:**
   - Click on: `android-app-data-collect`

3. **Navigate to Firestore:**
   - Click "Firestore Database" in left sidebar
   - Or go directly: https://console.firebase.google.com/project/android-app-data-collect/firestore

4. **View Data:**
   - You'll see the `devices` collection
   - Click on a device ID to see its document
   - Click on subcollections (contacts, sms, etc.) to view data

### Method 2: Firebase CLI

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login
firebase login

# View data
firebase firestore:get devices
```

### Method 3: Programmatic Access

```javascript
// Example: Get all devices
const admin = require('firebase-admin');
const db = admin.firestore();

const devices = await db.collection('devices').get();
devices.forEach(doc => {
    console.log('Device ID:', doc.id);
    console.log('Data:', doc.data());
});
```

## 📊 Data Path Examples

### Example Device Document Path:
```
/devices/ABC123
```

### Example Contact Path:
```
/devices/ABC123/contacts/xyz789
```

### Example SMS Path:
```
/devices/ABC123/sms/msg456
```

## 🔐 Access Control

Currently, data is accessible via:
- **Firebase Admin SDK** (server-side) - Full access
- **Firebase Console** - Full access (if you're project owner)
- **API Endpoints** - Read-only access via dashboard

## 📝 Important Notes

1. **Firestore vs Cloud Storage:**
   - Data is in **Firestore Database** (NoSQL document database)
   - NOT in Cloud Storage (file storage)
   - Firestore is for structured data (JSON-like documents)

2. **Data Organization:**
   - Each device has its own document
   - Each data type (contacts, SMS, etc.) is a subcollection
   - This allows efficient querying and scaling

3. **Automatic Features:**
   - Timestamps are automatically managed
   - Documents are auto-indexed
   - Queries are optimized automatically

## 🚀 Quick Access Links

- **Firebase Console**: https://console.firebase.google.com/project/android-app-data-collect
- **Firestore Database**: https://console.firebase.google.com/project/android-app-data-collect/firestore
- **Project Settings**: https://console.firebase.google.com/project/android-app-data-collect/settings

## 📈 Data Statistics

To see data statistics:
- Go to Firebase Console
- Click on "Firestore Database"
- View collection/document counts
- Or use `/api/stats` endpoint on your server
