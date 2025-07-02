package org.hotswap.hotswaphelper.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {
    /**
     * 查找父目录下最短的 bin 目录路径
     *
     * @param parentDirectory 父目录路径
     * @return 最短的 bin 目录路径（如 "jdk-17.0.1/bin"），找不到返回 null
     */
    public static String findShortestBinPath(Path parentDirectory) {
        if (!Files.isDirectory(parentDirectory)) {
            System.err.println("Provided path is not a directory");
            return null;
        }

        String shortestBinPath = null;
        int shortestLength = Integer.MAX_VALUE;

        try {
            List<Path> binPaths = new ArrayList<>();
            Files.walk(parentDirectory)
                    .filter(Files::isDirectory)
                    .filter(path -> path.getFileName().toString().equals("bin"))
                    .forEach(binPaths::add);

            for (Path binPath : binPaths) {
                String relativePath = parentDirectory.relativize(binPath).toString();
                int pathLength = relativePath.length();
                if (pathLength < shortestLength) {
                    shortestLength = pathLength;
                    shortestBinPath = relativePath;
                }
            }
        } catch (IOException e) {
            System.err.println("文件读取失败: " + e.getMessage());
        }

        return shortestBinPath;
    }

    /**
     * 检查父目录下是否存在名为 dcevm 的文件或目录
     *
     * @param parentDirectory 父目录路径
     * @return 如果存在名为 dcevm 的文件或目录，返回 true；否则返回 false
     */
    public static boolean findJdkDcevm(Path parentDirectory) {
        if (!Files.isDirectory(parentDirectory)) {
            System.err.println("Provided path is not a directory");
            return false;
        }

        try (Stream<Path> stream = Files.walk(parentDirectory)) {
            // 使用 Files.walk 遍历目录，并检查是否存在名为 dcevm 的文件或目录
            return stream.anyMatch(path -> path.getFileName().toString().equalsIgnoreCase("dcevm"));
        } catch (Exception e) {
            System.err.println("文件读取失败: " + e.getMessage());
            return false;
        }
    }
}