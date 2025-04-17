package org.hotswap.hotswaphelper.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.JavaSdkImpl;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * JDK 配置工具类（兼容 IntelliJ IDEA 2016.3+）
 * 功能：
 * 1. 自动检测 IDE 版本选择最优配置方式
 * 2. 支持新增/更新 JDK 配置
 * 3. 自动配置类路径、源码路径、文档路径
 * 4. 完善的错误处理和日志记录
 */
public class JdkConfigurationUtils {
    private static final Logger LOG = Logger.getInstance(JdkConfigurationUtils.class);

    // 标记是否检测到新版 API（2019.3+ 的 JdkUtil）
    private static final boolean HAS_MODERN_API = detectModernApi();

    /**
     * 配置项目 JDK（自动新增或更新）
     *
     * @param project 当前项目
     * @param jdkName JDK 名称（如 "JDK 17"）
     * @param jdkHome JDK 安装路径
     * @param version Java 版本（如 "1.8"、"11"、"17"）
     * @return 是否配置成功
     */
    public static boolean setupProjectJdk(@NotNull Project project, @NotNull String version, @NotNull String jdkName, @NotNull String jdkHome) {
        try {
            // 获取或创建 JDK 实例
            Sdk jdk = getOrCreateJdk(jdkName, jdkHome);
            if (jdk == null) {
                LOG.error("Failed to create JDK instance");
                return false;
            }
            ProjectJdkTable sdkTable = ProjectJdkTable.getInstance();
            sdkTable.addJdk(jdk);

            // 设置版本信息
            if (!setJdkVersion(jdk, version)) {
                LOG.warn("Failed to set JDK version, but continuing...");
            }

            // 应用到项目
            ProjectRootManager.getInstance(project).setProjectSdk(jdk);
            LOG.info("JDK configured successfully");
            return true;
        } catch (Exception e) {
            LOG.error("JDK configuration failed", e);
            return false;
        }
    }

    /**
     * 获取或创建 JDK 实例（自动处理新增/更新）
     */
    @Nullable
    private static Sdk getOrCreateJdk(@NotNull String name, @NotNull String home) {
        ProjectJdkTable sdkTable = ProjectJdkTable.getInstance();
        Sdk existingJdk = sdkTable.findJdk(name);

        // 移除现有 JDK（确保配置完全更新）
        if (existingJdk != null) {
            LOG.info("Removing existing JDK to refresh configuration");
            sdkTable.removeJdk(existingJdk);
        }

        // 创建新的 JDK 实例
        return createJdk(name, home);
    }

    /**
     * 创建 JDK 实例（自动选择最优方式）
     */
    @Nullable
    private static Sdk createJdk(@NotNull String name, @NotNull String home) {
        try {
            if (HAS_MODERN_API) {
                // 使用新版 API（2019.3+）
                return createJdkWithJdkUtil(name, home);
            }
            // 使用兼容模式（2016.3+）
            return createJdkWithJavaSdkImpl(name, home);
        } catch (Exception e) {
            LOG.error("JDK creation failed", e);
            return null;
        }
    }

    /**
     * 使用 JdkUtil（2019.3+ API）
     */
    private static Sdk createJdkWithJdkUtil(@NotNull String name, @NotNull String home) {
        try {
            Class<?> jdkUtil = Class.forName("com.intellij.openapi.projectRoots.impl.JdkUtil");
            Method createJdk = jdkUtil.getMethod("createJdk", String.class, String.class, boolean.class);
            return (Sdk) createJdk.invoke(null, name, home, false);
        } catch (Exception e) {
            LOG.warn("JdkUtil failed, falling back to JavaSdkImpl", e);
            return createJdkWithJavaSdkImpl(name, home);
        }
    }

    /**
     * 使用 JavaSdkImpl（兼容旧版）
     */
    private static Sdk createJdkWithJavaSdkImpl(@NotNull String name, @NotNull String home) {
        try {
            return JavaSdkImpl.getInstance().createJdk(name, home);
        } catch (Exception e) {
            LOG.error("JavaSdkImpl creation failed", e);
            return null;
        }
    }

    /**
     * 设置 JDK 版本信息
     */
    private static boolean setJdkVersion(@NotNull Sdk jdk, @NotNull String version) {
        try {
            SdkModificator modificator = jdk.getSdkModificator();
            modificator.setVersionString(version);
            modificator.commitChanges();
            return true;
        } catch (Exception e) {
            LOG.error("Failed to set JDK version", e);
            return false;
        }
    }

    /**
     * 检测是否支持新版 API
     */
    private static boolean detectModernApi() {
        try {
            Class.forName("com.intellij.openapi.projectRoots.impl.JdkUtil");
            LOG.warn("support JdkUtil 检测到新版 API（2019.3+ 的 JdkUtil）");
            return true;
        } catch (ClassNotFoundException e) {
            LOG.warn("support JavaSdkImpl 使用兼容模式（2016.3+）");
            LOG.warn("", e);
            return false;
        }
    }
}