package com.example.fitforge;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText etEmail;
    CardView btnSendResetLink;
    TextView tvBackToLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();

        etEmail          = findViewById(R.id.etEmail);
        btnSendResetLink = findViewById(R.id.btnSendResetLink);
        tvBackToLogin    = findViewById(R.id.tvBackToLogin);

        btnSendResetLink.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter email address");
                etEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter valid email");
                etEmail.requestFocus();
                return;
            }

            // Button disable karo taake baar baar click na ho
            btnSendResetLink.setEnabled(false);

            // Firebase se password reset email bhejo
            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(ForgotPassword.this,
                                "Reset link sent to " + email + "\nCheck your inbox!",
                                Toast.LENGTH_LONG).show();

                        // Login screen pe wapas bhejo
                        startActivity(new Intent(ForgotPassword.this, Login.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnSendResetLink.setEnabled(true);

                        // Error type ke hisab se message dikhao
                        String errorMsg = e.getMessage();
                        if (errorMsg != null && errorMsg.contains("no user record")) {
                            etEmail.setError("No account found with this email");
                            etEmail.requestFocus();
                        } else if (errorMsg != null && errorMsg.contains("badly formatted")) {
                            etEmail.setError("Invalid email format");
                            etEmail.requestFocus();
                        } else {
                            Toast.makeText(ForgotPassword.this,
                                    "Error: " + errorMsg,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPassword.this, Login.class));
            finish();
        });
    }
}