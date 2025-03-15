package com.example.notify;

import static java.util.Locale.filter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.notify.Adapters.NotesListAdapters;
import com.example.notify.Database.RoomDB;
import com.example.notify.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NotesListAdapters notesListAdapters;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab_add;

    private final ActivityResultLauncher<Intent> noteActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Notes new_notes = (Notes) result.getData().getSerializableExtra("note");

                            // ✅ Check if new_notes is null before using it
                            if (new_notes == null) {
                                Log.e("NOTES_ERROR", "Received null note from NotesTakerActivity");
                                return;
                            }

                            Log.d("NOTES_CHECK", "New Note: " + new_notes.toString());

                            database.maindao().insert(new_notes);
                            notes = database.maindao().getAll(); // Refresh the list
                            notesListAdapters.updateList(notes);
                            notes.clear();
                            notes.addAll(database.maindao().getAll());

                            Log.d("NOTES_CHECK", "Updated Notes List: " + notes.toString());

                            updateRecycler(notes);
                        } else {
                            Log.e("NOTES_ERROR", "Result data is null or resultCode is not OK");
                        }
                    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_home);
        fab_add = findViewById(R.id.fab_add);

        database = RoomDB.getInstance(this);
        notes = database.maindao().getAll();
        if (notes == null) {
            notes = new ArrayList<>();
        }
        Log.d("NOTES_CHECK", "Retrieved Notes: " + notes.toString());

        updateRecycler(notes);

        fab_add.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            noteActivityResultLauncher.launch(intent);
        });

        // Debugging: Check if notes exist
        Log.d("DatabaseCheck", "Notes Size: " + notes.size());

        // Add to onCreate():
//        SearchView searchView = findViewById(R.id.search_view);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filter(newText.toLowerCase());
//                return true;
//            }
//
//            private void filter(String text) {
//                List<Notes> filteredList = new ArrayList<>();
//                for (Notes note : database.maindao().getAll()) {
//                    if (note.getTitle().toLowerCase().contains(text) ||
//                            note.getNotes().toLowerCase().contains(text)) {
//                        filteredList.add(note);
//                    }
//                }
//                notesListAdapters.updateList(filteredList);
//            }
//        });

    }

    private void updateRecycler(List<Notes> notes) {
        if (notesListAdapters == null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
            notesListAdapters = new NotesListAdapters(MainActivity.this, notes, notesClickListener);
            recyclerView.setAdapter(notesListAdapters);
        } else {
            notesListAdapters.updateList(notes); // ✅ Update existing adapter
        }
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            // Handle click
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            intent.putExtra("existing_note", notes);
            noteActivityResultLauncher.launch(intent);
        }

        // Update LongClick listener:
        @Override
        public void LongClick(Notes notes, CardView cardView) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        database.maindao().delete(notes);
                        refreshNotes();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        private void refreshNotes() {
            notes = database.maindao().getAll();
            notesListAdapters.updateList(notes);
        }

        @Override
        public void LongClick(Notes notes, View cardView) {
            // Handle long click
        }
    };
}
