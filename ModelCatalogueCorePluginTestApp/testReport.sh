#!/bin/bash

DATE=$(date)


FOLDER="$(echo "target/test_$DATE" | sed "s/  */_/g")"

if [ -e "$FOLDER" ]
    then
        echo "$FOLDER already exists, quitting $0"
        exit 1
fi
mkdir -p "$FOLDER"
cd "$FOLDER"

README="readme.txt"
touch "$README"
echo "Command Used:
Functional:
    ChromeDriver version:
Code branch name: $(git rev-parse --abbrev-ref HEAD)
Code commit hash: $(git rev-parse HEAD)
" > "$README"

echo "$FOLDER"
