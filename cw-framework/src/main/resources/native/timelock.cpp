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
#include <random>
#include <cstdint>
#include <functional>
#include <array>

#ifdef _WIN32
    #include <windows.h>
    #include <iphlpapi.h>
    #include <intrin.h>
    #pragma comment(lib, "iphlpapi.lib")
    // 许可证信息存储文件路径 - Windows
    const char* LICENSE_FILE_PATH = R"(C:\ProgramData\CwFramework\license.dat)";
    const char* LICENSE_DIR_PATH = "C:\\ProgramData\\CwFramework";
#else
    #include <sys/stat.h>
    #include <unistd.h>
    #include <pwd.h>
    #include <sys/ioctl.h>
    #include <net/if.h>
    #include <ifaddrs.h>
    #include <netinet/in.h>
    #include <sys/types.h>
    #include <sys/socket.h>
    #include <arpa/inet.h>
    #include <cpuid.h>
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

// 安全密钥，用于AES加密
const std::array<uint8_t, 32> AES_KEY = {
    0x2B, 0x7E, 0x15, 0x16, 0x28, 0xAE, 0xD2, 0xA6,
    0xAB, 0xF7, 0x15, 0x88, 0x09, 0xCF, 0x4F, 0x3C,
    0x2B, 0x7E, 0x15, 0x16, 0x28, 0xAE, 0xD2, 0xA6,
    0xAB, 0xF7, 0x15, 0x88, 0x09, 0xCF, 0x4F, 0x3C
};

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

// 获取CPU信息
std::string getCpuId() {
    std::stringstream ss;
#ifdef _WIN32
    int cpuInfo[4] = {0};
    __cpuid(cpuInfo, 1);
    ss << std::hex << std::setfill('0')
       << std::setw(8) << cpuInfo[0]
       << std::setw(8) << cpuInfo[1]
       << std::setw(8) << cpuInfo[2]
       << std::setw(8) << cpuInfo[3];
#else
    unsigned int eax, ebx, ecx, edx;
    if (__get_cpuid(1, &eax, &ebx, &ecx, &edx)) {
        ss << std::hex << std::setfill('0')
           << std::setw(8) << eax
           << std::setw(8) << ebx
           << std::setw(8) << ecx
           << std::setw(8) << edx;
    }
#endif
    return ss.str();
}

// 获取MAC地址
std::string getMacAddress() {
    std::stringstream ss;
#ifdef _WIN32
    IP_ADAPTER_INFO adapterInfo[16];
    DWORD dwBufLen = sizeof(adapterInfo);
    
    DWORD dwStatus = GetAdaptersInfo(adapterInfo, &dwBufLen);
    if (dwStatus == ERROR_SUCCESS) {
        PIP_ADAPTER_INFO pAdapterInfo = adapterInfo;
        if (pAdapterInfo) {
            for (UINT i = 0; i < pAdapterInfo->AddressLength; i++) {
                ss << std::hex << std::setfill('0') << std::setw(2) 
                   << static_cast<int>(pAdapterInfo->Address[i]);
                if (i < pAdapterInfo->AddressLength - 1) {
                    ss << ":";
                }
            }
        }
    }
#else
    struct ifaddrs *ifaddr, *ifa;
    if (getifaddrs(&ifaddr) == -1) {
        return "";
    }
    
    for (ifa = ifaddr; ifa != NULL; ifa = ifa->ifa_next) {
        if (ifa->ifa_addr && ifa->ifa_addr->sa_family == AF_PACKET) {
            struct sockaddr_ll *s = (struct sockaddr_ll*)ifa->ifa_addr;
            for (int i = 0; i < 6; i++) {
                ss << std::hex << std::setfill('0') << std::setw(2) 
                   << static_cast<int>(s->sll_addr[i]);
                if (i < 5) {
                    ss << ":";
                }
            }
            break;
        }
    }
    
    freeifaddrs(ifaddr);
#endif
    return ss.str();
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

// 生成唯一的机器ID
std::string generateMachineId() {
    std::string cpuId = getCpuId();
    std::string macAddress = getMacAddress();
    std::string computerName = getComputerName();
    
    // 组合所有硬件信息
    std::string combinedInfo = cpuId + macAddress + computerName;
    
    // 使用SHA-256哈希算法（简化版）
    std::hash<std::string> hasher;
    size_t hashValue = hasher(combinedInfo);
    
    std::stringstream ss;
    ss << std::hex << std::setfill('0') << std::setw(16) << hashValue;
    
    return ss.str();
}

// 简单的AES加密（实际应用中应使用专业加密库）
std::vector<uint8_t> aesEncrypt(const std::string& input) {
    std::vector<uint8_t> result;
    result.reserve(input.size());
    
    // 简化的AES加密实现（实际应用中应使用OpenSSL等库）
    for (size_t i = 0; i < input.length(); i++) {
        uint8_t encrypted = input[i] ^ AES_KEY[i % AES_KEY.size()];
        encrypted = (encrypted << 4) | (encrypted >> 4); // 简单的位运算
        result.push_back(encrypted);
    }
    
    return result;
}

// 简单的AES解密
std::string aesDecrypt(const std::vector<uint8_t>& input) {
    std::string result;
    result.reserve(input.size());
    
    // 简化的AES解密实现
    for (size_t i = 0; i < input.size(); i++) {
        uint8_t decrypted = input[i];
        decrypted = (decrypted >> 4) | (decrypted << 4); // 反向位运算
        decrypted = decrypted ^ AES_KEY[i % AES_KEY.size()];
        result.push_back(static_cast<char>(decrypted));
    }
    
    return result;
}

// 将二进制数据转换为十六进制字符串
std::string toHexString(const std::vector<uint8_t>& data) {
    std::stringstream ss;
    for (uint8_t byte : data) {
        ss << std::hex << std::setfill('0') << std::setw(2) << static_cast<int>(byte);
    }
    return ss.str();
}

// 将十六进制字符串转换为二进制数据
std::vector<uint8_t> fromHexString(const std::string& hexStr) {
    std::vector<uint8_t> result;
    for (size_t i = 0; i < hexStr.length(); i += 2) {
        std::string byteString = hexStr.substr(i, 2);
        uint8_t byte = static_cast<uint8_t>(std::stoi(byteString, nullptr, 16));
        result.push_back(byte);
    }
    return result;
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
        std::vector<uint8_t> encryptedData = aesEncrypt(licenseData);
        std::string hexData = toHexString(encryptedData);
        
        // 写入文件
        std::ofstream outFile(LICENSE_FILE_PATH, std::ios::binary);
        if (!outFile.is_open()) {
            return false;
        }
        
        outFile << hexData;
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
        std::string hexData;
        std::getline(inFile, hexData);
        inFile.close();
        
        // 解密数据
        std::vector<uint8_t> encryptedData = fromHexString(hexData);
        std::string decryptedData = aesDecrypt(encryptedData);
        
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

// 验证许可证密钥
bool validateLicenseKey(const std::string& licenseKey, const std::string& machineId) {
    // 实际应用中应使用数字签名或其他高级验证方法
    if (licenseKey.length() < 16) {
        return false;
    }
    
    // 检查许可证密钥是否包含机器ID的哈希
    std::hash<std::string> hasher;
    size_t machineIdHash = hasher(machineId);
    std::stringstream ss;
    ss << std::hex << std::setfill('0') << std::setw(8) << (machineIdHash & 0xFFFFFFFF);
    std::string machineIdHashStr = ss.str();
    
    // 检查许可证密钥是否包含机器ID哈希（简化版）
    return licenseKey.find(machineIdHashStr) != std::string::npos;
}

extern "C" {
    // 验证时间锁，确认当前时间是否在有效期内
    #ifdef _WIN32
    __declspec(dllexport)
    #else
    __attribute__((visibility("default")))
    #endif
    bool validateTimeLock(const char* expireTimeStr, const char* licenseKey) {
        try {
            // 获取当前机器ID
            std::string currentMachineId = generateMachineId();
            
            // 首先尝试从持久化存储中加载许可证信息
            std::string storedMachineId, storedExpireDate, storedLicenseKey;
            bool hasStoredLicense = loadLicenseInfo(storedMachineId, storedExpireDate, storedLicenseKey);
            
            // 如果有存储的许可证信息，则优先使用存储的过期日期
            std::string effectiveExpireDate;
            std::string effectiveLicenseKey;
            
            if (hasStoredLicense) {
                // 验证机器ID是否匹配
                if (storedMachineId != currentMachineId) {
                    return false; // 机器ID不匹配，可能是复制到其他机器
                }
                
                // 使用存储的过期日期和许可证密钥
                effectiveExpireDate = storedExpireDate;
                effectiveLicenseKey = storedLicenseKey;
            } else {
                // 没有存储的许可证信息，使用传入的过期日期和许可证密钥
                if (!expireTimeStr || !licenseKey) {
                    return false;
                }
                
                effectiveExpireDate = expireTimeStr;
                effectiveLicenseKey = licenseKey;
                
                // 将许可证信息保存到持久化存储
                saveLicenseInfo(currentMachineId, effectiveExpireDate, effectiveLicenseKey);
            }
            
            // 验证许可证密钥
            if (!validateLicenseKey(effectiveLicenseKey, currentMachineId)) {
                return false;
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
            return currentTime <= expireTime;
        } catch (...) {
            return false;
        }
    }
    
    // 获取系统信息作为机器标识符
    #ifdef _WIN32
    __declspec(dllexport)
    #else
    __attribute__((visibility("default")))
    #endif
    bool getMachineId(char* outBuffer, int bufferSize) {
        try {
            // 获取机器ID
            std::string machineId = generateMachineId();
            
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
    #ifdef _WIN32
    __declspec(dllexport)
    #else
    __attribute__((visibility("default")))
    #endif
    bool setLicense(const char* expireTimeStr, const char* licenseKey) {
        try {
            if (!expireTimeStr || !licenseKey) {
                return false;
            }
            
            // 获取当前机器ID
            std::string machineId = generateMachineId();
            
            // 保存许可证信息
            return saveLicenseInfo(machineId, expireTimeStr, licenseKey);
        } catch (...) {
            return false;
        }
    }
    
    // 重置许可证（用于测试）
    #ifdef _WIN32
    __declspec(dllexport)
    #else
    __attribute__((visibility("default")))
    #endif
    bool resetLicense() {
        try {
            // 删除许可证文件
            return removeFile(LICENSE_FILE_PATH);
        } catch (...) {
            return false;
        }
    }
    
    // 生成许可证密钥（仅用于开发/测试）
    #ifdef _WIN32
    __declspec(dllexport)
    #else
    __attribute__((visibility("default")))
    #endif
    bool generateLicenseKey(const char* machineId, char* outBuffer, int bufferSize) {
        try {
            if (!machineId || !outBuffer || bufferSize <= 0) {
                return false;
            }
            
            // 生成随机许可证密钥
            std::string machineIdStr(machineId);
            
            // 计算机器ID的哈希值
            std::hash<std::string> hasher;
            size_t machineIdHash = hasher(machineIdStr);
            
            // 创建随机数生成器
            std::random_device rd;
            std::mt19937 gen(rd());
            std::uniform_int_distribution<> dis(0, 15);
            
            // 生成随机许可证密钥
            std::stringstream ss;
            ss << std::hex << std::setfill('0') << std::setw(8) << (machineIdHash & 0xFFFFFFFF);
            
            // 添加随机部分
            for (int i = 0; i < 16; i++) {
                ss << std::hex << dis(gen);
            }
            
            // 添加时间戳
            auto now = std::chrono::system_clock::now();
            auto now_ms = std::chrono::time_point_cast<std::chrono::milliseconds>(now);
            auto epoch = now_ms.time_since_epoch();
            auto value = std::chrono::duration_cast<std::chrono::milliseconds>(epoch).count();
            ss << std::hex << std::setfill('0') << std::setw(12) << (value & 0xFFFFFFFFFFFF);
            
            std::string licenseKey = ss.str();
            
            // 复制到输出缓冲区
            #ifdef _WIN32
            strncpy_s(outBuffer, bufferSize, licenseKey.c_str(), _TRUNCATE);
            #else
            strncpy(outBuffer, licenseKey.c_str(), bufferSize - 1);
            outBuffer[bufferSize - 1] = '\0';
            #endif
            
            return true;
        } catch (...) {
            return false;
        }
    }
} 