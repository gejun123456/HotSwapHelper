package org.hotswap.hotswaphelper.actions;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import org.hotswap.hotswaphelper.HotSwapDebugExecutor;
import org.hotswap.hotswaphelper.IconUtils;
import org.hotswap.hotswaphelper.utils.GradleUtils;
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

        RunProfile profile = selectedConfiguration.getConfiguration();

        // 1. 如果是 Gradle 运行配置 (如 runIde, bootRun 等 Gradle 任务)
        if (GradleUtils.isGradleRunConfiguration(profile)) {
            // 标记本次启动是由 HotSwap Debug 触发
            if (profile instanceof UserDataHolder) {
                ((UserDataHolder) profile).putUserData(HOTSWAP_DEBUG_TRIGGERED, true);
            }
            // 使用 DefaultDebugExecutor，交由 IntelliJ 原生 GradleDebugRunner 正确挂载调试器
            Executor debugExecutor = DefaultDebugExecutor.getDebugExecutorInstance();
            ProgramRunnerUtil.executeConfiguration(selectedConfiguration, debugExecutor);
            return;
        }

        // 2. 如果是普通 Java / Spring Boot 模块配置 (ModuleRunProfile)
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
