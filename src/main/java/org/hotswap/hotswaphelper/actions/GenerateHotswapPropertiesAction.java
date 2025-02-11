package org.hotswap.hotswaphelper.actions;

import org.hotswap.hotswaphelper.utils.HotswapPropertiesGenerator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

public class GenerateHotswapPropertiesAction extends AnAction {
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (project == null || file == null) {
            return;
        }

        com.intellij.openapi.application.WriteAction.runAndWait(() -> {
            VirtualFile propertiesFile = HotswapPropertiesGenerator.generateHotswapProperties(file);
            if (propertiesFile != null) {
                // 刷新文件系统
                VirtualFileManager.getInstance().syncRefresh();
                
                // 打开并跳转到新生成的文件
                FileEditorManager.getInstance(project).openFile(propertiesFile, true);
            }
        });
    }
    
    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (project == null || file == null || !file.isDirectory()) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }

        boolean isResourceFolder = ProjectRootManager.getInstance(project)
                .getFileIndex()
                .isInSourceContent(file) && 
                "resources".equals(file.getName());

        String path = file.getPath();
        if (isResourceFolder) {
            isResourceFolder = path.contains("/resources") && 
                             (path.contains("/main/") || path.contains("/test/"));
        }

        VirtualFile existingFile = file.findChild("hotswap-agent.properties");

        e.getPresentation().setEnabledAndVisible(isResourceFolder && existingFile == null);
    }
}