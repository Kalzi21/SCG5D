package com.example.notify.Models;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TaggedUsersConverter {

    // Convert List<String> to a Firestore-compatible format (List<Object>)
    public static List<Object> toFirestore(List<String> taggedUsers) {
        return new ArrayList<>(taggedUsers);
    }

    // Convert Firestore data (List<Object>) back to List<String>
    public static List<String> fromFirestore(Object data) {
        List<String> taggedUsers = new ArrayList<>();
        if (data instanceof List) {
            for (Object item : (List<?>) data) {
                if (item instanceof String) {
                    taggedUsers.add((String) item);
                }
            }
        }
        return taggedUsers;
    }
}