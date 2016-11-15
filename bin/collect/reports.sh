#!/bin/bash

set -x

source ./bin/lib/test-setup.sh

cd ModelCatalogueCorePluginTestApp

if [ "$TEST_SUITE" = "unit_and_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    mkdir -p "$HOME/reports/last-app-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/last-app-tests-reports" || true
fi

if [ "$TEST_SUITE" = "functional" ] || [ "$TEST_SUITE" = "" ] ; then
    mkdir -p "$HOME/reports/test-app-functional-tests-reports"
    mkdir -p "$HOME/reports/test-app-functional-geb-reports"
    mkdir -p "$HOME/reports/assets/modelcatalogue"
    cp -Rf target/test-reports/ "$HOME/reports/test-app-functional-tests-reports" || true
    cp -Rf target/geb-reports/ "$HOME/reports/test-app-functional-geb-reports" || true
    cp -Rf target/assets/modelcatalogue/modelcatalogue*.js "$HOME/reports/assets/modelcatalogue" || true
fi

cd ..
