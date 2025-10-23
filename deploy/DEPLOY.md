# 部署说明

## 1. 概要

本文档说明在 CentOS 系统上安装并配置 Docker 与 docker-compose 的步骤，以及这是一个部署三端架构的项目的通用模板

---

## 2. 安装 Docker

### 2.1 卸载旧版（可选，避免冲突）

```bash
sudo yum remove -y docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
```

### 2.2 安装必要工具

```bash
sudo yum install -y yum-utils
```

### 2.3 添加 Docker YUM 仓库（示例使用阿里镜像）

```bash
sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

### 2.4 安装 Docker Engine

```bash
sudo yum install -y docker-ce docker-ce-cli containerd.io
```

### 2.5 启动并设置开机自启

```bash
sudo systemctl start docker

sudo systemctl enable docker
```

### 2.6 配置镜像加速器（强烈建议）

```bash
sudo mkdir -p /etc/docker

sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://hub-mirror.c.163.com",
    "https://mirror.baidubce.com",
    "https://docker.m.daocloud.io"
  ]
}
EOF

sudo systemctl daemon-reload

sudo systemctl restart docker
```

### 2.7 验证安装

```bash
docker --version

docker info
```

**注意**：`docker info` 输出中应包含你刚配置的 `Registry Mirrors`。

---

## 3. 安装 docker-compose

> 推荐把可执行文件放到 `/usr/local/bin`（系统 PATH 通常包含该路径）。

### 3.1 下载（自动适配系统与架构）

```bash
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```

### 3.2 授予执行权限

```bash
sudo chmod +x /usr/local/bin/docker-compose
```

### 3.3 （可选）创建软链到 `/usr/bin`（方便兼容某些脚本）

```bash
# 如果 /usr/bin/docker-compose 不存在
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
```

**如果出现 `ln: failed to create symbolic link '/usr/bin/docker-compose': File exists`：**

* 说明目标文件已存在。检查哪个可执行文件被使用：

  ```bash
  ls -l /usr/bin/docker-compose
  
  which docker-compose
  /usr/bin/docker-compose --version
  ```
* 若要覆盖：

  ```bash
  sudo rm -f /usr/bin/docker-compose
  
  sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
  ```
* 或者直接使用 `/usr/local/bin/docker-compose`，无需创建软链。

### 3.4 验证安装

```bash
docker-compose --version
```

### 3.5 示例：快速检查脚本

把下面脚本保存为 `check_docker.sh`，运行检查安装状态：

```bash
#!/bin/bash
echo "docker version:"
docker --version || echo "docker 未安装或不可用"
echo
echo "docker info (first lines):"
docker info 2>/dev/null | head -n 20 || echo "docker 服务未运行或权限不足"
echo
echo "docker-compose version:"
docker-compose --version || echo "docker-compose 未安装或不可用"
```

运行：

```bash
chmod +x check_docker.sh

./check_docker.sh
```

---

## 4.部署目录介绍

### 4.1 admin目录

- 确保构建目录是dist/
- 部署时将端口改成项目实际端口即可
- nginx文件里的端口也要和实际端口一致

### 4.2 nginx目录

- 这是三个项目之间使用的nginx
- 只需要将域名和端口对上即可

### 4.3 server目录

- 将jar包名和端口号以及要激活的配置文件改成自己的即可

### 4.4 web目录

- 将构建好的文件夹名称和端口号改成自己的即可
---
## 5.部署

### 5.1授予`deploy.sh`权限

```bash
chmod +x deploy.sh
```

### 5.2 执行

```bash
./deploy.sh
```
---
## 6.更新ssl证书

1. 替换ssl目录里的pem和key,名字记得改成blog

2. 执行`docker-compose exec nginx nginx -s reload`或者执行`./deploy.sh`


## 7.其他

### 7.1 docker的重启策略

- `no`:在任何情况下都不会自动重启
- `on-failure`:仅在容器以“失败”状态（即非 0 退出码）退出时才自动重启
- `always`:总是会尝试重启除非你删除了容器
- `unless-stopped`:如果容器因崩溃而停止or服务器重启，它会自动重启

