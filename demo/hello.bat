@echo off
rem WinScript Support 高亮验证脚本（未闭合的 % 在最后一行）
:: 双冒号注释风格
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "NAME=WinScript"
set "COUNT=%1"

if "%COUNT%"=="" (
    echo Usage: %~nx0 ^<count^>
    goto :eof
)

for /L %%i in (1,1,%COUNT%) do (
    echo Item %%i of %COUNT%: !NAME!
)

call :greet %COUNT%
exit /b 0

:greet
echo Hello from %SCRIPT_DIR%, arg=%1
goto :eof

rem 下面这行故意不闭合变量，应显示 BAD_CHARACTER：echo %UNCLOSED
