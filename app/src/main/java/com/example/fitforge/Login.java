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

public class Login extends AppCompatActivity {

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

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               Intent intent = new Intent(Login.this , HomeScreen.class);
//               start
            }
        });

        tvJoin.setOnClickListener(v -> {

        });

        etPhone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                etPhone.setError(null);
            }
        });

        etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                etPassword.setError(null);
            }
        });

        btnSignIn.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!validatePhone(phone) || !validatePassword(password)) {
                return;
            }

            // Add Sign In logic here
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

    public abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}














//package com.example.fitforge;
//
////import static androidx.core.content.ContextCompat.startActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//
////import androidx.appcompat.app.AppCompatActivity;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.material.button.MaterialButton;
//
//public class Login extends AppCompatActivity {
//
//    EditText etPhone, etPassword;
//    TextView tvJoin , tvSkip;
//    MaterialButton btnSignIn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        tvJoin = findViewById(R.id.tvJoin);
//        btnSignIn = findViewById(R.id.signInButton);
//        etPhone = findViewById(R.id.etPhone);
//        etPassword = findViewById(R.id.etPassword);
//        tvSkip = findViewById(R.id.skip);
//
//        tvSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Login.this,HomeScreen.class);
//                startActivity(intent);
//            }
//        });
//
//        tvJoin.setOnClickListener(v -> {
//
//        });
//
////        tvSkip.setOnClickListener(v -> {
////            startActivity(new Intent(Login.this, Home.class));
////
////        });
//
//        etPhone.addTextChangedListener(new SimpleTextWatcher() {
//            @Override
//            public void afterTextChanged(android.text.Editable s) {
//                etPhone.setError(null);
//            }
//        });
//
//        etPassword.addTextChangedListener(new SimpleTextWatcher() {
//            @Override
//            public void afterTextChanged(android.text.Editable s) {
//                etPassword.setError(null);
//            }
//        });
//
//        btnSignIn.setOnClickListener(v -> {
//
//            String phone = etPhone.getText().toString().trim();
//            String password = etPassword.getText().toString().trim();
//
//            if (!validatePhone(phone) || !validatePassword(password)) {
//                return;
//            }
//
//        });
//    }
//
//    private boolean validatePhone(String phone) {
//        if (phone.isEmpty()) {
//            etPhone.setError("Enter Mobile Number");
//            return false;
//        } else if (phone.length() != 11) {
//            etPhone.setError("Mobile Number must be 11 digits");
//            return false;
//        }
//        return true;
//    }
//
//    private boolean validatePassword(String password) {
//        if (password.isEmpty()) {
//            etPassword.setError("Enter Password");
//            return false;
//        } else if (password.length() < 6 || password.length() > 12) {
//            etPassword.setError("Password must be 6-12 characters");
//            return false;
//        }
//        return true;
//    }
//
//    public abstract class SimpleTextWatcher implements TextWatcher {
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//        public void onTextChanged(CharSequence s, int start, int before, int count) {}
//    }
//}