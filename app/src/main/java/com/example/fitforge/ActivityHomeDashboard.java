package com.example.fitforge;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ActivityHomeDashboard extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // Keep track of selected tab to avoid re-creating same fragment
    private int currentNavId = -1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dashboard);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Load default fragment on first launch
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), R.id.nav_home);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Don't reload if same tab tapped again
            if (id == currentNavId) return false;

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment(), id);
                return true;
            } else if (id == R.id.nav_workout) {
                loadFragment(new WorkoutFragment(), id);
                return true;
            } else if (id == R.id.nav_stats) {
                loadFragment(new ProgressTrackingFragment(), id);
                return true;
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment(), id);
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment, int navId) {
        currentNavId = navId;

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.fragmentContainer, fragment);

        transaction.commit();
    }
}