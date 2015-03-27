#!/bin/bash

# fail if any line fails
set -e

cd ModelCatalogueCorePlugin

# local builds needs to run in clean environment
if [ -z "$TEST_SUITE" ]; then
    ./grailsw clean-all
    ./grailsw refresh-dependencies
fi

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

# slow and polluting (imports)
if [ "$TEST_SUITE" = "core_integration_slow" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration: x.org.modelcatalogue.**.*
    mkdir -p $HOME/reports/slow-integration-tests-reports
    cp -Rf target/test-reports $HOME/reports/slow-integration-tests-reports
fi

# karma and functional tests needs to fetch the bower components
if [ "$TEST_SUITE" = "core_integration" ] || [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "" ] ; then
    npm install
    bower install
fi

# karma tests, part of the integration as they needs the fixtures generated from the integration tests
if [ "$TEST_SUITE" = "core_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./node_modules/karma/bin/karma start --single-run --browsers Firefox
fi

# if we're running app tests
if [ "$TEST_SUITE" = "app_integration" ] || [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "" ] ; then
    cd ..
    cd ModelCatalogueCorePluginTestApp

    # local builds needs to run in clean environment
    if [ -z "$TEST_SUITE" ]; then
        ./grailsw clean-all
        ./grailsw refresh-dependencies
    fi
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
