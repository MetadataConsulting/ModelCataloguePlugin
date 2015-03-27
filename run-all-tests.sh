#!/bin/bash

# fail if any line fails
set -e

cd ModelCatalogueCorePlugin

if [ "$TEST_SUITE" = "core_karma" ] || [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "" ] ; then
    npm install
    bower install
fi

./grailsw clean-all
./grailsw refresh-dependencies

# plugin unit tests
if [ "$TEST_SUITE" = "core_unit" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app unit:
    mkdir -p $HOME/reports/unit-tests-reports
    cp -Rf target/test-reports $HOME/reports/unit-tests-reports
fi

# plugin integration tests
if [ "$TEST_SUITE" = "core_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration: org.modelcatalogue.**.*
    mkdir -p $HOME/reports/fast-integration-tests-reports
    cp -Rf target/test-reports $HOME/reports/fast-integration-tests-reports
fi

# slow and polluting
if [ "$TEST_SUITE" = "core_integration_slow" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration: x.org.modelcatalogue.**.*
    mkdir -p $HOME/reports/slow-integration-tests-reports
    cp -Rf target/test-reports $HOME/reports/slow-integration-tests-reports
fi

# karma tests
if [ "$TEST_SUITE" = "core_karma" ] || [ "$TEST_SUITE" = "" ] ; then
    ./node_modules/karma/bin/karma start --single-run --browsers Firefox
fi

if [ "$TEST_SUITE" = "app_integration" ] || [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "" ] ; then
    cd ..
    cd ModelCatalogueCorePluginTestApp

    ./grailsw clean-all
    ./grailsw refresh-dependencies
fi

if [ "$TEST_SUITE" = "app_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration:
    mkdir -p $HOME/reports/test-app-integration-tests-reports
    cp -Rf target/test-reports $HOME/reports/test-app-integration-tests-reports
fi

if [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app functional: -war
    mkdir -p $HOME/reports/test-app-functional-tests-reports
    cp -Rf target/test-reports $HOME/reports/test-app-functional-tests-reports
fi

cd ..
