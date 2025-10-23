# eclipse-temurin 是高质量的 OpenJDK 构建版
# jre-alpine 提供了最小的 Java 运行环境
FROM eclipse-temurin:21-jre-alpine

# 设置工作目录
WORKDIR /app

# 复制 jar 包
# 注意：这里使用了通配符 *.jar，假设 target 目录只有一个 jar 包
# 如果不是，请修改为你的确切 jar 包名称
COPY blog-server.jar app.jar

# 暴露你指定的 12345 端口
EXPOSE 12345

# 启动 Spring Boot 应用
# $JAVA_OPTS 将由 docker-compose.yml 传入，用于内存优化
# --spring.profiles.active=prod 激活 prod 配置
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]