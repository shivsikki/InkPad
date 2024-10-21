package com.example.inkpad.notes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.inkpad.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {

    FloatingActionButton addNotesButton;
    RecyclerView notesRecyclerView;
    TextView noNotesTextView;
    NotesAdapter notesAdapter;
    List<Note> notesList;
    List<Note> originalNotesList;
    NotesDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable homeIcon = getResources().getDrawable(R.drawable.back_home);
        toolbar.setNavigationIcon(homeIcon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        notesRecyclerView = findViewById(R.id.recylerview);
        noNotesTextView = findViewById(R.id.no_notes_text);
        addNotesButton = findViewById(R.id.createnotesbutton);

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        notesRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        dbHelper = new NotesDatabaseHelper(this);
        originalNotesList = dbHelper.getAllNotes();
        notesList = new ArrayList<>(originalNotesList);

        notesAdapter = new NotesAdapter(this, notesList, getRandomColor());
        notesRecyclerView.setAdapter(notesAdapter);

        updateViewVisibility();

        addNotesButton.setOnClickListener(view -> {
            Intent intent = new Intent(NotesActivity.this, AddNotesActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            originalNotesList.clear();
            originalNotesList.addAll(dbHelper.getAllNotes());
            notesList.clear();
            notesList.addAll(originalNotesList);
            notesAdapter.notifyDataSetChanged();
            Log.d("NotesActivity", "Notes added, updating UI");
            updateViewVisibility();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        originalNotesList.clear();
        originalNotesList.addAll(dbHelper.getAllNotes());
        notesList.clear();
        notesList.addAll(originalNotesList);
        notesAdapter.notifyDataSetChanged();
        Log.d("NotesActivity", "Activity resumed, updating UI");
        updateViewVisibility();
    }

    private void updateViewVisibility() {
        Log.d("NotesActivity", "Number of notes: " + notesList.size());

        if (notesList.isEmpty()) {
            noNotesTextView.setVisibility(View.VISIBLE);
            notesRecyclerView.setVisibility(View.GONE);
            Log.d("NotesActivity", "No notes, showing TextView");
        } else {
            noNotesTextView.setVisibility(View.GONE);
            notesRecyclerView.setVisibility(View.VISIBLE);
            Log.d("NotesActivity", "Notes found, showing RecyclerView");
        }
    }

    private void noteSearch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Notes");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Search", (dialog, which) -> {
            String query = input.getText().toString().toLowerCase();
            List<Note> filteredNotes = filterNotes(query);
            notesAdapter.updateNotes(filteredNotes);
            addNotesButton.setVisibility(View.GONE);
            findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private List<Note> filterNotes(String query) {
        List<Note> filteredNotes = new ArrayList<>();
        for (Note note : originalNotesList) {
            if (note.getTitle().toLowerCase().contains(query) ||
                    note.getContent().toLowerCase().contains(query)) {
                filteredNotes.add(note);
            }
        }
        return filteredNotes;
    }

    public void onBackButtonClick(View view) {
        notesAdapter.updateNotes(originalNotesList);
        addNotesButton.setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setVisibility(View.GONE);
    }

    public int getRandomColor() {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.green);

        Random random = new Random();
        int number = random.nextInt(colorcode.size());
        return getResources().getColor(colorcode.get(number));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        if (item.getItemId() == R.id.action_search)
            noteSearch();
        return super.onOptionsItemSelected(item);
    }
}
