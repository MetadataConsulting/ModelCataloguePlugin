#!/bin/bash
# Script for generating a functional-test-running-script.
# Assumes it is run from the root of the project (ModelCatalogueCorePluginTestApp)

if [ ! "ModelCatalogueCorePluginTestApp" = "${PWD##*/}" ]
then
    echo "ERROR: Not executed in the root directory i.e. ModelCatalogueCorePluginTestApp"
    exit 1
fi

# SPECNAMES="$(find test/functional -print | grep Spec.groovy | sed "s|.*/\(.*\).groovy|\1|")"

SPECS_WITHOUT_IGNORE_NAMES="$(for SPECPATH in $(find test/functional -print | grep Spec.groovy)
do
    IGNORE="$(cat "${SPECPATH}" | sed -n "
        /@Ignore/ {
            N
            N
            N
            N
            /\nclass/ p
        }
    ")"

    # the sed command will print the contents of the file iff
    # there is a line matching @Ignore, with one line in the 4 below (N N N N)
    # that matches /\nclass/ i.e. begins with "class"

    # echo $IGNORE

    if [ -z "${IGNORE}" ] # if the string is empty,
    then
        # echo "${SPECPATH}" MOST LIKELY does not have Ignore at Class level, so we will take these"
        echo "${SPECPATH}" | sed "s|.*/\(.*\).groovy|\1|" # Strip out Spec name from file name
    else
        # echo "${SPECPATH}" definitely DOES have Ignore at Class level, so we will not take these"
        true

    fi
done)"

echo "SPECS_WITHOUT_IGNORE_NAMES: ${SPECS_WITHOUT_IGNORE_NAMES}"

GROUPED_SPECNAMES="$(printf "$SPECS_WITHOUT_IGNORE_NAMES" \
| sed -n "
    N
    N
    s/\n/ /g
    s/\(.*\)/\"\1\"/
    p
  "
)"
# Collect three Specs at a time:
# for each line pull in two lines: (N N),
# and then substitute newline \n with space (s/\n/ /g),
# add quotes around the line (s/\(.*\)/\"\1\"/)
# and then print (p)

printf "GROUPED_SPECNAMES: ${GROUPED_SPECNAMES}\n"

