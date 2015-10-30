#!/bin/bash


rm -rf build
find . -name '*.gen.fixture.js' -delete

for file in */ .*/ ; do
    if [ -f "$file/grailsw" ]; then
        echo "found grails wrapper in $file"
        cd "$file"

        PROP_VALUE=`cat "application.properties" | grep "app.grails.version" | cut -d'=' -f2`

        echo "$PROP_VALUE"

        ./grailsw clean-all --stacktrace
        rm -rf target
        rm -rf target-eclipse

        GRAILS_PROJECT_FOLDER="~/.grails/$PROP_VALUE/projects/${PWD##*/}"

        rm -rf "$GRAILS_PROJECT_FOLDER"

        # echo -e "\nDeleted $GRAILS_PROJECT_FOLDER\n"

        ./grailsw refresh-dependencies --non-interactive --stacktrace
	cd ..
    fi
done