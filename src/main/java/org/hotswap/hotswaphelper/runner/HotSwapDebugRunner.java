package org.hotswap.hotswaphelper.runner;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.text.VersionComparatorUtil;
import org.hotswap.hotswaphelper.CheckResult;
import org.hotswap.hotswaphelper.HotSwapDebugExecutor;
import org.hotswap.hotswaphelper.JdkManager;
import org.hotswap.hotswaphelper.settings.HotSwapHelperPluginSettingsProvider;
import org.hotswap.hotswaphelper.utils.MyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * @author bruce ge 2024/8/20
 */
public class HotSwapDebugRunner extends GenericDebuggerRunner implements MyRunner{


    @Override
    public void patch(@NotNull JavaParameters javaParameters, @Nullable RunnerSettings settings, @NotNull RunProfile runProfile, boolean beforeExecution) throws ExecutionException {
        super.patch(javaParameters, settings, runProfile, beforeExecution);
        MyRunner.patchProfile(javaParameters, runProfile);
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(HotSwapDebugExecutor.EXECUTOR_ID) &&
               profile instanceof ModuleRunProfile &&
               !(profile instanceof RunConfigurationWithSuppressedDefaultDebugAction);
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        if (MyRunner.checkJdk(environment)) return;
        super.execute(environment);
    }

    @Override
    protected @Nullable RunContentDescriptor attachVirtualMachine(RunProfileState state, @NotNull ExecutionEnvironment env, RemoteConnection connection, boolean pollConnection) throws ExecutionException {
        int versionName = ApplicationInfo.getInstance().getBuild().getBaselineVersion();
        //check if version > 2024.3
        if(versionName>=243) {
            if (state instanceof JavaCommandLine) {
                JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();
                String jdkPath = javaParameters.getJdkPath();
                Project project1 = env.getProject();
                boolean dontCheckJdk = HotSwapHelperPluginSettingsProvider.Companion.getInstance(project1).getCurrentState().getDontCheckJdk();
                CheckResult checkResult = JdkManager.checkJdkHome(jdkPath,dontCheckJdk);
                //make sure if < 17
                if(checkResult.getJavaVersion()<17) {
                    ParametersList vmParametersList = javaParameters.getVMParametersList();
                    List<String> parameters = vmParametersList.getParameters();
                    for (String parameter : parameters) {
                        if (parameter.contains("-javaagent:") && parameter.contains("debugger-agent.jar")) {
                            File agentFile = MyUtils.getDebuggerAgentFile();
                            vmParametersList.replaceOrAppend(parameter, "-javaagent:" + agentFile.getAbsolutePath());
                            break;
                        }
                    }
                }
            }
        }
        return super.attachVirtualMachine(state, env, connection, pollConnection);
    }

    @Override
    protected @Nullable RunContentDescriptor attachVirtualMachine(RunProfileState state, @NotNull ExecutionEnvironment env, RemoteConnection connection, long pollTimeout) throws ExecutionException {
        return super.attachVirtualMachine(state, env, connection, pollTimeout);
    }
}
