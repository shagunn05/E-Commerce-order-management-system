# PowerShell launch script for Integrated Library and Inventory Management System
Write-Host "=========================================================================" -ForegroundColor Cyan
Write-Host "  Compiling Integrated Library and Inventory Management System (Java 27)" -ForegroundColor Cyan
Write-Host "=========================================================================" -ForegroundColor Cyan

if (-not (Test-Path -Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

javac -d bin -sourcepath src src/com/managementsystem/Main.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n[ERROR] Compilation failed! Please check your JDK installation." -ForegroundColor Red
    pause
    exit $LASTEXITCODE
}

Write-Host "`n[SUCCESS] Compilation successful! Starting system...`n" -ForegroundColor Green
java -cp bin com.managementsystem.Main
