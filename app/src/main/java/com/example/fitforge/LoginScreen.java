package com.example.fitforge;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LoginScreen extends AppCompatActivity {

    EditText etPhone, etPassword;
    TextView tvJoin, tvSkip;
    MaterialButton btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvJoin = findViewById(R.id.tvJoin);
        btnSignIn = findViewById(R.id.signInButton);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        tvSkip = findViewById(R.id.skip);

        // ✅ Skip Button
        tvSkip.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
//            startActivity(intent);
        });

        // ✅ Join Button (Signup Screen open karna ho to)
        tvJoin.setOnClickListener(v -> {
            // Intent intent = new Intent(LoginScreen.this, SignupScreen.class);
            // startActivity(intent);
        });

        // ✅ Phone TextWatcher
        etPhone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                etPhone.setError(null);
            }
        });

        // ✅ Password TextWatcher
        etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                etPassword.setError(null);
            }
        });

        // ✅ Sign In Button
        btnSignIn.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!validatePhone(phone) || !validatePassword(password)) {
                return;
            }

            // ✅ Agar validation sahi ho to next screen
//            Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
//            startActivity(intent);
        });
    }

    private boolean validatePhone(String phone) {
        if (phone.isEmpty()) {
            etPhone.setError("Enter Mobile Number");
            return false;
        } else if (phone.length() != 11) {
            etPhone.setError("Mobile Number must be 11 digits");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            etPassword.setError("Enter Password");
            return false;
        } else if (password.length() < 6 || password.length() > 12) {
            etPassword.setError("Password must be 6-12 characters");
            return false;
        }
        return true;
    }

    // ✅ Custom TextWatcher
    public abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}