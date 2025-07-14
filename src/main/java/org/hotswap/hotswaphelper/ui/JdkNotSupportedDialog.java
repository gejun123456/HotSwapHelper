package org.hotswap.hotswaphelper.ui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.components.labels.LinkListener;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.JBUI;
import lombok.val;
import org.hotswap.hotswaphelper.settings.HotSwapHelperPluginSettingsProvider;
import org.hotswap.hotswaphelper.utils.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author bruce ge 2024/8/22
 */
public class JdkNotSupportedDialog extends DialogWrapper {
    public LinkLabel jbrLink;
    private TextFieldWithBrowseButton jdkDirectoryField = new TextFieldWithBrowseButton();
    private JPanel panel1;
    private JPanel thePanel;
    private LinkLabel qqGroupLink;
    private LinkLabel githubLink;
    private JLabel errorLabel;
    private LinkLabel dontCheckJdk;
    private LinkLabel documentationLink;
    private JButton downloadJdkButton;
    private JTextField currentJdkText;
    private JButton clearDownloadCacheButton;
    public JPanel downloadJdkPanel;

    public JdkNotSupportedDialog(@Nullable Project project, boolean canBeParent, String errorText) {
        super(project, canBeParent);
        ApplicationManager.getApplication().runWriteAction(() -> {
            errorLabel.setText(errorText);
            ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
            val sdk = projectRootManager.getProjectSdk();
            String javaVersion = sdk.getVersionString();
            currentJdkText.setText("当前项目jdk: " + javaVersion);
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            jdkDirectoryField.getTextField().setText(HotSwapHelperPluginSettingsProvider.Companion.getInstance(project).getCurrentState().getJdkDirectory());
            jdkDirectoryField.addBrowseFolderListener(new TextBrowseFolderListener(descriptor) {
                @Override
                protected void onFileChosen(@NotNull VirtualFile chosenFile) {
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        // 当用户选择文件夹后，更新 TextFieldWithBrowseButton 的文本
                        String chosenFilePath = chosenFile.getPath();
                        jdkDirectoryField.setText(chosenFilePath);
                        System.out.println("Selected folder: " + chosenFilePath);
//              String  jdkDirectory =  HotSwapHelperPluginSettingsProvider.Companion.getInstance(project).getCurrentState().getJdkDirectory();
                        HotSwapHelperPluginSettingsProvider.State state = HotSwapHelperPluginSettingsProvider.Companion.getInstance(project).getState();
                        state.setJdkDirectory(chosenFilePath);
                        // 可以在这里添加更多逻辑，例如更新 UI 或处理文件夹路径
                    });
                }
            });
            jdkDirectoryField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
                @Override
                protected void textChanged(@NotNull DocumentEvent e) {
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        HotSwapHelperPluginSettingsProvider.State state = HotSwapHelperPluginSettingsProvider.Companion.getInstance(project).getState();
                        state.setJdkDirectory(jdkDirectoryField.getTextField().getText());
                    });
                }
            });

            githubLink.setListener(new LinkListener() {
                @Override
                public void linkSelected(LinkLabel aSource, Object aLinkData) {
                    BrowserUtil.browse("https://github.com/gejun123456/HotSwapHelper/releases/tag/1.0");
                }
            }, null);
            qqGroupLink.setListener(new LinkListener() {
                @Override
                public void linkSelected(LinkLabel aSource, Object aLinkData) {
                    BrowserUtil.browse("https://qm.qq.com/q/pOUouYAUsU");
                }
            }, null);

            dontCheckJdk.setListener(new LinkListener() {
                @Override
                public void linkSelected(LinkLabel aSource, Object aLinkData) {
                    //shows setting pages. show intellij settings page.
                    Messages.showInfoMessage("You can disable jdk check in settings->hotswap helper", "Disable Jdk Check");
                }
            }, null);

            jbrLink.setListener(new LinkListener() {
                @Override
                public void linkSelected(LinkLabel aSource, Object aLinkData) {
                    BrowserUtil.browse("https://github.com/JetBrains/JetBrainsRuntime");
                }
            }, null);

            documentationLink.setListener(new LinkListener() {
                @Override
                public void linkSelected(LinkLabel aSource, Object aLinkData) {
                    BrowserUtil.browse("https://github.com/gejun123456/HotSwapHelper");
                }
            }, null);
            setTitle("Current Jdk Is Not Supported");
            JButton submit = new JButton("提交");

            downloadJdkPanel.setVisible(false);

            downloadJdkButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        System.out.println("点击了下载按钮");
                        String jdkDirectory = HotSwapHelperPluginSettingsProvider.Companion.getInstance(project).getCurrentState().getJdkDirectory();
                        if (ObjUtil.isEmpty(jdkDirectory)) {
//                    Messages.showWarningDialog("请先设置jdk下载路径", "请先设置jdk下载路径");
                            SwingUtilities.invokeLater(() -> Messages.showWarningDialog("请先设置jdk下载路径", "请先设置jdk下载路径"));
                            return;
                        }
//                Messages.showInfoMessage("正在下载和设置项目jdk 请稍等", "正在下载和设置项目jdk");
                        SwingUtilities.invokeLater(() -> Messages.showInfoMessage("正在下载和设置项目jdk 请稍等", "正在下载和设置项目jdk"));
                        setJdk(project, jdkDirectory);
                    });
                }
            });

            clearDownloadCacheButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
            init();
        });
    }

    /**
     * 从完整的 Java 版本字符串中提取大版本号
     *
     * @param versionString 完整的版本字符串，例如 "1.8.0_231"
     * @return 大版本号，例如 "1.8" 或 "8"
     */
    private static String extractMajorVersion(String versionString) {
        if (versionString == null || versionString.isEmpty()) {
            return "Unknown";
        }

        // 去除版本字符串中的非数字和点号部分
        String cleanedVersion = versionString.replaceAll("[^\\d.]", "");

        // 按点号分割版本字符串
        String[] parts = cleanedVersion.split("\\.");

        if (parts.length == 0) {
            return "Unknown";
        }

        // 判断版本号格式
        if (parts[0].equals("1") && parts.length > 1) {
            // 旧版本格式：1.x
            return "1." + parts[1];
        } else {
            // 新版本格式：x
            return parts[0];
        }
    }

    private void setJdk(@Nullable Project project, String jdksPath) {
        AtomicReference<String> majorJavaVersionAtomic = new AtomicReference<>();
        AtomicReference<String> hotswapJdkNameAtomic = new AtomicReference<>();
        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        val sdk = projectRootManager.getProjectSdk();
        String javaVersion = sdk.getVersionString();
        // 获取当前操作系统架构
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        String osKey;
        if (osName.contains("mac")) {
            osKey = "macos";
        } else if (osName.contains("win")) {
            osKey = "windows";
        } else if (osName.contains("linux")) {
            osKey = "linux";
        } else {
            SwingUtilities.invokeLater(() -> Messages.showWarningDialog(StrUtil.format("找不到本系统架构{}的可用jdk", osArch), "设置项目jdk失败"));
            return;
        }
        // 构建完整的操作系统键
        String fullOsKey = osKey + "-" + osArch;
        // 提取大版本号
        majorJavaVersionAtomic.set(extractMajorVersion(javaVersion));
        hotswapJdkNameAtomic.set("hotswap-jdk-" + majorJavaVersionAtomic.get());
        ProjectJdkTable sdkTable = ProjectJdkTable.getInstance();
        Sdk[] allJdks = sdkTable.getAllJdks();
        Sdk findJdk = null;
        // dcevmJdk
        List<Sdk> dcevmJdks = Arrays.stream(allJdks).filter(jdk -> {
            String jdkVersionString = jdk.getVersionString();
            String jdkHomePath = jdk.getHomePath();
            boolean jdkDcevm = FileUtils.findJdkDcevm(Paths.get(jdkHomePath));
            //说明已经有可用jdk 则需要设置为本项目jdk
            return jdkDcevm;
        }).collect(Collectors.toList());
        // this version
        try {
            findJdk = dcevmJdks.stream().filter(jdk -> {
                String jdkVersionString = jdk.getVersionString();
                String jdkHomePath = jdk.getHomePath();
//                boolean jdkDcevm = FileUtils.findJdkDcevm(Paths.get(jdkHomePath));
//                if (jdkDcevm) {
                // 提取大版本号
                majorJavaVersionAtomic.set(extractMajorVersion(javaVersion));
                if (jdkVersionString.equals(majorJavaVersionAtomic.get())) {
                    //说明已经有可用jdk 则需要设置为本项目jdk
                    return true;
                }
                throw new RuntimeException();
//                }
//                return false;
            }).findFirst().orElse(null);
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> Messages.showInfoMessage(StrUtil.format("检测到你已经设置了{}个jdk {}个Dcevm的jdk", allJdks.length, dcevmJdks.size()), "chech Jdk"));
        if (!ObjUtil.isEmpty(findJdk)) {
            String jdkHome = findJdk.getHomePath();
            // 切换到该jdk
            changeJdk(project, majorJavaVersionAtomic.get(), hotswapJdkNameAtomic.get(), jdkHome);
            return;
        }
        String githubProxy = "https://github.proxy.class3.fun/";
        // 读取 JSON 文件
//        String jdkMappingPath = "/home/ntfs/Common/Project/IdeaProjects/hotswap/src/main/resources/jdk-mapping.yml";
        String body = null;
        try {
            HttpRequest httpRequest = HttpUtil.createGet(githubProxy + "https://raw.githubusercontent.com/lidiaoo/hotswap-jdk/refs/heads/main/jdk-mapping.yml");
            body = httpRequest.execute().body();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            Messages.showWarningDialog("github无法访问 无法继续", "github无法访问");
            return;
        }
        try {
            String jdkFile;
            String jdkZipPath;
            try {
                // 加载 YAML 文件
                Yaml yaml = new Yaml();
//            InputStream inputStream = new FileInputStream(jdkMappingPath);
                Map<String, Map<String, Map<String, List<String>>>> mapping = yaml.load(body);
                // 获取文件路径
                Map<String, Map<String, List<String>>> jdkMap = mapping.get("jdk");
                Map<String, List<String>> map = jdkMap.get(fullOsKey);
                List<String> jdkFiles = map.get(majorJavaVersionAtomic.get());
                jdkFile = CollUtil.getFirst(jdkFiles);
                String jdkFileName = FileUtil.file(jdkFile).getName();
                jdkZipPath = Paths.get(jdksPath, jdkFileName).toString();
                System.out.println("JDK file for " + fullOsKey + " and JDK " + majorJavaVersionAtomic.get() + ": " + jdkFiles);
            } catch (Exception e) {
                System.out.println(StrUtil.format("找不到本系统架构{}的可用jdk", fullOsKey));
//                Messages.showWarningDialog(StrUtil.format("找不到本系统架构{}的可用jdk", fullOsKey), "设置项目jdk失败");
                SwingUtilities.invokeLater(() -> Messages.showWarningDialog(StrUtil.format("找不到本系统架构{}的可用jdk", fullOsKey), "设置项目jdk失败"));
                return;
            }
            // 定义下载完成后的回调
            String finalJdkZipPath = jdkZipPath;
            Runnable onDownloadComplete = () -> {
//                Messages.showInfoMessage("Download completed successfully!", "Download Success");
                SwingUtilities.invokeLater(() -> Messages.showInfoMessage("Download completed successfully!", "Download Success"));
                System.out.println("下载成功");
                ApplicationManager.getApplication().runWriteAction(() -> {
                    // 在这里执行写操作
                    // 解压文件
                    String jdkPathStr;
                    Path jdkDirName;
                    Path jdkHomePath;
                    String jdkHome;
                    try {
                        jdkPathStr = ArchiveUtils.findShortestBinPath(Paths.get(finalJdkZipPath));
                        jdkDirName = Paths.get(jdkPathStr);
                        if (jdkDirName.toString().endsWith("bin") || jdkDirName.toString().endsWith("bin" + File.separator)) {
                            jdkDirName = jdkDirName.getParent();
                        }
                        jdkHomePath = Paths.get(jdksPath, jdkDirName.toString());
                        jdkHome = jdkHomePath.toString();
                        FileUtil.del(jdkHomePath);
                        ArchiveUtils.extract(Paths.get(finalJdkZipPath), Paths.get(jdksPath));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
//                        Messages.showWarningDialog("设置项目jdk失败 文件损坏", "设置项目jdk失败");
                        SwingUtilities.invokeLater(() -> Messages.showWarningDialog("设置项目jdk失败 文件损坏", "设置项目jdk失败"));
                        return;
                    }
                    changeJdk(project, majorJavaVersionAtomic.get(), hotswapJdkNameAtomic.get(), jdkHome);
                });
            };
            // 定义下载失败后的回调
            Runnable onDownloadFailed = () -> {
//                Messages.showWarningDialog("Download failed!", "Download Failed");
                SwingUtilities.invokeLater(() -> Messages.showWarningDialog("Download failed!", "Download Failed"));
                System.out.println("下载失败");
                ApplicationManager.getApplication().runWriteAction(() -> {
                    // 在这里执行写操作
                });
            };
            DownloadTask downloadTask = new DownloadTask(project, jdkFile, jdkZipPath, onDownloadComplete, onDownloadFailed, false);
            ProgressManager.getInstance().run(downloadTask);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    private void changeJdk(@NotNull Project project, String majorJavaVersion, String hotswapJdkName, String jdkHome) {
        try {
            JdkConfigurationUtils.setupProjectJdk(project, majorJavaVersion, hotswapJdkName, jdkHome);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
//                        Messages.showWarningDialog("设置项目jdk成功 设置jdk执行权限失败 请手动赋予jdk目录执行权限", "设置项目jdk成功");
            SwingUtilities.invokeLater(() -> Messages.showWarningDialog("设置项目jdk失败", "设置项目jdk失败"));
            return;
        }
        currentJdkText.setText("当前项目jdk: " + majorJavaVersion);
        try {
            FilePermissionUtils.setPermissionsRecursive(Paths.get(jdkHome));
//                        Messages.showInfoMessage("设置项目jdk成功 设置jdk执行权限成功", "设置项目jdk成功");
            SwingUtilities.invokeLater(() -> Messages.showInfoMessage("设置项目jdk成功 设置jdk执行权限成功", "设置项目jdk成功"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
//                        Messages.showWarningDialog("设置项目jdk成功 设置jdk执行权限失败 请手动赋予jdk目录执行权限", "设置项目jdk成功");
            SwingUtilities.invokeLater(() -> Messages.showWarningDialog("设置项目jdk成功 设置jdk执行权限失败 请手动赋予jdk目录执行权限", "设置项目jdk成功"));
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        thePanel = new JPanel();
        thePanel.setLayout(new GridLayoutManager(10, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(thePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, this.$$$getMessageFromBundle$$$("string", "jdkNotSupported"));
        thePanel.add(label1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Error is:");
        thePanel.add(label2, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        errorLabel = new JLabel();
        errorLabel.setText("Label");
        thePanel.add(errorLabel, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Download jdk from:(testd jdk)");
        thePanel.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        githubLink = new LinkLabel();
        githubLink.setText("github");
        thePanel.add(githubLink, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        this.$$$loadLabelText$$$(label4, this.$$$getMessageFromBundle$$$("HotSwapHelperIntellijPluginBundle", "无法下载可以加入qq群"));
        thePanel.add(label4, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        qqGroupLink = new LinkLabel();
        qqGroupLink.setText("qq群");
        thePanel.add(qqGroupLink, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dontCheckJdk = new LinkLabel();
        dontCheckJdk.setText("don't check jdk");
        thePanel.add(dontCheckJdk, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Documentation:");
        thePanel.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        documentationLink = new LinkLabel();
        this.$$$loadLabelText$$$(documentationLink, this.$$$getMessageFromBundle$$$("string", "documentation"));
        thePanel.add(documentationLink, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(281, 17), null, 0, false));
        final JLabel label6 = new JLabel();
        this.$$$loadLabelText$$$(label6, this.$$$getMessageFromBundle$$$("string", "afterdownloading"));
        thePanel.add(label6, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("After java11 can downloading jbr");
        thePanel.add(label7, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jbrLink = new LinkLabel();
        jbrLink.setText("downloadJbr");
        thePanel.add(jbrLink, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("download \tJBRSDK with JCEF or DCEVM");
        thePanel.add(label8, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(281, 17), null, 0, false));
        downloadJdkPanel = new JPanel();
        downloadJdkPanel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        thePanel.add(downloadJdkPanel, new GridConstraints(8, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        currentJdkText = new JTextField();
        currentJdkText.setText("");
        downloadJdkPanel.add(currentJdkText, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        downloadJdkPanel.add(spacer1, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JTextField textField1 = new JTextField();
        textField1.setText("下载hotswap jdk路径");
        downloadJdkPanel.add(textField1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        jdkDirectoryField = new TextFieldWithBrowseButton();
        jdkDirectoryField.setText("选择路径");
        downloadJdkPanel.add(jdkDirectoryField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(281, 30), null, 0, false));
        clearDownloadCacheButton = new JButton();
        clearDownloadCacheButton.setText("清理下载缓存");
        downloadJdkPanel.add(clearDownloadCacheButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        downloadJdkButton = new JButton();
        downloadJdkButton.setText("下载");
        downloadJdkPanel.add(downloadJdkButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return thePanel;
    }
}
