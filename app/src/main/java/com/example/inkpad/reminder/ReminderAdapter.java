package com.example.inkpad.reminder;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.inkpad.R;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminders;
    private Context context;

    public ReminderAdapter(Context context, List<Reminder> reminders) {
        this.context = context;
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reminder_list_item, parent, false);
        return new ReminderViewHolder(view);
    }

    private String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.nameTextView.setText(reminder.getName());
        holder.messageTextView.setText(reminder.getMessage());
        holder.dateTextView.setText(reminder.getDate());
        holder.timeTextView.setText(formatTime(reminder.getTimeInMillis()));
        holder.notificationSwitch.setChecked(true); // Assuming reminder is active

        holder.notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                if (isChecked) {
                    // Schedule the notification
                    ReminderNotification.scheduleNotification(context.getApplicationContext(),
                            reminder.getName(),
                            reminder.getMessage(),
                            reminder.getTimeInMillis(),
                            reminder.getNotificationId());
                } else {
                    // Cancel the notification
                    ReminderNotification.cancelNotification(context.getApplicationContext(), reminder.getNotificationId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Handle long click for context menu
        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(v, reminder, position);
            return true; // Return true to indicate the event is handled
        });
    }

    private void showPopupMenu(View view, Reminder reminder, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenu().add("Delete"); // Add "Delete" option

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Delete")) {
                showDeleteConfirmationDialog(reminder, position);
            }
            return true; // Indicate that the menu item click was handled
        });

        popupMenu.show(); // Show the popup menu
    }

    private void showDeleteConfirmationDialog(Reminder reminder, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Reminder")
                .setMessage("Are you sure you want to delete this reminder?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Remove the reminder from the database
                    ReminderDatabaseHelper databaseHelper = new ReminderDatabaseHelper(context);
                    boolean isDeleted = databaseHelper.deleteReminder(reminder.getNotificationId());

                    if (isDeleted) {
                        // Remove the reminder from the list
                        reminders.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, reminders.size()); // Update the remaining items
                        Toast.makeText(context, "Reminder deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error deleting reminder", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null) // Dismiss the dialog
                .show();
    }

    public void refreshReminders() {
        ReminderDatabaseHelper databaseHelper = new ReminderDatabaseHelper(context);
        reminders.clear(); // Clear the current list
        reminders.addAll(databaseHelper.getAllReminders()); // Fetch all reminders from the database
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView messageTextView;
        TextView dateTextView;
        TextView timeTextView;
        Switch notificationSwitch;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.reminder_name);
            messageTextView = itemView.findViewById(R.id.reminder_message);
            dateTextView = itemView.findViewById(R.id.reminder_time);
            timeTextView = itemView.findViewById(R.id.reminder_date);
            notificationSwitch = itemView.findViewById(R.id.notification_switch);
        }
    }
}
