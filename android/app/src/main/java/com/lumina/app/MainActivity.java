package com.lumina.app;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    
    private static final int LOCK_REQUEST_CODE = 221;
    private boolean isAuthenticatedSession = false;

    @Override
    public void onResume() {
        super.onResume();
        
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isLockEnabled = prefs.getBoolean("app_lock_enabled", false);
        
        if (isLockEnabled && !isAuthenticatedSession) {
            if (this.bridge != null && this.bridge.getWebView() != null) {
                this.bridge.getWebView().setVisibility(View.INVISIBLE);
            }
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager.isKeyguardSecure()) {
                Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("Unlock App", "Use your device PIN, Pattern, or Biometrics to unlock Expense Tracker Pro");
                if (intent != null) {
                    startActivityForResult(intent, LOCK_REQUEST_CODE);
                }
            } else {
                // Device has no lock screen setup, so just let them in
                isAuthenticatedSession = true;
                if (this.bridge != null && this.bridge.getWebView() != null) {
                    this.bridge.getWebView().setVisibility(View.VISIBLE);
                }
            }
        } else if (this.bridge != null && this.bridge.getWebView() != null) {
            this.bridge.getWebView().setVisibility(View.VISIBLE);
        }
        
        // Ensure webview has native haptics when interacting with it
        if (this.bridge != null && this.bridge.getWebView() != null) {
            this.bridge.getWebView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (this.bridge != null && this.bridge.getWebView() != null && this.bridge.getWebView().canGoBack()) {
            this.bridge.getWebView().goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                isAuthenticatedSession = true;
                if (this.bridge != null && this.bridge.getWebView() != null) {
                    this.bridge.getWebView().setVisibility(View.VISIBLE);
                }
            } else {
                // User cancelled or failed authentication, close app
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Settings");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
