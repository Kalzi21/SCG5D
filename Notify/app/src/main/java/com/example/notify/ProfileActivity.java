package com.example.notify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout username, phoneNumber, email, password;
    private Button deactivateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Views
        username = findViewById(R.id.username);
        phoneNumber = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        deactivateButton = findViewById(R.id.btnDeactivate);

        // Click Listeners for Account Information Sections
        username.setOnClickListener(v -> openEditActivity("Username"));
        phoneNumber.setOnClickListener(v -> openEditActivity("Phone Number"));
        email.setOnClickListener(v -> openEditActivity("Email"));
        password.setOnClickListener(v -> openEditActivity("Password"));

        // Deactivate Account Button
        deactivateButton.setOnClickListener(v -> {
            // Handle account deactivation logic here
        });

        // Bottom Navigation Setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the selected item's ID

            if (itemId == R.id.nav_add) {
                Intent intent = new Intent(ProfileActivity.this, NotesTakerActivity.class);
                startActivityForResult(intent, 101);
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivityForResult(intent, 101);
                return true;
            } else if (itemId == R.id.nav_profile) {
                
                return true;
            } else if (itemId == R.id.nav_settings) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                // Handle notifications click
                return true;
            }

            return false; // Return false if no item is selected
        });
    }

    private void openEditActivity(String field) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("FIELD_NAME", field);
        startActivity(intent);
    }
}
