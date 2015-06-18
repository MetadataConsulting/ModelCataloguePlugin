#!/bin/bash

# fail if any line fails
set -e

./setup-frontend.sh

cd ModelCatalogueCorePluginTestApp

if [[ "$1" == "debug" ]]; then
    ./grailsw run-app --debug-fork
else
    ./grailsw run-app
fi

