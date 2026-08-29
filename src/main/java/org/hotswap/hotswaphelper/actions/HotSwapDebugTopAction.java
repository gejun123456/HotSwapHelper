package org.hotswap.hotswaphelper.actions;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.hotswap.hotswaphelper.HotSwapDebugExecutor;
import org.hotswap.hotswaphelper.IconUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Top-level action permanently visible on the New UI Main Toolbar.
 *
 * @author bruce ge
 */
public class HotSwapDebugTopAction extends AnAction implements DumbAware {

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

        Executor executor = ExecutorRegistry.getInstance().getExecutorById(HotSwapDebugExecutor.EXECUTOR_ID);
        if (executor != null) {
            ProgramRunnerUtil.executeConfiguration(selectedConfiguration, executor);
        }
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
            presentation.setEnabledAndVisible(true);
            presentation.setText("Debug with HotSwap: " + selectedConfiguration.getName());
            presentation.setDescription("Debug '" + selectedConfiguration.getName() + "' with HotSwap Agent");
        } else {
            presentation.setEnabled(false);
            presentation.setText("Debug with HotSwap Agent");
            presentation.setDescription("Debug with HotSwap Agent");
        }
    }
}
