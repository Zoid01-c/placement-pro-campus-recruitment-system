@echo off
echo ==========================================================
echo        Compiling PLACEMENT PRO (Java 8+ Compatible)
echo ==========================================================

if not exist "bin" mkdir bin

javac -encoding UTF-8 -d bin -sourcepath src src/Main.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] Compilation successful! Output written to bin/
    echo To run the application, execute: run.bat
) else (
    echo.
    echo [ERROR] Compilation failed! Please verify your Java JDK installation.
)
pause
