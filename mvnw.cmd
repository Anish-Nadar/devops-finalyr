@REM Marathon Portal Maven Wrapper Script
@echo off
setlocal

where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    mvn %*
    exit /b %ERRORLEVEL%
)

set "INTELLIJ_MVN=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
if exist "%INTELLIJ_MVN%" (
    call "%INTELLIJ_MVN%" %*
    exit /b %ERRORLEVEL%
)

echo ERROR: Maven not found on PATH or in standard JetBrains location.
exit /b 1

