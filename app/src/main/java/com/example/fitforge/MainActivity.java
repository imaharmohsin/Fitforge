package com.example.fitforge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {

            // User logged in hai?
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // Seedha Dashboard
                startActivity(new Intent(MainActivity.this, ActivityHomeDashboard.class));

            } else {
                // Onboarding dekha hai pehle?
                boolean onboardingDone = getSharedPreferences("FitForge", MODE_PRIVATE)
                        .getBoolean("onboardingDone", false);

                if (onboardingDone) {
                    // Login pe bhejo
                    startActivity(new Intent(MainActivity.this, Login.class));
                } else {
                    // Pehli baar - Onboarding dikhao
                    startActivity(new Intent(MainActivity.this, OnBoarding.class));
                }
            }
            finish();

        }, 500);
    }
}