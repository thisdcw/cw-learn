@echo off
echo 设置TimeLock库文件...

REM 创建目录结构
mkdir "src\main\resources\native\win32-x86-64" 2>nul

REM 复制DLL文件
copy "src\main\resources\native\build\timelock.dll" "src\main\resources\native\win32-x86-64\timelock.dll" /Y
copy "src\main\resources\native\build\timelock.dll" "src\main\resources\native\timelock.dll" /Y

echo 完成!
echo 现在可以使用以下命令启动应用:
echo java -Djna.library.path=src/main/resources/native -Dapp.license.bypass=true -jar target/cw-framework-0.0.1-SNAPSHOT.jar 