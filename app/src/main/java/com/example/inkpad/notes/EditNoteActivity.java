package com.example.inkpad.notes;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.inkpad.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditNoteActivity extends AppCompatActivity {
    FloatingActionButton update;
    EditText titleEditText, contentEditText;
    NotesDatabaseHelper dbHelper;
    int noteId;
    boolean fromNoteDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable homeIcon = getResources().getDrawable(R.drawable.back_home);
        toolbar.setNavigationIcon(homeIcon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        titleEditText = findViewById(R.id.editedtilte);
        contentEditText = findViewById(R.id.editedcontent);
        update = findViewById(R.id.uploadeditbutton);
        dbHelper = new NotesDatabaseHelper(this);

        noteId = getIntent().getIntExtra("noteId", -1);
        String noteTitle = getIntent().getStringExtra("noteTitle");
        String noteContent = getIntent().getStringExtra("noteContent");
        fromNoteDetails = getIntent().getBooleanExtra("fromNoteDetails", false);  // Get the flag

        titleEditText.setText(noteTitle);
        contentEditText.setText(noteContent);

        update.setOnClickListener(v -> {
            String updatedTitle = titleEditText.getText().toString().trim();
            String updatedContent = contentEditText.getText().toString().trim();

            if (!updatedTitle.isEmpty() && !updatedContent.isEmpty()) {
                boolean isUpdated = dbHelper.updateNote(noteId, updatedTitle, updatedContent);
                if (isUpdated) {
                    Toast.makeText(EditNoteActivity.this, "Note updated", Toast.LENGTH_SHORT).show();

                    if (fromNoteDetails) {
                        Intent intent = new Intent(EditNoteActivity.this, NotesActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(EditNoteActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditNoteActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
