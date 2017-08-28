package adbdatetime.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import adbdatetime.ui.InputDialog;

public class AdbDateTimeSetActionClass extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        InputDialog dialog = new InputDialog(project);
        dialog.show();
    }
}
