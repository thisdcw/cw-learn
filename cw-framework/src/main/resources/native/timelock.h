#ifndef TIMELOCK_H
#define TIMELOCK_H

#ifdef __cplusplus
extern "C" {
#endif

// 验证时间锁，确认当前时间是否在有效期内
__declspec(dllexport) bool validateTimeLock(const char* expireTimeStr, const char* licenseKey);

// 获取系统信息作为机器标识符
__declspec(dllexport) bool getMachineId(char* outBuffer, int bufferSize);

// 设置许可证
__declspec(dllexport) bool setLicense(const char* expireTimeStr, const char* licenseKey);

// 重置许可证（用于测试）
__declspec(dllexport) bool resetLicense();

#ifdef __cplusplus
}
#endif

#endif // TIMELOCK_H 