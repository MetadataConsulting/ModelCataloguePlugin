#!/bin/bash

set -x

source ./bin/lib/test-setup.sh

cd ModelCatalogueCorePlugin

# plugin unit tests
if [ "$TEST_SUITE" = "unit" ] || [ "$TEST_SUITE" = "core_unit" ] || [ "$TEST_SUITE" = "" ] ; then
    set -x
    mkdir -p "$HOME/reports/unit-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/unit-tests-reports" || true
fi

# plugin integration all tests
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "" ] ; then
    set -x
    mkdir -p "$HOME/reports/fast-integration-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/fast-integration-tests-reports" || true
fi


# plugin integration tests
if [ "$TEST_SUITE" = "core_integration" ] ; then
    set -x
    mkdir -p "$HOME/reports/fast-integration-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/fast-integration-tests-reports" || true
fi

# slow and polluting (imports)
if [ "$TEST_SUITE" = "core_integration_slow" ]  ; then
    set -x
    mkdir -p "$HOME/reports/slow-integration-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/slow-integration-tests-reports" || true
fi
cd ..

cd ModelCatalogueFormsPlugin
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "forms_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    set -x
    mkdir -p "$HOME/reports/forms-integration-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/forms-integration-tests-reports" || true
fi
cd ..

cd ModelCatalogueElasticSearchPlugin
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "es_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    set -x
    mkdir -p "$HOME/reports/es-integration-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/es-integration-tests-reports" || true
fi
cd ..

cd ModelCatalogueCorePlugin
# karma tests, part of the integration as they needs the fixtures generated from the integration tests
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "core_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    set -x
    mkdir -p "$HOME/reports/karma-tests-reports"
    cp -Rf target/reports/ "$HOME/reports/karma-tests-reports" || true
fi
cd ..

cd ModelCatalogueCorePluginTestApp

if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "app_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    set -x
    mkdir -p "$HOME/reports/test-app-integration-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/test-app-integration-tests-reports" || true
fi

if [ "$TEST_SUITE" = "functional" ] || [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "" ] ; then
    set -x
    mkdir -p "$HOME/reports/test-app-functional-tests-reports"
    mkdir -p "$HOME/reports/test-app-functional-geb-reports"
    cp -Rf target/test-reports/ "$HOME/reports/test-app-functional-tests-reports" || true
    cp -Rf target/geb-reports/ "$HOME/reports/test-app-functional-geb-reports" || true
fi

if [ "$TEST_SUITE" = "app_functional_a" ] ; then
    set -x
    mkdir -p "$HOME/reports/test-app-functional-a-tests-reports"
    mkdir -p "$HOME/reports/test-app-functional-a-geb-reports"
    cp -Rf target/test-reports/ "$HOME/reports/test-app-functional-a-tests-reports" || true
    cp -Rf target/geb-reports/ "$HOME/reports/test-app-functional-a-geb-reports" || true
fi

if [ "$TEST_SUITE" = "app_functional_b" ] ; then
    set -x
    mkdir -p "$HOME/reports/test-app-functional-b-tests-reports"
    mkdir -p "$HOME/reports/test-app-functional-b-geb-reports"
    cp -Rf target/test-reports/ "$HOME/reports/test-app-functional-b-tests-reports" || true
    cp -Rf target/geb-reports/ "$HOME/reports/test-app-functional-b-geb-reports" || true
fi

if [ "$TEST_SUITE" = "app_functional_c" ] ; then
    set -x
    mkdir -p "$HOME/reports/test-app-functional-c-tests-reports"
    mkdir -p "$HOME/reports/test-app-functional-c-geb-reports"
    cp -Rf target/test-reports/ "$HOME/reports/test-app-functional-c-tests-reports" || true
    cp -Rf target/geb-reports/ "$HOME/reports/test-app-functional-c-geb-reports" || true
fi

cd ..
