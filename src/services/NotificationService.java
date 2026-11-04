package services;

import models.Notification;
import utils.FileManager;
import utils.IDGenerator;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service managing notifications dispatched to students, companies, and system users.
 */
public class NotificationService {

    private List<Notification> notifications;

    public NotificationService() {
        this.notifications = new ArrayList<>();
        reload();
    }

    public void reload() {
        this.notifications = FileManager.loadNotifications();
    }

    public void saveAll() {
        FileManager.saveNotifications(this.notifications);
    }

    public Notification sendNotification(String recipientId, String title, String message) {
        String notifId = IDGenerator.generateNotificationId(this.notifications.size());
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

        Notification notif = new Notification(notifId, recipientId, title, message, timestamp);
        this.notifications.add(0, notif); // Prepend to show newest first
        saveAll();
        return notif;
    }

    public List<Notification> getNotificationsForUser(String userId) {
        List<Notification> userNotifs = new ArrayList<>();
        for (Notification n : this.notifications) {
            if (n.getRecipientId().equalsIgnoreCase(userId) || n.getRecipientId().equalsIgnoreCase("ALL")) {
                userNotifs.add(n);
            }
        }
        return userNotifs;
    }

    public int getUnreadCount(String userId) {
        int count = 0;
        for (Notification n : getNotificationsForUser(userId)) {
            if (!n.isRead()) {
                count++;
            }
        }
        return count;
    }

    public void markAllAsRead(String userId) {
        for (Notification n : this.notifications) {
            if (n.getRecipientId().equalsIgnoreCase(userId) || n.getRecipientId().equalsIgnoreCase("ALL")) {
                n.setRead(true);
            }
        }
        saveAll();
    }
}
