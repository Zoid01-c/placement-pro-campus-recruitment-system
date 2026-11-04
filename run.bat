@echo off
if not exist "bin\Main.class" (
    echo [ERROR] Application is not compiled yet. Running compile.bat first...
    call compile.bat
)

echo Starting PLACEMENT PRO...
java -cp bin Main %*
