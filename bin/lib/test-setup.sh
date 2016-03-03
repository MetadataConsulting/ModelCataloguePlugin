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
        0) TEST_SUITE="unit" ;;
        1) TEST_SUITE="integration" ;;
        2) TEST_SUITE="functional" ;;
    esac
fi
