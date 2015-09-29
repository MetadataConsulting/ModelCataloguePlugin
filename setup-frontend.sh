#!/bin/bash

set -e

pwd
echo "executing npm install in folders where package.json is exists"

for file in */ .*/ ; do
    if [ -f $file/package.json ]; then
        echo "found package.json in $file"
        cd "$file"
        echo "running npm install in $file"
        npm install
       	echo "npm install successful"
	cd ..
    fi
done

pwd
echo "executing npm install in folders where bower.json is exists"

for file in */ .*/ ; do
    if [ -f $file/bower.json ]; then
        echo "found bower.json in $file"
        cd "$file"
        echo "running bower install in $file"
        bower install
        echo "bower install successful"
        cd ..
    fi
done