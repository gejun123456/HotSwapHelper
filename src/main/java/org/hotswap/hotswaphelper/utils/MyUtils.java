package org.hotswap.hotswaphelper.utils;

import com.sun.jna.Platform;

/**
 * @author bruce ge 2024/8/19
 */
public class MyUtils {

    public static String WINDOWSHOTSWAPFOLDER = "C:/tmp/HotSwapAgent/";
    public static String OTHERHOTSWAPFOLDER = "/tmp/HotSwapAgent/";


    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }


    public static String getHotSwapFolder() {
        return isWindows() ? WINDOWSHOTSWAPFOLDER : OTHERHOTSWAPFOLDER;
    }
}
