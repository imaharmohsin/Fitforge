package com.example.fitforge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ThirdOnboarding extends AppCompatActivity {

    CardView btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_onboarding);

        btnGetStarted = findViewById(R.id.btnGetStarted);

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Onboarding complete mark karo
                getSharedPreferences("FitForge", MODE_PRIVATE)
                        .edit()
                        .putBoolean("onboardingDone", true)
                        .apply();

                // Login pe bhejo
                Intent intent = new Intent(ThirdOnboarding.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}