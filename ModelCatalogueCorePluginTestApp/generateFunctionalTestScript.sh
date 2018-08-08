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
    p
  "
)"
# Collect three Specs at a time:
# for each line pull in two lines: (-e N -e N),
# and then substitute newline \n with space (s/\n/ /g),
# and then print (p)

printf "GROUPED_SPECNAMES: ${GROUPED_SPECNAMES}"

# Grouped Specnames into Array:

echo "groupedSpecnamesArray content:"
IFS=$'\n' groupedSpecnamesArray=($(printf "$GROUPED_SPECNAMES"))
for t in "${groupedSpecnamesArray[@]}"
do
    echo "$t LOL"
done
echo "Read array content!"


# SPEC_BATCHES_ARRAY_INIT will be code to initialize an array in the generated script.

SPEC_BATCHES_ARRAY_INIT="declare -a SPEC_BATCHES_ARRAY=("
for i in $(seq ${#groupedSpecnamesArray[*]}); do
    echo "$i ${groupedSpecnamesArray[$i-1]}"
    SPEC_BATCHES_ARRAY_INIT="${SPEC_BATCHES_ARRAY_INIT}\"${groupedSpecnamesArray[$i-1]}\"
    "
done
SPEC_BATCHES_ARRAY_INIT="${SPEC_BATCHES_ARRAY_INIT})"
printf "SPEC_BATCHES_ARRAY_INIT: ${SPEC_BATCHES_ARRAY_INIT}"

# create functionalTestScript.sh

FUNCTIONAL_TEST_SCRIPT="functionalTestScript.sh"
touch "$FUNCTIONAL_TEST_SCRIPT"
chmod +x "$FUNCTIONAL_TEST_SCRIPT"


# Write script:

printf "#!/bin/bash
# Generated from generateFunctionalTestScript.sh
TEST_REPORT_DIR=\"\$(./testReport.sh)\"

if [ ! -d \"\${TEST_REPORT_DIR}\" ]
then
    echo \"TEST_REPORT_DIR \"\${TEST_REPORT_DIR}\" needs to be a directory path\"
    exit 1
fi

OVERALL_TEST_REPORT_PATH=\"\${TEST_REPORT_DIR}/overallTestReport.txt\"
touch \"\${OVERALL_TEST_REPORT_PATH}\"

DOWNLOAD_FILE_PATH="/home/james/Downloads/functionalTestDownloads" # Make these into arguments
CHROME_DRIVER_PATH="/usr/local/lib/node_modules/chromedriver/bin/chromedriver"
GRAILS_TEST_COMMAND_ARGS=(test-app -Xmx8G -Dgeb.env=chrome "-DdownloadFilepath=\${DOWNLOAD_FILE_PATH}" "-Dwebdriver.chrome.driver=\${CHROME_DRIVER_PATH}" functional:)


${SPEC_BATCHES_ARRAY_INIT}
for i2 in \$(seq \${#SPEC_BATCHES_ARRAY[*]})
do
    i=\"\$((\$i2-1))\"
    batch=\"\${SPEC_BATCHES_ARRAY[\$i]}\"
    echo \"index/batch: \$i \${batch}\"

    batchArr=(\$(echo \${batch}))
    allTestArgs=(\"\${GRAILS_TEST_COMMAND_ARGS[@]}\" \"\${batchArr[@]}\")
    echo \"batchArr[0]: \${batchArr[0]}\"
    echo \"allTestArgs: \${allTestArgs[@]}\"

    batchUnderscore=\"\$(echo \${batch} | sed \"s/ /_/g\")\"
    echo \"batchUnderscore: \${batchUnderscore}\"
    batchReportDir=\"\${TEST_REPORT_DIR}/\${batchUnderscore}\"
    echo \"batchReportDir: \${batchReportDir}\"

    # ./grailsw \"\${allTestArgs[@]}\" | tee -a \"\${OVERALL_TEST_REPORT_PATH}\"
    printf \"\\\nJust ran \${batch}\\\n\" >> \"\${OVERALL_TEST_REPORT_PATH}\"


    mkdir \"\${batchReportDir}\"
    mv target/test-reports \"\${batchReportDir}\"
done
" > "$FUNCTIONAL_TEST_SCRIPT" # Overwrites the file
