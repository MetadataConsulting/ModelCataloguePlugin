#!/bin/bash

# fail if any line fails
set -e

./setup-frontend.sh

cd ModelCatalogueCorePluginTestApp
if [[ "$1" == "debug" ]]; then
    ./grailsw prod run-app --debug-fork
elif [[ "$1" == "offline" ]]; then
    ./grailsw prod run-app -Dmc.offline=true
elif [[ "$1" ]]; then
    ./grailsw prod run-app -Dmc.config.location="$1"
else
    ./grailsw prod run-app
fi


