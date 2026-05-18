@echo off
setlocal
set MVNW_VERSION=3.9.9
set BASE_DIR=%~dp0
set MAVEN_HOME=%BASE_DIR%\.mvn\apache-maven-%MVNW_VERSION%
set MAVEN_BIN=%MAVEN_HOME%\bin\mvn.cmd

if not exist "%MAVEN_BIN%" (
  mkdir "%BASE_DIR%\.mvn" 2>nul
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/%MVNW_VERSION%/binaries/apache-maven-%MVNW_VERSION%-bin.zip' -OutFile '%BASE_DIR%\.mvn\apache-maven-%MVNW_VERSION%-bin.zip'; Expand-Archive -Force '%BASE_DIR%\.mvn\apache-maven-%MVNW_VERSION%-bin.zip' '%BASE_DIR%\.mvn'"
)

"%MAVEN_BIN%" %*
