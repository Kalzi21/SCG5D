package com.example.notify.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notify.Models.Notes;
import com.example.notify.NotesClickListener;
import com.example.notify.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NotesListAdapters extends RecyclerView.Adapter<NotesViewHolder> {
    private Context context;
    private List<Notes> list;
    private NotesClickListener listener;

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
        Notes currentNote = list.get(position);

        holder.textView_title.setText(currentNote.getTitle());
        holder.textView_notes.setText(currentNote.getNotes());
        holder.textView_date.setText(formatTimestamp(currentNote.getDate()));

        holder.imageView_pin.setVisibility(currentNote.isPinned() ? View.VISIBLE : View.GONE);
        holder.icon_favourite.setVisibility(currentNote.isFavourite() ? View.VISIBLE : View.GONE);
        holder.icon_archive.setVisibility(currentNote.isArchived() ? View.VISIBLE : View.GONE);

        if (currentNote.isArchived()) {
            holder.notes_container.setCardBackgroundColor(Color.LTGRAY);
        } else if (currentNote.isFavourite()) {
            holder.notes_container.setCardBackgroundColor(Color.YELLOW);
        } else {
            int color_code = getRandomColor();
            holder.notes_container.setCardBackgroundColor(holder.itemView.getResources().getColor(color_code, null));
        }

        holder.notes_container.setOnClickListener(view -> {
            if (listener != null) {
                listener.onClick(currentNote);
            }
        });

        holder.notes_container.setOnLongClickListener(view -> {
            if (listener != null) {
                showActionDialog(currentNote, holder.notes_container);
                return true;
            }
            return false;
        });
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "Unknown Date";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm a", Locale.ENGLISH);
        return sdf.format(timestamp.toDate());
    }

    private void showActionDialog(Notes note, CardView notes_container) {
        CharSequence[] actions = new CharSequence[]{
                note.isPinned() ? "Unpin" : "Pin",
                note.isFavourite() ? "Remove Favourite" : "Add to Favourites",
                note.isArchived() ? "Unarchive" : "Archive",
                "Edit",
                "Delete"
        };

        new AlertDialog.Builder(context)
                .setTitle("Note Actions")
                .setItems(actions, (dialog, which) -> {
                    switch (which) {
                        case 0: listener.onActionClick(note, "toggle_pin"); break;
                        case 1: listener.onActionClick(note, "favourite"); break;
                        case 2: listener.onActionClick(note, "archive"); break;
                        case 3: listener.onActionClick(note, "edit"); break;
                        case 4: listener.onActionClick(note, "delete"); break;
                    }
                })
                .show();
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
    CardView notes_container;
    TextView textView_title, textView_notes, textView_date;
    ImageView imageView_pin, icon_favourite, icon_archive;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        notes_container = itemView.findViewById(R.id.notes_container);
        textView_title = itemView.findViewById(R.id.textview_title);
        textView_notes = itemView.findViewById(R.id.textView_notes);
        textView_date = itemView.findViewById(R.id.textView_date);
        icon_favourite = itemView.findViewById(R.id.icon_favourite);
        icon_archive = itemView.findViewById(R.id.icon_archive);
        imageView_pin = itemView.findViewById(R.id.imageView_pin);
    }
}