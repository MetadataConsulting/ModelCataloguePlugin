#!/bin/bash

# fail if any line fails
set -e

cd ModelCatalogueCorePlugin
npm install
bower install

#./grailsw clean-all
#./grailsw refresh-dependencies
#./grailsw test-app unit:
#./grailsw test-app integration:
#./node_modules/karma/bin/karma start --single-run --browsers Firefox

cd ..
cd ModelCatalogueCorePluginTestApp

#./grailsw clean-all
#./grailsw refresh-dependencies
#./grailsw test-app integration:
./grailsw test-app functional: -war -Dgeb.env=chrome

cd ..
