@echo off
setlocal
set "GRADLE_VERSION=8.10.2"
if not defined GRADLE_USER_HOME set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "DIST_DIR=%GRADLE_USER_HOME%\wrapper\dists\gradle-%GRADLE_VERSION%-bin"
set "GRADLE_HOME=%DIST_DIR%\gradle-%GRADLE_VERSION%"
set "ARCHIVE=%DIST_DIR%\gradle-%GRADLE_VERSION%-bin.zip"
if not exist "%GRADLE_HOME%\bin\gradle.bat" (
  if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"
  if not exist "%ARCHIVE%" powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ARCHIVE%'"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath '%ARCHIVE%' -DestinationPath '%DIST_DIR%' -Force"
)
call "%GRADLE_HOME%\bin\gradle.bat" %*
exit /b %ERRORLEVEL%
