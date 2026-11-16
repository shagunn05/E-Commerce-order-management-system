@echo off
setlocal
set "MAVEN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
if not exist "%MAVEN_CMD%" (
    where mvn >nul 2>nul
    if %errorlevel% equ 0 (
        set "MAVEN_CMD=mvn"
    ) else (
        echo [ERROR] Maven not found! Please ensure Maven is installed or set in PATH.
        exit /b 1
    )
)
"%MAVEN_CMD%" %*
