@echo off
chcp 65001 >nul
title MySQL root 密码重置工具
set LOG=%~dp0reset_log.txt
echo ===== %date% %time% 重置开始 ===== > "%LOG%"

echo [1/5] 停止 MySQL80 服务 >> "%LOG%"
net stop MySQL80 >> "%LOG%" 2>&1
echo step1 停止服务 errorlevel=%errorlevel% >> "%LOG%"
if errorlevel 1 goto :err

echo [2/5] 启动临时 mysqld(skip-grant-tables) >> "%LOG%"
start /b "" "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" --defaults-file="C:\ProgramData\MySQL\MySQL Server 8.0\my.ini" --skip-grant-tables >> "%LOG%" 2>&1

echo [3/5] 等待 MySQL 就绪 >> "%LOG%"
set /a n=0
:wait_loop
timeout /t 1 /nobreak >nul
set /a n+=1
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqladmin.exe" -uroot --skip-password ping >> "%LOG%" 2>&1
if errorlevel 1 (
    if %n% LSS 30 goto wait_loop
    echo [错误] MySQL 启动超时(30秒) >> "%LOG%"
    goto :err
)
echo 就绪，等待了 %n% 秒 >> "%LOG%"

echo [4/5] 重置 root 密码为 123456 >> "%LOG%"
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -uroot --skip-password -e "SET GLOBAL validate_password.policy=LOW;" >> "%LOG%" 2>&1
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -uroot --skip-password -e "SET GLOBAL validate_password.length=6;" >> "%LOG%" 2>&1
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -uroot --skip-password -e "FLUSH PRIVILEGES; ALTER USER 'root'@'localhost' IDENTIFIED BY '123456'; FLUSH PRIVILEGES;" >> "%LOG%" 2>&1
echo step4 重置密码 errorlevel=%errorlevel% >> "%LOG%"
if errorlevel 1 goto :err

echo [5/5] 关闭临时实例，恢复服务 >> "%LOG%"
taskkill /f /im mysqld.exe >> "%LOG%" 2>&1
timeout /t 3 /nobreak >nul
net start MySQL80 >> "%LOG%" 2>&1
echo step5 启动服务 errorlevel=%errorlevel% >> "%LOG%"

echo [验证] 用新密码 123456 测试连接 >> "%LOG%"
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -uroot -p123456 -e "SELECT 1;" >> "%LOG%" 2>&1
echo 验证 errorlevel=%errorlevel% >> "%LOG%"

echo.
echo ================================================
echo   脚本执行完毕！请把 reset_log.txt 内容发给助手
echo ================================================
pause
exit /b 0

:err
taskkill /f /im mysqld.exe >> "%LOG%" 2>&1
echo [FAILED] 脚本出错，请把 reset_log.txt 内容发给助手 >> "%LOG%"
echo.
echo ================================================
echo   执行出错！请把 reset_log.txt 内容发给助手
echo ================================================
pause
exit /b 1
