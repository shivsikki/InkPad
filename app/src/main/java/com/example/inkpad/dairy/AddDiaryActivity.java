package com.example.inkpad.dairy;

import static android.app.ProgressDialog.show;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.inkpad.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddDiaryActivity extends AppCompatActivity {

    private TextView textViewDate, textViewDay;
    private EditText editTextDiary, editTextTopic;
    private Button buttonSave, buttonCalendar;
    private DiaryDatabaseHelper diaryDbHelper;
    private Calendar calendar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable homeIcon = getResources().getDrawable(R.drawable.back_home);
        toolbar.setNavigationIcon(homeIcon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        textViewDate = findViewById(R.id.textViewDate);
        textViewDay = findViewById(R.id.textViewDay);
        editTextDiary = findViewById(R.id.editTextDiary);
        editTextTopic = findViewById(R.id.editTextTopic);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCalendar = findViewById(R.id.buttonCalendar);

        diaryDbHelper = new DiaryDatabaseHelper(this);
        calendar = Calendar.getInstance();

        displayCurrentDateAndDay();
        loadDiaryEntry();

        buttonSave.setOnClickListener(v -> saveDiaryEntry());
        buttonCalendar.setOnClickListener(v -> openDatePicker());
    }

    private void displayCurrentDateAndDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        String currentDate = dateFormat.format(calendar.getTime());
        String currentDay = dayFormat.format(calendar.getTime());

        textViewDate.setText(currentDate);
        textViewDay.setText(currentDay);
    }

    private void loadDiaryEntry() {
        String selectedDate = textViewDate.getText().toString();
        String currentDate = getCurrentDate();

        Cursor cursor = diaryDbHelper.getDiaryEntryByDate(selectedDate);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String diaryText = cursor.getString(cursor.getColumnIndex(DiaryDatabaseHelper.COLUMN_DIARY_TEXT));
            @SuppressLint("Range") String diaryTopic = cursor.getString(cursor.getColumnIndex(DiaryDatabaseHelper.COLUMN_DIARY_TOPIC));
            editTextDiary.setText(diaryText);
            editTextTopic.setText(diaryTopic);
            cursor.close();
        } else {
            if (selectedDate.equals(currentDate)) {
                editTextDiary.setText("");
                editTextTopic.setText("");
            } else {
                editTextDiary.setText("No content entered that day.");
                editTextTopic.setText("No topic entered.");
            }
        }
    }


    private String getCurrentDate() {
        Calendar todayCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(todayCalendar.getTime());
    }


    private void saveDiaryEntry() {
        String diaryText = editTextDiary.getText().toString().trim();
        String diaryTopic = editTextTopic.getText().toString().trim();
        String diaryDate = textViewDate.getText().toString();

        if (!diaryText.isEmpty() || !diaryTopic.isEmpty()) {
            boolean isEntryExists = diaryDbHelper.getDiaryEntryByDate(diaryDate).getCount() > 0;
            boolean isSaved;
            if (isEntryExists) {
                isSaved = diaryDbHelper.updateDiaryEntry(diaryText, diaryTopic, diaryDate);
            } else {
                isSaved = diaryDbHelper.insertDiaryEntry(diaryText, diaryTopic, diaryDate);
            }

            if (isSaved) {
                Toast.makeText(AddDiaryActivity.this, "Diary saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddDiaryActivity.this, "Error saving diary!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddDiaryActivity.this, "Please write something in the diary or topic!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);


            if (calendar.after(Calendar.getInstance())) {
                Toast.makeText(AddDiaryActivity.this, "You can't go to a future date in a diary", Toast.LENGTH_SHORT).show();
            } else {
                displayCurrentDateAndDay();
                loadDiaryEntry();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void deleteData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete the content?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    // Delete the data for the selected day
                    String diaryDate = textViewDate.getText().toString();
                    boolean isDeleted = diaryDbHelper.deleteDiaryEntry(diaryDate);
                    if (isDeleted) {
                        Toast.makeText(AddDiaryActivity.this, "Diary deleted!", Toast.LENGTH_SHORT).show();
                        editTextDiary.setText("");
                        editTextTopic.setText("");
                    } else {
                        Toast.makeText(AddDiaryActivity.this, "Error deleting diary!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dairy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        if (item.getItemId() == R.id.action_delete)
            deleteData();
        return super.onOptionsItemSelected(item);
    }
}
