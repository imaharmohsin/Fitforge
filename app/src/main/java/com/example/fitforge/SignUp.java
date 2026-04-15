package com.example.fitforge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SignUp extends AppCompatActivity {

    EditText etName, etPhone, etPassword, etConfirm;
    MaterialButton btnCreate;
    TextView tvLogin;
    ImageView backArrow;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);
        btnCreate = findViewById(R.id.btnCreate);
        tvLogin = findViewById(R.id.tvLogin);
        backArrow = findViewById(R.id.back_arrow);

        backArrow.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                etName.setError("Enter name");
                return;
            }

            if (TextUtils.isEmpty(phone)) {
                etPhone.setError("Enter phone number");
                return;
            }

            if (phone.length() != 11) {
                etPhone.setError("Mobile number must be 11 digits");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Enter password");
                return;
            }

            if (password.length() < 6 || password.length() > 12) {
                etPassword.setError("Password must be 6 to 12 characters");
                return;
            }

            if (!password.equals(confirm)) {
                etConfirm.setError("Password not matched");
                return;
            }

            Toast.makeText(SignUp.this, "Account Created", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SignUp.this, Login.class));
            finish();
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUp.this, Login.class));
        });
    }
}