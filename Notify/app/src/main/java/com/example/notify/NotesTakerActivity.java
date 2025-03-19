package com.example.notify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.notify.Database.RoomDB;
import com.example.notify.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotesTakerActivity extends AppCompatActivity {
    EditText editText_title, editText_notes;
    ImageView imageView_save;
    Notes notes;

    RoomDB database;

    Notes existingNote; // Declare at class level to use in onClick


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker2);

        // Initialize UI elements

        imageView_save = findViewById(R.id.imageView_save);
        editText_title = findViewById(R.id.editText_title);
        editText_notes = findViewById(R.id.editText_notes);


        // Initialize the database
        database = RoomDB.getInstance(this);

        imageView_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String title = editText_title.getText().toString();
                    String description = editText_notes.getText().toString();

                    if (description.isEmpty()) {
                        Toast.makeText(NotesTakerActivity.this, "Please add some notes!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyy HH:mm a");
                    Date date = new Date();

                    notes = new Notes();
                    notes.setTitle(title);
                    notes.setNotes(description);
                    notes.setDate(formatter.format(date));

                    Intent intent = new Intent();
                    intent.putExtra("note", notes);
                    setResult(Activity.RESULT_OK, intent);
                    finish();  // Close activity after saving the note
                } catch (Exception e) {
                    e.printStackTrace();  // Log the error
                    Toast.makeText(NotesTakerActivity.this, "Error saving note", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the selected item's ID

            if (itemId == R.id.nav_add) {
                Intent intent = new Intent(NotesTakerActivity.this, NotesTakerActivity.class);
                startActivityForResult(intent, 101);
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent intent = new Intent(NotesTakerActivity.this, HomeActivity.class);
                startActivityForResult(intent, 101);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Handle profile click
                return true;
            } else if (itemId == R.id.nav_settings) {
                // Handle settings click
                return true;
            } else if (itemId == R.id.nav_notifications) {
                // Handle notifications click
                return true;
            }

            return false; // Return false if no item is selected
        });


        // Check if editing an existing note
        existingNote = (Notes) getIntent().getSerializableExtra("existing_note");
        if (existingNote != null) {
            editText_title.setText(existingNote.getTitle());
            editText_notes.setText(existingNote.getNotes());
        }


    }

    private void saveNote() {
        // Get input values
        String title = editText_title.getText().toString();
        String description = editText_notes.getText().toString();

        if (description.isEmpty()) {
            Toast.makeText(this, "Notes cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare date formatter
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a", Locale.getDefault());
        Date date = new Date();

        // Create/update note
        if (existingNote != null) {
            // Update existing note
            existingNote.setTitle(title);
            existingNote.setNotes(description);
            existingNote.setDate(formatter.format(date));
        } else {
            // Create new note
            notes = new Notes();
            notes.setTitle(title);
            notes.setNotes(description);
            notes.setDate(formatter.format(date));
            notes.setPinned(false); // Explicitly set to false (redundant but safe)
            database.maindao().insert(notes);
        }

        // Return result
        Intent intent = new Intent();
        intent.putExtra("note", (existingNote != null) ? existingNote : notes);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }
}
