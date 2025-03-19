package com.example.notify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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
import com.example.notify.Database.RoomDB;
import com.example.notify.Models.Notes;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private EditText searchBar;
    private TextView allNotesLabel, favouritesLabel, archivedLabel, noNotesText;
    private RecyclerView recyclerView;
    private NotesListAdapters notesListAdapters;
    private List<Notes> notes = new ArrayList<>();
    private RoomDB database;
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

        // Initialize Database
        database = RoomDB.getInstance(this);
        notes = database.maindao().getAll();
        if (notes == null) {
            notes = new ArrayList<>();
        }

        // Update RecyclerView with Notes
        updateRecycler(notes);

        // Set onClickListener for the FAB to add notes
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the selected item's ID

            if (itemId == R.id.nav_add) {
                Intent intent = new Intent(HomeActivity.this, NotesTakerActivity.class);
                startActivityForResult(intent, 101);
                return true;
            } else if (itemId == R.id.nav_home) {
                // Handle home click
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Handle profile click
                return true;
            } else if (itemId == R.id.nav_settings) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                // Handle notifications click
                return true;
            }

            return false; // Return false if no item is selected
        });

        // Check if notes exist and show message
        if (notes.isEmpty()) {
            noNotesText.setVisibility(View.VISIBLE);
        } else {
            noNotesText.setVisibility(View.GONE);
        }

        // Set the onClickListeners for the labels (All Notes, Favourites, Archived)
        allNotesLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                highlightLabel(allNotesLabel);
                showNotes("all");
            }
        });

        favouritesLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                highlightLabel(favouritesLabel);
                showNotes("favourites");
            }
        });

        archivedLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                highlightLabel(archivedLabel);
                showNotes("archived");
            }
        });

        // Add search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter notes based on the search query
                filterNotes(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed
            }
        });
    }

    // Method to filter notes
    private void filterNotes(String query) {
        List<Notes> filteredNotes = new ArrayList<>();

        // Loop through all notes and check if the title or content matches the query
        for (Notes note : notes) {
            if (note.getTitle().toLowerCase().contains(query.toLowerCase())){
                filteredNotes.add(note);
            }
        }

        // Update the RecyclerView with the filtered list
        notesListAdapters.updateList(filteredNotes);
    }

    private void highlightLabel(TextView selectedLabel) {
        // Reset all labels to normal
        allNotesLabel.setTextColor(getResources().getColor(R.color.black));
        favouritesLabel.setTextColor(getResources().getColor(R.color.black));
        archivedLabel.setTextColor(getResources().getColor(R.color.black));

        allNotesLabel.setTypeface(null, Typeface.NORMAL);
        favouritesLabel.setTypeface(null, Typeface.NORMAL);
        archivedLabel.setTypeface(null, Typeface.NORMAL);

        // Apply selected style
        selectedLabel.setTextColor(getResources().getColor(R.color.purple_500)); // Purple color
        selectedLabel.setTypeface(null, Typeface.BOLD); // Underlined text
    }

    private void showNotes(String category) {
        // This method filters the notes based on the selected category (all, favourites, archived)
        List<Notes> filteredNotes = new ArrayList<>();

        if ("all".equals(category)) {
            filteredNotes = database.maindao().getAll();
        } else if ("favourites".equals(category)) {
            filteredNotes = database.maindao().getFavourites(); // Get favourite notes (pinned = true)
        } else if ("archived".equals(category)) {
            filteredNotes = database.maindao().getArchived(); // Get archived notes (archived = true)
        }

        updateRecycler(filteredNotes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notesListAdapters.notifyDataSetChanged(); // Refresh the adapter
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                if (new_notes != null) {
                    if (new_notes.getID() == 0) { // New note (ID is 0)
                        database.maindao().insert(new_notes);
                    } else { // Existing note (ID is not 0)
                        database.maindao().update(new_notes);
                    }

                    // Fetch the latest notes from the database
                    notes.clear();  // Clear the existing list
                    notes.addAll(database.maindao().getAll());  // Add all updated notes

                    notesListAdapters.notifyDataSetChanged();  // Notify the adapter about changes
                } else {
                    Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapters = new NotesListAdapters(HomeActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapters);
        notesListAdapters.notifyDataSetChanged();
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes note) {
            // Handle note click if required
            Toast.makeText(HomeActivity.this, "Note clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void LongClick(Notes note, CardView cardView) {
            // Handle long click if required
            Toast.makeText(HomeActivity.this, "Note long clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onActionClick(Notes note, String action) {
            switch (action) {
                case "toggle_pin":
                    if (!note.isPinned()) {
                        database.maindao().unpinAll();
                    }
                    // Toggle the pin state of the selected note
                    database.maindao().pin(note.getID(), !note.isPinned());
                    break;

                case "favourite":
                    database.maindao().setFavourite(note.getID(), !note.isFavourite());
                    break;
                case "archive":
                    database.maindao().setArchived(note.getID(), !note.isArchived());
                    break;
                case "delete":
                    database.maindao().delete(note);
                    break;
                case "edit":
                    openNoteForEditing(note);
                    return;
            }
            refreshNotes();
        }

        private void refreshNotes() {
            notes = database.maindao().getAll();
            notesListAdapters.updateList(notes);
        }



        @Override
        public void LongClick(Notes notes, View cardView) {

        }
    };
    private void openNoteForEditing(Notes note) {
        Intent intent = new Intent(HomeActivity.this, NotesTakerActivity.class);
        intent.putExtra("existing_note", note); // Pass the existing note to edit
        startActivityForResult(intent, 101); // Use a request code (e.g., 101)
    }
}
