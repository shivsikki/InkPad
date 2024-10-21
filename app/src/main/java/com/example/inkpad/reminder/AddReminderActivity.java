package com.example.inkpad.reminder;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.inkpad.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Calendar;


public class AddReminderActivity extends AppCompatActivity {

    private EditText nameEditText, messageEditText;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private FloatingActionButton saveButton;
    private ReminderDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable homeIcon = getResources().getDrawable(R.drawable.back_home);
        toolbar.setNavigationIcon(homeIcon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        nameEditText = findViewById(R.id.edit_text_name);
        messageEditText = findViewById(R.id.edit_text_message);
        timePicker = findViewById(R.id.time_picker);
        datePicker = findViewById(R.id.date_picker);
        saveButton = findViewById(R.id.button_save);

        dbHelper = new ReminderDatabaseHelper(this);

        timePicker.setIs24HourView(true);

        saveButton.setOnClickListener(v -> saveReminder());
    }

    private void saveReminder() {
        String name = nameEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, 0);
        long timeInMillis = calendar.getTimeInMillis();

        // Check if the time is in the past
        if (timeInMillis < System.currentTimeMillis()) {
            Toast.makeText(this, "Please select a future date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        } else {
            int notificationId = name.hashCode(); // Generate a unique notification ID
            boolean isInserted = dbHelper.insertReminder(name, message, timeInMillis, year + "-" + (month + 1) + "-" + day, notificationId);
            if (isInserted) {
                Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show();
                ReminderNotification.scheduleNotification(this, name, message, timeInMillis, notificationId); // Schedule the notification

                // Set result to indicate success
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish(); // Close the activity and return to the previous one
            } else {
                Toast.makeText(this, "Failed to save reminder", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
