package org.hotswap.hotswaphelper.utils;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ArchiveUtils {

    /**
     * 查找压缩包中最短的 bin 目录路径（不解压）
     *
     * @param archivePath 压缩包路径（支持 .zip, .tar, .tar.gz, .tar.bz2, .tar.xz 等）
     * @return 最短的 bin 目录路径（如 "jdk-17.0.1/bin"），找不到返回 null
     */
    public static String findShortestBinPath(Path archivePath) throws IOException, ArchiveException, CompressorException {
        if (!isSupportedArchive(archivePath)) {
            throw new ArchiveException("Archive is not supported");
        }
        String shortestBinPath = null;
        int shortestLength = Integer.MAX_VALUE;
        try {
            try (InputStream inputStream = Files.newInputStream(archivePath); InputStream bufferedIn = new BufferedInputStream(inputStream); InputStream compressedIn = tryDecompress(bufferedIn); ArchiveInputStream archiveIn = new ArchiveStreamFactory().createArchiveInputStream(compressedIn)) {
                ArchiveEntry entry;
                while ((entry = archiveIn.getNextEntry()) != null) {
                    if (!archiveIn.canReadEntryData(entry)) {
                        continue; // 跳过不可读的条目（如加密文件）
                    }
                    if (entry.isDirectory() && isBinDirectory(entry.getName())) {
                        int pathLength = entry.getName().length();
                        if (pathLength < shortestLength) {
                            shortestLength = pathLength;
                            shortestBinPath = entry.getName();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("压缩包格式不支持或损坏: " + e.getMessage());
        }
        return shortestBinPath;
    }

    /**
     * 检查是否为 bin 目录
     */
    private static boolean isBinDirectory(String entryName) {
        Path path = Paths.get(entryName);
        return path.getFileName() != null && path.getFileName().toString().equals("bin");
    }

    /**
     * 尝试解压流（支持 .gz, .bz2, .xz 等压缩格式）
     */
    private static InputStream tryDecompress(InputStream inputStream) throws CompressorException, IOException {
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        inputStream.mark(100); // 标记以便回退
        try {
            return new CompressorStreamFactory().createCompressorInputStream(inputStream);
        } catch (CompressorException e) {
            inputStream.reset(); // 不是压缩文件，返回原始流
            return inputStream;
        }
    }

    public static boolean isSupportedArchive(Path path) {
        String ext = FileNameUtils.getExtension(path.toString().toLowerCase());
        return ext.matches("zip|tar|gz|bz2|xz");
    }

    /**
     * 解压任意格式的压缩文件（自动检测格式）
     *
     * @param archiveFile 压缩文件路径（支持 .zip, .tar, .tar.gz, .tar.bz2, .7z 等）
     * @param outputDir   解压目标目录
     */
    public static void extract(Path archiveFile, Path outputDir) throws IOException {
        if (!Files.exists(archiveFile)) {
            throw new FileNotFoundException("压缩文件不存在: " + archiveFile);
        }

        // 创建目标目录
        Files.createDirectories(outputDir);

        // 根据文件扩展名选择解压方式
        String fileName = archiveFile.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".7z")) {
            extract7z(archiveFile, outputDir);
        } else if (fileName.endsWith(".zip")) {
            extractArchive(archiveFile, outputDir, ArchiveStreamFactory.ZIP);
        } else if (fileName.endsWith(".tar")) {
            extractArchive(archiveFile, outputDir, ArchiveStreamFactory.TAR);
        } else if (fileName.endsWith(".tar.gz") || fileName.endsWith(".tgz")) {
            extractCompressedTar(archiveFile, outputDir, CompressorStreamFactory.GZIP);
        } else if (fileName.endsWith(".tar.bz2") || fileName.endsWith(".tbz2")) {
            extractCompressedTar(archiveFile, outputDir, CompressorStreamFactory.BZIP2);
        } else if (fileName.endsWith(".tar.xz")) {
            extractCompressedTar(archiveFile, outputDir, CompressorStreamFactory.XZ);
        } else {
            throw new IllegalArgumentException("不支持的压缩格式: " + fileName);
        }
    }

    // 解压 ZIP/TAR 文件
    private static void extractArchive(Path archiveFile, Path outputDir, String archiveType) throws IOException {
        try (InputStream is = Files.newInputStream(archiveFile);
             ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(archiveType, is)) {
            extractEntries(ais, outputDir);
        } catch (Exception e) {
            throw new IOException("解压失败: " + e.getMessage(), e);
        }
    }

    // 解压 TAR.GZ / TAR.BZ2 / TAR.XZ
    private static void extractCompressedTar(Path archiveFile, Path outputDir, String compressorType) throws IOException {
        try (InputStream is = Files.newInputStream(archiveFile);
             InputStream cis = new CompressorStreamFactory().createCompressorInputStream(compressorType, is);
             ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.TAR, cis)) {
            extractEntries(ais, outputDir);
        } catch (Exception e) {
            throw new IOException("解压失败: " + e.getMessage(), e);
        }
    }

    // 解压 7Z 文件（特殊处理）
    private static void extract7z(Path archiveFile, Path outputDir) throws IOException {
        try (SevenZFile sevenZFile = new SevenZFile(archiveFile.toFile())) {
            SevenZArchiveEntry entry;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                Path entryPath = outputDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (OutputStream os = Files.newOutputStream(entryPath)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = sevenZFile.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    // 通用条目解压逻辑
    private static void extractEntries(ArchiveInputStream ais, Path outputDir) throws IOException {
        ArchiveEntry entry;
        while ((entry = ais.getNextEntry()) != null) {
            Path entryPath = outputDir.resolve(entry.getName()).normalize();
            if (!entryPath.startsWith(outputDir)) {
                throw new IOException("非法路径: " + entry.getName()); // 防止路径穿越攻击
            }

            if (entry.isDirectory()) {
                Files.createDirectories(entryPath);
            } else {
                Files.createDirectories(entryPath.getParent());
                try (OutputStream os = Files.newOutputStream(entryPath)) {
                    IOUtils.copy(ais, os);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Path archivePath = Paths.get("jdk-17.0.1.tar.gz");
            String binPath = findShortestBinPath(archivePath);
            System.out.println("Found bin directory: " + binPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}