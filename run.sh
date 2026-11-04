#!/usr/bin/env bash
if [ ! -f "bin/Main.class" ]; then
    echo "[!] Binaries missing. Compiling first..."
    ./compile.sh
fi

java -cp bin Main "$@"
