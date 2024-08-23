package org.hotswap.plugin;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.execution.dashboard.actions.ExecutorAction;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.NotNull;

/**
 * @author bruce ge 2024/8/20
 */
public class HotSwapDebugAction extends ExecutorAction {
    @Override
    protected Executor getExecutor() {
        return ExecutorRegistry.getInstance().getExecutorById(HotSwapDebugExecutor.EXECUTOR_ID);
    }

    @Override
    protected void update(@NotNull AnActionEvent e, boolean running) {
        Presentation presentation = e.getPresentation();
        if (running) {
            presentation.setText("reRun with hotswap");
            presentation.setDescription("reRun with hotswap");
            presentation.setIcon(AllIcons.Actions.RestartDebugger);
        }
        else {
            presentation.setText("debug with hotswap agent");
            presentation.setDescription("debug with hotswap agent");
            presentation.setIcon(IconUtils.hotSwapDebugIcon);
        }
    }
}
