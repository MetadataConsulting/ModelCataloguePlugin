#!/bin/bash

# fail if any line fails
set -e

cd ModelCatalogueCorePluginTestApp

if [[ "$1" ]]; then
    ./grailsw prod dbm-update --non-interactive -Dmc.config.location="$1"
elif test -f "../.default-mc-config-location" ; then
    default_mc_config_location=$(cat "../.default-mc-config-location")
    echo -e "\n\nUsing $default_mc_config_location configuration!\n\n"
    ./grailsw prod dbm-update --non-interactive -Dmc.config.location="$default_mc_config_location"
elif ! test -f "~/.grails/mc-config.groovy" ; then
    echo -e "\n\nLocal Model Catalogue production configuration is missing!\nPlease copy file ./ModelCatalogueCorePluginTestApp/grails-app/conf/mc-config.groovy.example into ~/.grails/mc-config.groovy and update it with your local production database settings.\n"
else
    ./grailsw prod dbm-update --non-interactive
fi