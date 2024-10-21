package com.example.inkpad.notes;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.inkpad.R;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private final Context context;
    private final List<Note> notesList;
    private final int colors;
    private final NotesDatabaseHelper dbHelper;

    public NotesAdapter(Context context, List<Note> notesList, int colors) {
        this.context = context;
        this.notesList = notesList;
        this.colors = colors;
        this.dbHelper = new NotesDatabaseHelper(context);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notes_layout, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Note note = notesList.get(position);

        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());

        int color = ((NotesActivity)context).getRandomColor();
        holder.noteCardView.setCardBackgroundColor(color);

        holder.noteCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoteDetails.class);
                intent.putExtra("noteId", note.getId());
                intent.putExtra("noteTitle", note.getTitle());
                intent.putExtra("noteContent", note.getContent());
                context.startActivity(intent);
            }
        });

        // Ensure the PopupMenu is not triggered more than once
        holder.settingsImageView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.setGravity(Gravity.END);
            popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(menuItem -> {
                Intent intent = new Intent(v.getContext(), EditNoteActivity.class);
                intent.putExtra("noteId", note.getId());  // Pass the note ID
                intent.putExtra("noteTitle", note.getTitle());  // Pass the note title
                intent.putExtra("noteContent", note.getContent());  // Pass the note content
                v.getContext().startActivity(intent);
                return true;
            });

            popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(menuItem -> {
                showDeleteConfirmationDialog(note, position);
                return true;
            });

            // Show popup menu
            popupMenu.show();
        });
    }



    @Override
    public int getItemCount() {
        return notesList.size();
    }

    private void showDeleteConfirmationDialog(Note note, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?");

        builder.setPositiveButton("Yes", (dialogInterface, which) -> {

            dbHelper.deleteNote(note.getId());

            dialogInterface.dismiss();


            notesList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notesList.size());


            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
        });


        builder.setNegativeButton("No", (dialogInterface, which) -> {

            dialogInterface.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateNotes(List<Note> newNotesList) {
        notesList.clear();
        notesList.addAll(newNotesList);
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView noteCardView;
        TextView titleTextView;
        TextView contentTextView;
        ImageView settingsImageView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteCardView = itemView.findViewById(R.id.notecard);
            titleTextView = itemView.findViewById(R.id.notetitle);
            contentTextView = itemView.findViewById(R.id.notecontent);
            settingsImageView = itemView.findViewById(R.id.menupopbutton);
        }
    }
}
