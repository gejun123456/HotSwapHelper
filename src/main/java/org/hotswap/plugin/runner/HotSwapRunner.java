package org.hotswap.plugin.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import org.hotswap.plugin.HotSwapExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author bruce ge 2024/8/19
 */
public class HotSwapRunner extends DefaultJavaProgramRunner {

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(HotSwapExecutor.EXECUTOR_ID) &&
               profile instanceof ModuleRunProfile &&
               !(profile instanceof RunConfigurationWithSuppressedDefaultRunAction);

    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        if (MyRunner.checkJdk(environment)) return;
        super.execute(environment);
    }

    @Override
    public void patch(@NotNull JavaParameters javaParameters, @Nullable RunnerSettings settings, @NotNull RunProfile runProfile, boolean beforeExecution) {
        super.patch(javaParameters, settings, runProfile, beforeExecution);
        MyRunner.patchProfile(javaParameters, runProfile);
    }

}
