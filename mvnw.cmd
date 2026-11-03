@REM Marathon Portal Maven Wrapper Script
@echo off
setlocal

where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 goto :run_system_mvn

set "INTELLIJ_MVN=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
if exist "%INTELLIJ_MVN%" goto :run_intellij_mvn

echo ERROR: Maven not found on PATH or in standard JetBrains location.
exit /b 1

:run_system_mvn
mvn %*
goto :finish

:run_intellij_mvn
call "%INTELLIJ_MVN%" %*
goto :finish

:finish
exit /b %ERRORLEVEL%
