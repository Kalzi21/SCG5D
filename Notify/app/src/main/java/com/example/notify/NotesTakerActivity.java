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
import com.example.notify.Models.Notes;
import com.example.notify.firebase.FirebaseNoteRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class NotesTakerActivity extends AppCompatActivity {
    private EditText editText_title, editText_notes;
    private ImageView imageView_save;
    private Notes existingNote;
    private FirebaseNoteRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker2);

        // Initialize UI elements
        imageView_save = findViewById(R.id.imageView_save);
        editText_title = findViewById(R.id.editText_title);
        editText_notes = findViewById(R.id.editText_notes);

        // Initialize FirebaseNoteRepository
        repository = new FirebaseNoteRepository();

        // Check if editing an existing note
        existingNote = (Notes) getIntent().getSerializableExtra("existing_note");
        if (existingNote != null) {
            editText_title.setText(existingNote.getTitle());
            editText_notes.setText(existingNote.getNotes());
        }

        imageView_save.setOnClickListener(view -> saveNote());

        // Bottom navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
                startActivityForResult(new Intent(this, NotesTakerActivity.class), 101);
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivityForResult(new Intent(this, HomeActivity.class), 101);
                return true;
            }
            return false;
        });
    }

    private void saveNote() {
        String title = editText_title.getText().toString();
        String description = editText_notes.getText().toString();

        if (description.isEmpty()) {
            Toast.makeText(this, "Notes cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a", Locale.getDefault());
        String formattedDate = formatter.format(new Date());

        if (existingNote != null) {
            // Update existing note
            repository.updateNote(existingNote.getID(), title, description, false, false, false, existingNote.getTaggedUsers())
                    .addOnSuccessListener(aVoid -> setResultAndFinish(existingNote))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
        } else {
            // Create new note in Firestore
            repository.createNote(title, description, formattedDate, false, false, false, Arrays.asList())
                    .addOnSuccessListener(documentReference -> {
                        String userId = repository.getCurrentUserId();
                        if (userId != null) {
                            existingNote = new Notes(documentReference.getId(), userId, null, title, description, formattedDate, false, false, false);
                            setResultAndFinish(existingNote);
                        } else {
                            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error saving note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
        }
    }

    private void setResultAndFinish(Notes note) {
        Intent intent = new Intent();
        intent.putExtra("note", note);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
