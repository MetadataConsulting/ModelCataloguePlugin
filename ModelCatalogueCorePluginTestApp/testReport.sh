#!/bin/bash
# Creates a new folder for test reports to go in and outputs the folder name.

DATE=$(date "+date_%Y_%m_%d_time_%H_%M_%S")


# Create folder
FOLDER="$(echo "target/test_$DATE" | sed "s/  */_/g")"

if [ -e "$FOLDER" ]
    then
        echo "$FOLDER already exists, quitting $0"
        exit 1
fi
mkdir -p "$FOLDER"
cd "$FOLDER"

# Create a README file
README="readme.txt"
touch "$README"

# Add text to the README file
echo "Command Used:
Functional:
    ChromeDriver version:
Code branch name: $(git rev-parse --abbrev-ref HEAD)
Code commit hash: $(git rev-parse HEAD)
" > "$README"

echo "$FOLDER" # The output of this script.
