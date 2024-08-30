package org.hotswap.hotswaphelper;

/**
 * @author bruce ge 2024/8/19
 */
public class CheckResult {
    private boolean hasFound;

    private String errorText;

    private int javaVersion;


    public int getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(int javaVersion) {
        this.javaVersion = javaVersion;
    }

    public boolean isHasFound() {
        return hasFound;
    }

    public void setHasFound(boolean hasFound) {
        this.hasFound = hasFound;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
}
