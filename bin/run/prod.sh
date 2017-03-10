#!/bin/bash

export GRAILS_OPTS="-Xmx2G -Xms1G -XX:MaxPermSize=1G -server"

# fail if any line fails
set -e

date ; echo -e "\n"

if test -f ".docker-ip" ; then
    DOCKER_IP=`cat ".docker-ip"`
    echo
    echo "Using ElasticSearch in docker at $DOCKER_IP for searching the catalogue."
    echo
else
    DOCKER_IP=""
    echo
    echo "ElasticSearch in docker is not running. Default database search will be used or local ElasticSearch instance if configured in config file."
    echo
fi

if test -f ".default-mc-config-location" ; then
    MC_CONFIG_LOCATION=`cat ".default-mc-config-location"`
    echo -e "Configuration location file found .default-mc-config-location."
    echo -e "Trying to load external configuration from $MC_CONFIG_LOCATION."
    if test -f "$MC_CONFIG_LOCATION" ; then
        echo -e "Using $MC_CONFIG_LOCATION configuration!"
        echo
    else
        echo -e "Configuration file $MC_CONFIG_LOCATION not found!"
        exit 1
    fi
else
    MC_CONFIG_LOCATION="$HOME/.grails/mc-config.groovy"
    echo -e "Using default configuration $MC_CONFIG_LOCATION!\n"
fi

if [ ! -e "$MC_CONFIG_LOCATION" ]; then
    echo -e "\nLocal Model Catalogue production configuration is missing!\nPlease copy file ./ModelCatalogueCorePluginTestApp/grails-app/conf/mc-config.groovy.example into ~/.grails/mc-config.groovy and update it with your local production database settings.\n"
    exit 1
fi

cd ModelCatalogueCorePluginTestApp

./gradlew npmInstall bowerInstall

if [[ "$1" == "debug" ]]; then
    ./grailsw prod run-app --debug-fork -Dmc.config.location="$MC_CONFIG_LOCATION" -Dmc.search.elasticsearch.host="$DOCKER_IP"
elif [[ "$1" == "offline" ]]; then
    ./grailsw prod run-app -Dmc.offline=true -Dmc.config.location="$MC_CONFIG_LOCATION" -Dmc.search.elasticsearch.host="$DOCKER_IP"
elif [[ "$1" ]]; then
    ./grailsw prod run-app -Dmc.config.location="$1" -Dmc.search.elasticsearch.host="$DOCKER_IP"
else
    ./grailsw prod run-app -Dmc.config.location="$MC_CONFIG_LOCATION" -Dmc.search.elasticsearch.host="$DOCKER_IP"
fi


