package com.example.inkpad.notes;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.inkpad.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteDetails extends AppCompatActivity {

    FloatingActionButton edit;
    TextView titleTextView, contentTextView;
    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable homeIcon = getResources().getDrawable(R.drawable.back_home);
        toolbar.setNavigationIcon(homeIcon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        titleTextView = findViewById(R.id.notedetailtilte);
        contentTextView = findViewById(R.id.notedetailcontent);
        edit = findViewById(R.id.editbutton);


        noteId = getIntent().getIntExtra("noteId", -1);
        String noteTitle = getIntent().getStringExtra("noteTitle");
        String noteContent = getIntent().getStringExtra("noteContent");

        titleTextView.setText(noteTitle);
        contentTextView.setText(noteContent);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteDetails.this, EditNoteActivity.class);
                intent.putExtra("noteId", noteId);
                intent.putExtra("noteTitle", noteTitle);
                intent.putExtra("noteContent", noteContent);
                intent.putExtra("fromNoteDetails", true);
                startActivity(intent);
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
