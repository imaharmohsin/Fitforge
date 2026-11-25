package com.example.fitforge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.health.connect.client.PermissionController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Set;

public class HomeFragment extends Fragment {

    // ── Views ──────────────────────────────────────────────────────────────
    private TextView tvGreeting;
    private TextView tvSubGreeting;

    // Steps card
    private TextView tvStepsValue;
    private TextView tvStepsPct;
    private TextView tvStepsLeft;
    private ProgressBar progressSteps;

    // Calories card
    private TextView tvCaloriesValue;
    private ProgressBar progressCalories;

    // Workouts card
    private TextView tvWorkoutCount;

    // Mini stat cards
    private TextView tvHeartRateValue;
    private TextView tvDistanceValue;
    private TextView tvSleepValue;
    private TextView tvActiveMinutes;

    // Weekly summary
    private TextView tvWeeklyWorkouts;
    private TextView tvWeeklyTime;
    private TextView tvWeeklyKcal;

    // ── Firebase ───────────────────────────────────────────────────────────
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // ── Health Connect ─────────────────────────────────────────────────────
    private HealthConnectManager healthManager;
    private ActivityResultLauncher<Set<String>> permissionLauncher;

    // ── Goals ──────────────────────────────────────────────────────────────
    private static final int STEPS_GOAL    = 10000;
    private static final int CALORIES_GOAL = 600;

    // ──────────────────────────────────────────────────────────────────────
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        healthManager = new HealthConnectManager(requireContext());

        permissionLauncher = registerForActivityResult(
                PermissionController.createRequestPermissionResultContract(),
                granted -> {
                    if (granted.containsAll(healthManager.getRequiredPermissions())) {
                        // ✅ Sab permissions mil gayi — data load karo
                        loadHealthData();
                    } else if (granted.isEmpty()) {
                        // ❌ User ne sab deny kiya — sirf zeros dikhao, toast mat dikhao
                        showDefaultValues();
                    } else {
                        // ⚠️ Kuch permissions mili, kuch nahi — jo mili unka data load karo
                        loadHealthData();
                    }
                }
        );

        initViews(view);
        setGreeting();
        checkAndRequestPermissions();
    }

    // ── Init Views ─────────────────────────────────────────────────────────
    private void initViews(View view) {
        tvGreeting       = view.findViewById(R.id.tvGreeting);
        tvSubGreeting    = view.findViewById(R.id.tvSubGreeting);

        // Steps
        tvStepsValue     = view.findViewById(R.id.tvStepsValue);
        tvStepsPct       = view.findViewById(R.id.tvStepsPct);
        tvStepsLeft      = view.findViewById(R.id.tvStepsLeft);
        progressSteps    = view.findViewById(R.id.progressSteps);

        // Calories
        tvCaloriesValue  = view.findViewById(R.id.tvCaloriesValue);
        progressCalories = view.findViewById(R.id.progressCalories);

        // Workouts
        tvWorkoutCount   = view.findViewById(R.id.tvWorkoutCount);

        // Mini cards
        tvHeartRateValue = view.findViewById(R.id.tvHeartRateValue);
        tvDistanceValue  = view.findViewById(R.id.tvDistanceValue);
        tvSleepValue     = view.findViewById(R.id.tvSleepValue);
        tvActiveMinutes  = view.findViewById(R.id.tvActiveMinutes);

        // Weekly summary
        tvWeeklyWorkouts = view.findViewById(R.id.tvWeeklyWorkouts);
        tvWeeklyTime     = view.findViewById(R.id.tvWeeklyTime);
        tvWeeklyKcal     = view.findViewById(R.id.tvWeeklyKcal);
    }

    // ── Greeting ───────────────────────────────────────────────────────────
    private void setGreeting() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int hour = cal.get(java.util.Calendar.HOUR_OF_DAY);

        String timeGreeting;
        if (hour >= 5 && hour < 12) {
            timeGreeting = "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            timeGreeting = "Good Afternoon";
        } else {
            timeGreeting = "Good Evening";
        }

        String[] subTexts = {
                "Ready for your sweat session?",
                "Let's crush today's workout!",
                "Stay consistent, stay strong!"
        };
        tvSubGreeting.setText(subTexts[(int)(Math.random() * subTexts.length)]);
        tvGreeting.setText(timeGreeting + ", User!");

        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        String finalGreeting = timeGreeting;

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && isAdded()) {
                        String name = doc.getString("fullName");
                        if (name != null && !name.isEmpty()) {
                            String firstName = name.split(" ")[0];
                            tvGreeting.setText(finalGreeting + ", " + firstName + "!");
                        }
                    }
                });
    }

    // ── Health Connect Permissions ─────────────────────────────────────────
    private void checkAndRequestPermissions() {
        if (!healthManager.isAvailable()) {
            // Health Connect nahi hai — default values dikhao
            showDefaultValues();
            return;
        }

        // Pehle check karo — kya permissions already granted hain?
        healthManager.checkGrantedPermissions(alreadyGranted -> {
            if (alreadyGranted.containsAll(healthManager.getRequiredPermissions())) {
                // ✅ Already granted — seedha data load karo, dialog mat dikhao
                loadHealthData();
            } else {
                // Dialog dikhao sirf tab jab zaroorat ho
                permissionLauncher.launch(healthManager.getRequiredPermissions());
            }
        });
    }

    // ── Health Data Load ───────────────────────────────────────────────────
    private void loadHealthData() {

        // ── Steps ──
        healthManager.getTodaySteps(new HealthConnectManager.HealthDataCallback() {
            @Override public void onCaloriesReceived(double cal) {}
            @Override public void onHeartRateReceived(long bpm) {}
            @Override public void onDistanceReceived(double m) {}
            @Override public void onSleepReceived(long mins) {}
            @Override public void onError(String msg) {
                runOnUi(() -> updateStepsUI(0));
            }

            @Override
            public void onStepsReceived(long steps) {
                runOnUi(() -> updateStepsUI(steps));
            }
        });

        // ── Calories ──
        healthManager.getTodayCalories(new HealthConnectManager.HealthDataCallback() {
            @Override public void onStepsReceived(long s) {}
            @Override public void onHeartRateReceived(long bpm) {}
            @Override public void onDistanceReceived(double m) {}
            @Override public void onSleepReceived(long mins) {}
            @Override public void onError(String msg) {
                runOnUi(() -> updateCaloriesUI(0));
            }

            @Override
            public void onCaloriesReceived(double calories) {
                runOnUi(() -> updateCaloriesUI((int) calories));
            }
        });

        // ── Heart Rate ──
        healthManager.getHeartRate(new HealthConnectManager.HealthDataCallback() {
            @Override public void onStepsReceived(long s) {}
            @Override public void onCaloriesReceived(double c) {}
            @Override public void onDistanceReceived(double m) {}
            @Override public void onSleepReceived(long mins) {}
            @Override public void onError(String msg) {
                runOnUi(() -> tvHeartRateValue.setText("--"));
            }

            @Override
            public void onHeartRateReceived(long bpm) {
                runOnUi(() -> tvHeartRateValue.setText(String.valueOf(bpm)));
            }
        });

        // ── Distance ──
        healthManager.getTodayDistance(new HealthConnectManager.HealthDataCallback() {
            @Override public void onStepsReceived(long s) {}
            @Override public void onCaloriesReceived(double c) {}
            @Override public void onHeartRateReceived(long bpm) {}
            @Override public void onSleepReceived(long mins) {}
            @Override public void onError(String msg) {
                runOnUi(() -> tvDistanceValue.setText("--"));
            }

            @Override
            public void onDistanceReceived(double meters) {
                double km = meters / 1000.0;
                runOnUi(() -> tvDistanceValue.setText(
                        String.format(java.util.Locale.getDefault(), "%.1f", km)));
            }
        });

        // ── Sleep ──
        healthManager.getLastNightSleep(new HealthConnectManager.HealthDataCallback() {
            @Override public void onStepsReceived(long s) {}
            @Override public void onCaloriesReceived(double c) {}
            @Override public void onHeartRateReceived(long bpm) {}
            @Override public void onDistanceReceived(double m) {}
            @Override public void onError(String msg) {
                runOnUi(() -> tvSleepValue.setText("--"));
            }

            @Override
            public void onSleepReceived(long minutes) {
                double hrs = minutes / 60.0;
                runOnUi(() -> tvSleepValue.setText(
                        String.format(java.util.Locale.getDefault(), "%.1f", hrs)));
            }
        });

        // ── Weekly Workouts (Firestore) ──
        loadWeeklyStats();
    }

    // ── Steps UI update ────────────────────────────────────────────────────
    private void updateStepsUI(long steps) {
        int pct  = (int) Math.min((steps * 100) / STEPS_GOAL, 100);
        long left = Math.max(STEPS_GOAL - steps, 0);

        tvStepsValue.setText(String.format(java.util.Locale.getDefault(),
                "%,d", steps));
        tvStepsPct.setText(pct + "%");
        tvStepsLeft.setText(String.format(java.util.Locale.getDefault(),
                "%,d left", left));
        progressSteps.setProgress(pct);

        // Active minutes estimate: ~1 min per 100 steps
        int activeMins = (int)(steps / 100);
        tvActiveMinutes.setText(String.valueOf(activeMins));
    }

    // ── Calories UI update ─────────────────────────────────────────────────
    private void updateCaloriesUI(int calories) {
        tvCaloriesValue.setText(String.valueOf(calories));
        int pct = (int) Math.min((calories * 100) / CALORIES_GOAL, 100);
        progressCalories.setProgress(pct);
    }

    // ── Weekly Stats from Firestore ────────────────────────────────────────
    private void loadWeeklyStats() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .collection("weeklyStats")
                .document(getCurrentWeekId())
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded()) return;
                    if (doc.exists()) {
                        Long workouts  = doc.getLong("workoutCount");
                        Long totalMins = doc.getLong("totalMinutes");
                        Long kcal      = doc.getLong("totalKcal");

                        tvWorkoutCount.setText(workouts  != null ? String.valueOf(workouts)  : "0");
                        tvWeeklyWorkouts.setText(workouts != null ? String.valueOf(workouts) : "0");
                        tvWeeklyKcal.setText(kcal != null
                                ? String.format(java.util.Locale.getDefault(), "%,d", kcal) : "0");

                        if (totalMins != null) {
                            long h = totalMins / 60;
                            long m = totalMins % 60;
                            tvWeeklyTime.setText(h + "h " + m + "m");
                        } else {
                            tvWeeklyTime.setText("0h 0m");
                        }
                    } else {
                        // No data yet — show zeros
                        tvWorkoutCount.setText("0");
                        tvWeeklyWorkouts.setText("0");
                        tvWeeklyTime.setText("0h 0m");
                        tvWeeklyKcal.setText("0");
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    tvWorkoutCount.setText("0");
                    tvWeeklyWorkouts.setText("0");
                    tvWeeklyTime.setText("--");
                    tvWeeklyKcal.setText("--");
                });
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /** Returns a unique week ID string like "2025-W23" */
    private String getCurrentWeekId() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int year = cal.get(java.util.Calendar.YEAR);
        int week = cal.get(java.util.Calendar.WEEK_OF_YEAR);
        return year + "-W" + String.format(java.util.Locale.getDefault(), "%02d", week);
    }

    // ── Default Values (jab permissions na hon) ────────────────────────────
    private void showDefaultValues() {
        if (!isAdded()) return;
        tvStepsValue.setText("0");
        tvStepsPct.setText("0%");
        tvStepsLeft.setText("10,000 left");
        progressSteps.setProgress(0);
        tvCaloriesValue.setText("0");
        progressCalories.setProgress(0);
        tvHeartRateValue.setText("--");
        tvDistanceValue.setText("--");
        tvSleepValue.setText("--");
        tvActiveMinutes.setText("0");
    }

    /** Safely run on UI thread only if fragment is still attached */
    private void runOnUi(Runnable r) {
        if (isAdded() && getActivity() != null) {
            requireActivity().runOnUiThread(r);
        }
    }
}