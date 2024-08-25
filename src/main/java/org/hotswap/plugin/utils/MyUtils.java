package org.hotswap.plugin.utils;

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