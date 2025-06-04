# CW Framework - 带时间锁的SpringBoot框架

这是一个使用JNA和C++实现时间锁的SpringBoot2框架，可以让JAR包在一年或指定时间之后无法运行，防止盗版破解。

## 功能特性

- 基于JNA和C++实现的时间锁机制
- 支持设定过期时间，到期后应用将无法运行
- **许可证持久化存储**，即使重启应用也无法绕过过期限制
- 支持机器绑定，防止非法复制
- 提供完整的用户管理模块作为业务示例
- 提供灵活的许可证管理API

## 技术栈

- Java 8
- Spring Boot 2.6.6
- JNA 5.13.0
- C++ 11
- Maven

## 快速开始

### 先决条件

- JDK 8+
- Maven 3.6+
- CMake 3.0+
- Visual Studio 2022或其他C++编译器

### 构建步骤

1. 构建C++原生库

```bash
# Windows
build-native.bat

# Linux/MacOS
chmod +x build-native.sh
./build-native.sh
```

2. 编译Java项目

```bash
mvn clean package
```

3. 运行应用

```bash
java -jar target/cw-framework-0.0.1-SNAPSHOT.jar
```

## 许可证配置

在应用启动时，可以通过环境变量或系统属性指定许可证密钥：

```bash
java -DLICENSE_KEY=your-license-key -jar target/cw-framework-0.0.1-SNAPSHOT.jar
```

或者在`application.yml`中配置：

```yaml
app:
  license:
    key: your-license-key
```

## 许可证存储机制

该框架使用加密存储机制保存许可证信息：

- **Windows**: 许可证信息存储在 `C:\ProgramData\CwFramework\license.dat`
- **Linux/macOS**: 许可证信息存储在 `~/.cwframework/license.dat`

存储的信息包括：
- 机器标识符（防止复制到其他机器）
- 过期日期（确保重启后仍然检查正确的过期时间）
- 许可证密钥

## API接口

### 用户管理

- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 获取指定ID的用户
- `POST /api/users` - 创建新用户
- `PUT /api/users/{id}` - 更新用户信息
- `DELETE /api/users/{id}` - 删除用户

### 许可证管理

- `GET /api/license/validate` - 验证当前许可证
- `GET /api/license/machine-id` - 获取当前机器ID
- `POST /api/license/validate-with-date` - 使用指定日期验证许可证
- `GET /api/license/info` - 获取许可证信息
- `POST /api/license/set` - 设置新的许可证
- `POST /api/license/reset` - 重置许可证（仅用于测试）

### 测试接口

- `POST /api/test/quick-expire` - 创建一个1分钟后过期的许可证
- `POST /api/test/expired` - 创建一个已过期的许可证
- `POST /api/test/days-valid/{days}` - 创建一个指定天数后过期的许可证
- `POST /api/test/reset` - 重置许可证
- `GET /api/test/validate` - 验证当前许可证

## 扩展方法

要扩展此框架：

1. 在`com.cw.framework.security`包中添加更多的安全验证逻辑
2. 在C++库中添加更复杂的加密和验证算法
3. 实现基于网络的许可证验证服务

## 贡献

欢迎提交问题和拉取请求！

## 许可证

[MIT](LICENSE)