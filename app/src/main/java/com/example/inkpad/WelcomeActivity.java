package com.example.inkpad;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.MotionEvent;
import android.text.InputType;
import android.widget.ViewFlipper;
import android.animation.ObjectAnimator;
import android.view.animation.Animation;
import android.view.View;
import android.widget.FrameLayout;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator;
import com.example.inkpad.memories.MemoriesActivity;
import com.example.inkpad.dairy.AddDiaryActivity;
import com.example.inkpad.notes.NotesActivity;
import com.example.inkpad.p_info.P_infoActivity;
import com.example.inkpad.register.RegisterDatabaseHelper;
import com.example.inkpad.reminder.ReminderListActivity;
import com.example.inkpad.todo.ToDoActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;

public class WelcomeActivity extends AppCompatActivity {

    private Button todoButton, diaryButton, reminderButton, memoriesButton, notesButton, p_infoButton, logoutButton, aboutUsButton;
    private RegisterDatabaseHelper dbHelper, dbHelper_data;
    private SharedPreferences sharedPreferences;
    private ViewFlipper viewFlipper;
    private LinearLayout dotsLayout;
    private int numberOfDots;
    private ImageView[] dots;
    private ConstraintLayout settingsMenu;
    private FrameLayout overlay;
    private TextView userIDTextView, emailIDTextView;
    private boolean isMenuOpen = false;
    private GestureDetector gestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settingsMenu = findViewById(R.id.settingsMenu);
        overlay = findViewById(R.id.overlay);
        logoutButton = findViewById(R.id.logoutButton);
        aboutUsButton = findViewById(R.id.aboutUsButton);
        userIDTextView = findViewById(R.id.userID);
        emailIDTextView = findViewById(R.id.emailID);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("userEmail", null);
        if (email == null) {
            Toast.makeText(this, "Email not found. Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper_data = new RegisterDatabaseHelper(this);
        Cursor cursor = dbHelper_data.getUserData(email);
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String emailID = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            userIDTextView.setText(username);
            emailIDTextView.setText(emailID);
        } else {
            Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
        }

        overlay.setOnTouchListener((v, event) -> {
            if (isMenuOpen) {
                int[] menuLocation = new int[2];
                settingsMenu.getLocationOnScreen(menuLocation);
                float menuLeft = menuLocation[0];
                float menuRight = menuLeft + settingsMenu.getWidth();

                if (event.getRawX() < menuLeft || event.getRawX() > menuRight) {
                    slideOutMenu();
                }
            }
            return true;
        });

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        aboutUsButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });

        dbHelper = new RegisterDatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        viewFlipper = findViewById(R.id.viewFlipper);
        dotsLayout = findViewById(R.id.dotsLayout);

        numberOfDots = viewFlipper.getChildCount();
        dots = new ImageView[numberOfDots];

        for (int i = 0; i < numberOfDots; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.inactive_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }

        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        viewFlipper.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.active_dot));
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out));
        viewFlipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Update dots when the animation finishes
                int displayedChild = viewFlipper.getDisplayedChild();
                updateDots(displayedChild);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        viewFlipper.startFlipping();

        todoButton = findViewById(R.id.todoButton);
        diaryButton = findViewById(R.id.diaryButton);
        reminderButton = findViewById(R.id.reminderButton);
        memoriesButton = findViewById(R.id.memoriesButton);
        notesButton = findViewById(R.id.notesButton);
        p_infoButton = findViewById(R.id.p_infoButton);

        todoButton.setOnClickListener(v -> {
            Toast.makeText(WelcomeActivity.this, "ToDo Clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WelcomeActivity.this, ToDoActivity.class);
            startActivity(intent);
        });
        diaryButton.setOnClickListener(v -> {
            Toast.makeText(WelcomeActivity.this, "Diary Clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WelcomeActivity.this, AddDiaryActivity.class);
            startActivity(intent);
        });
        reminderButton.setOnClickListener(v -> {
            Toast.makeText(WelcomeActivity.this, "Reminder Clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WelcomeActivity.this, ReminderListActivity.class);
            startActivity(intent);
        });
        notesButton.setOnClickListener(v -> {
            Toast.makeText(WelcomeActivity.this, "Notes Clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WelcomeActivity.this, NotesActivity.class);
            startActivity(intent);
        });
        memoriesButton.setOnClickListener(v -> {
            Toast.makeText(WelcomeActivity.this, "Memories Clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WelcomeActivity.this,MemoriesActivity.class);
            startActivity(intent);
        });
        p_infoButton.setOnClickListener(v -> showPasswordDialog());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showPasswordDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_p_info_access);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        EditText passwordEditText = dialog.findViewById(R.id.passwordEditText);
        Button submitButton = dialog.findViewById(R.id.submitButton);

        String email = sharedPreferences.getString("userEmail", null);
        if (email == null) {
            Toast.makeText(this, "Email not found. Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }
        String correctPassword = dbHelper.getPassword(email);

        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    return true;
                }
            }
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordEditText.setSelection(passwordEditText.getText().length());
                    return true;
                }
            }
            return false;
        });

        submitButton.setOnClickListener(v -> {
            String enteredPassword = passwordEditText.getText().toString().trim();
            if (enteredPassword.equals(correctPassword)) {
                dialog.dismiss();
                Intent intent = new Intent(WelcomeActivity.this, P_infoActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(WelcomeActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                passwordEditText.setText("");
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            if (isMenuOpen) {
                slideOutMenu();
            } else {
                slideInMenu();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void slideInMenu() {
        settingsMenu.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(settingsMenu, "translationX", settingsMenu.getWidth(), 0);
        animator.setDuration(300);
        animator.start();
        overlay.setVisibility(View.VISIBLE);
        isMenuOpen = true;
    }

    private void slideOutMenu() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(settingsMenu, "translationX", 0, settingsMenu.getWidth());
        animator.setDuration(300); // Animation duration in milliseconds
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                settingsMenu.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
            }
        });
        isMenuOpen = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private void updateDots(int currentSlide) {
        for (int i = 0; i < numberOfDots; i++) {
            if (i == currentSlide) {
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.active_dot));
            } else {
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.inactive_dot));
            }
        }
    }

    private class SwipeGestureListener extends SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.slide_in_left));
                        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.slide_out_right));
                        viewFlipper.showPrevious();
                        updateDots(viewFlipper.getDisplayedChild());
                        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.slide_in));
                        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.slide_out));
                        viewFlipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                updateDots(viewFlipper.getDisplayedChild());
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                    }
                    else {
                        viewFlipper.showNext();
                        updateDots(viewFlipper.getDisplayedChild());
                    }
                    return true;
                }
            }
            return false;
        }
    }
}
