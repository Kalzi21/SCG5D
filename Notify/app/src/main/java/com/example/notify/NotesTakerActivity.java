package com.example.notify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notify.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotesTakerActivity extends AppCompatActivity {
    EditText editText_title, editText_notes;
    ImageView imageView_save;
    Notes notes;
    Notes existingNote; // Declare at class level to use in onClick

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker2);

        // Initialize UI elements
        imageView_save = findViewById(R.id.imageView_save);
        editText_title = findViewById(R.id.editText_title);
        editText_notes = findViewById(R.id.editText_notes);

        // Check if editing an existing note
        existingNote = (Notes) getIntent().getSerializableExtra("existing_note");
        if (existingNote != null) {
            editText_title.setText(existingNote.getTitle());
            editText_notes.setText(existingNote.getNotes());
        }

        imageView_save.setOnClickListener(view -> saveNote());
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
        }

        // Return result
        Intent intent = new Intent();
        intent.putExtra("note", (existingNote != null) ? existingNote : notes);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}