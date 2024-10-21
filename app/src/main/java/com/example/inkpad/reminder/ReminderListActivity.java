package com.example.inkpad.reminder;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.inkpad.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ReminderListActivity extends AppCompatActivity {
    private ReminderDatabaseHelper dbHelper;
    private ReminderAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noremindertext; // Ensure this is initialized correctly
    private FloatingActionButton addButton;
    private List<Reminder> reminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable homeIcon = getResources().getDrawable(R.drawable.back_home);
        toolbar.setNavigationIcon(homeIcon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dbHelper = new ReminderDatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_view_reminders);
        addButton = findViewById(R.id.btn_add_reminder);
        noremindertext = findViewById(R.id.no_reminders_text);

        // Initial load of reminders
        loadReminders();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReminderListActivity.this, AddReminderActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the reminders in case any were added or deleted
        loadReminders();
    }

    private void loadReminders() {
        reminders = dbHelper.getAllReminders(); // Method to retrieve all reminders from the database

        // Update the adapter's reminders list and notify the adapter
        if (adapter == null) {
            adapter = new ReminderAdapter(this, reminders);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.refreshReminders(); // Refresh the reminders from the database
        }

        // Show or hide "No Reminders" text based on the reminders list
        if (reminders != null) {
            if (reminders.isEmpty()) {
                noremindertext.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noremindertext.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK)
            loadReminders();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
