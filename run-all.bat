@echo off
TITLE Amazon Sales Analytics & Management System - Full Stack Launcher
cls
echo =========================================================================
echo   🛒 Amazon Sales Analytics & Management System
echo   Enterprise-Grade Full-Stack Application
echo   Backend: Spring Boot 3 + Spring JDBC | Database: PostgreSQL 16 / H2
echo   Frontend: React.js + Vite | Documentation: Swagger UI
echo =========================================================================
echo.

echo [1/2] Starting Spring Boot 3 Backend Server on port 8080...
start "Amazon Sales Backend (Spring Boot 3)" cmd /k "cd /d "%~dp0amazon-sales-management" && run.bat"

timeout /t 3 /nobreak >nul

echo [2/2] Starting React + Vite Frontend Dashboard on port 5173...
start "Amazon Sales Frontend (React + Vite)" cmd /k "cd /d "%~dp0amazon-sales-frontend" && npm run dev"

echo.
echo =========================================================================
echo   System Services Successfully Launched!
echo.
echo   - React Web Dashboard:       http://localhost:5173
echo   - Spring Boot REST API:      http://localhost:8080/api/v1/sales
echo   - Swagger / OpenAPI Docs:    http://localhost:8080/swagger-ui/index.html
echo   - OpenAPI JSON Schema:       http://localhost:8080/v3/api-docs
echo   - Spring Actuator Health:    http://localhost:8080/actuator/health
echo =========================================================================
echo.
pause
