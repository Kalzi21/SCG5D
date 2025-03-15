package com.example.notify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.notify.Database.RoomDB;
import com.example.notify.Models.Notes;
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
        fab_add = findViewById(R.id.fab_add);

        // Initialize Database
        database = RoomDB.getInstance(this);
        notes = database.maindao().getAll();
        if (notes == null) {
            notes = new ArrayList<>();
        }

        // Update RecyclerView with Notes
        updateRecycler(notes);

        // Set onClickListener for the FAB to add notes
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NotesTakerActivity.class);
                startActivityForResult(intent, 101);
            }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                if (new_notes != null) {
                    database.maindao().insert(new_notes); // Insert into database

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
    };
}
