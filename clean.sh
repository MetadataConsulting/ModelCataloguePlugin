#!/bin/bash

rm -rf build

for file in */ .*/ ; do
    if [ -f $file/grailsw ]; then
        echo "found grails wrapper in $file"
        cd "$file"
        ./grailsw clean-all
        rm -rf target
	cd ..
    fi
done