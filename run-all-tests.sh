#!/bin/bash

# fail if any line fails
set -e

cd ModelCatalogueCorePlugin
npm install
bower install

./grailsw clean-all
./grailsw refresh-dependencies


./grailsw test-app unit:
mkdir -p $HOME/reports/unit-tests-reports
cp -Rf target/test-reports $HOME/reports/unit-tests-reports


./grailsw test-app integration: org.modelcatalogue.**.*
mkdir -p $HOME/reports/fast-integration-tests-reports
cp -Rf target/test-reports $HOME/reports/fast-integration-tests-reports


# slow and polluting
./grailsw test-app integration: x.org.modelcatalogue.**.*
mkdir -p $HOME/reports/slow-integration-tests-reports
cp -Rf target/test-reports $HOME/reports/slow-integration-tests-reports


./node_modules/karma/bin/karma start --single-run --browsers Firefox

cd ..
cd ModelCatalogueCorePluginTestApp

./grailsw clean-all
./grailsw refresh-dependencies


./grailsw test-app integration:
mkdir -p $HOME/reports/test-app-integration-tests-reports
cp -Rf target/test-reports $HOME/reports/test-app-integration-tests-reports


./grailsw test-app functional: -war
mkdir -p $HOME/reports/test-app-functional-tests-reports
cp -Rf target/test-reports $HOME/reports/test-app-functional-tests-reports
mkdir -p $HOME/reports/test-app-functional-geb-reports
cp -Rf target/geb-reports $HOME/reports/test-app-functional-geb-reports

cd ..
