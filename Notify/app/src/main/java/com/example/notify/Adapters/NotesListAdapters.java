package com.example.notify.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.notify.Firebase.FirebaseManager;
import com.example.notify.Models.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notify.Models.Notes;
import com.example.notify.NotesClickListener;
import com.example.notify.R;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NotesListAdapters extends RecyclerView.Adapter<NotesViewHolder> {
    Context context;
    List<Notes> list;
    NotesClickListener listener;
    private boolean isActive = true; // Add this flag

    public void clearListeners() {
        this.isActive = false;
        this.listener = null;
    }

    private FirebaseManager firebaseManager;
    private List<Notes> notes;

    // Modify constructor to accept Context (generic) instead of MainActivity
    public NotesListAdapters(Context context, List<Notes> list, NotesClickListener listener) {
        this.context = context;
        this.list = (list != null) ? list : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        if (list == null || position < 0 || position >= list.size()) {
            return; // or handle this case appropriately
        }


        Notes note = list.get(position);
        if (note == null) {
            return;
        }

        // Load settings from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        boolean isRandomColorsEnabled = sharedPreferences.getBoolean("RandomColorsEnabled", true);
        int selectedColor = sharedPreferences.getInt("SelectedColor", Color.WHITE); // Default to white if no color is selected

        // Your existing binding code
        holder.textView_title.setText(list.get(position).getTitle());
        holder.textView_notes.setText(list.get(position).getNotes());
        holder.textView_date.setText(list.get(position).getDate());
      
        if (list.get(position).isPinned()) {
            holder.imageView_pin.setVisibility(View.VISIBLE);
        } else {
            holder.imageView_pin.setVisibility(View.GONE);
        }

        if (isRandomColorsEnabled) {
            // Use random color
            int color_code = getRandomColor();
            holder.notes_container.setCardBackgroundColor(holder.itemView.getResources().getColor(color_code, null));
        } else {
            // Use a single color (e.g., white or any other color)
            holder.notes_container.setCardBackgroundColor(selectedColor);
        }

        // Modified long-click listener for multi-actions
        holder.notes_container.setOnLongClickListener(view -> {
            showActionDialog(list.get(position), holder.notes_container);
            return true;
        });

        // Keep your existing click listeners
        holder.notes_container.setOnClickListener(view ->
                listener.onClick(list.get(holder.getAdapterPosition())));

        // Update onBindViewHolder

        holder.icon_archive.setVisibility(note.isArchived() ? View.VISIBLE : View.GONE);

// Add color coding for categories
        if (note.isArchived()) {
            holder.notes_container.setCardBackgroundColor(Color.LTGRAY);
        } else if (note.isFavourite()) {
            holder.notes_container.setCardBackgroundColor(Color.YELLOW);
        }

        // Load image if available
        String imageUriString = list.get(position).getImageUri();
        if (imageUriString != null && !imageUriString.isEmpty()) {
            Uri imageUri = Uri.parse(imageUriString);
            holder.noteImage.setImageURI(imageUri);
            holder.noteImage.setVisibility(View.VISIBLE); // Make the ImageView visible
        } else {
            holder.noteImage.setVisibility(View.GONE); // Hide the ImageView if no image
        }

        // Load tasks if available
        String tasksJson = list.get(position).getTasks();
        if (tasksJson != null && !tasksJson.isEmpty()) {
            try {
                Gson gson = new Gson();
                Type taskListType = new TypeToken<List<String>>() {}.getType();
                List<String> tasks = gson.fromJson(tasksJson, taskListType);

                // Clear existing views in todoContainer
                holder.todoContainer.removeAllViews();

                // Add tasks to todoContainer
                for (String task : tasks) {
                    CheckBox checkBox = new CheckBox(context);
                    checkBox.setText(task);
                    holder.todoContainer.addView(checkBox);
                }
            } catch (JsonSyntaxException e) {
                Log.e("NotesListAdapters", "Invalid JSON: " + tasksJson, e);
            }
        } else {
            // If no tasks, ensure the container is empty
            holder.todoContainer.removeAllViews();
        }

        // Clear existing tags
        holder.todoContainer.removeAllViews();

// Add tagged users
        if (note.getTaggedUsernames() != null && !note.getTaggedUsernames().isEmpty()) {
            holder.tagsContainer.removeAllViews();
            for (String username : note.getTaggedUsernames()) {
                TextView tagView = new TextView(context);
                tagView.setText("@" + username);
                tagView.setBackgroundResource(R.drawable.tag_background);
                tagView.setPadding(8, 4, 8, 4);
                tagView.setTextColor(ContextCompat.getColor(context, R.color.white));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 8, 0);
                tagView.setLayoutParams(params);

                holder.tagsContainer.addView(tagView);
            }
            holder.tagsContainer.setVisibility(View.VISIBLE);
        } else {
            holder.tagsContainer.setVisibility(View.GONE);
        }

        // Add this near your other UI bindings:

        if (note.getReminderTime() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            holder.reminderText.setText("Reminder: " + sdf.format(new Date(note.getReminderTime())));
            holder.reminderText.setVisibility(View.VISIBLE);
        } else {
            holder.reminderText.setVisibility(View.GONE);
        }
    }

    private void showActionDialog(Notes note, CardView notes_container) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_note_actions, null);

        // Initialize buttons
        Button btnPin = dialogView.findViewById(R.id.btnPin);
        Button btnFavourite = dialogView.findViewById(R.id.btnFavourite);
        Button btnArchive = dialogView.findViewById(R.id.btnArchive);
        Button btnEdit = dialogView.findViewById(R.id.btnEdit);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        // Set button text based on note state
        btnPin.setText(note.isPinned() ? "Unpin" : "Pin");
        btnFavourite.setText(note.isFavourite() ? "Remove Favourite" : "Add to Favourites");
        btnArchive.setText(note.isArchived() ? "Unarchive" : "Archive");

        // Create the dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Note Actions")
                .setView(dialogView) // Set the custom layout
                .create();

        // Set button click listeners
        btnPin.setOnClickListener(v -> {
            listener.onActionClick(note, "toggle_pin");
            dialog.dismiss();
        });

        btnFavourite.setOnClickListener(v -> {
            listener.onActionClick(note, "favourite");
            dialog.dismiss();
        });

        btnArchive.setOnClickListener(v -> {
            listener.onActionClick(note, "archive");
            dialog.dismiss();
        });

        btnEdit.setOnClickListener(v -> {
            listener.onActionClick(note, "edit");
            dialog.dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            listener.onActionClick(note, "delete");
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }


    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.color1);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);
        colorCode.add(R.color.color6);

        Random random = new Random();
        int random_color = random.nextInt(colorCode.size());
        return colorCode.get(random_color);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<Notes> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {
    public ImageView noteImage;
    CardView notes_container;
    TextView textView_title, textView_notes, textView_date, reminderText;
    ImageView imageView_pin,icon_favourite, icon_archive;
    LinearLayout todoContainer, tagsContainer; // Add this

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        notes_container = itemView.findViewById(R.id.notes_container);
        textView_title = itemView.findViewById(R.id.textview_title);
        tagsContainer = itemView.findViewById(R.id.tagsContainer);
        textView_notes = itemView.findViewById(R.id.textView_notes);
        textView_date = itemView.findViewById(R.id.textView_date);
        reminderText = itemView.findViewById(R.id.reminderText);
        icon_archive = itemView.findViewById(R.id.icon_archive); // Add this
        imageView_pin = itemView.findViewById(R.id.imageView_pin);
        noteImage = itemView.findViewById(R.id.noteImage); // Initialize noteImage
        todoContainer = itemView.findViewById(R.id.todoContainer); // Initialize todoContainer
    }
}
