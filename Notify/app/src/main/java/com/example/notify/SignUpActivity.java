package com.example.notify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notify.Models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText emailField, usernameField, passwordField;
    private CheckBox termsCheckBox;
    private ImageView profileImageView;
    private Button signUpButton, googleButton, selectImageButton;
    private TextView loginLink;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseApp.initializeApp(this);

        emailField = findViewById(R.id.emailField);
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        profileImageView = findViewById(R.id.profileImageView);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        signUpButton = findViewById(R.id.signUpButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        googleButton = findViewById(R.id.googleButton);
        loginLink = findViewById(R.id.loginLink);

        // Select Profile Picture
        selectImageButton.setOnClickListener(view -> openFileChooser());

        // Inside the signUpButton.setOnClickListener:
        signUpButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String imageUri = null;

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!termsCheckBox.isChecked()) {
                Toast.makeText(SignUpActivity.this, "Please agree to Terms & Conditions", Toast.LENGTH_SHORT).show();
            } else {
                // Firebase Authentication
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Save user details to Firestore
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                User user = new User(userId, username, email, null);

                                FirebaseFirestore.getInstance().collection("users")
                                        .document(userId)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(SignUpActivity.this, "Sign-Up Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(SignUpActivity.this, "Failed to save user details", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(SignUpActivity.this, "Sign-Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    // 🔹 Open File Chooser to Select an Image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
}
