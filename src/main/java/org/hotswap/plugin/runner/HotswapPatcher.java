package org.hotswap.plugin.runner;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import org.hotswap.plugin.HotSwapDebugExecutor;

/**
 * @author bruce ge 2024/8/20
 */
public class HotswapPatcher extends JavaProgramPatcher {
    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        if(executor instanceof HotSwapDebugExecutor){
            MyRunner.patchProfile(javaParameters, configuration);;
        }
    }
}
