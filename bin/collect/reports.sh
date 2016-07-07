#!/bin/bash

set -x

source ./bin/lib/test-setup.sh

function copy_functional_test_results() {
    mkdir -p "$HOME/reports/test-app-functional-tests-reports"
    mkdir -p "$HOME/reports/test-app-functional-geb-reports"
    mkdir -p "$HOME/reports/assets/modelcatalogue"
    cp -Rf target/test-reports/ "$HOME/reports/test-app-functional-tests-reports" || true
    cp -Rf target/geb-reports/ "$HOME/reports/test-app-functional-geb-reports" || true
    cp -Rf target/assets/modelcatalogue/modelcatalogue*.js "$HOME/reports/assets/modelcatalogue" || true
    cp     target/stacktrace.log "$HOME/reports/stacktrace.log" || true
}

cd ModelCatalogueCorePlugin

if [ "$TEST_SUITE" = "unit_and_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    mkdir -p "$HOME/reports/last-core-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/last-core-tests-reports" || true
fi

cd ..

cd ModelCatalogueCorePluginTestApp

if [ "$TEST_SUITE" = "unit_and_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    mkdir -p "$HOME/reports/last-app-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/last-app-tests-reports" || true
fi

if [ "$TEST_SUITE" = "functional" ] || [ "$TEST_SUITE" = "" ] ; then
    copy_functional_test_results
fi

cd ..
