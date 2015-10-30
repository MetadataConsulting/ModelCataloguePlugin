#!/bin/bash

# fail if any line fails
set -e

date ; echo -e "\n"


mkdir -p build

./setup-frontend.sh

cd ModelCatalogueCorePluginTestApp
./grailsw war

cp target/*.war ../build/

echo "War created in ./build/ directory. You need to have put mc-config.groovy configuration file in your Tomcat's conf directory"



