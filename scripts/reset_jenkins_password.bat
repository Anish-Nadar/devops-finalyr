@echo off
:: Batch script to reset Jenkins password for Anish201205 to Anish@05
:: Automatically requests Administrator privileges if not already elevated

NET SESSION >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo Requesting Administrator privileges to update Jenkins service files...
    powershell -Command "Start-Process '%~f0' -Verb RunAs"
    exit /b
)

echo ==========================================================
echo RESETTING JENKINS PASSWORD TO: Anish@05
echo ==========================================================

powershell -ExecutionPolicy Bypass -File "%~dp0reset_jenkins_password.ps1"

echo.
pause

