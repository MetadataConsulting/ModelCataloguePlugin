#!/bin/bash

export GRAILS_OPTS="-Xmx1G -Xms512m -XX:MaxPermSize=512m -server"

# fail if any line fails
set -e

date ; echo -e "\n"

cd ModelCatalogueCorePluginTestApp

./gradlew npmInstall bowerInstall

ARG="$1"

if [[ "$ARG" == "blank" ]]; then
    shift 1
    export MC_BLANK_DEV=true
    ./grailsw run-app "$@"
elif [[ "$ARG" == "debug" ]]; then
    shift 1
    ./grailsw run-app --debug-fork "$@"
elif [[ "$ARG" == "war" ]]; then
    shift 1
    ./grailsw dev run-war "$@"
else
    ./grailsw run-app
fi

