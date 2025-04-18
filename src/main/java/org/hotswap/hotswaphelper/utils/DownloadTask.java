package org.hotswap.hotswaphelper.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DownloadTask extends Task.Backgroundable {
    private final String downloadUrl;
    private final String destinationPath;
    private final Runnable onDownloadComplete;
    private final Runnable onDownloadFailed;
    private final boolean overwriteExisting;

    public DownloadTask(Project project, String downloadUrl, String destinationPath,
                        Runnable onDownloadComplete, Runnable onDownloadFailed, boolean overwriteExisting) {
        super(project, "Downloading File", true);
        this.downloadUrl = downloadUrl;
        this.destinationPath = destinationPath;
        this.onDownloadComplete = onDownloadComplete;
        this.onDownloadFailed = onDownloadFailed;
        this.overwriteExisting = overwriteExisting;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            // Check if we should skip download
            if (!overwriteExisting && FileUtil.exist(destinationPath) && !FileUtil.isEmpty(new File(destinationPath))) {
                if (onDownloadComplete != null) {
                    ApplicationManager.getApplication().invokeLater(onDownloadComplete::run);
                }
                return;
            }

            // Create parent directories if they don't exist
            FileUtil.mkParentDirs(destinationPath);
            FileUtils.touch(new File(destinationPath));

            URL url = new URL(downloadUrl);
            connection = (HttpURLConnection) url.openConnection();

            // Set Chrome-like headers
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");

            connection.connect();

            // Check HTTP response code
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Server returned HTTP " + responseCode + ": " + connection.getResponseMessage());
            }

            // Get content length for progress tracking
            int contentLength = connection.getContentLength();
            progressIndicator.setIndeterminate(contentLength <= 0);

            inputStream = connection.getInputStream();

            // Download with progress tracking
            try (InputStream progressStream = new ProgressInputStream(inputStream, contentLength, progressIndicator)) {
                Files.copy(progressStream, Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
            }

            // Completion callback
            if (onDownloadComplete != null) {
                ApplicationManager.getApplication().invokeLater(onDownloadComplete::run);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            // Clean up partially downloaded file
            if (FileUtil.exist(destinationPath)) {
                FileUtil.del(destinationPath);
            }

            // Failure callback
            if (onDownloadFailed != null) {
                ApplicationManager.getApplication().invokeLater(onDownloadFailed::run);
            }
        } finally {
            IoUtil.close(inputStream);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * InputStream wrapper that updates progress indicator
     */
    private static class ProgressInputStream extends InputStream {
        private final InputStream wrapped;
        private final int totalSize;
        private final ProgressIndicator progressIndicator;
        private int bytesRead;

        ProgressInputStream(InputStream wrapped, int totalSize, ProgressIndicator progressIndicator) {
            this.wrapped = wrapped;
            this.totalSize = totalSize;
            this.progressIndicator = progressIndicator;
            this.bytesRead = 0;
        }

        @Override
        public int read() throws IOException {
            int data = wrapped.read();
            if (data != -1) {
                bytesRead++;
                updateProgress();
            }
            return data;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int count = wrapped.read(b, off, len);
            if (count != -1) {
                bytesRead += count;
                updateProgress();
            }
            return count;
        }

        private void updateProgress() {
            if (totalSize > 0) {
                double fraction = (double) bytesRead / totalSize;
                progressIndicator.setFraction(fraction);
                progressIndicator.setText2(String.format("Downloaded %s/%s",
                        formatSize(bytesRead), formatSize(totalSize)));
            }
        }

        private String formatSize(int bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }

        @Override
        public void close() throws IOException {
            wrapped.close();
        }
    }
}