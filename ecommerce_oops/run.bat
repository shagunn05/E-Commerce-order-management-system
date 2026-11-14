@echo off
TITLE E-Commerce Order Management System (Java OOP)
echo =========================================================================
echo   Compiling Java OOP and Collections Framework Project
echo =========================================================================

if not exist bin mkdir bin

javac -d bin *.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please check JDK installation.
    pause
    exit /b %errorlevel%
)

echo.
echo [SUCCESS] Compilation successful!
echo =========================================================================
echo Choose which program to run:
echo   1. Full E-Commerce OOP Project (Main.java)
echo   2. Collections Framework Dedicated Demo (CollectionsDemo.java)
echo =========================================================================
set /p choice="Enter choice (1 or 2): "

if "%choice%"=="2" (
    echo.
    echo Running CollectionsDemo...
    echo.
    java -cp bin CollectionsDemo
) else (
    echo.
    echo Running Main...
    echo.
    java -cp bin Main
)

echo.
echo =========================================================================
pause
