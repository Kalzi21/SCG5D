package com.example.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Check if the app has permission to post notifications
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_channel")
                    .setSmallIcon(R.drawable.ic_reminder)
                    .setContentTitle("Reminder")
                    .setContentText("Don't forget to complete your tasks!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            notificationManager.notify(1, builder.build());
        } else {
            // Handle the case where permission is not granted
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}