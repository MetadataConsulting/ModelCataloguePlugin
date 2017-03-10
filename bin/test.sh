#!/bin/bash

source ./bin/lib/test-setup.sh

export FILE_OPENER_SKIP=true
export ES_VERSION=2.3.5

if [[ "$TRAVIS" != "" ]] ; then
    if [ "$TEST_SUITE" = "functional" ] || [ "$TEST_SUITE" = "" ] ; then
        echo "preparing metadata database"
        mysql -u root -e "create user 'metadata'@'%' identified by 'metadata';create database metadata;grant all privileges on metadata.* to 'metadata'@'%'"
        echo "running elasticsearch"
        wget -qO- "http://download.elasticsearch.org/elasticsearch/release/org/elasticsearch/distribution/tar/elasticsearch/$ES_VERSION/elasticsearch-$ES_VERSION.tar.gz" | tar xvz
        "./elasticsearch-$ES_VERSION/bin/elasticsearch" -d --default.path.conf=conf/test/esconfig
        wget --retry-connrefused --read-timeout=20 --timeout=15 --tries=20 --waitretry=3 -O - http://localhost:9200/
    fi
fi

# please update sibling script /collect/reports.sh when you update this file

cd ModelCatalogueCorePluginTestApp

./gradlew npmInstall bowerInstall

# local builds needs to run in clean environment
if [ "$TEST_SUITE" = "" ] ; then
    ./grailsw clean-all --non-interactive
    ./grailsw refresh-dependencies --non-interactive
fi

if [ "$TEST_SUITE" = "unit_and_integration" ] || [ "$TEST_SUITE" = "" ] ; then
    # plugin unit tests
    ./grailsw test-app unit: --non-interactive
    mkdir -p "$HOME/reports/core-unit-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/core-unit-tests-reports" || true

    export MC_ES_DISABLED=true
    ./grailsw test-app integration: --non-interactive
    mkdir -p "$HOME/reports/app-integration-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/app-integration-tests-reports" || true

    # karma tests, part of the integration as they needs the fixtures generated from the integration tests
    ./node_modules/karma/bin/karma start --single-run --browsers Firefox
    mkdir -p "$HOME/reports/karma-tests-reports"
    cp -Rf target/test-reports/ "$HOME/reports/karma-tests-reports" || true
fi

if [ "$TEST_SUITE" = "functional" ] || [ "$TEST_SUITE" = "" ] ; then
    ./grailsw test-app functional: -war --non-interactive
fi

cd ..
