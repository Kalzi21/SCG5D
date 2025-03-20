package com.example.notify;

import android.content.Intent; // ✅ Add this import
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch randomColorsSwitch;
    private Button chooseColorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        randomColorsSwitch = findViewById(R.id.randomColorsSwitch);
        chooseColorButton = findViewById(R.id.chooseColorButton);

        // Load the random colors preference
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isRandomColorsEnabled = sharedPreferences.getBoolean("RandomColorsEnabled", true);
        randomColorsSwitch.setChecked(isRandomColorsEnabled);

        // Set up the switch listener
        randomColorsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("RandomColorsEnabled", isChecked);
            editor.apply();
            chooseColorButton.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });

        // Set up the color picker button listener
        chooseColorButton.setOnClickListener(v -> showColorPickerDialog());

        //  Move BottomNavigationView initialization inside onCreate()
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); 

            if (itemId == R.id.nav_add) {
                startActivity(new Intent(SettingsActivity.this, NotesTakerActivity.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true; // Already in settings
            } else if (itemId == R.id.nav_notifications) {
                // Handle notifications click
                return true;
            }

            return false; 
        });
    }

    private void showColorPickerDialog() {
        int[] colors = getResources().getIntArray(R.array.single_colors);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Color");

        String[] colorNames = {"Orange", "Blue", "Green", "Red", "Purple", "Pink"};
        builder.setItems(colorNames, (dialog, which) -> {
            SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("SelectedColor", colors[which]);
            editor.apply();
        });

        builder.show();
    }
}
