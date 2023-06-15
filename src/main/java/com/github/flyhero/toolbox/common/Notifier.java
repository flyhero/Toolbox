package com.github.flyhero.toolbox.common;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class Notifier {


    public static void notifyError(String content, Project project) {
        notify(content, NotificationType.ERROR, project);
    }

    public static void notifyWarn(String content, Project project) {
        notify(content, NotificationType.WARNING, project);
    }

    public static void notifyInfo(String content, Project project) {
        notify(content, NotificationType.INFORMATION, project);
    }

    public static void notify(String content, NotificationType type, Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("Custom Notification Group")
                .createNotification(content, type)
                .notify(project);
    }
}
