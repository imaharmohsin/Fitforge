package com.example.fitforge;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Setting_Screen extends AppCompatActivity {

    private TextView tvProfileName;
    private TextView tvAvatarInitials;
    private CardView cvEditProfile;

    private SwitchMaterial switchNotifications;
    private SwitchMaterial switchDarkMode;

    private LinearLayout rowPersonalInfo;
    private LinearLayout rowPrivacy;
    private LinearLayout rowUnits;
    private TextView tvUnitsValue;
    private LinearLayout rowLogout;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // SharedPreferences
    private SharedPreferences prefs;
    private static final String PREF_NAME         = "fitforge_prefs";
    private static final String KEY_NAME          = "user_full_name";
    private static final String KEY_USERNAME      = "username";
    private static final String KEY_NOTIFICATIONS = "pref_notifications";
    private static final String KEY_DARK_MODE     = "pref_dark_mode";
    private static final String KEY_UNITS         = "pref_units";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_screen);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        initViews();
        loadProfileFromFirebase();  // Firebase se name load karo
        loadPreferences();
        setListeners();
    }

    private void initViews() {
        tvProfileName    = findViewById(R.id.tvProfileName);
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials);
        cvEditProfile    = findViewById(R.id.cvEditProfile);

        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode      = findViewById(R.id.switchDarkMode);

        rowPersonalInfo = findViewById(R.id.rowPersonalInfo);
        rowPrivacy      = findViewById(R.id.rowPrivacy);
        rowUnits        = findViewById(R.id.rowUnits);
        tvUnitsValue    = findViewById(R.id.tvUnitsValue);
        rowLogout       = findViewById(R.id.rowLogout);
    }

    // ── Firebase se name load karo ───────────────────────────────────────────
    private void loadProfileFromFirebase() {
        // Pehle cache se dikhao
        String cachedName = prefs.getString(KEY_NAME, "Alex Forge");
        if (tvProfileName != null)    tvProfileName.setText(cachedName);
        if (tvAvatarInitials != null) tvAvatarInitials.setText(getInitials(cachedName));
        if (tvUnitsValue != null)     tvUnitsValue.setText(prefs.getString(KEY_UNITS, "Metric (kg)"));

        // Firebase se fresh data
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name     = doc.getString("fullName");
                        String username = doc.getString("username");

                        if (name != null) {
                            if (tvProfileName != null)    tvProfileName.setText(name);
                            if (tvAvatarInitials != null) tvAvatarInitials.setText(getInitials(name));

                            prefs.edit()
                                    .putString(KEY_NAME,     name)
                                    .putString(KEY_USERNAME, username != null ? username : "@user")
                                    .apply();
                        }
                    }
                });
    }

    private void loadPreferences() {
        if (switchNotifications != null)
            switchNotifications.setChecked(prefs.getBoolean(KEY_NOTIFICATIONS, true));
        if (switchDarkMode != null)
            switchDarkMode.setChecked(prefs.getBoolean(KEY_DARK_MODE, true));
    }

    private void setListeners() {

        if (cvEditProfile != null)
            cvEditProfile.setOnClickListener(v -> showEditProfileDialog());

        if (switchNotifications != null) {
            switchNotifications.setOnCheckedChangeListener((btn, isChecked) -> {
                prefs.edit().putBoolean(KEY_NOTIFICATIONS, isChecked).apply();
                Toast.makeText(this,
                        isChecked ? "Notifications ON" : "Notifications OFF",
                        Toast.LENGTH_SHORT).show();
            });
        }

        if (switchDarkMode != null) {
            switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
                prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
                Toast.makeText(this,
                        isChecked ? "Dark Mode ON" : "Dark Mode OFF",
                        Toast.LENGTH_SHORT).show();
            });
        }

        if (rowPersonalInfo != null)
            rowPersonalInfo.setOnClickListener(v -> showEditProfileDialog());

        if (rowPrivacy != null)
            rowPrivacy.setOnClickListener(v ->
                    Toast.makeText(this, "Privacy & Security", Toast.LENGTH_SHORT).show());

        if (rowUnits != null)
            rowUnits.setOnClickListener(v -> showUnitsDialog());

        if (rowLogout != null)
            rowLogout.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    // ── Edit Profile Dialog ──────────────────────────────────────────────────
    private void showEditProfileDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_profile);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.88f),
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setDimAmount(0.6f);
        }

        android.widget.EditText etFullName = dialog.findViewById(R.id.etFullName);
        android.widget.EditText etUsername = dialog.findViewById(R.id.tvUsername);
        MaterialButton btnSave   = dialog.findViewById(R.id.btnSave);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);

        String currentName = prefs.getString(KEY_NAME, "Alex Forge");
        String currentUser = prefs.getString(KEY_USERNAME, "@alex_forge").replace("@", "");

        if (etFullName != null) { etFullName.setText(currentName); etFullName.setSelection(currentName.length()); }
        if (etUsername != null) { etUsername.setText(currentUser); etUsername.setSelection(currentUser.length()); }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String newName = etFullName != null ? etFullName.getText().toString().trim() : "";
                if (TextUtils.isEmpty(newName)) {
                    if (etFullName != null) { etFullName.setError("Name cannot be empty"); etFullName.requestFocus(); }
                    return;
                }

                String typedUser = etUsername != null ? etUsername.getText().toString().trim() : "";
                String newUsername = !TextUtils.isEmpty(typedUser)
                        ? "@" + typedUser.toLowerCase().replaceAll("[^a-z0-9_]", "_")
                        : "@" + newName.toLowerCase().trim().replaceAll("\\s+", "_").replaceAll("[^a-z0-9_]", "");

                // Firestore update
                if (mAuth.getCurrentUser() != null) {
                    String uid = mAuth.getCurrentUser().getUid();
                    db.collection("users").document(uid)
                            .update("fullName", newName, "username", newUsername)
                            .addOnSuccessListener(unused -> {
                                prefs.edit()
                                        .putString(KEY_NAME,     newName)
                                        .putString(KEY_USERNAME, newUsername)
                                        .apply();

                                if (tvProfileName != null)    tvProfileName.setText(newName);
                                if (tvAvatarInitials != null) tvAvatarInitials.setText(getInitials(newName));

                                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Update failed: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show());
                }
            });
        }

        if (btnCancel != null) btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // ── Units Dialog ─────────────────────────────────────────────────────────
    private void showUnitsDialog() {
        String[] options = {"Metric (kg)", "Imperial (lbs)"};
        String current   = prefs.getString(KEY_UNITS, "Metric (kg)");
        int checked      = current.equals("Imperial (lbs)") ? 1 : 0;

        new AlertDialog.Builder(this)
                .setTitle("Select Units")
                .setSingleChoiceItems(options, checked, (d, which) -> {
                    String selected = options[which];
                    prefs.edit().putString(KEY_UNITS, selected).apply();
                    if (tvUnitsValue != null) tvUnitsValue.setText(selected);
                    d.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Logout ───────────────────────────────────────────────────────────────
    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        // Firebase logout
        mAuth.signOut();

        // Local cache clear
        prefs.edit().clear().apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Login screen pe bhejo — back stack clear karo
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // ── Utility ──────────────────────────────────────────────────────────────
    private String getInitials(String fullName) {
        if (TextUtils.isEmpty(fullName)) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return String.valueOf(parts[0].charAt(0)).toUpperCase();
        return (String.valueOf(parts[0].charAt(0)) +
                String.valueOf(parts[parts.length - 1].charAt(0))).toUpperCase();
    }
}