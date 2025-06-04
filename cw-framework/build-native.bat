@echo off
echo 开始构建C++原生库...

REM 创建构建目录
mkdir build 2>NUL
cd build

REM 运行CMake配置
echo 运行CMake配置...
cmake -G "Visual Studio 17 2022" -A x64 ..\src\main\resources\native

REM 编译
echo 编译C++库...
cmake --build . --config Release

REM 复制DLL到resources目录
echo 复制DLL到resources目录...
copy /Y bin\Release\timelock.dll ..\src\main\resources\native\

echo C++库构建完成!
cd ..

exit /b 0 