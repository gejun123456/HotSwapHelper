package org.hotswap.hotswaphelper.utils;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.util.text.StringUtil;
import org.hotswap.hotswaphelper.HotSwapDebugExecutor;
import org.hotswap.hotswaphelper.actions.HotSwapDebugTopAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Routes only configurations with a matching HotSwap-capable runner. */
public final class HotSwapDebugLauncher {
    private static final Logger LOG = Logger.getInstance(HotSwapDebugLauncher.class);

    private HotSwapDebugLauncher() {
    }

    @Nullable
    public static Executor getSupportedExecutor(@NotNull RunProfile profile) {
        Executor executor = GradleUtils.isGradleRunConfiguration(profile)
                ? DefaultDebugExecutor.getDebugExecutorInstance()
                : ExecutorRegistry.getInstance().getExecutorById(HotSwapDebugExecutor.EXECUTOR_ID);
        return executor != null && ProgramRunner.getRunner(executor.getId(), profile) != null ? executor : null;
    }

    @NotNull
    public static String getUnsupportedDescription(@NotNull RunnerAndConfigurationSettings settings) {
        return "HotSwap Debug is unavailable for '" + settings.getName() + "' ("
                + settings.getType().getDisplayName() + "). No compatible runner is registered. "
                + "Export VM options from Settings > HotSwapHelper, add them to the application's JVM options, "
                + "and use the configuration's standard Debug action.";
    }

    public static void execute(@NotNull Project project, @NotNull RunnerAndConfigurationSettings settings) {
        RunProfile profile = settings.getConfiguration();
        // Recheck at execution time: the selected configuration or registered runners may have changed.
        Executor executor = getSupportedExecutor(profile);
        if (executor == null) {
            LOG.info("No HotSwap Debug runner: configurationType=" + settings.getType().getId()
                    + ", profileClass=" + profile.getClass().getName()
                    + ", ideBuild=" + ApplicationInfo.getInstance().getBuild());
            new Notification("hot.swap.notification.balloon", "HotSwap Debug unavailable",
                    StringUtil.escapeXmlEntities(getUnsupportedDescription(settings)), NotificationType.WARNING)
                    .notify(project);
            return;
        }

        if (GradleUtils.isGradleRunConfiguration(profile) && profile instanceof UserDataHolder) {
            // Only mark a launch after verifying that the native Gradle debug runner exists.
            ((UserDataHolder) profile).putUserData(HotSwapDebugTopAction.HOTSWAP_DEBUG_TRIGGERED, true);
        }
        ProgramRunnerUtil.executeConfiguration(settings, executor);
    }
}
