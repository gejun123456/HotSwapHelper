package org.hotswap.hotswaphelper.utils;

import com.sun.jna.Platform;

import java.io.File;
import java.nio.file.Files;

/**
 * @author bruce ge 2024/8/19
 */
public class MyUtils {

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
}
