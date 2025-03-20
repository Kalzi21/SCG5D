package com.example.notify.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notify.HomeActivity;
import com.example.notify.R;
import com.example.notify.firebase.FirebaseManager;
import com.example.notify.models.User;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView signUpTextView;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Manager
        firebaseManager = new FirebaseManager();

        // Initialize UI Elements
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        signUpTextView = findViewById(R.id.signUpTextView);

        // Login Button Click Listener
        loginButton.setOnClickListener(view -> loginUser());

        // Navigate to SignUpActivity
        signUpTextView.setOnClickListener(view -> navigateToSignUp());
    }

    // 🔹 Login User Method
    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate Input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show Progress Bar
        progressBar.setVisibility(View.VISIBLE);

        // Attempt to log in
        firebaseManager.loginUser(email, password, new FirebaseManager.FirebaseCallback() {
            @Override
            public void onSuccess(Object result) {
                progressBar.setVisibility(View.GONE);
                User user = (User) result;
                showToast("Welcome, " + user.getUsername() + "!");

                // Navigate to HomeActivity
                navigateToHome();
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                showToast("Login Failed: " + e.getMessage());
                Log.e("LoginError", e.getMessage());
            }
        });
    }

    // 🔹 Navigate to HomeActivity
    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Close LoginActivity to prevent going back
    }

    // 🔹 Navigate to SignUpActivity
    private void navigateToSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    // 🔹 Helper Method to Show Toast Messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}