package com.lumina.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebStorage;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import com.lumina.app.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        
        Switch switchAppLock = findViewById(R.id.switch_app_lock);
        switchAppLock.setChecked(prefs.getBoolean("app_lock_enabled", false));
        switchAppLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("app_lock_enabled", isChecked).apply();
            }
        });

        findViewById(R.id.btn_clear_recorded_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear web cache and internal cache directory
                WebStorage.getInstance().deleteAllData();
                clearCache(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, "Recorded data cleared", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_clear_all_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Clear SharedPreferences
                SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                prefs.edit().clear().apply();

                // 2. Clear WebStorage (localStorage, IndexedDB)
                WebStorage.getInstance().deleteAllData();
                
                // 3. Clear cache
                clearCache(SettingsActivity.this);

                Toast.makeText(SettingsActivity.this, "All data cleared. Restarting...", Toast.LENGTH_SHORT).show();

                // 4. Restart app
                Intent intent = new Intent(SettingsActivity.this, OnboardingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                Runtime.getRuntime().exit(0);
            }
        });
    }

    private void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
