package adbdatetime.ui;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import adbdatetime.utils.AdbUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;


public class InputDialog extends DialogWrapper {

    private JPanel mainPanel;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private Project project;
    private final static int iconSize = 18;
    private final static int iconPadding = 14;
    private static ResourceBundle resourceBundle = getBundle("adbdatetime.strings", Locale.ENGLISH);

    public InputDialog(@Nullable Project project) {
        super(project, true);
        setTitle(resourceBundle.getString("adb_datetime_title"));
        init();
        intComponents();
        setResizable(false);
        setModal(false);
        this.project = project;

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return this.mainPanel;
    }


    @NotNull
    @Override
    protected Action[] createActions() {
        // set custom actions
        Action[] actions = new Action[3];
        actions[2] = new ResetAction(resourceBundle.getString("reset"));
        actions[1] = new SetAction(resourceBundle.getString("set"));
        actions[0] = new RootAction(resourceBundle.getString("adb_grant_root_permissions"));
        return actions;
    }

    /**
     * Initial components
     */
    private void intComponents() {

        // images
        URL timeIconURL = InputDialog.class.getResource("/images/ic_time.png");
        URL dateIconURL = InputDialog.class.getResource("/images/ic_date.png");
        Image timeImage = Toolkit.getDefaultToolkit().getImage(timeIconURL);
        Image dateImage = Toolkit.getDefaultToolkit().getImage(dateIconURL);
        ImageIcon timeIcon = new ImageIcon(timeImage.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        ImageIcon dateIcon = new ImageIcon(dateImage.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));

        // time picker
        TimePickerSettings timeSettings = new TimePickerSettings();
        timeSettings.setAllowEmptyTimes(false);
        timeSettings.setInitialTimeToNow();
        timeSettings.use24HourClockFormat();
        timeSettings.generatePotentialMenuTimes(TimePickerSettings.TimeIncrement.FiveMinutes, null, null);
        this.timePicker = new TimePicker(timeSettings);
        JButton timePickerButton = this.timePicker.getComponentToggleTimeMenuButton();
        timePickerButton.setText("");
        timePickerButton.setIcon(timeIcon);
        Dimension timeButtonSize = new Dimension(timeIcon.getIconWidth() + iconPadding, timeIcon.getIconHeight() + iconPadding);
        timePickerButton.setPreferredSize(timeButtonSize);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.weightx = 1;
        this.mainPanel.add(this.timePicker, c);

        // date picker
        DatePickerSettings datePickerSettings = new DatePickerSettings(new Locale("en"));
        datePickerSettings.setAllowEmptyDates(false);
        this.datePicker = new DatePicker();
        this.datePicker.setDateToToday();
        this.datePicker.setSettings(datePickerSettings);
        JButton datePickerButton = this.datePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(dateIcon);
        Dimension dateButtonSize = new Dimension(dateIcon.getIconWidth() + iconPadding, dateIcon.getIconHeight() + iconPadding);
        datePickerButton.setPreferredSize(dateButtonSize);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        this.mainPanel.add(this.datePicker, c);

    }

    /**
     * Get DateTime
     *
     * @return
     */
    private LocalDateTime getDateTime() {
        return LocalDateTime.of(this.datePicker.getDate(), this.timePicker.getTime());
    }


    /**
     * Reset DateTime
     */
    private void resetDateTime() {
        this.datePicker.setDateToToday();
        this.timePicker.setTimeToNow();
        AdbUtils.setTime(this.project, getDateTime());
    }


    /**
     * Set action
     */
    private class SetAction extends DialogWrapperAction {

        SetAction(@NotNull String name) {
            super(name);
        }

        @Override
        protected void doAction(ActionEvent e) {
            AdbUtils.setTime(project, getDateTime());
        }
    }


    /**
     * Reset action
     */
    private class ResetAction extends DialogWrapperAction {

        ResetAction(@NotNull String name) {
            super(name);
        }

        @Override
        protected void doAction(ActionEvent e) {
            resetDateTime();
        }
    }

    /**
     * Root permission action
     */
    private class RootAction extends DialogWrapperAction {

        RootAction(@NotNull String name) {
            super(name);
        }

        @Override
        protected void doAction(ActionEvent e) {
            AdbUtils.grantRootPermissions(project);
        }
    }
}
