#!/usr/bin/env bash
# ==========================================================
#        Compiling PLACEMENT PRO (Java 8+ Compatible)
# ==========================================================

mkdir -p bin

javac -encoding UTF-8 -d bin -sourcepath src src/Main.java

if [ $? -eq 0 ]; then
    echo ""
    echo "[SUCCESS] Compilation successful! Output placed in bin/"
    echo "To launch the system, run: ./run.sh"
else
    echo ""
    echo "[ERROR] Compilation failed. Please check JDK 8+ installation."
fi
