package com.example.inkpad;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    private static final String INSTAGRAM_URL_SHIVAM = "https://www.instagram.com/shibbs_075";
    private static final String INSTAGRAM_URL_KUNJ = "https://www.instagram.com/kunj_account";
    private static final String EMAIL_SHIVAM = "officialshibbs@gmail.com";
    private static final String EMAIL_KUNJ = "sonikunj0208@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ImageView instagramLogo = findViewById(R.id.instagramLogo);
        ImageView emailLogo = findViewById(R.id.emailLogo);

        instagramLogo.setOnClickListener(v -> showInstagramOptions());
        emailLogo.setOnClickListener(v -> showEmailOptions());
    }

    private void showInstagramOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Instagram Account")
                .setItems(new CharSequence[]{"Shivam Bhat", "Kunj Soni (not available)"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            openInstagram(INSTAGRAM_URL_SHIVAM);
                        } else if (which == 1) {
                            openInstagram(INSTAGRAM_URL_KUNJ);
                        }
                    }
                });
        builder.create().show();
    }

    private void showEmailOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Email Address")
                .setItems(new CharSequence[]{"Shivam Bhat", "Kunj Soni"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            sendEmail(EMAIL_SHIVAM);
                        } else if (which == 1) {
                            sendEmail(EMAIL_KUNJ);
                        }
                    }
                });
        builder.create().show();
    }

    private void openInstagram(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.instagram.android");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    private void sendEmail(String emailAddress) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");  // MIME type for email apps
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});  // Set recipient email address
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry from the App");  // Optional: Set default subject
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");  // Optional: Set default body text

        try {
            startActivity(Intent.createChooser(emailIntent, "Choose an email client:"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show();
        }
    }
}
