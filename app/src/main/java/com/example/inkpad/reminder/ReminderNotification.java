package com.example.inkpad.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

public class ReminderNotification {

    public static void scheduleNotification(Context context, String title, String message, long timeInMillis, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("notificationId", notificationId);

        // Use FLAG_IMMUTABLE for Android 31 and above
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        // Check for exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager1.canScheduleExactAlarms()) {
                // Redirect to the settings screen where the user can grant exact alarm permissions
                Intent intent1 = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // Add this line to fix the error
                context.startActivity(intent1);
                return;  // Exit early if permission isn't granted
            }
        }
        // Set the exact alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void cancelNotification(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        pendingIntent.cancel();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
