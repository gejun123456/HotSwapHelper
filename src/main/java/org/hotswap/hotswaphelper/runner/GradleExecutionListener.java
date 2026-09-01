package org.hotswap.hotswaphelper.runner;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolder;
import org.hotswap.hotswaphelper.HotSwapDebugExecutor;
import org.hotswap.hotswaphelper.actions.HotSwapDebugTopAction;
import org.hotswap.hotswaphelper.utils.GradleUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Listens for Gradle task execution to dynamically inject HotSwapAgent init-script.
 * Ensures strict cleanup so normal Debug or Run is 100% clean and unpolluted.
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
            // 严格检查：只有通过 HotSwap Debug 触发，才注入 init script
            boolean isHotSwapTriggered = HotSwapDebugExecutor.EXECUTOR_ID.equals(executorId);
            if (runProfile instanceof UserDataHolder) {
                UserDataHolder holder = (UserDataHolder) runProfile;
                if (Boolean.TRUE.equals(holder.getUserData(HotSwapDebugTopAction.HOTSWAP_DEBUG_TRIGGERED))) {
                    isHotSwapTriggered = true;
                    // 用完即清理标记，避免影响后续普通启动
                    holder.putUserData(HotSwapDebugTopAction.HOTSWAP_DEBUG_TRIGGERED, null);
                }
            }

            if (runProfile instanceof ExternalSystemRunConfiguration) {
                Project project = env.getProject();
                ExternalSystemRunConfiguration externalConfig = (ExternalSystemRunConfiguration) runProfile;
                ExternalSystemTaskExecutionSettings settings = externalConfig.getSettings();
                if (settings != null) {
                    if (isHotSwapTriggered) {
                        File initScript = GradleUtils.getOrGenerateInitScript(project);
                        if (initScript.exists()) {
                            GradleUtils.patchGradleScriptParameters(settings, initScript);
                        }
                    } else {
                        // 如果是普通 Debug 或普通 Run：确保彻底清理 init-script，绝不污染官方启动！
                        GradleUtils.removeGradleScriptParameters(settings);
                    }
                }
            }
        }
    }

    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        // Gradle 进程一旦拉起，立即将 settings 中的 --init-script 清理复原，避免残留到配置中
        RunProfile runProfile = env.getRunProfile();
        if (runProfile instanceof ExternalSystemRunConfiguration) {
            ExternalSystemRunConfiguration externalConfig = (ExternalSystemRunConfiguration) runProfile;
            ExternalSystemTaskExecutionSettings settings = externalConfig.getSettings();
            if (settings != null) {
                GradleUtils.removeGradleScriptParameters(settings);
            }
        }
    }
}
