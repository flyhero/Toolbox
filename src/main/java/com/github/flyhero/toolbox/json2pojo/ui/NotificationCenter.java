package com.github.flyhero.toolbox.json2pojo.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;

public class NotificationCenter {
    public static void info(String message) {
        sendNotification(message, NotificationType.INFORMATION);
    }

    public static void error(String message) {
        sendNotification(message, NotificationType.ERROR);
    }

    public static void sendNotification(String message, NotificationType notificationType) {
        if (message == null || message.trim().length() == 0) {
            return;
        }
        Notification notification = new Notification("com.dim.plugin.Gsonformat", "Gsonformat ", espaceString(message), notificationType);
        Notifications.Bus.notify(notification);
    }


    private static String espaceString(String string) {
        // replace with both so that it returns are preserved in the notification ballon and in the event log
        return string.replaceAll("\n", "\n<br />");
    }

    public static void sendNotificationForProject(String message, NotificationType notificationType, Project project) {
        if (message == null || message.trim().length() == 0) {
            return;
        }
        Notification notification = new Notification("com.dim.plugin.Gsonformat", "Gsonformat ", espaceString(message), notificationType);
        Notifications.Bus.notify(notification,project);
    }
}
