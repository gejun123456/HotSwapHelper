package org.hotswap.plugin.runner;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.hotswap.plugin.HotSwapDebugExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author bruce ge 2024/8/20
 */
public class HotSwapDebugRunner extends GenericDebuggerRunner implements MyRunner{


    @Override
    public void patch(@NotNull JavaParameters javaParameters, @Nullable RunnerSettings settings, @NotNull RunProfile runProfile, boolean beforeExecution) throws ExecutionException {
        super.patch(javaParameters, settings, runProfile, beforeExecution);
        patchProfile(javaParameters, runProfile);
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(HotSwapDebugExecutor.EXECUTOR_ID) &&
               profile instanceof ModuleRunProfile &&
               !(profile instanceof RunConfigurationWithSuppressedDefaultDebugAction);
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        if (checkJdk(environment)) return;
        super.execute(environment);
    }
}
