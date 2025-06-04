@echo off
echo 启动应用程序（本地开发环境）...

REM 首先确保DLL文件在正确位置
call setup-dll.bat

REM 设置开发环境
set SPRING_PROFILES_ACTIVE=dev
set JNA_LIBRARY_PATH=src/main/resources/native
set APP_LICENSE_BYPASS=true

REM 使用Maven启动应用
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Djna.library.path=src/main/resources/native -Dapp.license.bypass=true

echo 应用已停止运行。 