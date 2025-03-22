package com.example.notify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface;
import androidx.cardview.widget.CardView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notify.Adapters.NotesListAdapters;
import com.example.notify.Models.Notes;
import com.example.notify.firebase.FirebaseNoteRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private EditText searchBar;
    private TextView allNotesLabel, favouritesLabel, archivedLabel, noNotesText;
    private RecyclerView recyclerView;
    private NotesListAdapters notesListAdapters;
    private List<Notes> notes = new ArrayList<>();
    private FirebaseNoteRepository firebaseNoteRepository;
    private FirebaseAuth auth;
    private FloatingActionButton fab_add;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Views
        searchBar = findViewById(R.id.searchBar);
        allNotesLabel = findViewById(R.id.allNotesLabel);
        favouritesLabel = findViewById(R.id.favouritesLabel);
        archivedLabel = findViewById(R.id.archivedLabel);
        noNotesText = findViewById(R.id.noNotesText);
        recyclerView = findViewById(R.id.recycler_home);

        auth = FirebaseAuth.getInstance();
        firebaseNoteRepository = new FirebaseNoteRepository();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup initial data loading
        loadNotes();

        // Set navigation listeners
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_add) {
                Intent intent = new Intent(HomeActivity.this, NotesTakerActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

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
        firebaseNoteRepository.readNotes()
                .addOnSuccessListener(documentSnapshots -> {
                    notes.clear();
                    for (DocumentSnapshot doc : documentSnapshots) {
                        Notes note = doc.toObject(Notes.class);
                        if (note != null) {
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
                    Toast.makeText(HomeActivity.this, "Error1: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void filterNotes(String query) {
        List<Notes> filteredNotes = new ArrayList<>();
        for (Notes note : notes) {
            if (note.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredNotes.add(note);
            }
        }
        updateRecycler(filteredNotes);
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
        firebaseNoteRepository.readNotes()
                .addOnSuccessListener(documentSnapshots -> {
                    List<Notes> filteredNotes = new ArrayList<>();
                    for (DocumentSnapshot doc : documentSnapshots) {
                        Notes note = doc.toObject(Notes.class);
                        if (note != null) {
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
                    Toast.makeText(HomeActivity.this, "Error2: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapters = new NotesListAdapters(HomeActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapters);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Toast.makeText(HomeActivity.this, "Note clicked", Toast.LENGTH_SHORT).show();
            // You might want to open note details here
            // For example:
            // Intent intent = new Intent(HomeActivity.this, NoteDetailActivity.class);
            // intent.putExtra("note", notes);
            // startActivity(intent);
        }

        @Override
        public void LongClick(Notes notes, CardView cardView) {
            // Handle long click with CardView
            Toast.makeText(HomeActivity.this, "Note long clicked (CardView)", Toast.LENGTH_SHORT).show();
            // You might want to show a popup menu here
        }

        @Override
        public void onActionClick(Notes notes, String action) {
            switch (action) {
                case "toggle_pin":
                    firebaseNoteRepository.updateNote(notes.getID(), notes.getTitle(), notes.getNotes(), !notes.isPinned(), notes.isFavourite(), notes.isArchived(), notes.getTaggedUsers())
                            .addOnSuccessListener(aVoid -> loadNotes())
                            .addOnFailureListener(e -> Toast.makeText(HomeActivity.this, "Failed to update note", Toast.LENGTH_SHORT).show());
                    break;
                case "favourite":
                    firebaseNoteRepository.updateNote(notes.getID(), notes.getTitle(), notes.getNotes(), notes.isPinned(), !notes.isFavourite(), notes.isArchived(), notes.getTaggedUsers())
                            .addOnSuccessListener(aVoid -> loadNotes())
                            .addOnFailureListener(e -> Toast.makeText(HomeActivity.this, "Failed to update note", Toast.LENGTH_SHORT).show());
                    break;
                case "archive":
                    firebaseNoteRepository.updateNote(notes.getID(), notes.getTitle(), notes.getNotes(), notes.isPinned(), notes.isFavourite(), !notes.isArchived(), notes.getTaggedUsers())
                            .addOnSuccessListener(aVoid -> loadNotes())
                            .addOnFailureListener(e -> Toast.makeText(HomeActivity.this, "Failed to update note", Toast.LENGTH_SHORT).show());
                    break;
                case "delete":
                    firebaseNoteRepository.deleteNote(notes.getID())
                            .addOnSuccessListener(aVoid -> loadNotes())
                            .addOnFailureListener(e -> Toast.makeText(HomeActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show());
                    break;
            }
        }

        @Override
        public void LongClick(Notes notes, View cardView) {
            // Handle long click with View
            Toast.makeText(HomeActivity.this, "Note long clicked (View)", Toast.LENGTH_SHORT).show();
            // You might want to show a popup menu here
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadNotes();
    }
}