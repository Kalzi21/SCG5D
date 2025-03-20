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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notify.Models.Notes;
import com.example.notify.NotesClickListener;
import com.example.notify.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesListAdapters extends RecyclerView.Adapter<NotesViewHolder> {
    Context context;
    List<Notes> list;
    NotesClickListener listener;
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
        Notes note = new Notes();
        holder.icon_favourite.setVisibility(note.isFavourite() ? View.VISIBLE : View.GONE);
        holder.icon_archive.setVisibility(note.isArchived() ? View.VISIBLE : View.GONE);

// Add color coding for categories
        if (note.isArchived()) {
            holder.notes_container.setCardBackgroundColor(Color.LTGRAY);
        } else if (note.isFavourite()) {
            holder.notes_container.setCardBackgroundColor(Color.YELLOW);
        }

        // Load image if available
        if (list.get(position).getImageUri() != null) {
            Uri imageUri = Uri.parse(list.get(position).getImageUri());
            holder.noteImage.setImageURI(imageUri);
            holder.noteImage.setVisibility(View.VISIBLE);
        } else {
            holder.noteImage.setVisibility(View.GONE);
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

    public void updateList(List<Notes> newNotes) {
        this.list = newNotes;
        notifyDataSetChanged();
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {
    public ImageView noteImage;
    CardView notes_container;
    TextView textView_title, textView_notes, textView_date;
    ImageView imageView_pin,icon_favourite, icon_archive;
    LinearLayout todoContainer; // Add this

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        notes_container = itemView.findViewById(R.id.notes_container);
        textView_title = itemView.findViewById(R.id.textview_title);
        textView_notes = itemView.findViewById(R.id.textView_notes);
        textView_date = itemView.findViewById(R.id.textView_date);
        icon_favourite = itemView.findViewById(R.id.icon_favourite); // Add this
        icon_archive = itemView.findViewById(R.id.icon_archive); // Add this
        imageView_pin = itemView.findViewById(R.id.imageView_pin);
        noteImage = itemView.findViewById(R.id.noteImage); // Initialize noteImage
        todoContainer = itemView.findViewById(R.id.todoContainer); // Initialize todoContainer
    }
}
