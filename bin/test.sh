#!/bin/bash

source ./bin/lib/test-setup.sh

export FILE_OPENER_SKIP=true

# please update sibling script /collect/reports.sh when you update this file

# karma and functional tests needs to fetch the bower components
./setup-frontend.sh

cd ModelCatalogueCorePlugin

# local builds needs to run in clean environment
if [ "$TEST_SUITE" = "" ] ; then
    ./grailsw clean-all --non-interactive
    ./grailsw refresh-dependencies --non-interactive
fi

if [ "$TEST_SUITE" = "unit_and_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    # plugin unit tests
    ./grailsw test-app unit: --non-interactive
    mkdir -p "$HOME/reports/core-unit-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/core-unit-tests-reports"

    # plugin integration all tests
    ./grailsw test-app integration: --non-interactive
    mkdir -p "$HOME/reports/core-integration-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/core-integration-tests-reports"

    # karma tests, part of the integration as they needs the fixtures generated from the integration tests
    ./node_modules/karma/bin/karma start --single-run --browsers Firefox
fi

cd ..

cd ModelCatalogueCorePluginTestApp

# local builds needs to run in clean environment
if [ "$TEST_SUITE" = "" ] ; then
    ./grailsw clean-all --non-interactive
    ./grailsw refresh-dependencies --non-interactive
fi

if [ "$TEST_SUITE" = "unit_and_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    export MC_ES_DISABLED=true
    ./grailsw test-app integration: --non-interactive
fi

if [ "$TEST_SUITE" = "functional" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app functional: -war --non-interactive
fi

cd ..
