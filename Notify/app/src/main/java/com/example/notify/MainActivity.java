package com.example.notify;


import static java.util.Locale.filter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notify.databinding.ActivityMainBinding;

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

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

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


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

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

    // In MainActivity.java
    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            // Your existing click handling
            openNoteForEditing(notes);
        }

        @Override
        public void LongClick(Notes notes, CardView cardView) {

        }

        @Override
        public void onActionClick(Notes notes, String action) {
            switch (action) {
                case "toggle_pin":
                    togglePin(notes);
                    break;
                case "edit":
                    openNoteForEditing(notes);
                    break;
                case "delete":
                    deleteNote(notes);
                    break;
            }
        }

        @Override
        public void LongClick(Notes notes, View cardView) {

        }

        private void togglePin(Notes notes) {
            boolean newState = !notes.isPinned();
            database.maindao().pin(notes.getID(), newState);
            refreshNotes();
        }

        private void deleteNote(Notes notes) {
            database.maindao().delete(notes);
            refreshNotes();
        }

        private void openNoteForEditing(Notes notes) {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            intent.putExtra("existing_note", notes);
            noteActivityResultLauncher.launch(intent);
        }

        private void refreshNotes() {
            notes = database.maindao().getAll();
            notesListAdapters.updateList(notes);
        }
    };
}

