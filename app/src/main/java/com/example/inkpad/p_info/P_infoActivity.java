package com.example.inkpad.p_info;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.inkpad.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;

public class P_infoActivity extends AppCompatActivity {

    private EditText editFirstName, editMiddleName, editLastName;
    private TextView textDOB;
    private Button buttonDOB, buttonUploadDoc, buttonUploadCert, buttonSave;
    private ImageView imageDoc, imageCert;
    private P_infoDatabaseHelper dbHelper;
    private byte[] selectedDocImage, selectedCertImage;
    private static final int PICK_DOC_IMAGE = 1;
    private static final int PICK_CERT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinfo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable homeIcon = getResources().getDrawable(R.drawable.back_home);
        toolbar.setNavigationIcon(homeIcon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        editFirstName = findViewById(R.id.edit_firstname);
        editMiddleName = findViewById(R.id.edit_middlename);
        editLastName = findViewById(R.id.edit_lastname);
        textDOB = findViewById(R.id.text_dob);
        buttonDOB = findViewById(R.id.button_dob);
        buttonUploadDoc = findViewById(R.id.button_upload_doc);
        buttonUploadCert = findViewById(R.id.button_upload_certificate);
        imageDoc = findViewById(R.id.image_doc);
        imageCert = findViewById(R.id.image_certificate);
        buttonSave = findViewById(R.id.button_save);

        dbHelper = new P_infoDatabaseHelper(this);

        loadSavedData();

        buttonDOB.setOnClickListener(v -> pickDate());
        buttonUploadDoc.setOnClickListener(v -> openGallery(PICK_DOC_IMAGE));
        buttonUploadCert.setOnClickListener(v -> openGallery(PICK_CERT_IMAGE));
        buttonSave.setOnClickListener(v -> saveData());
        imageDoc.setOnClickListener(v -> showFullImage(selectedDocImage));
        imageCert.setOnClickListener(v -> showFullImage(selectedCertImage));
    }


    private void loadSavedData() {
        P_info personalInfo = dbHelper.getPersonalInfo();
        if (personalInfo != null) {
            editFirstName.setText(personalInfo.getFirstName());
            editMiddleName.setText(personalInfo.getMiddleName());
            editLastName.setText(personalInfo.getLastName());
            textDOB.setText(personalInfo.getDob());

            if (personalInfo.getDocImage() != null) {
                Bitmap docBitmap = BitmapFactory.decodeByteArray(personalInfo.getDocImage(), 0, personalInfo.getDocImage().length);
                imageDoc.setImageBitmap(docBitmap);
                selectedDocImage = personalInfo.getDocImage();
            }

            if (personalInfo.getCertImage() != null) {
                Bitmap certBitmap = BitmapFactory.decodeByteArray(personalInfo.getCertImage(), 0, personalInfo.getCertImage().length);
                imageCert.setImageBitmap(certBitmap);
                selectedCertImage = personalInfo.getCertImage();
            }
        }
    }

    private void pickDate() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
            textDOB.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, requestCode);
    }

    private void showFullImage(byte[] imageData) {
        if (imageData != null) {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_full_image);
            ImageView fullImageView = dialog.findViewById(R.id.full_image_view);
            fullImageView.setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
            fullImageView.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } else {
            Toast.makeText(this, "No image available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArray = baos.toByteArray();

                if (requestCode == PICK_DOC_IMAGE) {
                    selectedDocImage = byteArray;
                    imageDoc.setImageBitmap(selectedImage);
                } else if (requestCode == PICK_CERT_IMAGE) {
                    selectedCertImage = byteArray;
                    imageCert.setImageBitmap(selectedImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveData() {
        String firstName = editFirstName.getText().toString();
        String middleName = editMiddleName.getText().toString();
        String lastName = editLastName.getText().toString();
        String dob = textDOB.getText().toString();

        boolean isSaved = dbHelper.savePersonalInfo(new P_info(firstName, middleName, lastName, dob, selectedDocImage, selectedCertImage));
        if (isSaved)
            Toast.makeText(this, "Personal information saved successfully!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Failed to save personal information.", Toast.LENGTH_SHORT).show();
    }

    public void deletedata() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Data")
                .setMessage("Are you sure you want to delete all the entered data?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    dbHelper.deletePersonalInfo();

                    editFirstName.setText("");
                    editMiddleName.setText("");
                    editLastName.setText("");
                    textDOB.setText("");
                    imageDoc.setImageResource(0);
                    imageCert.setImageResource(0);

                    selectedDocImage = null;
                    selectedCertImage = null;

                    Toast.makeText(this, "All data deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pinfo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        if(item.getItemId() == R.id.action_delete)
            deletedata();
        return super.onOptionsItemSelected(item);
    }
}
