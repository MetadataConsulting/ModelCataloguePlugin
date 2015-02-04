#!/bin/bash

# fail if any line fails
set -e

cd ModelCatalogueCorePlugin
npm install
bower install

./grailsw clean-all
./grailsw refresh-dependencies
./grailsw test-app unit:
cp -Rf target/test-reports $HOME/reports/unit-tests-reports
./grailsw test-app integration: org.modelcatalogue.**.*
cp -Rf target/test-reports $HOME/reports/fast-integration-tests-reports
# slow and polluting
./grailsw test-app integration: x.org.modelcatalogue.**.*
cp -Rf target/test-reports $HOME/reports/slow-integration-tests-reports
./node_modules/karma/bin/karma start --single-run --browsers Firefox

cd ..
cd ModelCatalogueCorePluginTestApp

./grailsw clean-all
./grailsw refresh-dependencies
./grailsw test-app integration:
cp -Rf target/test-reports $HOME/reports/test-app-integration-tests-reports
./grailsw test-app functional: -war
cp -Rf target/test-reports $HOME/reports/test-app-functional-tests-reports
cp -Rf target/geb-reports $HOME/reports/test-app-functional-geb-reports

cd ..
