package org.hotswap.hotswaphelper;

import com.intellij.util.lang.JavaVersion;
import org.hotswap.hotswaphelper.utils.MyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author bruce ge 2024/8/19
 */
public class JdkManager {
    public static CheckResult checkJdkHome(String jdkhome,boolean dontCheckJdk){
        //get java version from jdk home.
        //get the release file.
        String downloadJdkInGithubRelease = " please download jdk in github release";
        String theVersion = "1.8";
        File release = new File(jdkhome, "release");
        CheckResult result = new CheckResult();
        if (release.exists()) {
            //read the file.
            //read the file
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(release)) {
                properties.load(fis);
                String javaVersion = properties.getProperty("JAVA_VERSION");
                JavaVersion parse = JavaVersion.parse(javaVersion);
                int feature = parse.feature;
                String implementor = properties.getProperty("IMPLEMENTOR");
                if(implementor!=null&&implementor.toLowerCase().contains("jetbrain")){
                    result.setJbr(true);
                }
                if(feature<8){
                    //not supported.
                    result.setHasFound(false);
                    result.setErrorText("before jdk1.8 is not supported");
                    return result;
                }
                String osArch = properties.getProperty("OS_ARCH");
                if (osArch == null) {
                    osArch = System.getProperty("os.arch", "");
                }
                String osName = properties.getProperty("OS_NAME");
                if (osName == null) {
                    osName = System.getProperty("os.name", "");
                }
                boolean isMac = osName.toLowerCase().contains("darwin") || System.getProperty("os.name", "").toLowerCase().contains("mac");
                boolean isArm64 = osArch.equalsIgnoreCase("aarch64") || osArch.equalsIgnoreCase("arm64")
                        || System.getProperty("os.arch", "").equalsIgnoreCase("aarch64") || System.getProperty("os.arch", "").equalsIgnoreCase("arm64");

                if (feature == 8) {
                    result.setJavaVersion(8);
                    if (isMac && isArm64) {
                        // On Apple Silicon (M1/M2/M3) Mac, native ARM64 JDK 8 does not support x86_64 DCEVM
                        result.setDcevm(false);
                        if (dontCheckJdk) {
                            result.setHasFound(true);
                            result.setErrorText("");
                            return result;
                        } else {
                            result.setHasFound(false);
                            result.setErrorText("DCEVM-8 does not support native Mac Apple Silicon (ARM64).\n\n" +
                                    "Recommended Solutions:\n" +
                                    "1. Upgrade to JetBrains Runtime (JBR 17/21) which natively supports Apple Silicon.\n" +
                                    "2. Or enable 'Don't check JDK' in HotSwap Helper settings to use HotSwapAgent with standard JDK 8 (supports XML & method-body hot reload).\n" +
                                    "3. Or use an x86_64 JDK 8 via Rosetta 2.");
                            return result;
                        }
                    }

                    boolean dcevmFound = false;
                    if (MyUtils.isWindows()) {
                        File file = new File(jdkhome, "jre/bin/dcevm");
                        dcevmFound = file.exists();
                    } else {
                        File thepath1 = new File(jdkhome, "jre/lib/dcevm");
                        File thePath2 = new File(jdkhome, "jre/lib/amd64/dcevm");
                        dcevmFound = thepath1.exists() || thePath2.exists();
                    }

                    result.setDcevm(dcevmFound);
                    if (dontCheckJdk) {
                        result.setHasFound(true);
                        result.setErrorText("");
                        return result;
                    }

                    if (dcevmFound) {
                        result.setHasFound(true);
                        result.setErrorText("");
                        return result;
                    } else {
                        result.setHasFound(false);
                        result.setErrorText("dcevm not found in your jdk home: " + jdkhome + "," + downloadJdkInGithubRelease);
                        return result;
                    }
                }
                if (dontCheckJdk) {
                    result.setHasFound(true);
                    result.setJavaVersion(feature);
                    return result;
                }
                File file = new File(jdkhome, "lib/hotswap/hotswap-agent.jar");
                    if(feature == 11){
                        if(file.exists()){
                            result.setHasFound(true);
                            result.setJavaVersion(11);
                            return result;
                        } else {
                            if(result.isJbr()){
                                result.setHasFound(true);
                                result.setJavaVersion(11);
                                return result;
                            }
                            result.setHasFound(false);
                            result.setErrorText("hotSwap file not exist in your jdk home," +
                                                "the path is" + file.getAbsolutePath() + downloadJdkInGithubRelease);
                            return result;
                        }
                    } else if(feature>=17){
                        //todo maybe just check if current is jbr?
                        if(file.exists()){
                            result.setHasFound(true);
                            result.setJavaVersion(feature);
                            return result;
                        } else {
                            if(result.isJbr()){
                                result.setHasFound(true);
                                result.setJavaVersion(feature);
                                return result;
                            }
                            result.setHasFound(false);
                            result.setErrorText("hotSwap file not exist in your jdk home,"+file.getAbsolutePath()+"please download jdk in github release");
                            return result;
                        }
                    } else {
                        result.setHasFound(false);
                        result.setErrorText("jdk version is not supported, please download jdk");
                        return result;
                    }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            result.setHasFound(false);
            result.setErrorText("release file not found in path:"+release.getAbsolutePath()+" Please download jdk");
            return result;
        }
    }
}
