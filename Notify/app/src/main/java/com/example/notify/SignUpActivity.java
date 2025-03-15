package com.example.notify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailField, usernameField, passwordField;
    private CheckBox termsCheckBox;
    private Button signUpButton, googleButton, facebookButton, appleButton;
    private TextView loginLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailField = findViewById(R.id.emailField);
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        signUpButton = findViewById(R.id.signUpButton);
        googleButton = findViewById(R.id.googleButton);
        facebookButton = findViewById(R.id.facebookButton);
        appleButton = findViewById(R.id.appleButton);
        loginLink = findViewById(R.id.loginLink);

        signUpButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!termsCheckBox.isChecked()) {
                Toast.makeText(SignUpActivity.this, "Please agree to Terms & Conditions", Toast.LENGTH_SHORT).show();
            } else {
                // Perform sign-up logic (e.g., Firebase authentication)
                Toast.makeText(SignUpActivity.this, "Sign-Up Successful", Toast.LENGTH_SHORT).show();
            }
        });

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
