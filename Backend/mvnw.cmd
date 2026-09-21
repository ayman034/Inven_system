@echo off
setlocal

where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    call mvn %*
    exit /b %ERRORLEVEL%
)

if exist "%LOCALAPPDATA%\Maven\apache-maven-3.9.11\bin\mvn.cmd" (
    call "%LOCALAPPDATA%\Maven\apache-maven-3.9.11\bin\mvn.cmd" %*
    exit /b %ERRORLEVEL%
)

echo Maven was not found. Install Maven or set MAVEN_HOME, then run this command again.
exit /b 1
