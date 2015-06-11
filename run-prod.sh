#!/bin/bash

# fail if any line fails
set -e

./setup-frontend.sh

cd ModelCatalogueCorePluginTestApp
if [[ "$1" == "debug" ]]; then
    ./grailsw prod run-app --debug-fork
else
    ./grailsw prod run-app
fi


