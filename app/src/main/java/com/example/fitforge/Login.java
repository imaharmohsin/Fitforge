package com.example.fitforge;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText etPhone, etPassword;
    TextView tvJoin, skip, tvTogglePassword, tvForgetPassword;
    CardView signInButton, googleSignInButton;
    boolean passwordVisible = false;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInClient googleSignInClient;

    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        etPhone            = findViewById(R.id.etMobile);
        etPassword         = findViewById(R.id.etPassword);
        tvJoin             = findViewById(R.id.tvJoin);
        signInButton       = findViewById(R.id.signInButton);
        tvTogglePassword   = findViewById(R.id.tvTogglePassword);
        tvForgetPassword   = findViewById(R.id.tvForgotPassword);
        googleSignInButton = findViewById(R.id.btnGoogle);

        // Google Sign-In Setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("404591266329-1fp5e4ufigjdjhulipkp3gu8htblah9c.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // ✅ FIX: Pehle signOut karo taake fresh token mile - Error 10 fix
        googleSignInButton.setOnClickListener(v -> {
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });
        });

        // Password Toggle
        tvTogglePassword.setOnClickListener(v -> {
            if (passwordVisible) {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tvTogglePassword.setText("👁");
                passwordVisible = false;
            } else {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                tvTogglePassword.setText("🙈");
                passwordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        etPhone.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void afterTextChanged(Editable s) { etPhone.setError(null); }
        });
        etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void afterTextChanged(Editable s) { etPassword.setError(null); }
        });

        // Normal Sign In
        signInButton.setOnClickListener(v -> {
            String phone    = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!validatePhone(phone) || !validatePassword(password)) return;

            db.collection("users")
                    .whereEqualTo("mobile", phone)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot.isEmpty()) {
                            etPhone.setError("No account found with this number");
                            etPhone.requestFocus();
                            return;
                        }

                        String email = querySnapshot.getDocuments().get(0).getString("email");

                        if (email == null) {
                            etPhone.setError("Account error, please sign up again");
                            return;
                        }

                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(authResult -> {
                                    startActivity(new Intent(Login.this, ActivityHomeDashboard.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    etPassword.setError("Incorrect password");
                                    etPassword.requestFocus();
                                });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Connection error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show()
                    );
        });
        

        tvForgetPassword.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, ForgotPassword.class));
            finish();
        });

        tvJoin.setOnClickListener(v ->
                startActivity(new Intent(Login.this, SignUp.class)));
    }

    // Google Sign-In Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In Failed: " + e.getStatusCode(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Firebase mein Google account se login
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    boolean isNewUser = authResult.getAdditionalUserInfo().isNewUser();
                    if (isNewUser) {
                        saveGoogleUserToFirestore(account);
                    } else {
                        startActivity(new Intent(Login.this, ActivityHomeDashboard.class));
                        finish();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Auth Failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    // Firestore mein Google user ka data save karo
    private void saveGoogleUserToFirestore(GoogleSignInAccount account) {
        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", account.getEmail());
        userData.put("fullName", account.getDisplayName());
        userData.put("username", "@" + account.getDisplayName()
                .toLowerCase().replaceAll(" ", "_"));
        userData.put("mobile", "");
        userData.put("profileImage", account.getPhotoUrl() != null ?
                account.getPhotoUrl().toString() : "");
        userData.put("loginMethod", "google");

        db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(unused -> {
                    startActivity(new Intent(Login.this, ActivityHomeDashboard.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error saving user: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private boolean validatePhone(String phone) {
        if (phone.isEmpty()) {
            etPhone.setError("Enter Mobile Number");
            return false;
        }
        if (phone.length() != 11) {
            etPhone.setError("Must be 11 digits");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty())    { etPassword.setError("Enter Password"); return false; }
        if (password.length() < 6) { etPassword.setError("Min 6 characters"); return false; }
        return true;
    }

    public abstract static class SimpleTextWatcher implements android.text.TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}