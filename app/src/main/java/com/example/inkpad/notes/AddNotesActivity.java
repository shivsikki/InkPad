package com.example.inkpad.notes;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.inkpad.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddNotesActivity extends AppCompatActivity {

    FloatingActionButton save;
    EditText title, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        title = findViewById(R.id.notetilte);
        content = findViewById(R.id.notecontent);
        save = findViewById(R.id.savebutton);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable homeIcon = getResources().getDrawable(R.drawable.back_home);
        toolbar.setNavigationIcon(homeIcon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteTitle = title.getText().toString();
                String noteContent = content.getText().toString();

                if (noteTitle.isEmpty() || noteContent.isEmpty()) {
                    Toast.makeText(AddNotesActivity.this, "Title or Content cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                NotesDatabaseHelper dbHelper = new NotesDatabaseHelper(AddNotesActivity.this);
                long result = dbHelper.addNote(noteTitle, noteContent);

                if (result > -1) {
                    Log.d("AddNotesActivity", "Note successfully inserted with ID: " + result);
                    Toast.makeText(AddNotesActivity.this, "Note saved!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish(); // Close the activity after saving
                } else {
                    Log.e("AddNotesActivity", "Failed to insert note.");
                    Toast.makeText(AddNotesActivity.this, "Failed to save note.", Toast.LENGTH_SHORT).show();
                }
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
