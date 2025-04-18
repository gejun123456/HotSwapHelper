package org.hotswap.hotswaphelper.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

public class FilePermissionUtils {

    /**
     * 递归设置目录和文件的权限（跨平台）
     *
     * @param path 目录或文件路径
     * @throws IOException 如果发生 I/O 错误
     */
    public static void setPermissionsRecursive(Path path) throws IOException {
        // 设置当前路径的权限
        setPermissions(path);

        // 如果是目录，递归设置子文件和子目录的权限
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    setPermissions(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc == null) {
                        setPermissions(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw exc; // 抛出异常而不是静默忽略
                    }
                }
            });
        }
    }

    /**
     * 跨平台设置权限
     *
     * @param path 文件或目录路径
     * @throws IOException 如果发生 I/O 错误
     */
    private static void setPermissions(Path path) throws IOException {
        // 对于POSIX系统（Linux/Mac）
        if (isPosix(path)) {
            // 设置所有者、组和其他用户的读、写和执行权限
            Set<PosixFilePermission> perms = EnumSet.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.GROUP_READ,
                    PosixFilePermission.GROUP_WRITE,
                    PosixFilePermission.GROUP_EXECUTE,
                    PosixFilePermission.OTHERS_READ,
                    PosixFilePermission.OTHERS_WRITE,
                    PosixFilePermission.OTHERS_EXECUTE
            );
            Files.setPosixFilePermissions(path, perms);
        }
        // 对于Windows系统
        else {
            // Windows不需要显式设置"执行"权限，主要通过读取权限控制
            // 但我们需要确保至少有以下权限：
            DosFileAttributeView attrs = Files.getFileAttributeView(path, DosFileAttributeView.class);
            if (attrs != null) {
                // 移除只读属性（如果有）
                attrs.setReadOnly(false);
            }
        }
    }

    /**
     * 检查是否支持POSIX权限
     *
     * @param path 文件或目录路径
     * @return 是否支持POSIX权限
     * @throws IOException 如果发生 I/O 错误
     */
    private static boolean isPosix(Path path) throws IOException {
        return Files.getFileStore(path).supportsFileAttributeView(PosixFileAttributeView.class);
    }

    /**
     * 测试方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java FilePermissionUtils <directory>");
            return;
        }

        try {
            Path path = Paths.get(args[0]);
            setPermissionsRecursive(path);
            System.out.println("Permissions set successfully for: " + path);
        } catch (IOException e) {
            System.err.println("Failed to set permissions: " + e.getMessage());
            e.printStackTrace();
        }
    }
}