package models;

import java.io.Serializable;

/**
 * Model representing system notifications for students and users.
 */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private String notificationId;
    private String recipientId; // User ID (Student ID or "ALL")
    private String title;
    private String message;
    private String timestamp;
    private boolean read;

    public Notification() {
        this.read = false;
    }

    public Notification(String notificationId, String recipientId, String title,
                        String message, String timestamp) {
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.read = false;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s (%s)", read ? "READ" : "NEW", title, message, timestamp);
    }
}
