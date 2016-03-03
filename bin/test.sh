#!/bin/bash

# fail if any line fails
set -e
set -x

date ; echo -e "\n"

if [[ "$1" != "" ]]; then
    TEST_SUITE="$1"
fi

if [[ "$TEST_SUITE" == "all" ]]; then
    TEST_SUITE=""
fi

if [[ "$CIRCLE_NODE_TOTAL" != "" ]]; then
    case $CIRCLE_NODE_INDEX in
        0) TEST_SUITE="unit" ;;
        1) TEST_SUITE="integration" ;;
        2) TEST_SUITE="functional" ;;
    esac
fi

# karma and functional tests needs to fetch the bower components
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "core_integration" ] || [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "app_functional_a" ] || [ "$TEST_SUITE" = "app_functional_b" ] || [ "$TEST_SUITE" = "app_functional_c" ] || [ "$TEST_SUITE" = "forms_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./setup-frontend.sh
fi

cd ModelCatalogueCorePlugin

# local builds needs to run in clean environment
if [ -z "$TEST_SUITE" ]; then
    ./grailsw clean-all --non-interactive
    ./grailsw refresh-dependencies --non-interactive
fi

# plugin unit tests
if [ "$TEST_SUITE" = "unit" ] || [ "$TEST_SUITE" = "core_unit" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app unit: --non-interactive
    mkdir -p "$HOME/reports/unit-tests-reports"
    cp -Rf target/test-reports "$HOME/reports/unit-tests-reports"
fi

# plugin integration all tests
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration:
    mkdir -p "$HOME/reports/fast-integration-tests-reports"
    cp -Rf target/test-reports "$HOME/reports/fast-integration-tests-reports"
fi


# plugin integration tests
if [ "$TEST_SUITE" = "core_integration" ] ; then
    ./grailsw test-app integration: org.modelcatalogue.**.* --non-interactive
    mkdir -p "$HOME/reports/fast-integration-tests-reports"
    cp -Rf target/test-reports "$HOME/reports/fast-integration-tests-reports"
fi

# slow and polluting (imports)
if [ "$TEST_SUITE" = "core_integration_slow" ]  ; then
    ./grailsw test-app integration: x.org.modelcatalogue.**.* --non-interactive
    mkdir -p "$HOME/reports/slow-integration-tests-reports"
    cp -Rf target/test-reports "$HOME/reports/slow-integration-tests-reports"
fi
cd ..

cd ModelCatalogueFormsPlugin
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "forms_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration: --non-interactive
fi
cd ..

cd ModelCatalogueElasticSearchPlugin
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "es_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration: --non-interactive
fi
cd ..

cd ModelCatalogueCorePlugin
# karma tests, part of the integration as they needs the fixtures generated from the integration tests
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "core_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./node_modules/karma/bin/karma start --single-run --browsers Firefox
    mkdir -p "$HOME/reports/karma-tests-reports"
    cp -Rf target/reports "$HOME/reports/karma-tests-reports"
fi
cd ..

cd ModelCatalogueCorePluginTestApp
# if we're running app tests
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "functional" ] ||  [ "$TEST_SUITE" = "app_integration" ] || [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "" ] ; then
    # local builds needs to run in clean environment
    if [ -z "$TEST_SUITE" ]; then
        ./grailsw clean-all --non-interactive
        ./grailsw refresh-dependencies --non-interactive
    fi
fi

if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "app_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration: --non-interactive
    mkdir -p "$HOME/reports/test-app-integration-tests-reports"
    cp -Rf target/test-reports "$HOME/reports/test-app-integration-tests-reports"
fi

if [ "$TEST_SUITE" = "functional" ] || [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app functional: -war --non-interactive
    mkdir -p "$HOME/reports/test-app-functional-tests-reports"
    mkdir -p "$HOME/reports/test-app-functional-geb-reports"
    cp -Rf target/test-reports "$HOME/reports/test-app-functional-tests-reports"
    cp -Rf target/geb-reports "$HOME/reports/test-app-functional-geb-reports"
fi

if [ "$TEST_SUITE" = "app_functional_a" ] ; then
    ./grailsw test-app functional: org.modelcatalogue.core.a.**.* -war --non-interactive
    mkdir -p "$HOME/reports/test-app-functional-a-tests-reports"
    mkdir -p "$HOME/reports/test-app-functional-a-geb-reports"
    cp -Rf target/test-reports "$HOME/reports/test-app-functional-a-tests-reports"
    cp -Rf target/geb-reports "$HOME/reports/test-app-functional-a-geb-reports"
fi

if [ "$TEST_SUITE" = "app_functional_b" ] ; then
    ./grailsw test-app functional: org.modelcatalogue.core.b.**.* -war --non-interactive
    mkdir -p "$HOME/reports/test-app-functional-b-tests-reports"
    mkdir -p "$HOME/reports/test-app-functional-b-geb-reports"
    cp -Rf target/geb-reports "$HOME/reports/test-app-functional-b-tests-reports"
    cp -Rf target/test-reports "$HOME/reports/test-app-functional-b-geb-reports"
fi

if [ "$TEST_SUITE" = "app_functional_c" ] ; then
    ./grailsw test-app functional: org.modelcatalogue.core.c.**.* -war --non-interactive
    mkdir -p "$HOME/reports/test-app-functional-c-tests-reports"
    mkdir -p "$HOME/reports/test-app-functional-c-geb-reports"
    cp -Rf target/test-reports "$HOME/reports/test-app-functional-c-tests-reports"
    cp -Rf target/geb-reports "$HOME/reports/test-app-functional-c-geb-reports"
fi

cd ..
