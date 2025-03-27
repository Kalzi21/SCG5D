package com.example.notify;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notify.Firebase.FirebaseManager;
import com.example.notify.Models.Notes;
import com.example.notify.Firebase.FirebaseNoteRepository;
import com.example.notify.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotesTakerActivity extends AppCompatActivity {
    private EditText editText_title, editText_notes;
    private ImageView imageView_save;
    private Notes existingNote;
    private FirebaseNoteRepository repository;
    private Uri imageUri;
    private long reminderTime;

    private EditText tagInput;
    private LinearLayout selectedTagsContainer;
    private List<String> taggedUserIds = new ArrayList<>();
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker2);

        // Initialize UI elements
        imageView_save = findViewById(R.id.imageView_save);
        editText_title = findViewById(R.id.editText_title);
        editText_notes = findViewById(R.id.editText_notes);
        selectedTagsContainer = findViewById(R.id.selectedTagsContainer);

        // Initialize FirebaseNoteRepository
        repository = new FirebaseNoteRepository();

        // Check if editing an existing note
        existingNote = (Notes) getIntent().getSerializableExtra("existing_note");
        if (existingNote != null) {
            editText_title.setText(existingNote.getTitle());
            editText_notes.setText(existingNote.getNotes());
            reminderTime = existingNote.getReminderTime();
            if (existingNote.getTaggedUsersList() != null) {
                taggedUserIds = new ArrayList<>(existingNote.getTaggedUsersList());
                updateSelectedTagsUI();
            }
            updateReminderUI();
        }

        imageView_save.setOnClickListener(view -> saveNote());

        tagInput = findViewById(R.id.tagInput);
        selectedTagsContainer = findViewById(R.id.selectedTagsContainer);
        firebaseManager = new FirebaseManager();

// Setup tag input
        tagInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("@")) {
                    showUserSearchDialog(s.toString());
                }
            }
            // ... other required methods
        });

// Handle existing note tags
        if (existingNote != null && existingNote.getTaggedUsers() != null) {
            taggedUserIds = existingNote.getTaggedUsersList();
            updateSelectedTagsUI();
        }

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
            } else if (itemId == R.id.nav_profile) {
                startActivityForResult(new Intent(this, ProfileActivity.class), 101);
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivityForResult(new Intent(this, SettingsActivity.class), 101);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                // Handle notifications click
                return true;
            }

            return false;
        });

        // Add reminder button
        findViewById(R.id.setReminderButton).setOnClickListener(v -> showDateTimePicker());
    }

    private void showUserSearchDialog(String searchQuery) {
        // Query Firestore for users matching searchQuery
        firebaseManager.searchUsers(searchQuery.replace("@", ""), new FirebaseManager.FirebaseCallback() {
            @Override
            public void onSuccess(Object result) {
                List<User> users = (List<User>) result;
                showUserSelectionDialog(users);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(NotesTakerActivity.this, "Error searching users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserSelectionDialog(List<User> users) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select User to Tag");

        String[] usernames = users.stream().map(User::getUsername).toArray(String[]::new);

        builder.setItems(usernames, (dialog, which) -> {
            User selectedUser = users.get(which);
            if (!taggedUserIds.contains(selectedUser.getUserId())) {
                taggedUserIds.add(selectedUser.getUserId());
                updateSelectedTagsUI();
            }
            tagInput.setText("");
        });

        builder.show();
    }

    private void updateSelectedTagsUI() {
        selectedTagsContainer.removeAllViews();

        for (String userId : taggedUserIds) {
            firebaseManager.getUserFromFirestore(userId, new FirebaseManager.FirebaseCallback() {
                @Override
                public void onSuccess(Object result) {
                    User user = (User) result;
                    addTagChip(user.getUsername(), userId);
                }

                @Override
                public void onFailure(Exception e) {
                    // Handle error
                }
            });
        }
    }

    private void addTagChip(String username, String userId) {
        Chip chip = new Chip(this);
        chip.setText(username);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            taggedUserIds.remove(userId);
            selectedTagsContainer.removeView(chip);
        });

        selectedTagsContainer.addView(chip);
    }

    private void saveNote() {
        String title = editText_title.getText().toString();
        String description = editText_notes.getText().toString();

        if (description.isEmpty()) {
            Toast.makeText(this, "Please add some notes!", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyy HH:mm a");
        Date date = new Date();

        Notes note = new Notes();
        note.setTitle(title);
        note.setNotes(description);
        note.setDate(formatter.format(date));
        note.setPinned(true); // This will now work correctly
        note.setFavourite(false);
        note.setArchived(true);
        note.setImageUri(imageUri != null ? imageUri.toString() : null);
        note.setReminderTime(reminderTime);
        note.setTaggedUsersList(taggedUserIds);

        if (existingNote != null) {
            // Update existing note
            repository.updateNote(existingNote.getDocumentId(), (Map<String, Object>) note).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Note updated!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Error updating note", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Add new note
            repository.addNote(note).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view1, hourOfDay, minute) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                    reminderTime = calendar.getTimeInMillis();
                    updateReminderUI();
                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
            );
            timePickerDialog.show();
        });
        datePickerDialog.show();
    }

    private void updateReminderUI() {
        TextView reminderText = findViewById(R.id.reminderText);
        if (reminderTime > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            reminderText.setText("Reminder set for: " + sdf.format(new Date(reminderTime)));
            reminderText.setVisibility(View.VISIBLE);
        } else {
            reminderText.setVisibility(View.GONE);
        }
    }
}