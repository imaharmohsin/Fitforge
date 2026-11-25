package com.example.fitforge;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.permission.HealthPermission;
import androidx.health.connect.client.records.DistanceRecord;
import androidx.health.connect.client.records.HeartRateRecord;
import androidx.health.connect.client.records.SleepSessionRecord;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord;

import java.util.HashSet;
import java.util.Set;

public class HealthConnectManager {

    private final Context context;
    private HealthConnectClient client;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // ── Callbacks ──────────────────────────────────────────────────────────
    public interface HealthDataCallback {
        void onStepsReceived(long steps);
        void onHeartRateReceived(long bpm);
        void onCaloriesReceived(double calories);
        void onDistanceReceived(double meters);
        void onSleepReceived(long minutes);
        void onError(String message);
    }

    public interface PermissionCallback {
        void onResult(Set<String> grantedPermissions);
    }

    // ── Constructor ────────────────────────────────────────────────────────
    public HealthConnectManager(Context context) {
        this.context = context.getApplicationContext();
        init();
    }

    // ── Availability ───────────────────────────────────────────────────────
    public boolean isAvailable() {
        int status = HealthConnectClient.getSdkStatus(context);
        return status == HealthConnectClient.SDK_AVAILABLE;
    }

    private void init() {
        if (isAvailable()) {
            client = HealthConnectClient.getOrCreate(context);
        }
    }

    // ── Required Permissions ───────────────────────────────────────────────
    public Set<String> getRequiredPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add(HealthPermission.getReadPermission(
                kotlin.jvm.JvmClassMappingKt.getKotlinClass(StepsRecord.class)));
        permissions.add(HealthPermission.getReadPermission(
                kotlin.jvm.JvmClassMappingKt.getKotlinClass(HeartRateRecord.class)));
        permissions.add(HealthPermission.getReadPermission(
                kotlin.jvm.JvmClassMappingKt.getKotlinClass(TotalCaloriesBurnedRecord.class)));
        permissions.add(HealthPermission.getReadPermission(
                kotlin.jvm.JvmClassMappingKt.getKotlinClass(DistanceRecord.class)));
        permissions.add(HealthPermission.getReadPermission(
                kotlin.jvm.JvmClassMappingKt.getKotlinClass(SleepSessionRecord.class)));
        return permissions;
    }

    // ── Check Already Granted Permissions ─────────────────────────────────
    // HealthConnectHelper.getGrantedPermissions() background thread pe runBlocking
    // use karta hai — isliye yahan bhi new Thread() mein call karo
    public void checkGrantedPermissions(PermissionCallback callback) {
        if (!isAvailable() || client == null) {
            mainHandler.post(() -> callback.onResult(new HashSet<>()));
            return;
        }
        new Thread(() -> {
            try {
                Set<String> granted = HealthConnectHelper.INSTANCE
                        .getGrantedPermissions(client);
                mainHandler.post(() -> callback.onResult(
                        granted != null ? granted : new HashSet<>()));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(new HashSet<>()));
            }
        }).start();
    }

    // ── Steps ──────────────────────────────────────────────────────────────
    public void getTodaySteps(HealthDataCallback callback) {
        if (!isAvailable() || client == null) {
            callback.onError("Health Connect not available");
            return;
        }
        HealthConnectHelper.INSTANCE.getSteps(client, (Long steps) -> {
            mainHandler.post(() -> callback.onStepsReceived(steps));
            return null;
        });
    }

    // ── Heart Rate ─────────────────────────────────────────────────────────
    public void getHeartRate(HealthDataCallback callback) {
        if (!isAvailable() || client == null) {
            callback.onError("Health Connect not available");
            return;
        }
        HealthConnectHelper.INSTANCE.getHeartRate(client, (Long bpm) -> {
            mainHandler.post(() -> callback.onHeartRateReceived(bpm));
            return null;
        });
    }

    // ── Calories ───────────────────────────────────────────────────────────
    public void getTodayCalories(HealthDataCallback callback) {
        if (!isAvailable() || client == null) {
            callback.onError("Health Connect not available");
            return;
        }
        HealthConnectHelper.INSTANCE.getCalories(client, (Double cal) -> {
            mainHandler.post(() -> callback.onCaloriesReceived(cal));
            return null;
        });
    }

    // ── Distance ───────────────────────────────────────────────────────────
    public void getTodayDistance(HealthDataCallback callback) {
        if (!isAvailable() || client == null) {
            callback.onError("Health Connect not available");
            return;
        }
        HealthConnectHelper.INSTANCE.getDistance(client, (Double dist) -> {
            mainHandler.post(() -> callback.onDistanceReceived(dist));
            return null;
        });
    }

    // ── Sleep ──────────────────────────────────────────────────────────────
    public void getLastNightSleep(HealthDataCallback callback) {
        if (!isAvailable() || client == null) {
            callback.onError("Health Connect not available");
            return;
        }
        HealthConnectHelper.INSTANCE.getSleep(client, (Long mins) -> {
            mainHandler.post(() -> callback.onSleepReceived(mins));
            return null;
        });
    }
}