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
    public static CheckResult checkJdkHome(String jdkhome){
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
                if(feature==8){
                    if(MyUtils.isWindows()) {
                        //check if dcevm exist or not.
                        File file = new File(jdkhome, "jre/bin/dcevm");
                        if (file.exists()) {
                            //dcevm exist.
                            result.setHasFound(true);
                            result.setErrorText("");
                            result.setJavaVersion(8);
                            return result;
                        }
                        else{
                            result.setHasFound(false);
                            result.setErrorText("dcevm not found in your jdk home:"+file.getAbsolutePath()+","+downloadJdkInGithubRelease);
                            return result;
                        }
                    } else {
                        File thepath1 = new File(jdkhome, "jre/lib/dcevm");
                        File thePath2 = new File(jdkhome, "jre/lib/amd64/dcevm");
                        if (thepath1.exists() || thePath2.exists()) {
                            //dcevm exist.
                            result.setHasFound(true);
                            result.setErrorText("");
                            result.setJavaVersion(8);
                            return result;
                        } else{
                            result.setHasFound(false);
                            result.setErrorText("dcevm not found in your path:"+thepath1.getAbsolutePath()
                                                +" or path:"+thePath2.getAbsolutePath()+","+downloadJdkInGithubRelease);
                            return result;
                        }
                    }

                } else {
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
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            result.setHasFound(false);
            result.setErrorText("release file not found in your jdk home:"+jdkhome+" Please download jdk");
            return result;
        }
    }
}
