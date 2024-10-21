package com.example.inkpad.memories;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.inkpad.R;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MemoriesActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PHOTO = 1;
    private static final int REQUEST_CODE_VIDEO = 2;

    private EditText descriptionEditText;
    private ImageView displayImageView;
    private VideoView displayVideoView;
    private MemoriesDatabaseHelper dbHelper;
    private Button saveButton, choosePhotoButton, chooseVideoButton;

    private byte[] selectedImageBytes = null;
    private Uri selectedMediaUri = null;
    private String selectedMediaType = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable homeIcon = getResources().getDrawable(R.drawable.back_home);
        toolbar.setNavigationIcon(homeIcon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dbHelper = new MemoriesDatabaseHelper(this);

        choosePhotoButton = findViewById(R.id.choosePhotoButton);
        chooseVideoButton = findViewById(R.id.chooseVideoButton);
        saveButton = findViewById(R.id.saveButton);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        displayImageView = findViewById(R.id.displayImageView);
        displayVideoView = findViewById(R.id.displayVideoView);

        choosePhotoButton.setOnClickListener(v -> openGallery(REQUEST_CODE_PHOTO));
        chooseVideoButton.setOnClickListener(v -> openGallery(REQUEST_CODE_VIDEO));

        saveButton.setOnClickListener(v -> saveMemory());

        loadSavedMemory();
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        if (requestCode == REQUEST_CODE_PHOTO) {
            intent.setType("image/*");
        } else if (requestCode == REQUEST_CODE_VIDEO) {
            intent.setType("video/*");
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            selectedMediaUri = data.getData();
            if (selectedMediaUri == null) {
                Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                if (requestCode == REQUEST_CODE_PHOTO) {
                    InputStream imageStream = getContentResolver().openInputStream(selectedMediaUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    if (selectedImage != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        selectedImageBytes = baos.toByteArray();
                        selectedMediaType = "photo";
                        displayImageView.setImageBitmap(selectedImage);
                        displayImageView.setVisibility(View.VISIBLE);
                        displayVideoView.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Error decoding image", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == REQUEST_CODE_VIDEO) {
                    selectedMediaType = "video";
                    displayVideoView.setVideoURI(selectedMediaUri);
                    displayVideoView.setVisibility(View.VISIBLE);
                    displayImageView.setVisibility(View.GONE);
                    displayVideoView.setOnPreparedListener(mediaPlayer -> {
                        displayVideoView.start();
                    });
                }
            } catch (Exception e) {
                Log.e("MemoriesActivity", "Error loading media: " + e.getMessage(), e);
                Toast.makeText(this, "Error loading media", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveMemory() {
        String description = descriptionEditText.getText().toString().trim();
        if (selectedMediaUri != null && !description.isEmpty()) {
            // Use the URI directly for videos instead of converting to a file path
            Memory memory = new Memory(description, selectedMediaUri.toString(), selectedMediaType, selectedImageBytes);
            boolean isSaved = dbHelper.saveMemory(memory);
            if (isSaved) {
                Toast.makeText(this, "Memory saved successfully", Toast.LENGTH_SHORT).show();
                clearSelection(); // Clear selection after saving
            } else {
                Toast.makeText(this, "Failed to save memory", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select media and provide a description", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearSelection() {
        selectedMediaUri = null;
        selectedImageBytes = null;
        selectedMediaType = null;
        descriptionEditText.setText("");
        displayImageView.setVisibility(View.GONE);
        displayVideoView.setVisibility(View.GONE);
    }

    private void loadSavedMemory() {
        Memory memory = dbHelper.getMemory();
        if (memory != null) {
            descriptionEditText.setText(memory.getDescription());
            if ("photo".equals(memory.getMediaType())) {
                displayImageView.setImageBitmap(BitmapFactory.decodeByteArray(memory.getImageBytes(), 0, memory.getImageBytes().length));
                displayImageView.setVisibility(View.VISIBLE);
                displayVideoView.setVisibility(View.GONE);
            } else if ("video".equals(memory.getMediaType())) {
                Uri videoUri = Uri.parse(memory.getMediaUri());
                displayVideoView.setVideoURI(videoUri);
                displayVideoView.setVisibility(View.VISIBLE);
                displayImageView.setVisibility(View.GONE);
            }
        }
    }

    public void deletedata() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Data")
                .setMessage("Are you sure you want to delete all the entered data?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteMemories();
                    clearSelection(); // Clear selection after deletion
                    Toast.makeText(this, "All data deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_memories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId() == R.id.action_delete) deletedata();
        return super.onOptionsItemSelected(item);
    }
}
