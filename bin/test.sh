#!/bin/bash

source ./bin/lib/test-setup.sh

# please update sibling script /collect/reports.sh when you update this file

# karma and functional tests needs to fetch the bower components
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "core" ] || [ "$TEST_SUITE" = "core_integration" ] || [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "app_functional_a" ] || [ "$TEST_SUITE" = "app_functional_b" ] || [ "$TEST_SUITE" = "app_functional_c" ] ||  [ "$TEST_SUITE" = "" ] ; then
    ./setup-frontend.sh
fi

cd ModelCatalogueCorePlugin

# local builds needs to run in clean environment
if [ -z "$TEST_SUITE" ]; then
    ./grailsw clean-all --non-interactive
    ./grailsw refresh-dependencies --non-interactive
fi

# plugin unit tests
if [ "$TEST_SUITE" = "unit" ] || [ "$TEST_SUITE" = "core_unit" ] || [ "$TEST_SUITE" = "core" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app unit: --non-interactive
fi

# plugin integration all tests
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration:
    mkdir -p "$HOME/reports/fast-integration-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/fast-integration-tests-reports"
fi


# plugin integration tests
if [ "$TEST_SUITE" = "core_integration" ] || [ "$TEST_SUITE" = "core" ] ; then
    ./grailsw test-app integration: org.modelcatalogue.**.* --non-interactive
fi

# slow and polluting (imports)
if [ "$TEST_SUITE" = "core_integration_slow" ] || [ "$TEST_SUITE" = "core" ] ; then
    ./grailsw test-app integration: x.org.modelcatalogue.**.* --non-interactive
fi
cd ..

# moved to test app
#cd ModelCatalogueFormsPlugin
#if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "forms_integration" ] || [ "$TEST_SUITE" = "other_integration" ] || [ "$TEST_SUITE" = "" ] ; then
#    ./grailsw test-app integration: --non-interactive
#fi
#cd ..

# moved to test app
cd ModelCatalogueElasticSearchPlugin
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "es_integration" ] || [ "$TEST_SUITE" = "other_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration: --non-interactive
fi
cd ..

cd ModelCatalogueGenomicsPlugin
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "gel_integration" ] || [ "$TEST_SUITE" = "other_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration: --non-interactive
fi
cd ..

cd ModelCatalogueCorePlugin
# karma tests, part of the integration as they needs the fixtures generated from the integration tests
if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "core_integration" ]|| [ "$TEST_SUITE" = "core" ] || [ "$TEST_SUITE" = "" ] ; then
    ./node_modules/karma/bin/karma start --single-run --browsers Firefox
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

if [ "$TEST_SUITE" = "integration" ] || [ "$TEST_SUITE" = "app_integration" ] || [ "$TEST_SUITE" = "other_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app integration: --non-interactive
fi

if [ "$TEST_SUITE" = "functional" ] || [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "" ] ; then
    set -x
    ./grailsw test-app functional: -war --non-interactive
fi

if [ "$TEST_SUITE" = "app_functional_a" ] ; then
    set -x
    ./grailsw test-app functional: org.modelcatalogue.core.a.**.* -war --non-interactive
fi

if [ "$TEST_SUITE" = "app_functional_b" ] ; then
    set -x
    ./grailsw test-app functional: org.modelcatalogue.core.b.**.* -war --non-interactive
fi

if [ "$TEST_SUITE" = "app_functional_c" ] ; then
    set -x
    ./grailsw test-app functional: org.modelcatalogue.core.c.**.* -war --non-interactive
fi

cd ..
