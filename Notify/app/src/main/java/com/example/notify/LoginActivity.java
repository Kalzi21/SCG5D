package com.example.notify;

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
import com.example.notify.Firebase.FirebaseManager;
import com.example.notify.Models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

        try {
            FirebaseApp.initializeApp(this);
        } catch (Exception e) {
            Log.e("LoginActivity", "Firebase init failed", e);
            Toast.makeText(this, "App initialization failed", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize Firebase Manager
        firebaseManager = new FirebaseManager();

        // Initialize UI Elements
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        signUpTextView = findViewById(R.id.signUp);

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

        // Use FirebaseManager's login method
        if (firebaseManager != null) {
            firebaseManager.loginUser(email, password, new FirebaseManager.FirebaseCallback() {
                @Override
                public void onSuccess(Object result) {
                    progressBar.setVisibility(View.GONE);
                    User user = (User) result;
                    Toast.makeText(LoginActivity.this, "Welcome, " + user.getUsername() + "!", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                }

                @Override
                public void onFailure(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "System Error: Please restart the app", Toast.LENGTH_SHORT).show();
            Log.e("LoginActivity", "FirebaseManager is null!");
        }
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