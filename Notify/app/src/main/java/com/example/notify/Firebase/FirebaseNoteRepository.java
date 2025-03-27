package com.example.notify.Firebase;

import com.example.notify.Models.Notes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseNoteRepository {
    private final FirebaseFirestore db;
    private final CollectionReference notesCollection;

    public FirebaseNoteRepository() {
        db = FirebaseFirestore.getInstance();
        notesCollection = db.collection("notes");
    }

    // Add a new note
    public Task<DocumentReference> addNote(Notes note) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> noteData = new HashMap<>();
        noteData.put("userId", userId); // Include user ID
        noteData.put("title", note.getTitle());
        noteData.put("notes", note.getNotes());
        noteData.put("date", note.getDate());
        noteData.put("imageUri", note.getImageUri());
        noteData.put("reminderTime", note.getReminderTime());
        noteData.put("tasks", note.getTasks());
        noteData.put("isPinned", note.isPinned());
        noteData.put("isFavourite", note.isFavourite());
        noteData.put("isArchived", note.isArchived());
        noteData.put("taggedUsers", note.getTaggedUsers());

        return notesCollection.add(noteData);
    }

    public Task<DocumentSnapshot> getUser(String userId) {
        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get();
    }

    // Update an existing note
    public Task<Void> updateNote(String noteId, Map<String, Object> updates) {

        return notesCollection.document(noteId).update(updates);
    }

    // Delete a note
    public Task<Void> deleteNote(String noteId) {
        return notesCollection.document(noteId).delete();
    }

    // Read all notes
    public Task<QuerySnapshot> readNotes(String userId) {
        return notesCollection.whereEqualTo("userId", userId).get();
    }


}