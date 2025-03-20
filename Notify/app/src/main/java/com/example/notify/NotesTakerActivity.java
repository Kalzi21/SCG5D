package com.example.notify;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.example.notify.Database.RoomDB;
import com.example.notify.Models.Notes;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesTakerActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    EditText editText_title, editText_notes;
    ImageView imageView_save;
    Notes notes;
    private Uri imageUri; // To store the selected image URI
    private long reminderTime; // To store the reminder time
    RoomDB database;

    Notes existingNote; // Declare at class level to use in onClick
    private List<String> tasks = new ArrayList<>();


    private void checkNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now post notifications
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "reminder_channel", // Channel ID
                    "Reminders", // Channel name
                    NotificationManager.IMPORTANCE_HIGH // Importance level
            );
            channel.setDescription("Channel for reminder notifications");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker2);


        createNotificationChannel(); // Create the notification channel
        checkNotificationPermission();


        // Initialize UI elements

        imageView_save = findViewById(R.id.imageView_save);
        editText_title = findViewById(R.id.editText_title);
        editText_notes = findViewById(R.id.editText_notes);


        // Initialize the database
        database = RoomDB.getInstance(this);

        // Handle image selection
        ImageView btnAddImage = findViewById(R.id.btnAddImage);
        btnAddImage.setOnClickListener(v -> openImagePicker());

        // Handle reminder setting
        ImageView reminderLayout = findViewById(R.id.reminderIcon);
        reminderLayout.setOnClickListener(v -> setReminder());

        // Handle task addition
        Button btnAddTodo = findViewById(R.id.btnAddTodo);
        btnAddTodo.setOnClickListener(v -> addTodoItem());

        notes = new Notes(); // Initialize the note object here

        imageView_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String title = editText_title.getText().toString();
                    String description = editText_notes.getText().toString();

                    if (description.isEmpty()) {
                        Toast.makeText(NotesTakerActivity.this, "Please add some notes!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyy HH:mm a");
                    Date date = new Date();

                    notes = new Notes();
                    notes.setTitle(title);
                    notes.setNotes(description);
                    notes.setDate(formatter.format(date));

                    // Add image URI if available
                    if (imageUri != null) {
                        notes.setImageUri(imageUri.toString());
                    }

                    // Add reminder time if set
                    if (reminderTime > 0) {
                        notes.setReminderTime(reminderTime);
                    }

                    // Add tasks if available
                    if (tasks != null && !tasks.isEmpty()) {
                        Gson gson = new Gson();
                        String tasksJson = gson.toJson(tasks);
                        notes.setTasks(tasksJson);
                    }

                    // Save the note to the database
                    if (notes.getID() == 0) { // New note
                        database.maindao().insert(notes);
                    } else { // Existing note
                        database.maindao().update(notes);
                    }

                    // Notify the user
                    Intent intent = new Intent();
                    intent.putExtra("note", notes);
                    setResult(Activity.RESULT_OK, intent);
                    Toast.makeText(NotesTakerActivity.this, "Note saved!", Toast.LENGTH_SHORT).show();
                    finish();  // Close activity after saving the note
                } catch (Exception e) {
                    e.printStackTrace();  // Log the error
                    Toast.makeText(NotesTakerActivity.this, "Error saving note", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the selected item's ID

            if (itemId == R.id.nav_add) {
                Intent intent = new Intent(NotesTakerActivity.this, NotesTakerActivity.class);
                startActivityForResult(intent, 101);
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent intent = new Intent(NotesTakerActivity.this, HomeActivity.class);
                startActivityForResult(intent, 101);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Handle profile click
                return true;
            } else if (itemId == R.id.nav_settings) {
                // Handle settings click
                return true;
            } else if (itemId == R.id.nav_notifications) {
                // Handle notifications click
                return true;
            }

            return false; // Return false if no item is selected
        });


        // Check if editing an existing note
        existingNote = (Notes) getIntent().getSerializableExtra("existing_note");
        if (existingNote != null) {
            // If editing an existing note, copy its data into the new note object
            notes.setID(existingNote.getID());
            notes.setTitle(existingNote.getTitle());
            notes.setNotes(existingNote.getNotes());
            notes.setDate(existingNote.getDate());
            notes.setPinned(existingNote.isPinned());
            notes.setFavourite(existingNote.isFavourite());
            notes.setArchived(existingNote.isArchived());
            notes.setImageUri(existingNote.getImageUri());
            notes.setReminderTime(existingNote.getReminderTime());
            notes.setTasks(existingNote.getTasks());

            editText_title.setText(existingNote.getTitle());
            editText_notes.setText(existingNote.getNotes());


            // Load tasks if available

        String tasksJson = existingNote.getTasks();
            if (tasksJson != null && !tasksJson.isEmpty()) {
                Gson gson = new Gson();
                Type taskListType = new TypeToken<List<String>>() {}.getType();
                tasks = gson.fromJson(tasksJson, taskListType);

                // Display tasks in the UI
                LinearLayout todoContainer = findViewById(R.id.todoContainer);
                for (String task : tasks) {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(task);
                    checkBox.setTextColor(Color.BLACK);
                    todoContainer.addView(checkBox);
                }
            }
            }



    }


    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void setReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Use FLAG_IMMUTABLE for Android 12 and above
            pendingIntent = PendingIntent.getBroadcast(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            // Use the old flags for older versions
            pendingIntent = PendingIntent.getBroadcast(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            );
        }

        // Set the reminder time (e.g., 10 seconds from now)
        long triggerTime = System.currentTimeMillis() + 10000; // 10 seconds
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

        Toast.makeText(this, "Reminder set!", Toast.LENGTH_SHORT).show();
    }

    private void addTodoItem() {
        // Create a new task
        EditText taskInput = new EditText(this); // Use EditText for editable tasks
        taskInput.setHint("Enter a task");
        taskInput.setHintTextColor(Color.BLACK);

        // Add the task input field to the UI
        LinearLayout todoContainer = findViewById(R.id.todoContainer);
        todoContainer.addView(taskInput);

        // Handle task submission (e.g., when the user presses Enter)
        taskInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String newTask = taskInput.getText().toString().trim();
                if (!newTask.isEmpty()) {
                    tasks.add(newTask);

                    // Replace the EditText with a CheckBox
                    todoContainer.removeView(taskInput);
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(newTask);
                    checkBox.setTextColor(Color.BLACK);
                    todoContainer.addView(checkBox);

                    // Serialize tasks into a JSON string
                    Gson gson = new Gson();
                    String tasksJson = gson.toJson(tasks);

                    // Ensure the note object is not null before calling setTasks
                    if (notes != null) {
                        notes.setTasks(tasksJson);
                    } else {
                        Log.e("NotesTakerActivity", "Note object is null");
                    }
                }
                return true;
            }
            return false;
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Display the image
            ImageView noteImage = findViewById(R.id.noteImage);
            noteImage.setImageURI(imageUri);
            noteImage.setVisibility(View.VISIBLE);
        }
    }

}
