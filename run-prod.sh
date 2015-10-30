#!/bin/bash

# fail if any line fails
set -e

date ; echo -e "\n"

./setup-frontend.sh

cd ModelCatalogueCorePluginTestApp
if [[ "$1" == "debug" ]]; then
    ./grailsw prod run-app --debug-fork
elif [[ "$1" == "offline" ]]; then
    ./grailsw prod run-app -Dmc.offline=true
elif [[ "$1" ]]; then
    ./grailsw prod run-app -Dmc.config.location="$1"
elif test -f "../.default-mc-config-location" ; then
    location=`cat "../.default-mc-config-location"`
    echo -e "Using $location configuration!\n\n"
    ./grailsw prod run-app -Dmc.config.location="$location"
elif ! test -f "~/.grails/mc-config.groovy" ; then
    echo -e "\Local Model Catalogue production configuration is missing!\nPlease copy file ./ModelCatalogueCorePluginTestApp/grails-app/conf/mc-config.groovy.example into ~/.grails/mc-config.groovy and update it with your local production database settings.\n"
else
    ./grailsw prod run-app
fi


