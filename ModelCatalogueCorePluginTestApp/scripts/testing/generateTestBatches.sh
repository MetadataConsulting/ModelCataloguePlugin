#!/bin/bash
# Script for generating a functional-test-running-script.
# Assumes it is run from the root of the project (ModelCatalogueCorePluginTestApp)

if [ ! "ModelCatalogueCorePluginTestApp" = "${PWD##*/}" ]
then
    echo "ERROR: Not executed in the root directory i.e. ModelCatalogueCorePluginTestApp"
    exit 1
fi

SPECNAMES="$(find test/functional -print | grep Spec.groovy | sed "s|.*/\(.*\).groovy|\1|")"

GROUPED_SPECNAMES="$(printf "$SPECNAMES" \
| sed -n "
    N
    N
    s/\n/ /g
    s/\(.*\)/\"\1\"/
    p
  "
)"
# Collect three Specs at a time:
# for each line pull in two lines: (-e N -e N),
# and then substitute newline \n with space (s/\n/ /g),
# and then print (p)

printf "GROUPED_SPECNAMES: ${GROUPED_SPECNAMES}\n"

