#!/bin/bash

rm -rf build
find . -name '*.gen.fixture.js' -delete

for file in */ .*/ ; do
    if [ -f $file/grailsw ]; then
        echo "found grails wrapper in $file"
        cd "$file"
        ./grailsw clean-all
        rm -rf target
        rm -rf target-eclipse
        ./grailsw refresh-dependencies --non-interactive
	cd ..
    fi
done