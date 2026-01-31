const admin = require('firebase-admin');
const serviceAccount = require('./servicesAccountsKey.json');

// Initialize Firebase Admin
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

async function createTestData() {
    try {
        console.log('Creating test data in Firestore...');
        console.log('Project ID:', serviceAccount.project_id);
        
        // Create a test device document
        const deviceRef = db.collection('devices').doc('demo-device-001');
        await deviceRef.set({
            device_id: 'demo-device-001',
            model: 'Demo Device',
            manufacturer: 'Demo Manufacturer',
            android_version: '12',
            phone_number: '+1234567890',
            imei: '123456789012345',
            last_seen: admin.firestore.FieldValue.serverTimestamp(),
            updated_at: admin.firestore.FieldValue.serverTimestamp()
        });
        console.log('✅ Device document created');
        
        // Create test contact
        const contactRef = deviceRef.collection('contacts').doc();
        await contactRef.set({
            name: 'John Doe',
            phone: '+1234567890',
            collected_at: admin.firestore.FieldValue.serverTimestamp()
        });
        console.log('✅ Test contact created');
        
        // Create test SMS
        const smsRef = deviceRef.collection('sms').doc();
        await smsRef.set({
            address: '+1234567890',
            body: 'This is a test SMS message',
            date: Date.now().toString(),
            type: '1',
            collected_at: admin.firestore.FieldValue.serverTimestamp()
        });
        console.log('✅ Test SMS created');
        
        // Verify
        const devicesSnapshot = await db.collection('devices').get();
        console.log(`\n📊 Total devices: ${devicesSnapshot.size}`);
        
        console.log('\n✅ Test data created successfully!');
        console.log('\nNow check Firebase Console:');
        console.log('https://console.firebase.google.com/project/android-app-data-collect/firestore');
        console.log('\nYou should see:');
        console.log('  - devices collection');
        console.log('  - demo-device-001 document');
        console.log('  - contacts subcollection');
        console.log('  - sms subcollection');
        
        process.exit(0);
    } catch (error) {
        console.error('❌ Error:', error.message);
        if (error.code === 'not-found') {
            console.error('\n⚠️  Firestore database might not be created yet.');
            console.error('   Go to Firebase Console and create the database:');
            console.error('   https://console.firebase.google.com/project/android-app-data-collect/firestore');
        }
        process.exit(1);
    }
}

createTestData();
