package com.example.notify.Firebase;

import android.content.Context;
import com.google.firebase.FirebaseApp;

public class FirebaseInitializer {
    private static boolean initialized = false;

    public static synchronized void initialize(Context context) {
        if (!initialized) {
            FirebaseApp.initializeApp(context);
            initialized = true;
        }
    }
}