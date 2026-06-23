package com.lumina.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import com.lumina.app.R;

public class OnboardingActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private EditText etName;
    private EditText etBudget;
    private Button btnNameNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Native Splash Screen API (Android 12+) should be installed here if we were using the core-splashscreen library.
        // But since we rely on the theme, it handles it before onCreate.
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean hasCompletedOnboarding = prefs.getBoolean("has_completed_onboarding", false);

        if (hasCompletedOnboarding) {
            launchMainActivity();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        viewFlipper = findViewById(R.id.viewFlipper);
        etName = findViewById(R.id.et_name);
        etBudget = findViewById(R.id.et_budget);
        btnNameNext = findViewById(R.id.btn_name_next);

        // Step 1 buttons
        findViewById(R.id.btn_tutorial_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        });

        findViewById(R.id.btn_tutorial_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(1); // Jump to Name screen
            }
        });

        // Step 2 logic (Name)
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnNameNext.setEnabled(s.toString().trim().length() > 0);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnNameNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        });

        // Step 3 buttons
        findViewById(R.id.btn_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });

        findViewById(R.id.btn_budget_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });
    }

    private void finishOnboarding() {
        String name = etName.getText().toString().trim();
        String budget = etBudget.getText().toString().trim();

        SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit();
        editor.putBoolean("has_completed_onboarding", true);
        editor.putString("user_name", name);
        if (!budget.isEmpty()) {
            editor.putString("user_budget", budget);
        }
        editor.apply();

        launchMainActivity();
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
