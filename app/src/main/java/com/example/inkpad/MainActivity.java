package com.example.inkpad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inkpad.register.RegisterActivity;
import com.example.inkpad.register.RegisterDatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private RegisterDatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // If logged in, go directly to WelcomeActivity
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_main);

        // Initialize Views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        // Initialize DatabaseHelper
        dbHelper = new RegisterDatabaseHelper(this);

        // Handle Login Button Click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate User Credentials
                if (validateUser(email, password)) {
                    // Retrieve the username from the database
                    String username = getUsernameFromDatabase(email);

                    // Save login state, email, and username in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("userEmail", email); // Save the logged-in user's email
                    editor.putString("userName", username); // Save the logged-in user's username
                    editor.apply();

                    // If credentials are valid, navigate to WelcomeActivity
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // If credentials are invalid, show a Toast message
                    Toast.makeText(MainActivity.this, "Email ID or Password is wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle Register TextView Click
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RegisterActivity
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // Validate User Credentials using DatabaseHelper
    private boolean validateUser(String email, String password) {
        // Call DatabaseHelper method to check if the user exists with the given email and password
        return dbHelper.validateUser(email, password);
    }

    // Retrieve the username from the database based on email
    private String getUsernameFromDatabase(String email) {
        String username = "Unknown"; // Default value if username is not found
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT username FROM users WHERE email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            username = cursor.getString(0); // Retrieve the username from the result
        }
        cursor.close();
        return username;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
