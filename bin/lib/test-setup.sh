#!/usr/bin/env bash

# fail if any line fails
set -e

date ; echo -e "\n"

if [[ "$1" != "" ]]; then
    TEST_SUITE="$1"
fi

if [[ "$TEST_SUITE" == "all" ]]; then
    TEST_SUITE=""
fi

if [[ "$CIRCLE_NODE_TOTAL" != "" ]]; then
    case $CIRCLE_NODE_INDEX in
        0) TEST_SUITE="functional" ;;
        1) TEST_SUITE="unit_and_integration" ;;
    esac
fi

if [[ "$TRAVIS" != "" ]] ; then
    echo -e "\nTest Results will be available at\n\nhttp://mc-test-results.metadata.org.uk.s3-website-eu-west-1.amazonaws.com/index.html?prefix=MetadataConsulting/ModelCataloguePlugin/$TRAVIS_BUILD_NUMBER/$TRAVIS_JOB_NUMBER/home/travis/reports/\n\n"
    MC_GEB_ENV="" #firefox is default
fi
