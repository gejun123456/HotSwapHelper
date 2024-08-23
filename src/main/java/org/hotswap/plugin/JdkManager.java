package org.hotswap.plugin;

import com.intellij.util.lang.JavaVersion;
import org.hotswap.plugin.utils.MyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author bruce ge 2024/8/19
 */
public class JdkManager {
    public static CheckResult checkJdkHome(String jdkhome){
        //get java version from jdk home.
        //get the release file.
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
                if(feature<8){
                    //not supported.
                    result.setHasFound(false);
                    result.setErrorText("before jdk1.8 is not supported");
                    return result;
                }
                if(feature==8){
                    if(MyUtils.isWindows()) {
                        //check if dcevm exist or not.
                        if (new File(jdkhome, "jre/bin/dcevm").exists()) {
                            //dcevm exist.
                            result.setHasFound(true);
                            result.setErrorText("");
                            result.setJavaVersion(8);
                            return result;
                        }
                        else{
                            result.setHasFound(false);
                            result.setErrorText("dcevm not found in your jdk home:"+jdkhome);
                            return result;
                        }
                    } else {
                        if (new File(jdkhome, "jre/lib/dcevm").exists()) {
                            //dcevm exist.
                            result.setHasFound(true);
                            result.setErrorText("");
                            result.setJavaVersion(8);
                            return result;
                        } else{
                            result.setHasFound(false);
                            result.setErrorText("dcevm not found in your jdk home:"+jdkhome);
                            return result;
                        }
                    }

                } else if(feature==11){
                    if(new File(jdkhome,"lib/hotswap/hotswap-agent.jar").exists()){
                        result.setHasFound(true);
                        result.setJavaVersion(11);
                        return result;
                    } else {
                        result.setHasFound(false);
                        result.setErrorText("hotSwap file not exist in your jdk home, please download jdk");
                        return result;
                    }
                } else if(feature==17){
                    if(new File(jdkhome,"lib/hotswap/hotswap-agent.jar").exists()){
                        result.setHasFound(true);
                        result.setJavaVersion(17);
                        return result;
                    } else {
                        result.setHasFound(false);
                        result.setErrorText("hotSwap file not exist in your jdk home, please download jdk");
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
            result.setErrorText("release file not found in your jdk home:"+jdkhome+"Please download jdk");
            return result;
        }
    }
}
