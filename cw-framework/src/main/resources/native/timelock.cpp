#include <iostream>
#include <ctime>
#include <string>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <chrono>
#include <vector>
#include <algorithm>
#include <cctype>
#include <cstring>

#ifdef _WIN32
    #include <windows.h>
    // 许可证信息存储文件路径 - Windows
    const char* LICENSE_FILE_PATH = "C:\\ProgramData\\CwFramework\\license.dat";
    const char* LICENSE_DIR_PATH = "C:\\ProgramData\\CwFramework";
#else
    #include <sys/stat.h>
    #include <unistd.h>
    #include <pwd.h>
    // 许可证信息存储文件路径 - Linux/macOS
    std::string getHomeDir() {
        const char* homeDir = getenv("HOME");
        if (homeDir == nullptr) {
            struct passwd* pwd = getpwuid(getuid());
            if (pwd) {
                homeDir = pwd->pw_dir;
            }
        }
        return homeDir ? std::string(homeDir) : std::string();
    }
    
    std::string LICENSE_DIR_PATH = getHomeDir() + "/.cwframework";
    std::string LICENSE_FILE_PATH = LICENSE_DIR_PATH + "/license.dat";
#endif

// 加密密钥，用于简单加密
const char* ENCRYPTION_KEY = "Dn8Vp2Kq9Zs7Lm3";

// 创建目录
bool createDirectory(const std::string& dirPath) {
#ifdef _WIN32
    return CreateDirectoryA(dirPath.c_str(), NULL) || GetLastError() == ERROR_ALREADY_EXISTS;
#else
    return mkdir(dirPath.c_str(), 0755) == 0 || errno == EEXIST;
#endif
}

// 删除文件
bool removeFile(const std::string& filePath) {
#ifdef _WIN32
    return DeleteFileA(filePath.c_str()) != 0;
#else
    return remove(filePath.c_str()) == 0;
#endif
}

// 获取计算机名/主机名
std::string getComputerName() {
#ifdef _WIN32
    char computerName[MAX_COMPUTERNAME_LENGTH + 1];
    DWORD size = MAX_COMPUTERNAME_LENGTH + 1;
    if (GetComputerNameA(computerName, &size)) {
        return std::string(computerName);
    }
#else
    char hostname[256];
    if (gethostname(hostname, sizeof(hostname)) == 0) {
        return std::string(hostname);
    }
#endif
    return "unknown-host";
}

// 简单的加密函数
std::string encryptString(const std::string& input, const std::string& key) {
    std::string result = input;
    for (size_t i = 0; i < input.length(); i++) {
        result[i] = input[i] ^ key[i % key.length()];
    }
    return result;
}

// 简单的解密函数
std::string decryptString(const std::string& input, const std::string& key) {
    return encryptString(input, key); // XOR加密解密使用相同算法
}

// 保存许可证信息到文件系统
bool saveLicenseInfo(const std::string& machineId, const std::string& expireDate, const std::string& licenseKey) {
    try {
        // 创建目录（如果不存在）
        if (!createDirectory(LICENSE_DIR_PATH)) {
            return false;
        }
        
        // 准备要保存的数据
        std::string licenseData = machineId + "|" + expireDate + "|" + licenseKey;
        
        // 加密数据
        std::string encryptedData = encryptString(licenseData, ENCRYPTION_KEY);
        
        // 写入文件
        std::ofstream outFile(LICENSE_FILE_PATH, std::ios::binary);
        if (!outFile.is_open()) {
            return false;
        }
        
        outFile.write(encryptedData.c_str(), encryptedData.length());
        outFile.close();
        
        return true;
    } catch (...) {
        return false;
    }
}

// 从文件系统加载许可证信息
bool loadLicenseInfo(std::string& machineId, std::string& expireDate, std::string& licenseKey) {
    try {
        // 读取文件
        std::ifstream inFile(LICENSE_FILE_PATH, std::ios::binary);
        if (!inFile.is_open()) {
            return false;
        }
        
        // 读取所有数据
        std::string encryptedData;
        std::string line;
        while (std::getline(inFile, line)) {
            encryptedData += line;
        }
        inFile.close();
        
        // 解密数据
        std::string decryptedData = decryptString(encryptedData, ENCRYPTION_KEY);
        
        // 解析数据
        size_t firstDelim = decryptedData.find('|');
        if (firstDelim == std::string::npos) {
            return false;
        }
        
        size_t secondDelim = decryptedData.find('|', firstDelim + 1);
        if (secondDelim == std::string::npos) {
            return false;
        }
        
        machineId = decryptedData.substr(0, firstDelim);
        expireDate = decryptedData.substr(firstDelim + 1, secondDelim - firstDelim - 1);
        licenseKey = decryptedData.substr(secondDelim + 1);
        
        return true;
    } catch (...) {
        return false;
    }
}

extern "C" {
    // 验证时间锁，确认当前时间是否在有效期内
    __declspec(dllexport) bool validateTimeLock(const char* expireTimeStr, const char* licenseKey) {
        try {
            // 获取当前机器ID
            std::string currentMachineId = getComputerName();
            
            // 首先尝试从持久化存储中加载许可证信息
            std::string storedMachineId, storedExpireDate, storedLicenseKey;
            bool hasStoredLicense = loadLicenseInfo(storedMachineId, storedExpireDate, storedLicenseKey);
            
            // 如果有存储的许可证信息，则优先使用存储的过期日期
            std::string effectiveExpireDate;
            if (hasStoredLicense) {
                // 验证机器ID是否匹配
                if (storedMachineId != currentMachineId) {
                    return false; // 机器ID不匹配，可能是复制到其他机器
                }
                
                // 使用存储的过期日期
                effectiveExpireDate = storedExpireDate;
            } else {
                // 没有存储的许可证信息，使用传入的过期日期
                effectiveExpireDate = expireTimeStr;
                
                // 将许可证信息保存到持久化存储
                saveLicenseInfo(currentMachineId, effectiveExpireDate, licenseKey);
            }
            
            // 解析到期时间字符串 (格式: YYYY-MM-DD)
            std::tm expireTm = {};
            std::istringstream ss(effectiveExpireDate);
            ss >> std::get_time(&expireTm, "%Y-%m-%d");
            
            if (ss.fail()) {
                return false;
            }
            
            // 转换为time_t
            std::time_t expireTime = std::mktime(&expireTm);
            
            // 获取当前时间
            std::time_t currentTime = std::time(nullptr);
            
            // 如果当前时间超过到期时间，返回false
            if (currentTime > expireTime) {
                return false;
            }
            
            // 这里可以添加额外的许可证密钥验证逻辑
            // 简单示例: 验证licenseKey长度>10
            if (licenseKey == nullptr || strlen(licenseKey) < 10) {
                return false;
            }
            
            return true;
        } catch (...) {
            return false;
        }
    }
    
    // 获取系统信息作为机器标识符
    __declspec(dllexport) bool getMachineId(char* outBuffer, int bufferSize) {
        try {
            // 获取计算机名/主机名
            std::string machineId = getComputerName();
            
            // 将结果复制到输出缓冲区
            if (outBuffer && bufferSize > 0) {
                #ifdef _WIN32
                strncpy_s(outBuffer, bufferSize, machineId.c_str(), _TRUNCATE);
                #else
                strncpy(outBuffer, machineId.c_str(), bufferSize - 1);
                outBuffer[bufferSize - 1] = '\0';
                #endif
                return true;
            }
            
            return false;
        } catch (...) {
            return false;
        }
    }
    
    // 设置许可证
    __declspec(dllexport) bool setLicense(const char* expireTimeStr, const char* licenseKey) {
        try {
            // 获取当前机器ID
            std::string machineId = getComputerName();
            
            // 保存许可证信息
            return saveLicenseInfo(machineId, expireTimeStr, licenseKey);
        } catch (...) {
            return false;
        }
    }
    
    // 重置许可证（用于测试）
    __declspec(dllexport) bool resetLicense() {
        try {
            // 删除许可证文件
            return removeFile(LICENSE_FILE_PATH);
        } catch (...) {
            return false;
        }
    }
} 