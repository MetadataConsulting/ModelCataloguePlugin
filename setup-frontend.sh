#!/bin/bash

# fail if any line fails
set -e

for file in */ .*/ ; do
    if [ -f $file/package.json ]; then
        cd "$file"
        echo "running npm install in $file"
        exec npm install
        cd ..
    fi
done

for file in */ .*/ ; do
    if [ -f $file/bower.json ]; then
        cd "$file"
        echo "running bower install in $file"
        exec bower install
        cd ..
    fi
done