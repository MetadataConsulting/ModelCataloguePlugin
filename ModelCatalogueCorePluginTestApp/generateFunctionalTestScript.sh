#!/bin/bash
# Script for generating a functional-test-running-script.
# Assumes it is run from the root of the project (ModelCatalogueCorePluginTestApp)

if [ ! "ModelCatalogueCorePluginTestApp" = "${PWD##*/}" ]
then
    echo "ERROR: Not executed in the root directory i.e. ModelCatalogueCorePluginTestApp"
    exit 1
fi

TEST_REPORT_DIR="$1" # Test report directory should be first argument to script
#if [ ! -d "$TEST_REPORT_DIR" ]
#then
#    echo "first argument ${TEST_REPORT_DIR} needs to be a directory path"
#    exit 1
#fi

SPECNAMES="$(find test/functional -print | grep Spec.groovy | sed "s|.*/\(.*\).groovy|\1|")"

DOWNLOAD_FILE_PATH="/home/james/Downloads/functionalTestDownloads"
CHROME_DRIVER_PATH="/usr/local/lib/node_modules/chromedriver/bin/chromedriver"
#GRAILS_TEST_COMMAND="XYZ"
GRAILS_TEST_COMMAND="./grailsw test-app -Xmx8G -Dgeb.env=chrome -DdownloadFilepath=${DOWNLOAD_FILE_PATH} -Dwebdriver.chrome.driver=${CHROME_DRIVER_PATH} functional: "
printf "$GRAILS_TEST_COMMAND\n"

COMMAND_MARKER="COMMAND_MARKER"
DIRECTORY_MARKER="DIRECTORY_MARKER"

GROUPED_SPECNAMES="$(printf "$SPECNAMES" \
| sed -n "
    N
    N
    s/\n/ /g
    p
  "
)"
# Collect three Specs at a time:
# for each line pull in two lines: (-e N -e N),
# and then substitute newline \n with space (s/\n/ /g),
# and then print (p)

GROUPED_SPECNAMES_DOUBLED_MARKED="$(printf "$GROUPED_SPECNAMES" \
| sed -n "
    s/\(.*\)/${COMMAND_MARKER}\1/
    p
    =
    s/${COMMAND_MARKER}\(.*\)/${DIRECTORY_MARKER}\1/
    p
  "
)"
# Print each line twice: One prefixed with COMMAND_MARKER, one prefixed with DIRECTORY_MARKER.
# Print line number before line with DIRECTORY_MARKER (=)

printf "GROUPED_SPECNAMES_DOUBLED_MARKED: ${GROUPED_SPECNAMES_DOUBLED_MARKED}\n"

GROUPED_SPECNAMES_DOUBLED_MARKED_2="$(printf "$GROUPED_SPECNAMES_DOUBLED_MARKED" \
| sed "
    /[0-9][0-9]*/ {
        N
        s/\(.*\)\n${DIRECTORY_MARKER}\(.*\)/${DIRECTORY_MARKER}\1_\2/
    }
  "
)"

# Incorporate line number (\1) into DIRECTORY_MARKER line

printf "GROUPED_SPECNAMES_DOUBLED_MARKED_2: ${GROUPED_SPECNAMES_DOUBLED_MARKED_2}"


TEST_REPORT_DIR_VAR="TEST_REPORT_DIR"
OVERALL_TEST_REPORT_PATH="\"\${${TEST_REPORT_DIR_VAR}}/overallTestReport.txt\""


COMMANDLINES="$(printf "$GROUPED_SPECNAMES_DOUBLED_MARKED_2" \
| sed "/${COMMAND_MARKER}/ s|${COMMAND_MARKER}\(.*\)|${GRAILS_TEST_COMMAND}\1 \| tee -a ${OVERALL_TEST_REPORT_PATH}; printf \"Just ran \1\" >> ${OVERALL_TEST_REPORT_PATH}|")"
# Add Grails Test Command to each line marked COMMAND_MARKER:
# Use | as delimiter for sed (s)ubstitute command since GRAILS_TEST_COMMAND has / in it

printf "COMMANDLINES: ${COMMANDLINES}\n"

MOVE_RESULTS_TO_DIRECTORY="$(printf "$COMMANDLINES" \
| sed "
    /${DIRECTORY_MARKER}/ {
        s| |_|g
        s|${DIRECTORY_MARKER}\(.*\)|mkdir \"\${${TEST_REPORT_DIR_VAR}}/\1\"; mv target/test-reports \"\${${TEST_REPORT_DIR_VAR}}/\1\"|

    }
  "
)"

printf "$MOVE_RESULTS_TO_DIRECTORY\n"


FUNCTIONAL_TEST_SCRIPT="functionalTestScript.sh"
touch "$FUNCTIONAL_TEST_SCRIPT"
chmod +x "$FUNCTIONAL_TEST_SCRIPT"


printf "#!/bin/bash
# Generated from generateFunctionalTestScript.sh
${TEST_REPORT_DIR_VAR}=\"\$(./testReport.sh)\"

if [ ! -d \"\$${TEST_REPORT_DIR_VAR}\" ]
then
    echo \"first argument \"\$${TEST_REPORT_DIR_VAR}\" needs to be a directory path\"
    exit 1
fi
touch ${OVERALL_TEST_REPORT_PATH}
$MOVE_RESULTS_TO_DIRECTORY
" > "$FUNCTIONAL_TEST_SCRIPT" # Overwrites the file
