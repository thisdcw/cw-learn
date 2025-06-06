# CW Framework - 带时间锁和微信支付的企业级框架

这是一个集成了时间锁安全机制和多种支付方式的企业级SpringBoot框架，支持微信支付v2/v3版本API，包含完整的安全锁机制，可用于构建安全可靠的商业应用。

## 核心功能

### 时间锁安全机制

- 基于JNA和C++实现的时间锁，防止应用被盗版使用
- 支持设定软件过期时间，到期后应用自动停止运行
- 许可证信息持久化存储，重启应用也无法绕过
- 机器绑定功能，防止非法复制到其他设备
- 提供灵活的许可证管理API接口

### 微信支付功能

- 支持微信支付V3和V2版本API
- 五种支付方式全覆盖：
  - JSAPI支付（公众号支付）
  - 小程序支付
  - APP支付
  - Native支付（扫码支付）
  - H5支付（移动网页支付）
- 支持支付相关完整功能：
  - 下单支付
  - 订单查询
  - 关闭订单
  - 申请退款
  - 支付回调通知处理
  - 退款回调通知处理
- 安全可靠的签名验证和参数校验

### 其他功能

- 通用响应封装（ResultBean）
- 完整的错误处理机制
- 灵活的配置管理

## 技术栈

- Java 11
- Spring Boot 2.6.6
- 微信支付Java SDK 0.2.15
- JNA 5.13.0 (Java Native Access)
- Hutool 5.8.38 工具库
- Maven 构建管理

## 快速开始

### 先决条件

- JDK 11+
- Maven 3.6+
- CMake 3.0+（用于构建C++原生库）
- 微信支付商户账号（用于微信支付功能）

### 构建步骤

1. 构建C++原生库（时间锁组件）

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

## 配置指南

### 许可证配置

在应用启动时，通过环境变量或系统属性指定许可证密钥：

```bash
java -DLICENSE_KEY=your-license-key -jar target/cw-framework-0.0.1-SNAPSHOT.jar
```

或在`application.yml`中配置：

```yaml
app:
  license:
    key: your-license-key
    expiry-date: 2025-12-31
```

### 微信支付配置

在`application-wxpay.yml`中配置微信支付参数：

```yaml
wx:
  pay:
    # 微信支付V3
    v3:
      app-id: wx8888888888888888  # 公众号AppID
      mch-id: 1900000109          # 商户号
      api-key: API_KEY            # API密钥（V2使用）
      api-v3-key: API_V3_KEY      # APIv3密钥
      cert-path: /path/to/cert/apiclient_cert.p12  # 证书路径
      notify-url: https://example.com/pay/notify   # 支付回调地址
      refund-notify-url: https://example.com/pay/refund/notify  # 退款回调地址
    
    # 其他配置
    mini-app-id: wx9999999999999999  # 小程序AppID
    app-id: wx7777777777777777       # APP应用AppID
```

## 微信支付使用示例

### 发起小程序支付

```java
@Autowired
private WxPayService wxPayService;

public Map<String, Object> createMiniAppPay(String orderId, BigDecimal amount, String openid) {
    // 构建支付请求参数
    WxPayOrderDTO orderDTO = new WxPayOrderDTO();
    orderDTO.setOutTradeNo(orderId);
    orderDTO.setDescription("商品描述");
    orderDTO.setAmount(amount);
    
    // 调用支付服务
    return wxPayService.miniAppPay(orderDTO, openid);
}
```

### 处理支付回调

```java
@PostMapping("/notify")
public String handlePayNotify(HttpServletRequest request) {
    try {
        // 解析支付结果通知
        WxPayNotifyResult result = wxPayService.parsePayNotify(request);
        
        if (result.isSuccess()) {
            // 处理支付成功逻辑
            orderService.handlePaySuccess(result.getOutTradeNo(), result.getTransactionId());
            return "<xml><return_code>SUCCESS</return_code><return_msg>OK</return_msg></xml>";
        }
    } catch (Exception e) {
        log.error("处理支付回调异常", e);
    }
    
    return "<xml><return_code>FAIL</return_code><return_msg>处理失败</return_msg></xml>";
}
```

## API接口

### 许可证管理接口

- `GET /api/license/validate` - 验证当前许可证
- `GET /api/license/machine-id` - 获取当前机器ID
- `GET /api/license/info` - 获取许可证信息
- `POST /api/license/set` - 设置新的许可证

### 微信支付接口

- `POST /api/wx/pay/jsapi` - 发起JSAPI支付
- `POST /api/wx/pay/mini-app` - 发起小程序支付
- `POST /api/wx/pay/app` - 发起APP支付
- `POST /api/wx/pay/native` - 发起Native支付
- `POST /api/wx/pay/h5` - 发起H5支付
- `GET /api/wx/pay/query/{outTradeNo}` - 查询订单
- `POST /api/wx/pay/close/{outTradeNo}` - 关闭订单
- `POST /api/wx/pay/refund` - 申请退款
- `POST /api/wx/pay/notify` - 支付回调通知接口
- `POST /api/wx/pay/refund/notify` - 退款回调通知接口

## 项目结构

```
src/main/java/com/cw/framework/
├── common/                 # 通用组件
│   └── ResultBean.java     # 通用响应封装
├── payment/                # 支付模块
│   ├── config/             # 支付配置
│   ├── controller/         # 支付控制器
│   ├── model/              # 支付数据模型
│   ├── service/            # 支付服务接口与实现
│   └── wechat/             # 微信支付实现
│       ├── v2/             # 微信支付V2版本
│       └── v3/             # 微信支付V3版本
├── security/               # 安全模块
│   ├── controller/         # 许可证控制器
│   ├── LicenseInfo.java    # 许可证信息
│   ├── LicenseValidator.java # 许可证验证
│   ├── TimeLockLibrary.java # JNA接口
│   └── TimeLockService.java # 时间锁服务
└── CwFrameworkApplication.java # 应用入口
```

## 扩展指南

### 添加新的支付方式

1. 在`payment`模块下创建新的支付服务接口和实现
2. 添加相应的配置类和控制器
3. 集成到现有框架中

### 增强安全机制

1. 在`security`包中添加更复杂的验证逻辑
2. 扩展C++库以实现更强大的加密和防破解功能
3. 实现基于网络的许可证验证服务

## 许可证

[MIT](LICENSE)

## 联系方式

如有问题或建议，请联系项目维护者。