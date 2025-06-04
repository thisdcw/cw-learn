package com.cw.framework.security;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * JNA接口类，用于与C++的timelock库通信
 */
public interface TimeLockLibrary extends Library {

    Logger log = LoggerFactory.getLogger(TimeLockLibrary.class);

    /**
     * 加载本地库实例
     */
    TimeLockLibrary INSTANCE = loadLibrary();

    /**
     * 验证时间锁，确认当前时间是否在有效期内
     *
     * @param expireTimeStr 到期时间字符串，格式为YYYY-MM-DD
     * @param licenseKey    许可证密钥
     * @return 如果验证通过返回true，否则返回false
     */
    boolean validateTimeLock(String expireTimeStr, String licenseKey);

    /**
     * 获取机器ID
     *
     * @param outBuffer  输出缓冲区
     * @param bufferSize 缓冲区大小
     * @return 如果成功获取返回true，否则返回false
     */
    boolean getMachineId(byte[] outBuffer, int bufferSize);
    
    /**
     * 设置许可证
     *
     * @param expireTimeStr 到期时间字符串，格式为YYYY-MM-DD
     * @param licenseKey    许可证密钥
     * @return 如果成功设置返回true，否则返回false
     */
    boolean setLicense(String expireTimeStr, String licenseKey);
    
    /**
     * 重置许可证（用于测试）
     *
     * @return 如果成功重置返回true，否则返回false
     */
    boolean resetLicense();
    
    /**
     * 为指定机器ID生成许可证密钥
     *
     * @param machineId   机器ID
     * @param outBuffer   输出缓冲区
     * @param bufferSize  缓冲区大小
     * @return 如果成功生成返回true，否则返回false
     */
    boolean generateLicenseKey(String machineId, byte[] outBuffer, int bufferSize);

    /**
     * 加载本地库
     */
    static TimeLockLibrary loadLibrary() {
        try {
            // 尝试从系统属性中获取JNA库路径
            String jnaLibraryPath = System.getProperty("jna.library.path");
            String libraryName = "timelock";
            
            // 根据平台确定库文件扩展名
            String libraryExtension;
            if (Platform.isWindows()) {
                libraryExtension = ".dll";
            } else if (Platform.isLinux()) {
                libraryExtension = ".so";
                libraryName = "lib" + libraryName;
            } else if (Platform.isMac()) {
                libraryExtension = ".dylib";
                libraryName = "lib" + libraryName;
            } else {
                throw new RuntimeException("Unsupported platform");
            }
            
            // 完整的库文件名
            String fullLibraryName = libraryName + libraryExtension;
            
            // 如果指定了JNA库路径，尝试从该路径加载
            if (jnaLibraryPath != null && !jnaLibraryPath.isEmpty()) {
                log.info("尝试从指定路径加载库: {}", jnaLibraryPath);
                
                // 检查多个可能的位置
                File libFile = new File(jnaLibraryPath, fullLibraryName);
                if (libFile.exists()) {
                    log.info("从路径加载库: {}", libFile.getAbsolutePath());
                    System.setProperty("jna.library.path", libFile.getParent());
                    return Native.load(libraryName, TimeLockLibrary.class);
                }
                
                // 检查平台特定目录
                String platformDir = getPlatformDir();
                File platformLibFile = new File(jnaLibraryPath, platformDir + "/" + fullLibraryName);
                if (platformLibFile.exists()) {
                    log.info("从平台特定路径加载库: {}", platformLibFile.getAbsolutePath());
                    System.setProperty("jna.library.path", platformLibFile.getParent());
                    return Native.load(libraryName, TimeLockLibrary.class);
                }
                
                // 尝试从资源中提取
                File tempLib = extractLibraryFromResources(jnaLibraryPath, fullLibraryName);
                if (tempLib != null && tempLib.exists()) {
                    log.info("从临时路径加载库: {}", tempLib.getAbsolutePath());
                    System.setProperty("jna.library.path", tempLib.getParent());
                    return Native.load(libraryName, TimeLockLibrary.class);
                }
            }
            
            // 如果上述方法都失败，尝试直接加载
            log.info("尝试直接加载库: {}", fullLibraryName);
            return Native.load(libraryName, TimeLockLibrary.class);
        } catch (Exception e) {
            log.error("加载本地库失败", e);
            throw new RuntimeException("加载本地库失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取平台特定目录名
     */
    static String getPlatformDir() {
        String arch = System.getProperty("os.arch").toLowerCase();
        String name;
        
        if (Platform.isWindows()) {
            name = "win32-" + (arch.contains("64") ? "x86-64" : "x86");
        } else if (Platform.isLinux()) {
            name = "linux-" + (arch.contains("64") ? "x86-64" : "x86");
        } else if (Platform.isMac()) {
            name = "darwin-" + (arch.contains("64") ? "x86-64" : "x86");
        } else {
            name = "unknown";
        }
        
        return name;
    }
    
    /**
     * 从资源中提取库文件
     */
    static File extractLibraryFromResources(String resourcePath, String libraryName) {
        try {
            // 尝试从多个可能的位置加载
            String[] possiblePaths = {
                resourcePath + "/" + libraryName,
                resourcePath + "/build/" + libraryName,
                resourcePath + "/" + getPlatformDir() + "/" + libraryName,
                "native/" + libraryName,
                "native/build/" + libraryName,
                "native/" + getPlatformDir() + "/" + libraryName
            };
            
            InputStream inputStream = null;
            for (String path : possiblePaths) {
                log.info("尝试从资源路径加载: {}", path);
                inputStream = TimeLockLibrary.class.getClassLoader().getResourceAsStream(path);
                if (inputStream != null) {
                    log.info("在资源路径找到库: {}", path);
                    break;
                }
            }
            
            if (inputStream == null) {
                log.error("在资源中未找到库文件");
                return null;
            }
            
            // 创建临时目录
            Path tempDir = Files.createTempDirectory("timelock-lib");
            File tempFile = new File(tempDir.toFile(), libraryName);
            
            // 复制库文件到临时目录
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            inputStream.close();
            tempFile.deleteOnExit();
            tempDir.toFile().deleteOnExit();
            
            return tempFile;
        } catch (IOException e) {
            log.error("提取库文件失败", e);
            return null;
        }
    }
} 