package com.example.notify;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notify.Adapters.NotesListAdapters;
import com.example.notify.Firebase.FirebaseNoteRepository;
import com.example.notify.Models.Notes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String currentUserId;
    private NotesListAdapters notesListAdapters;
    private List<Notes> notes = new ArrayList<>();
    private FirebaseNoteRepository firebaseNoteRepository;
    private FirebaseAuth auth;
    private FloatingActionButton fab_add;
    private TextView noNotesText, allNotesLabel, favouritesLabel, archivedLabel;
    private EditText searchBar;
    private boolean isActive = true;

    // In your Activity (e.g., HomeActivity)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid(); // Add this line

        FirebaseApp.initializeApp(this);

        // Initialize UI elements
        noNotesText = findViewById(R.id.noNotesText);
        recyclerView = findViewById(R.id.recycler_home);
        searchBar = findViewById(R.id.searchBar);
        allNotesLabel = findViewById(R.id.allNotesLabel);
        favouritesLabel = findViewById(R.id.favouritesLabel);
        archivedLabel = findViewById(R.id.archivedLabel);

        auth = FirebaseAuth.getInstance();
        firebaseNoteRepository = new FirebaseNoteRepository();

        // Check if user is logged in
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bottom Navigation Setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_add) {
                // Handle Add action
                Intent intent = new Intent(HomeActivity.this, NotesTakerActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_home) {
                // Already in home, do nothing
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                return true;
            } else if (itemId == R.id.nav_notifications) {
                // Handle notifications
                return true;
            }
            return false;
        });

        // Load notes from Firebase
        loadNotes();

        // Set up search bar text change listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up category filter tabs
        allNotesLabel.setOnClickListener(v -> {
            highlightLabel(allNotesLabel);
            showNotes("all");
        });

        favouritesLabel.setOnClickListener(v -> {
            highlightLabel(favouritesLabel);
            showNotes("favourites");
        });

        archivedLabel.setOnClickListener(v -> {
            highlightLabel(archivedLabel);
            showNotes("archived");
        });

        // Default selection
        highlightLabel(allNotesLabel);
    }

    private void loadNotes() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseNoteRepository.readNotes(userId)
                .addOnSuccessListener(documentSnapshots -> {
                    notes.clear();
                    for (DocumentSnapshot doc : documentSnapshots) {
                        Notes note = doc.toObject(Notes.class);
                        if (note != null) {
                            note.setDocumentId(doc.getId());
                            // Load tagged users' information
                            if (note.getTaggedUsersList() != null) {
                                for (String taggedUserId : note.getTaggedUsersList()) {
                                    firebaseNoteRepository.getUser(taggedUserId)
                                        .addOnSuccessListener(userDoc -> {
                                            if (userDoc.exists()) {
                                                String username = userDoc.getString("username");
                                                if (username != null) {
                                                    note.addTaggedUsername(username);
                                                    notesListAdapters.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                }
                            }
                            notes.add(note);
                        }
                    }
                    updateRecycler(notes);

                    // Show/hide empty state
                    if (notes.isEmpty()) {
                        noNotesText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noNotesText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Failed to fetch notes: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void filterNotes(String query) {
        List<Notes> filteredNotes = new ArrayList<>();
        for (Notes note : notes) {
            // Check title and description
            if (note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                note.getNotes().toLowerCase().contains(query.toLowerCase())) {
                filteredNotes.add(note);
                continue;
            }

            // Include notes where current user is tagged
            if (isUserTaggedInNote(note)) {
                filteredNotes.add(note);
                continue;
            }

            // Check tagged users
            if (note.getTaggedUsersList() != null) {
                for (String userId : note.getTaggedUsersList()) {
                    firebaseNoteRepository.getUser(userId).addOnSuccessListener(document -> {
                        String username = document.getString("username");
                        if (username != null && username.toLowerCase().contains(query.toLowerCase())) {
                            if (!filteredNotes.contains(note)) {
                                filteredNotes.add(note);
                                updateRecycler(filteredNotes);
                            }
                        }
                    });
                }
            }
        }
        updateRecycler(filteredNotes);
    }

    // Add this method to check if user is tagged
    private boolean isUserTaggedInNote(Notes note) {
        return note.getTaggedUsersList() != null && 
               note.getTaggedUsersList().contains(currentUserId);
    }

    private void highlightLabel(TextView selectedLabel) {
        allNotesLabel.setTextColor(getResources().getColor(R.color.black));
        favouritesLabel.setTextColor(getResources().getColor(R.color.black));
        archivedLabel.setTextColor(getResources().getColor(R.color.black));

        allNotesLabel.setTypeface(null, Typeface.NORMAL);
        favouritesLabel.setTypeface(null, Typeface.NORMAL);
        archivedLabel.setTypeface(null, Typeface.NORMAL);

        selectedLabel.setTextColor(getResources().getColor(R.color.purple_500));
        selectedLabel.setTypeface(null, Typeface.BOLD);
    }

    private void showNotes(String category) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseNoteRepository.readNotes(userId)
                .addOnSuccessListener(documentSnapshots -> {
                    List<Notes> filteredNotes = new ArrayList<>();
                    for (DocumentSnapshot doc : documentSnapshots) {
                        Notes note = doc.toObject(Notes.class);
                        if (note != null) {
                            String noteId = doc.getId(); // Set the Firestore document ID
                            if (category.equals("favourites") && note.isFavourite()) {
                                filteredNotes.add(note);
                            } else if (category.equals("archived") && note.isArchived()) {
                                filteredNotes.add(note);
                            } else if (category.equals("all")) {
                                filteredNotes.add(note);
                            }
                        }
                    }
                    updateRecycler(filteredNotes);

                    // Show/hide empty state based on filtered results
                    if (filteredNotes.isEmpty()) {
                        noNotesText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noNotesText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Failed to fetch notes: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void checkGooglePlayServices() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int resultCode = api.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(resultCode)) {
                // Show dialog to resolve the error
                api.makeGooglePlayServicesAvailable(this)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                initializeFirebaseOperations();
                            } else {
                                Log.e(TAG, "Could not resolve Google Play Services");
                                finish(); // Close app if essential services unavailable
                            }
                        });
            } else {
                Toast.makeText(this, "Google Play Services required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void initializeFirebaseOperations() {
        // Your Firebase initialization code here
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapters = new NotesListAdapters(
                HomeActivity.this,
                notes != null ? notes : new ArrayList<>(),
                notesClickListener
        );
        recyclerView.setAdapter(notesListAdapters);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Toast.makeText(HomeActivity.this, "Note clicked", Toast.LENGTH_SHORT).show();
            // Open note details activity if needed
        }

        @Override
        public void LongClick(Notes notes, CardView cardView) {
            Toast.makeText(HomeActivity.this, "Note long clicked (CardView)", Toast.LENGTH_SHORT).show();
            // Show a popup menu for additional actions
        }

        @Override
        public void onActionClick(Notes notes, String action) {
            // Early exit checks
            if (isFinishing() || isDestroyed() || notes == null) {
                return;
            }

            String noteId = notes.getDocumentId();
            if (noteId == null || noteId.isEmpty()) {
                return;
            }

            // Post the operation to ensure proper window focus
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                Map<String, Object> updates = new HashMap<>();
                switch (action) {
                    case "toggle_pin":
                        updates.put("isPinned", !notes.isPinned());
                        break;
                    case "favourite":
                        updates.put("isFavourite", !notes.isFavourite());
                        break;
                    case "archive":
                        updates.put("isArchived", !notes.isArchived());
                        break;
                    case "delete":
                        handleDeleteOperation(noteId);
                        return;
                    case "edit":
                        Intent editIntent = new Intent(HomeActivity.this, NotesTakerActivity.class);
                        editIntent.putExtra("existing_note", notes);
                        startActivity(editIntent);
                        return;
                    default:
                        return;
                }



                handleUpdateOperation(noteId, updates);
            }, 100);
        }

        private void performSafeOperation(Runnable operation) {
            if (!isActive || isFinishing() || isDestroyed()) {
                return;
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                if (isActive && !isFinishing() && !isDestroyed()) {
                    operation.run();
                }
            });
        }

        private void handleUpdateOperation(String noteId, Map<String, Object> updates) {
            firebaseNoteRepository.updateNote(noteId, updates)
                    .addOnSuccessListener(aVoid -> {
                        if (!isFinishing() && !isDestroyed()) {
                            loadNotes();
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (!isFinishing() && !isDestroyed()) {
                            Toast.makeText(HomeActivity.this,
                                    "Failed to update note", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private void handleDeleteOperation(String noteId) {
            firebaseNoteRepository.deleteNote(noteId)
                    .addOnSuccessListener(aVoid -> {
                        if (!isFinishing() && !isDestroyed()) {
                            loadNotes();
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (!isFinishing() && !isDestroyed()) {
                            Toast.makeText(HomeActivity.this,
                                    "Failed to delete note", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private void performNoteOperation(String action, Notes notes) {
        }

        @Override
        public void LongClick(Notes notes, View cardView) {
            Toast.makeText(HomeActivity.this, "Note long clicked (View)", Toast.LENGTH_SHORT).show();
            // Show a popup menu for additional actions
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadNotes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cancel any pending operations
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any listeners or callbacks
        if (notesListAdapters != null) {
            notesListAdapters.clearListeners();
        }
    }
}