@echo off
TITLE Amazon Sales Analytics & Management System (Spring Boot 3)
echo =========================================================================
echo   Launching Amazon Sales Analytics & Management System
echo   Backend: Spring Boot 3 + Spring JDBC | Database: PostgreSQL 16
echo =========================================================================
echo.

call "%~dp0mvnw.bat" spring-boot:run
if %errorlevel% neq 0 (
    echo.
    echo [INFO] PostgreSQL connection failed or not running.
    echo [INFO] Launching with Standalone In-Memory H2 Profile (PostgreSQL Mode)...
    call "%~dp0mvnw.bat" spring-boot:run -Dspring-boot.run.profiles=h2
)
pause
