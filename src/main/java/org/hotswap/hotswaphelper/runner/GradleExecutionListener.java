package org.hotswap.hotswaphelper.runner;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.project.Project;
import org.hotswap.hotswaphelper.HotSwapDebugExecutor;
import org.hotswap.hotswaphelper.utils.GradleUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Listens for Gradle task execution to dynamically inject HotSwapAgent init-script.
 *
 * @author bruce ge
 */
public class GradleExecutionListener implements ExecutionListener {

    @Override
    public void processStarting(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        RunProfile runProfile = env.getRunProfile();

        // 1. 如果是用 IDEA 跑 (标准 Java / Spring Boot 启动类)，直接返回，不走 Gradle 注入流程
        if (GradleUtils.isIdeaJavaRunConfiguration(runProfile)) {
            return;
        }

        // 2. 如果是用 Gradle 跑 (GradleRunConfiguration / ExternalSystemRunConfiguration)
        if (GradleUtils.isGradleRunConfiguration(runProfile)) {
            // 仅在用户明确使用 HotSwapDebugExecutor 时才动态注入
            boolean isHotSwapDebug = HotSwapDebugExecutor.EXECUTOR_ID.equals(executorId);

            if (isHotSwapDebug && runProfile instanceof ExternalSystemRunConfiguration) {
                Project project = env.getProject();
                ExternalSystemRunConfiguration externalConfig = (ExternalSystemRunConfiguration) runProfile;
                ExternalSystemTaskExecutionSettings settings = externalConfig.getSettings();
                if (settings != null) {
                    File initScript = GradleUtils.getOrGenerateInitScript(project);
                    if (initScript.exists()) {
                        GradleUtils.patchGradleScriptParameters(settings, initScript);
                    }
                }
            }
        }
    }
}
