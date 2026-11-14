@echo off
TITLE Integrated Library and Inventory Management System
echo =========================================================================
echo   Compiling Integrated Library and Inventory Management System (Java 27)
echo =========================================================================

if not exist bin mkdir bin

javac -d bin -sourcepath src src\com\managementsystem\Main.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please ensure JDK 17+ or Java 27 is installed.
    pause
    exit /b %errorlevel%
)

echo.
echo [SUCCESS] Compilation successful!
echo Launching application...
echo =========================================================================
echo.

java -cp bin com.managementsystem.Main

echo.
echo System session ended.
pause
