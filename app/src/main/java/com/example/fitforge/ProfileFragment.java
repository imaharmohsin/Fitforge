package com.example.fitforge;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {


     ImageView ivSettingIcon , ivProfilePic;

     CardView  cvCameraIcon , cvEditProfile;
     TextView  tvFullName  , tvUsername , tvWorkouts , tvStreak , tvBadgesCount , tvInfoName
             , tvInfoUsername , tvInfoEmail , tvInfoMobile , tvViewAllBadges;



     FirebaseAuth mAuth;
     FirebaseFirestore db;
     SharedPreferences prefs;
     static final String PREF_NAME       = "fitforge_prefs";
     static final String KEY_NAME        = "user_full_name";
     static final String KEY_USERNAME    = "username";
     static final String KEY_EMAIL       = "user_email";
     static final String KEY_MOBILE      = "user_mobile";
     static final String KEY_PROFILE_URI = "profile_image_uri";
     static final String KEY_WORKOUTS    = "workout_count";
     static final String KEY_STREAK      = "day_streak";
     static final String KEY_BADGES      = "badge_count";


     ActivityResultLauncher<String> imagePickerLauncher;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null) return;
                    try {
                        requireContext().getContentResolver()
                                .takePersistableUriPermission(
                                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException ignored) {}

                    prefs.edit().putString(KEY_PROFILE_URI, uri.toString()).apply();
                    loadProfileImage(uri);
                    Toast.makeText(getContext(),
                            "Profile picture updated!", Toast.LENGTH_SHORT).show();
                });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragments, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireActivity()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        initViews(view);
        loadCachedData();
        loadUserDataFromFirebase();
        loadProfileImageFromPrefs();
        setListeners();
    }


    private void initViews(View view) {
        ivSettingIcon   = view.findViewById(R.id.ivSettingIcon);
        ivProfilePic    = view.findViewById(R.id.ivProfilePic);
        cvCameraIcon    = view.findViewById(R.id.cvCameraIcon);
        tvFullName      = view.findViewById(R.id.tvFullName);
        tvUsername      = view.findViewById(R.id.tvUsername);
        tvWorkouts      = view.findViewById(R.id.tvWorkouts);
        tvStreak        = view.findViewById(R.id.tvStreak);
        tvBadgesCount   = view.findViewById(R.id.tvBadgesCount);
        cvEditProfile   = view.findViewById(R.id.cvEditProfile);
        tvInfoName      = view.findViewById(R.id.tvInfoName);
        tvInfoUsername  = view.findViewById(R.id.tvInfoUsername);
        tvInfoEmail     = view.findViewById(R.id.tvInfoEmail);
        tvInfoMobile    = view.findViewById(R.id.tvInfoMobile);
        tvViewAllBadges = view.findViewById(R.id.tvViewAllBadges);
    }

    // ── 2. SharedPrefs se cached data — screen blank na rahe ────────────────
    private void loadCachedData() {
        String name     = prefs.getString(KEY_NAME,     "Muhammad Mohsin");
        String username = prefs.getString(KEY_USERNAME, "@mohsin_fit");
        String email    = prefs.getString(KEY_EMAIL,    "imaharmohsin@gmail.com");
        String mobile   = prefs.getString(KEY_MOBILE,   "03XX-XXXXXXXX");
        int    workouts = prefs.getInt(KEY_WORKOUTS, 128);
        int    streak   = prefs.getInt(KEY_STREAK,    14);
        int    badges   = prefs.getInt(KEY_BADGES,     6);

        updateUI(name, username, email, mobile, workouts, streak, badges);
    }

    // ── 3. Firebase se fresh data ────────────────────────────────────────────
    private void loadUserDataFromFirebase() {
        if (mAuth.getCurrentUser() == null) return;

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc == null || !doc.exists() || !isAdded()) return;

                    String name     = doc.getString("fullName");
                    String username = doc.getString("username");
                    String email    = doc.getString("email");
                    String mobile   = doc.getString("mobile");

                    // null ho to cached value use karo
                    if (name     == null) name     = prefs.getString(KEY_NAME,     "Muhammad Mohsin");
                    if (username == null) username = prefs.getString(KEY_USERNAME, "@mohsin_fit");
                    if (email    == null) email    = prefs.getString(KEY_EMAIL,    "");
                    if (mobile   == null) mobile   = prefs.getString(KEY_MOBILE,   "");

                    Long wL = doc.getLong("workoutCount");
                    Long sL = doc.getLong("dayStreak");
                    Long bL = doc.getLong("badgeCount");
                    int workouts = wL != null ? wL.intValue() : prefs.getInt(KEY_WORKOUTS, 128);
                    int streak   = sL != null ? sL.intValue() : prefs.getInt(KEY_STREAK,    14);
                    int badges   = bL != null ? bL.intValue() : prefs.getInt(KEY_BADGES,     6);

                    updateUI(name, username, email, mobile, workouts, streak, badges);

                    // Cache update
                    prefs.edit()
                            .putString(KEY_NAME,     name)
                            .putString(KEY_USERNAME, username)
                            .putString(KEY_EMAIL,    email)
                            .putString(KEY_MOBILE,   mobile)
                            .putInt(KEY_WORKOUTS,    workouts)
                            .putInt(KEY_STREAK,      streak)
                            .putInt(KEY_BADGES,      badges)
                            .apply();
                })
                .addOnFailureListener(e -> {
                    if (isAdded())
                        Toast.makeText(getContext(),
                                "Could not refresh profile",
                                Toast.LENGTH_SHORT).show();
                });
    }

    // ── Helper: ek jagah se poora UI update ─────────────────────────────────
    private void updateUI(String name, String username, String email,
                          String mobile, int workouts, int streak, int badges) {
        if (!isAdded()) return;

        // Profile card
        if (tvFullName    != null) tvFullName.setText(name);
        if (tvUsername    != null) tvUsername.setText(username);  // chip

        // Stats
        if (tvWorkouts    != null) tvWorkouts.setText(String.valueOf(workouts));
        if (tvStreak      != null) tvStreak.setText(String.valueOf(streak));
        if (tvBadgesCount != null) tvBadgesCount.setText(String.valueOf(badges));

        // Personal Information rows
        if (tvInfoName     != null) tvInfoName.setText(name);
        if (tvInfoUsername != null) tvInfoUsername.setText(username);
        if (tvInfoEmail    != null) tvInfoEmail.setText(email);
        if (tvInfoMobile   != null) tvInfoMobile.setText(mobile);
    }

    // ── 4. Profile Image ─────────────────────────────────────────────────────
    private void loadProfileImageFromPrefs() {
        String saved = prefs.getString(KEY_PROFILE_URI, null);
        if (saved != null) loadProfileImage(Uri.parse(saved));
    }

    private void loadProfileImage(Uri uri) {
        if (ivProfilePic == null || !isAdded()) return;
        Glide.with(this)
                .load(uri)
                .circleCrop()
                .placeholder(R.drawable.person_icon)
                .error(R.drawable.person_icon)
                .into(ivProfilePic);
    }

    // ── 5. Click Listeners ───────────────────────────────────────────────────
    private void setListeners() {

        if (ivSettingIcon != null)
            ivSettingIcon.setOnClickListener(v ->
                    startActivity(
                            new Intent(requireActivity(), Setting_Screen.class)));

        if (cvCameraIcon != null)
            cvCameraIcon.setOnClickListener(v -> openImagePicker());

        if (ivProfilePic != null)
            ivProfilePic.setOnClickListener(v -> openImagePicker());

        if (cvEditProfile != null)
            cvEditProfile.setOnClickListener(v -> showEditProfileDialog());

        if (tvViewAllBadges != null)
            tvViewAllBadges.setOnClickListener(v ->
                    Toast.makeText(getContext(),
                            "Coming soon!", Toast.LENGTH_SHORT).show());
    }

    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    // ── 6. Edit Profile Dialog ───────────────────────────────────────────────
    private void showEditProfileDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_profile);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (requireActivity().getResources()
                            .getDisplayMetrics().widthPixels * 0.90f),
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setDimAmount(0.6f);
        }

        EditText       etName     = dialog.findViewById(R.id.etFullName);
        EditText       etUser     = dialog.findViewById(R.id.etUsernameDialog);
        EditText       etEmail    = dialog.findViewById(R.id.etEmailDialog);
        EditText       etMobile   = dialog.findViewById(R.id.etMobileDialog);
        MaterialButton btnSave    = dialog.findViewById(R.id.btnSave);
        MaterialButton btnCancel  = dialog.findViewById(R.id.btnCancel);

        // Current values pre-fill
        String cName   = prefs.getString(KEY_NAME,     "Muhammad Mohsin");
        String cUser   = prefs.getString(KEY_USERNAME, "@mohsin_fit")
                .replace("@", "");
        String cEmail  = prefs.getString(KEY_EMAIL,  "");
        String cMobile = prefs.getString(KEY_MOBILE, "");

        if (etName   != null) { etName.setText(cName);   etName.setSelection(cName.length()); }
        if (etUser   != null) { etUser.setText(cUser);   etUser.setSelection(cUser.length()); }
        if (etEmail  != null) { etEmail.setText(cEmail); etEmail.setSelection(cEmail.length()); }
        if (etMobile != null) { etMobile.setText(cMobile); etMobile.setSelection(cMobile.length()); }

        if (btnCancel != null)
            btnCancel.setOnClickListener(v -> dialog.dismiss());

        if (btnSave != null)
            btnSave.setOnClickListener(v ->
                    saveProfile(dialog, etName, etUser, etEmail, etMobile));

        dialog.show();
    }

    // ── 7. Save — Firebase + SharedPrefs + UI ───────────────────────────────
    private void saveProfile(Dialog dialog,
                             EditText etName, EditText etUser,
                             EditText etEmail, EditText etMobile) {

        // Validation
        String newName = etName != null
                ? etName.getText().toString().trim() : "";
        if (TextUtils.isEmpty(newName)) {
            if (etName != null) etName.setError("Name cannot be empty");
            return;
        }

        // Username — @ automatically add karo, special chars clean karo
        String rawUser = etUser != null
                ? etUser.getText().toString().trim()
                .toLowerCase().replaceAll("[^a-z0-9_]", "_")
                : newName.toLowerCase().replaceAll("\\s+", "_");
        String newUsername = "@" + rawUser;

        String newEmail  = etEmail  != null ? etEmail.getText().toString().trim()  : "";
        String newMobile = etMobile != null ? etMobile.getText().toString().trim() : "";

        // Firestore update
        if (mAuth.getCurrentUser() != null) {
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("fullName", newName);
            data.put("username", newUsername);
            if (!newEmail.isEmpty())  data.put("email",  newEmail);
            if (!newMobile.isEmpty()) data.put("mobile", newMobile);

            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .update(data)
                    .addOnSuccessListener(unused ->
                            onSaveSuccess(dialog, newName, newUsername,
                                    newEmail, newMobile))
                    .addOnFailureListener(e ->
                            // Offline — local mein save karo
                            onSaveSuccess(dialog, newName, newUsername,
                                    newEmail, newMobile));
        } else {
            // Login nahi — sirf local save
            onSaveSuccess(dialog, newName, newUsername, newEmail, newMobile);
        }
    }

    // Save ke baad SharedPrefs + UI update ek jagah
    private void onSaveSuccess(Dialog dialog,
                               String name, String username,
                               String email, String mobile) {
        if (!isAdded()) return;

        // SharedPrefs save
        prefs.edit()
                .putString(KEY_NAME,     name)
                .putString(KEY_USERNAME, username)
                .putString(KEY_EMAIL,    email)
                .putString(KEY_MOBILE,   mobile)
                .apply();

        // UI turant update
        updateUI(name, username, email, mobile,
                prefs.getInt(KEY_WORKOUTS, 128),
                prefs.getInt(KEY_STREAK,    14),
                prefs.getInt(KEY_BADGES,     6));

        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }


    @Override
    public void onResume() {
        super.onResume();
        loadCachedData();
    }
}