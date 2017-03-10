#!/bin/bash

# fail if any line fails
set -e

date ; echo -e "\n"


mkdir -p build

./gradlew npmInstall bowerInstall

cd ModelCatalogueCorePluginTestApp
./grailsw war


if [ -f "build" ]; then
    rm build/*
fi

cp target/*.war ../build/mc.war

echo "War created in ./build/ directory. You need to have put mc-config.groovy configuration file in your Tomcat's conf directory"
