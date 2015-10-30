#!/bin/bash

if [[ "$1" ]]; then
    echo "Overriding default mc configuration to $1."
    echo "$1" > ".default-mc-config-location"
else
    echo "Default mc configuration is now ~/.grails/mc-config.groovy again."
    if test -f ".default-mc-config-location" ; then
        rm ".default-mc-config-location"
    fi
fi