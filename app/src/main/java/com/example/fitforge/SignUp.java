package com.example.fitforge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText etFullName, etEmail, etMobile, etPassword, etConfirmPassword;
    CardView btnSignUp;
    TextView tvLogin, tvTogglePass, tvToggle;
    CheckBox cbTerms;
    boolean passVisible = false;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        etFullName        = findViewById(R.id.etFullName);
        etEmail           = findViewById(R.id.etEmail);
        etMobile          = findViewById(R.id.etMobile);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp         = findViewById(R.id.btnSignUp);
        tvLogin           = findViewById(R.id.tvLogin);
        tvTogglePass      = findViewById(R.id.tvTogglePass);
        tvToggle          = findViewById(R.id.tvToggle);
        cbTerms           = findViewById(R.id.cbTerms);

        tvTogglePass.setOnClickListener(v -> togglePassword());
        tvToggle.setOnClickListener(v -> togglePassword());

        btnSignUp.setOnClickListener(v -> {
            String name    = etFullName.getText().toString().trim();
            String email   = etEmail.getText().toString().trim();
            String mobile  = etMobile.getText().toString().trim();
            String pass    = etPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();

            if (name.isEmpty())            { etFullName.setError("Enter name"); return; }
            if (email.isEmpty())           { etEmail.setError("Enter email"); return; }
            if (mobile.isEmpty() || mobile.length() != 11) { etMobile.setError("Enter 11 digit mobile"); return; }
            if (pass.length() < 6)         { etPassword.setError("Min 6 chars"); return; }
            if (!pass.equals(confirm))     { etConfirmPassword.setError("Passwords don't match"); return; }
            if (!cbTerms.isChecked())      { Toast.makeText(this, "Please agree to Terms", Toast.LENGTH_SHORT).show(); return; }

            // Firebase Auth - Create Account
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();

                        // Firestore mein user data save karo
                        Map<String, Object> user = new HashMap<>();
                        user.put("fullName", name);
                        user.put("email",    email);
                        user.put("mobile",   mobile);
                        user.put("username", "@" + name.toLowerCase().replace(" ", "_"));

                        db.collection("users").document(uid).set(user)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUp.this, Login.class));
                                    finish();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUp.this, Login.class));
            finish();
        });
    }

    private void togglePassword() {
        if (passVisible) {
            etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            tvTogglePass.setText("👁");
            passVisible = false;
        } else {
            etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            tvTogglePass.setText("🙈");
            passVisible = true;
        }
        etPassword.setSelection(etPassword.getText().length());
    }
}