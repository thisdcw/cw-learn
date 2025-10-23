#!/bin.bash
# 确保脚本在任何命令失败时立即退出
set -e

echo "=============================================="
echo "    Blog Deployment Script"
echo "=============================================="

# 步骤 1: 停止并移除旧的容器、网络
# 使用 --remove-orphans 清理可能存在的旧服务
echo "Step 1: Stopping and removing old containers..."
docker-compose down --remove-orphans

# 步骤 2: 拉取 Dockerfile 中引用的最新基础镜像
echo "Step 2: Pulling latest base images (node, nginx, jre)..."
docker-compose pull

# 步骤 3: 强制重新构建所有服务
# --no-cache 确保使用最新的代码 (如果你需要)
# 通常不需要 --no-cache，除非你遇到缓存问题
echo "Step 3: Building services..."
docker-compose build

# 步骤 4: 以后台模式启动所有服务
echo "Step 4: Starting services..."
docker-compose up -d

# 步骤 5: 清理不再使用的 Docker 镜像 (悬空的)
echo "Step 5: Cleaning up dangling images..."
docker image prune -f

echo "=============================================="
echo "    Deployment Complete!"
echo "=============================================="

# 步骤 6: 显示当前运行的容器
docker-compose ps