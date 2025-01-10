package org.hotswap.hotswaphelper.utils;

import com.google.common.collect.Lists;
import com.sun.jna.Platform;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * @author bruce ge 2024/8/19
 */
public class MyUtils {

    public static List<String> allOpens = Lists.newArrayList("--add-opens=java.base/sun.nio.ch=ALL-UNNAMED"
            ,"--add-opens=java.base/java.lang=ALL-UNNAMED","--add-opens=java.base/java.lang.reflect=ALL-UNNAMED"
            ,"--add-opens=java.base/java.io=ALL-UNNAMED","--add-opens=java.base/sun.security.action=ALL-UNNAMED",
            "--add-opens=java.base/jdk.internal.reflect=ALL-UNNAMED","--add-opens=java.base/java.net=ALL-UNNAMED");

    public static String WINDOWSHOTSWAPFOLDER = "C:/tmp/HotSwapAgent/";
    public static String OTHERHOTSWAPFOLDER = "/tmp/HotSwapAgent/";


    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }


    public static File getHotSwapFolder() {
        //todo use user path instead of tmp file
        String property = System.getProperty("user.home");
        File file = new File(property, ".hotswap");
        return file;
    }

    public static File getHotSwapJarPath(){
        return new File(getHotSwapFolder(),"hotswap-agent.jar");
    }

    public static File getDebuggerAgentFile(){
        return new File(getHotSwapFolder(),"debugger-agent.jar");
    }
}
