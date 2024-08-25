package org.hotswap.plugin.runner;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.JavaTestConfigurationBase;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import org.hotswap.plugin.CheckResult;
import org.hotswap.plugin.JdkManager;
import org.hotswap.plugin.ui.JdkNotSupportedDialog;
import org.hotswap.plugin.utils.MyUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;

/**
 * @author bruce ge 2024/8/20
 */
public interface MyRunner {
    static void patchProfile(@NotNull JavaParameters javaParameters, @NotNull RunProfile runProfile) {
        if (runProfile instanceof RunConfiguration) {
            //get current jdk to check.
            Sdk jdk1 = javaParameters.getJdk();
            String homePath1 = jdk1.getHomePath();
            //todo maybe parse it from versionString?
            CheckResult result = JdkManager.checkJdkHome(homePath1);
            Sdk jdk = javaParameters.getJdk();
            //get user jdk version. if this is from dcevm jdk, add the agent to it.
//            String jdkPath = javaParameters.getJdkPath();
            String homePath = jdk.getHomePath();
            String versionString = jdk.getVersionString();
            //check if the jdk is from jetbrain.
            //check the version and replace with the decvm jdk.
            //todo jvm 参数未来可以自定义
            if (!(runProfile instanceof JavaTestConfigurationBase)) {
                int javaVersion = result.getJavaVersion();
                if(javaVersion==8) {
                    File agentFile = new File(MyUtils.getHotSwapFolder() + "hotswap-agent.jar");
                    if (!agentFile.exists()) {
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Messages.showErrorDialog(((RunConfiguration) runProfile).getProject(),
                                        "HotSwap agent jar not found,please add issue to github" +
                                        "https://github.com/gejun123456/HotSwapIntellij", "Error");
                            }
                        });
                        return;
                    } else {
                        javaParameters.getVMParametersList().addParametersString("-XXaltjvm=dcevm");
                        javaParameters.getVMParametersList().addParametersString("-javaagent:" + agentFile.getPath());
                    }
                } else if(javaVersion==11){
                    javaParameters.getVMParametersList().addParametersString("-XX:HotswapAgent=fatjar");
                } else if(javaVersion>=17){
                    javaParameters.getVMParametersList().addParametersString("-XX:+AllowEnhancedClassRedefinition");
                    javaParameters.getVMParametersList().addParametersString("-XX:HotswapAgent=fatjar");
                    //add --add-opens
                    javaParameters.getVMParametersList().addParametersString("--add-opens java.base/sun.nio.ch=ALL-UNNAMED");
                    javaParameters.getVMParametersList().addParametersString("--add-opens=java.base/java.lang=ALL-UNNAMED");
                    javaParameters.getVMParametersList().addParametersString("--add-opens=java.base/java.lang.reflect=ALL-UNNAMED");
                    javaParameters.getVMParametersList().addParametersString("--add-opens=java.base/java.io=ALL-UNNAMED");
                }
            }
        }
    }

    static boolean checkJdk(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        RunProfileState currentState = environment.getState();
        if (currentState == null) {
            return true;
        }
        if(currentState instanceof JavaCommandLine){
            JavaCommandLine theline = (JavaCommandLine) currentState;
            JavaParameters javaParameters = theline.getJavaParameters();
            Sdk jdk = javaParameters.getJdk();
            String homePath = jdk.getHomePath();
            if(homePath==null){
                throw new CantRunException("please select jdk");
            }
            //add setting to not check jdk.
            CheckResult result = JdkManager.checkJdkHome(homePath);
            if(!result.isHasFound()){
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JdkNotSupportedDialog dialog = new JdkNotSupportedDialog(environment.getProject(),true,result.getErrorText());
                        dialog.show();
                    }
                });
                return true;
            }
            //copy the hotwap agent file to the folder if 11 or 17.
            if(result.getJavaVersion()>8){
                try {
                    // make sure to use with the least version in plugin resource.
                    // user can config it later if needed.
                    //copy the hotswap agent to the place.
                    File agentFile = new File(homePath, "lib/hotswap/hotswap-agent.jar");
                    //copy the stream to the file.
                    if (agentFile.exists()) {
                        agentFile.delete();
                    }
                    InputStream resourceAsStream = JdkManager.class.getClassLoader().getResourceAsStream("hotswap-agent.jar");
                    //copy resource to the file
                    FileUtils.copyInputStreamToFile(resourceAsStream, agentFile);
                    resourceAsStream.close();
                }catch (Exception e){
                    //ignore this.
                }
            }
        }
        return false;
    }
}
