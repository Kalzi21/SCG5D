package com.example.notify.Firebase;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import com.example.notify.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public FirebaseManager() {
        try {
            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            storage = FirebaseStorage.getInstance();
            Log.d("FirebaseManager", "Successfully initialized Firebase services");
        } catch (Exception e) {
            Log.e("FirebaseManager", "Initialization failed", e);
            throw new RuntimeException("Firebase services initialization failed", e);
        }
    }

    // ✅ Register a New User
    public void registerUser(String email, String username, String password, Uri imageUri, FirebaseCallback callback) {
        if (email == null || username == null || password == null) {
            callback.onFailure(new IllegalArgumentException("Email, username, and password cannot be null"));
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Upload profile image if provided
                            if (imageUri != null) {
                                uploadProfilePicture(imageUri, userId, new FirebaseCallback() {
                                    @Override
                                    public void onSuccess(Object result) {
                                        String imageUrl = (String) result;
                                        saveUserToFirestore(userId, email, username, imageUrl, callback);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        callback.onFailure(e);
                                    }
                                });
                            } else {
                                // Save user without profile picture
                                saveUserToFirestore(userId, email, username, "", callback);
                            }
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // ✅ Save User Data to Firestore
    private void saveUserToFirestore(String userId, String email, String username, String profilePicUrl, FirebaseCallback callback) {
        User user = new User(userId, email, username, profilePicUrl);
        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                .addOnFailureListener(callback::onFailure);
    }

    // ✅ User Login
    public void loginUser(String email, String password, FirebaseCallback callback) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Fetch user from Firestore
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore.getInstance().collection("users").document(userId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    User user = documentSnapshot.toObject(User.class);
                                    callback.onSuccess(user);
                                });
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // ✅ Upload Profile Picture
    public void uploadProfilePicture(Uri imageUri, String userId, FirebaseCallback callback) {
        if (imageUri == null || userId == null) {
            callback.onFailure(new IllegalArgumentException("Image URI and user ID cannot be null"));
            return;
        }

        StorageReference storageRef = storage.getReference().child("profile_pictures/" + userId + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            callback.onSuccess(imageUrl);
                        }))
                .addOnFailureListener(callback::onFailure);
    }

    // ✅ Update Profile Picture URL in Firestore
    public void updateProfilePicture(String userId, String profilePicUrl, FirebaseCallback callback) {
        if (userId == null || profilePicUrl == null) {
            callback.onFailure(new IllegalArgumentException("User ID and profile picture URL cannot be null"));
            return;
        }

        db.collection("users").document(userId)
                .update("profilePictureUrl", profilePicUrl)
                .addOnSuccessListener(aVoid -> callback.onSuccess(profilePicUrl))
                .addOnFailureListener(callback::onFailure);
    }

    // ✅ Retrieve User Data from Firestore
    public void getUserFromFirestore(String userId, FirebaseCallback callback) {
        if (userId == null) {
            callback.onFailure(new IllegalArgumentException("User ID cannot be null"));
            return;
        }

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    // ✅ Get Currently Logged-in User
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // ✅ Logout User
    public void logoutUser() {
        auth.signOut();
    }

    // ✅ Delete User Account
    public void deleteUserAccount(FirebaseCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess("User account deleted");
                        } else {
                            callback.onFailure(task.getException());
                        }
                    });
        } else {
            callback.onFailure(new Exception("No user is currently logged in"));
        }
    }

    public void searchUsers(String query, FirebaseCallback callback) {
        db.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        users.add(doc.toObject(User.class));
                    }
                    callback.onSuccess(users);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // 🔹 Callback Interface for Handling Success/Failure
    public interface FirebaseCallback {
        void onSuccess(Object result);
        void onFailure(Exception e);
    }
}