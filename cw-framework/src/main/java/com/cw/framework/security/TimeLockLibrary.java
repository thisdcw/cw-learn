package com.cw.framework.security;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * 加载本地库
     */
    static TimeLockLibrary loadLibrary() {
        try {
            String libraryName = "timelock";

            // 根据平台确定库文件扩展名
            if (Platform.isWindows()) {
                libraryName = "timelock";
            } else if (Platform.isLinux()) {
                libraryName = "libtimelock.so";
            } else if (Platform.isMac()) {
                libraryName = "libtimelock.dylib";
            }

            log.info("Loading native library: {}", libraryName);
            return Native.load(libraryName, TimeLockLibrary.class);
        } catch (Exception e) {
            log.error("Failed to load native library", e);
            throw new RuntimeException("Failed to load native library: " + e.getMessage(), e);
        }
    }
} 