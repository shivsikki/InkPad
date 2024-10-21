package com.example.inkpad.reminder;

public class Reminder {
    private String name;
    private String message;
    private long timeInMillis;
    private String date;
    private int notificationId; // Added for unique notification ID

    public Reminder(String name, String message, long timeInMillis, String date, int notificationId) {
        this.name = name;
        this.message = message;
        this.timeInMillis = timeInMillis;
        this.date = date;
        this.notificationId = notificationId;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public String getDate() {
        return date;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

}
