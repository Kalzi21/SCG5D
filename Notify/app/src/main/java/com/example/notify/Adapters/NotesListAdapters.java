package com.example.notify.Adapters;

import static com.example.notify.Database.RoomDB.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notify.Database.RoomDB;
import com.example.notify.MainActivity;
import com.example.notify.Models.Notes;
import com.example.notify.NotesClickListener;
import com.example.notify.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesListAdapters extends RecyclerView.Adapter<NotesViewHolder>{
    Context context;
    List<Notes> list;
    NotesClickListener listener;
    private List<Notes> notes;


    public NotesListAdapters(MainActivity context, List<Notes> list, NotesClickListener listener) {
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
        // Your existing binding code
        holder.textView_title.setText(list.get(position).getTitle());
        holder.textView_notes.setText(list.get(position).getNotes());
        holder.textView_date.setText(list.get(position).getDate());

        // Your existing pin icon setup
        if (list.get(position).isPinned()) {
            holder.imageView_pin.setImageResource(R.drawable.ic_pin);
        }

        // Your existing color code
        int color_code = getRandomColor();
        holder.notes_container.setCardBackgroundColor(holder.itemView.getResources().getColor(color_code, null));

        // Modified long-click listener for multi-actions
        holder.notes_container.setOnLongClickListener(view -> {
            showActionDialog(list.get(position), holder.notes_container);
            return true;
        });

        // Keep your existing click listeners
        holder.notes_container.setOnClickListener(view ->
                listener.onClick(list.get(holder.getAdapterPosition())));
    }

    private void showActionDialog(Notes note, CardView cardView) {
        CharSequence[] actions = new CharSequence[]{
                note.isPinned() ? "Unpin" : "Pin",
                "Edit",
                "Delete"
        };

        new AlertDialog.Builder(context)
                .setTitle("Note Actions")
                .setItems(actions, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            listener.onActionClick(note, "toggle_pin");
                            break;
                        case 1:
                            listener.onActionClick(note, "edit");
                            break;
                        case 2:
                            listener.onActionClick(note, "delete");
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int getRandomColor(){

        List<Integer> colorCode = new ArrayList<>();

        colorCode.add(R.color.color1);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);
        colorCode.add(R.color.color6);

        Random random = new Random();
        int random_color = random.nextInt(colorCode.size());
        return  colorCode.get(random_color);

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

class NotesViewHolder extends RecyclerView.ViewHolder{

    CardView notes_container;
    TextView textView_title,textView_notes,textView_date;
    ImageView imageView_pin;
    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        notes_container = itemView.findViewById(R.id.notes_container);
        textView_title = itemView.findViewById(R.id.textview_title);
        textView_notes = itemView.findViewById(R.id.textView_notes);
        textView_date = itemView.findViewById(R.id.textView_date);
        imageView_pin = itemView.findViewById(R.id.imageView_pin);

    }
}
