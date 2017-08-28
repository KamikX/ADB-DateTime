package adbdatetime.utils;

import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import java.util.Locale;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;


public class NotificationUtils {
    private final static ResourceBundle resourceBundle = getBundle("adbdatetime.strings", Locale.ENGLISH);
    private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.balloonGroup(resourceBundle.getString("adb_datetime_title"));

    static void showNotification(String content, NotificationType type, Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Notification notification = NOTIFICATION_GROUP.createNotification(resourceBundle.getString("adb_datetime_title"), content, type, null);
            Notifications.Bus.notify(notification, project);
        });
    }
}
