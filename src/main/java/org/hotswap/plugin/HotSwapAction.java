package org.hotswap.plugin;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.execution.dashboard.actions.ExecutorAction;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.NotNull;

/**
 * @author bruce ge 2024/8/19
 */
public class HotSwapAction extends ExecutorAction {
    @Override
    protected Executor getExecutor() {
        return ExecutorRegistry.getInstance().getExecutorById("HotSwap Executor");
    }

    @Override
    protected void update(@NotNull AnActionEvent e, boolean running) {
        Presentation presentation = e.getPresentation();
        if (running) {
            presentation.setText("reRun with hotswap");
            presentation.setDescription("reRun with hotswap");
            presentation.setIcon(AllIcons.Actions.Restart);
        }
        else {
            presentation.setText("Run with hotswap agent");
            presentation.setDescription("Run with hotswap agent");
            presentation.setIcon(IconUtils.hotswapIcon);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //check if the jdk is from decvm or jbr. // when first run the action, ask user to set with the jdk?
        //get the jre path.
        super.actionPerformed(e);
    }
}
