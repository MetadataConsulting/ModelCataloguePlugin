#!/bin/bash

# fail if any line fails
set -e

cd ModelCatalogueCorePluginTestApp

if [[ "$1" ]]; then
    ./grailsw prod dbm-update -Dmc.config.location="$1"
else
    ./grailsw prod dbm-update
fi

