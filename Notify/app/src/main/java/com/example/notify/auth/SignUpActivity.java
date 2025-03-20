package com.example.notify.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notify.HomeActivity;
import com.example.notify.R;
import com.example.notify.firebase.FirebaseManager;
import com.example.notify.models.User;
import com.google.firebase.FirebaseApp;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText emailEditText, usernameEditText, passwordEditText;
    private Button signUpButton, selectImageButton;
    private ProgressBar progressBar;
    private ImageView profileImageView;
    private TextView loginTextView;
    private Uri imageUri;

    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        Log.d("SignUpActivity", "Firebase initialized");

        // Initialize Firebase Manager
        firebaseManager = new FirebaseManager();

        // Initialize UI Elements
        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        profileImageView = findViewById(R.id.profileImageView);
        progressBar = findViewById(R.id.progressBar);
        loginTextView = findViewById(R.id.loginTextView);

        // Select Profile Picture
        selectImageButton.setOnClickListener(view -> openFileChooser());

        // Sign Up Button Click Listener
        signUpButton.setOnClickListener(view -> registerUser());

        // Navigate to LoginActivity
        loginTextView.setOnClickListener(view -> navigateToLogin());
    }

    // 🔹 Open File Chooser to Select an Image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("ImageError", "Failed to load image", e);
                showToast("Failed to load image");
            }
        }
    }

    // 🔹 Register User with Firebase
    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate Input
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showToast("Please fill in all fields");
            return;
        }

        // Show Progress Bar
        progressBar.setVisibility(View.VISIBLE);

        // Register User
        firebaseManager.registerUser(email, username, password, imageUri, new FirebaseManager.FirebaseCallback() {
            @Override
            public void onSuccess(Object result) {
                progressBar.setVisibility(View.GONE);
                User user = (User) result;
                showToast("Welcome, " + user.getUsername() + "!");

                // Redirect to HomeActivity
                navigateToHome();
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                showToast("Sign-Up Failed: " + e.getMessage());
                Log.e("SignUpError", e.getMessage());
            }
        });
    }

    // 🔹 Navigate to HomeActivity
    private void navigateToHome() {
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Close SignUpActivity to prevent going back
    }

    // 🔹 Navigate to LoginActivity
    private void navigateToLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    // 🔹 Helper Method to Show Toast Messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}