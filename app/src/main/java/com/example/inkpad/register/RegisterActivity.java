package com.example.inkpad.register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inkpad.R;
import com.example.inkpad.WelcomeActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button registerButton;
    private RegisterDatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // If logged in, go directly to HelloActivity
            Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_register);

        // Initialize Views
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        // Initialize DatabaseHelper
        dbHelper = new RegisterDatabaseHelper(this);

        // Handle Register Button Click
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {

                    saveUser(username, email, password);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                }
            }
        });
    }

    private void saveUser(String username, String email, String password) {
        dbHelper.saveUser(username, email, password);
        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", email);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

}
