package com.example.inkpad.todo;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.inkpad.R;

public class ToDoFragment extends Fragment {

    private EditText editTextTask1, editTextTask2, editTextTask3, editTextTask4, editTextTask5, editTextTask6, editTextTask7;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7;
    private Button saveButton;
    private DatabaseHelperTodo dbHelpertodo;
    private long[] taskIds = new long[7];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        editTextTask1 = view.findViewById(R.id.editTextTask1);
        editTextTask2 = view.findViewById(R.id.editTextTask2);
        editTextTask3 = view.findViewById(R.id.editTextTask3);
        editTextTask4 = view.findViewById(R.id.editTextTask4);
        editTextTask5 = view.findViewById(R.id.editTextTask5);
        editTextTask6 = view.findViewById(R.id.editTextTask6);
        editTextTask7 = view.findViewById(R.id.editTextTask7);
        checkBox1 = view.findViewById(R.id.checkBox1);
        checkBox2 = view.findViewById(R.id.checkBox2);
        checkBox3 = view.findViewById(R.id.checkBox3);
        checkBox4 = view.findViewById(R.id.checkBox4);
        checkBox5 = view.findViewById(R.id.checkBox5);
        checkBox6 = view.findViewById(R.id.checkBox6);
        checkBox7 = view.findViewById(R.id.checkBox7);
        saveButton = view.findViewById(R.id.button_save);
        dbHelpertodo = new DatabaseHelperTodo(getContext());
        loadToDoItems();
        saveButton.setOnClickListener(v -> saveToDoItems());
        return view;
    }

    private void saveToDoItems() {
        if (editTextTask1.getText().toString().trim().isEmpty() &&
                editTextTask2.getText().toString().trim().isEmpty() &&
                editTextTask3.getText().toString().trim().isEmpty() &&
                editTextTask4.getText().toString().trim().isEmpty() &&
                editTextTask5.getText().toString().trim().isEmpty() &&
                editTextTask6.getText().toString().trim().isEmpty() &&
                editTextTask7.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Please fill any of the fields", Toast.LENGTH_LONG).show();
        }
        String[] tasks = new String[] {
                editTextTask1.getText().toString().trim(),
                editTextTask2.getText().toString().trim(),
                editTextTask3.getText().toString().trim(),
                editTextTask4.getText().toString().trim(),
                editTextTask5.getText().toString().trim(),
                editTextTask6.getText().toString().trim(),
                editTextTask7.getText().toString().trim()
        };
        boolean[] isChecked = new boolean[] {
                checkBox1.isChecked(),
                checkBox2.isChecked(),
                checkBox3.isChecked(),
                checkBox4.isChecked(),
                checkBox5.isChecked(),
                checkBox6.isChecked(),
                checkBox7.isChecked()
        };
        for (int i = 0; i < tasks.length; i++) {
            String task = tasks[i];
            if (!task.isEmpty()) {
                if (dbHelpertodo.doesTaskExistById(taskIds[i])) {
                    dbHelpertodo.updateToDoItem(taskIds[i], task, isChecked[i]);
                } else {
                    long taskId = dbHelpertodo.addToDoItem(task, isChecked[i]);
                    taskIds[i] = taskId;
                }
            } else if (taskIds[i] != -1) {
                dbHelpertodo.deleteToDoItemById(taskIds[i]);
                taskIds[i] = -1;
            }
        }

        Toast.makeText(getContext(), "Tasks saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void loadToDoItems() {
        Cursor cursor = dbHelpertodo.getAllToDoItems();
        if (cursor != null && cursor.moveToFirst()) {
            int index = 0;
            do {
                @SuppressLint("Range") long taskId = cursor.getLong(cursor.getColumnIndex(DatabaseHelperTodo.COLUMN_ID));
                @SuppressLint("Range") String task = cursor.getString(cursor.getColumnIndex(DatabaseHelperTodo.COLUMN_TASK));
                @SuppressLint("Range") boolean isCompleted = cursor.getInt(cursor.getColumnIndex(DatabaseHelperTodo.COLUMN_IS_COMPLETED)) == 1;
                taskIds[index] = taskId;
                if (index == 0) {
                    editTextTask1.setText(task);
                    checkBox1.setChecked(isCompleted);
                } else if (index == 1) {
                    editTextTask2.setText(task);
                    checkBox2.setChecked(isCompleted);
                } else if (index == 2) {
                    editTextTask3.setText(task);
                    checkBox3.setChecked(isCompleted);
                } else if (index == 3) {
                    editTextTask4.setText(task);
                    checkBox4.setChecked(isCompleted);
                } else if (index == 4) {
                    editTextTask5.setText(task);
                    checkBox5.setChecked(isCompleted);
                } else if (index == 5) {
                    editTextTask6.setText(task);
                    checkBox6.setChecked(isCompleted);
                } else if (index == 6) {
                    editTextTask7.setText(task);
                    checkBox7.setChecked(isCompleted);
                }
                index++;
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
