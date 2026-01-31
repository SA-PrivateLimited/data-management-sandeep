const admin = require('firebase-admin');
const serviceAccount = require('./servicesAccountsKey.json');

// Initialize Firebase Admin
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

async function testFirebase() {
    try {
        console.log('Testing Firebase connection...');
        console.log('Project ID:', serviceAccount.project_id);
        
        // Test write
        const testRef = db.collection('devices').doc('test-device');
        await testRef.set({
            device_id: 'test-device',
            model: 'Test Device',
            manufacturer: 'Test Manufacturer',
            test: true,
            created_at: admin.firestore.FieldValue.serverTimestamp()
        });
        console.log('✅ Test document written successfully');
        
        // Test read
        const doc = await testRef.get();
        if (doc.exists) {
            console.log('✅ Test document read successfully');
            console.log('Document data:', doc.data());
        } else {
            console.log('❌ Document does not exist');
        }
        
        // List all devices
        const devicesSnapshot = await db.collection('devices').get();
        console.log(`\n📊 Total devices in collection: ${devicesSnapshot.size}`);
        
        if (devicesSnapshot.size > 0) {
            console.log('\nDevice IDs:');
            devicesSnapshot.forEach(doc => {
                console.log(`  - ${doc.id}`);
            });
        } else {
            console.log('\n⚠️  No devices found. Collection is empty.');
            console.log('   This is normal if no data has been sent from the Android app yet.');
        }
        
        // Clean up test document
        await testRef.delete();
        console.log('\n✅ Test document deleted');
        
        process.exit(0);
    } catch (error) {
        console.error('❌ Error:', error.message);
        console.error('Full error:', error);
        process.exit(1);
    }
}

testFirebase();
