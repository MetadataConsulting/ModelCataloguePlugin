#!/bin/bash

export GRAILS_OPTS="-Xmx1G -Xms512m -XX:MaxPermSize=512m -server"

# fail if any line fails
set -e

date ; echo -e "\n"

./setup-frontend.sh

cd ModelCatalogueCorePluginTestApp

ARG="$1"

shift 1

if [[ "$ARG" == "blank" ]]; then
    export MC_BLANK_DEV=true
    ./grailsw run-app "$@"
elif [[ "$ARG" == "debug" ]]; then
    ./grailsw run-app --debug-fork "$@"
elif [[ "$ARG" == "war" ]]; then
    ./grailsw dev run-war "$@"
else
    ./grailsw run-app "$@"
fi

