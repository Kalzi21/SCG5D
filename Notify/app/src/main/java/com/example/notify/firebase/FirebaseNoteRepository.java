package com.example.notify.firebase;

import com.example.notify.Models.Notes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseNoteRepository {

    private final FirebaseFirestore db;
    private final CollectionReference notesCollection;
    private final FirebaseAuth auth;

    public FirebaseNoteRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.notesCollection = db.collection("notes"); // Firestore collection name
    }

    // ✅ Create a new note with tagged users
    public Task<DocumentReference> createNote(String title, String notes, String date, boolean pinned, boolean isFavourite, boolean isArchived, List<String> taggedUsers) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }

        Notes note = new Notes(null, userId, taggedUsers, title, notes, date, pinned, isFavourite, isArchived);

        return notesCollection.add(note).continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String generatedId = task.getResult().getId();
                note.setID(generatedId);
                return task.getResult().update("ID", generatedId).continueWith(t -> task.getResult());
            }
            return task;
        });
    }

    // ✅ Fixed: Read notes (fetch notes owned + notes where user is tagged)
    public Task<List<DocumentSnapshot>> readNotes() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forResult(Collections.emptyList()); // Return empty list instead of null
        }

        Task<QuerySnapshot> userNotesTask = notesCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get();

        Task<QuerySnapshot> taggedNotesTask = notesCollection
                .whereArrayContains("taggedUsers", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get();

        return Tasks.whenAllSuccess(userNotesTask, taggedNotesTask)
                .continueWith(task -> {
                    List<DocumentSnapshot> allNotes = new ArrayList<>();
                    for (Object result : task.getResult()) {
                        QuerySnapshot querySnapshot = (QuerySnapshot) result;
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            if (!allNotes.contains(document)) {
                                allNotes.add(document); // Avoid duplicates
                            }
                        }
                    }
                    allNotes.sort((a, b) -> b.getDate("date").compareTo(a.getDate("date"))); // Sort by date descending
                    return allNotes;
                });
    }

    // ✅ Update a specific note
    public Task<Void> updateNote(String noteId, String title, String notes, boolean pinned, boolean isFavourite, boolean isArchived, List<String> taggedUsers) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }

        return notesCollection.document(noteId).update(
                "title", title,
                "notes", notes,
                "pinned", pinned,
                "isFavourite", isFavourite,
                "isArchived", isArchived,
                "taggedUsers", taggedUsers
        );
    }

    // ✅ Delete a note
    public Task<Void> deleteNote(String noteId) {
        return notesCollection.document(noteId).delete();
    }

    // 🔹 Helper method to get the currently logged-in user's ID
    public String getCurrentUserId() {
        return (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;
    }
}
