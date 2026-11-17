package org.hotswap.hotswaphelper.actions;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.hotswap.hotswaphelper.IconUtils;
import org.hotswap.hotswaphelper.utils.HotSwapDebugLauncher;
import org.jetbrains.annotations.NotNull;

/**
 * Top-level action permanently visible on the New UI Main Toolbar.
 * Supports both standard Java/SpringBoot runs and Gradle task runs (e.g. runIde, bootRun).
 *
 * @author bruce ge
 */
public class HotSwapDebugTopAction extends AnAction implements DumbAware {

    public static final Key<Boolean> HOTSWAP_DEBUG_TRIGGERED = Key.create("HOTSWAP_DEBUG_TRIGGERED");

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        RunManager runManager = RunManager.getInstance(project);
        RunnerAndConfigurationSettings selectedConfiguration = runManager.getSelectedConfiguration();
        if (selectedConfiguration == null) {
            return;
        }

        HotSwapDebugLauncher.execute(project, selectedConfiguration);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setIcon(IconUtils.hotSwapDebugIcon);

        Project project = e.getProject();
        if (project == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }

        RunManager runManager = RunManager.getInstance(project);
        RunnerAndConfigurationSettings selectedConfiguration = runManager.getSelectedConfiguration();
        if (selectedConfiguration != null) {
            boolean supported = HotSwapDebugLauncher.getSupportedExecutor(selectedConfiguration.getConfiguration()) != null;
            presentation.setVisible(true);
            presentation.setEnabled(supported);
            presentation.setText("Debug with HotSwap: " + selectedConfiguration.getName());
            presentation.setDescription(supported
                    ? "Debug '" + selectedConfiguration.getName() + "' with HotSwap Agent"
                    : HotSwapDebugLauncher.getUnsupportedDescription(selectedConfiguration));
        } else {
            presentation.setVisible(true);
            presentation.setEnabled(false);
            presentation.setText("Debug with HotSwap Agent");
            presentation.setDescription("Debug with HotSwap Agent");
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
