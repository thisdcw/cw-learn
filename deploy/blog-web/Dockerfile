FROM node:22-alpine

# 设置工作目录
WORKDIR /app

# 复制构建好的 .output 文件夹到容器中
# 假设 Dockerfile 和 .output 在同一级目录 (即 blog-web/)
COPY .output .

# 暴露 Nuxt 默认的 3000 端口
EXPOSE 3000

# 设置环境变量，使其在生产模式下运行
ENV NODE_ENV=production

# 启动 Nuxt 服务器
CMD ["node", ".output/server/index.mjs"]