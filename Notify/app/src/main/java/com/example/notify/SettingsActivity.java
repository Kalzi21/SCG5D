package com.example.notify;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

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
            // Save the preference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("RandomColorsEnabled", isChecked);
            editor.apply();
            chooseColorButton.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });

        // Set up the color picker button listener
        chooseColorButton.setOnClickListener(v -> showColorPickerDialog());
    }

    private void showColorPickerDialog() {
        // Get the array of single colors
        int[] colors = getResources().getIntArray(R.array.single_colors);

        // Create a dialog to show the color options
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Color");

        // Create a list of color options
        String[] colorNames = {"Orange", "Blue", "Green", "Red", "Purple", "Pink"};
        builder.setItems(colorNames, (dialog, which) -> {
            // Save the selected color
            SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("SelectedColor", colors[which]);
            editor.apply();
        });

        // Show the dialog
        builder.show();
    }
}