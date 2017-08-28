package adbdatetime.utils;

import com.android.ddmlib.*;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static java.util.ResourceBundle.getBundle;

public class AdbUtils {

    private final static ResourceBundle resourceBundle = getBundle("adbdatetime.strings", Locale.ENGLISH);

    /**
     * Set datetime (adb shell date MMDDHHmmYY.ss)
     *
     * @param project
     * @param dateTime
     */
    public static void setTime(Project project, LocalDateTime dateTime) {
        AndroidDebugBridge androidBridge = AndroidSdkUtils.getDebugBridge(project);
        if (androidBridge == null) {
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(resourceBundle.getString("full_date_format"));
        DateTimeFormatter adbFormatter = DateTimeFormatter.ofPattern(resourceBundle.getString("adb_datetime_format"));
        System.out.println(dateTime.format(formatter));
        System.out.println(dateTime.format(adbFormatter));
        IDevice[] devices = androidBridge.getDevices();
        try {
            if (devices.length == 0) {
                NotificationUtils.showNotification(resourceBundle.getString("adb_no_device_connected"), NotificationType.INFORMATION, project);
                return;
            }

            if (!devices[0].isRoot()) {
                NotificationUtils.showNotification(resourceBundle.getString("adb_no_granted_root_permissions"), NotificationType.WARNING, project);
                return;
            }
            AdbReceiver receiver = new AdbReceiver();
            IDevice device = devices[0];
            device.executeShellCommand("date " + dateTime.format(adbFormatter), receiver, 15L, TimeUnit.SECONDS);
            for (String line : receiver.getAdbOutputLines()) {
                System.out.println(line);
            }

        } catch (TimeoutException | AdbCommandRejectedException | IOException | ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        }
    }


    /**
     * Grant  adb root permissions (adb root)
     *
     * @param project
     */
    public static void grantRootPermissions(Project project) {

        AndroidDebugBridge androidBridge = AndroidSdkUtils.getDebugBridge(project);
        if (androidBridge == null) {
            NotificationUtils.showNotification(resourceBundle.getString("adb_bridge_problem"), NotificationType.WARNING, project);
            return;
        }
        IDevice[] devices = androidBridge.getDevices();
        if (devices.length == 0) {
            NotificationUtils.showNotification(resourceBundle.getString("adb_no_device_connected"), NotificationType.INFORMATION, project);
            return;
        }
        IDevice device = devices[0];
        try {
            if (device.isRoot()) {
                NotificationUtils.showNotification(resourceBundle.getString("adb_already_granted_root_permissions"), NotificationType.INFORMATION, project);
                return;
            }
            if (device.root()) {
                NotificationUtils.showNotification(resourceBundle.getString("adb_grant_root_permissions_success"), NotificationType.INFORMATION, project);
            } else {
                NotificationUtils.showNotification(resourceBundle.getString("adb_grant_root_permissions_failed"), NotificationType.INFORMATION, project);
            }
        } catch (TimeoutException | AdbCommandRejectedException | IOException | ShellCommandUnresponsiveException e) {
            if (!checkRootPermissions(project)) {
                NotificationUtils.showNotification(resourceBundle.getString("adb_grant_root_permissions_error"), NotificationType.ERROR, project);
            } else {
                NotificationUtils.showNotification(resourceBundle.getString("adb_grant_root_permissions_success"), NotificationType.INFORMATION, project);
            }
        }

    }

    /**
     * Check root permissions
     *
     * @return
     */
    private static boolean checkRootPermissions(Project project) {
        AndroidDebugBridge androidBridge = AndroidSdkUtils.getDebugBridge(project);
        IDevice[] devices;
        boolean isRoot = false;

        // waiting for new devices list after run the adbd daemon with root
        do {
            devices = androidBridge.getDevices();
            System.out.println("Waiting for devices...");
        } while (devices.length == 0);

        // waiting for the device to be online
        do {
            System.out.println("Waiting device is offline...");
        } while (devices[0].isOffline());

        try {
            isRoot = devices[0].isRoot();
        } catch (TimeoutException | AdbCommandRejectedException | IOException | ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        }
        return isRoot;
    }

}
