#!/bin/bash
# Run functional tests in specified batches.
# Depends on testReport.sh to create a directory for test results to go into.
# Takes chromedriver path as an argument.

USAGE="Usage: Key: [option=(default)]. Mandatory if not in square brackets
$0 [-grailsCommand=(./grailsw)]
[-testReportDir=(directory created according to date)]
[-Dserver.port=(8080)]
-Dwebdriver.chrome.driver=(no default)
[-Dgeb.env=(chrome)]
[-DdownloadFilePath=(directory in testReportDir)]
"

for i in "$@"
do
case $i in
    -grailsCommand=*)
    GRAILS_COMMAND="${i#*=}"
    shift # past argument=value
    ;;


    -testReportDir=*)
    TEST_REPORT_DIR="${i#*=}"
    shift # past argument=value
    ;;

    -Dserver.port=*)
    SERVER_PORT="${i#*=}"
    shift # past argument=value
    ;;


    -Dwebdriver.chrome.driver=*)
    CHROME_DRIVER_PATH="${i#*=}"
    shift # past argument=value
    ;;

    -Dgeb.env=*)
    GEB_ENV="${i#*=}"
    shift # past argument=value
    ;;

    -DdownloadFilepath=*)
    DOWNLOAD_FILE_PATH="${i#*=}"
    shift # past argument=value
    ;;

    *)
    echo "Unknown option $i. See ${USAGE}"
    exit 1
    ;;
esac
done


if [ -z "${GRAILS_COMMAND}" ]
then
    GRAILS_COMMAND="./grailsw"
fi



if [ -z "${TEST_REPORT_DIR}" ]
then
    TEST_REPORT_DIR="$(./scripts/testing/testReport.sh)"
fi


if [ ! -d "${TEST_REPORT_DIR}" ]
then
    echo "TEST_REPORT_DIR "${TEST_REPORT_DIR}" needs to be a directory path"
    exit 1
fi


if [ -z "${SERVER_PORT}" ]
then
    SERVER_PORT=8080
fi

if [ -z "${GEB_ENV}" ]
then
    GEB_ENV="chrome"
fi

if [ -z "${CHROME_DRIVER_PATH}" ]
then
    echo "Missing chrome driver path. ${USAGE}"
    exit 1
fi
# /usr/local/lib/node_modules/chromedriver/bin/chromedriver

if [ -z "${DOWNLOAD_FILE_PATH}" ]
then
  DOWNLOAD_FILE_PATH="${TEST_REPORT_DIR}/downloads"

fi

if [ ! -e "${DOWNLOAD_FILE_PATH}" ]
then
    mkdir "${DOWNLOAD_FILE_PATH}"
fi

echo "Using download path at ${DOWNLOAD_FILE_PATH}"

COLLATED_CONSOLE_OUTPUT_FILE="${TEST_REPORT_DIR}/collatedConsoleOutput.txt"
touch "${COLLATED_CONSOLE_OUTPUT_FILE}"


START_TIME="$(date)"
echo "START TIME: ${START_TIME}" >> "${COLLATED_CONSOLE_OUTPUT_FILE}"

GRAILS_TEST_COMMAND_ARGS=(test-app -Xmx8G -Dserver.port=${SERVER_PORT} -Dgeb.env=${GEB_ENV} -DdownloadFilepath=${DOWNLOAD_FILE_PATH} -Dwebdriver.chrome.driver=${CHROME_DRIVER_PATH} functional:)
# GRAILS_TEST_COMMAND_ARGS=(test-app)

Full Functional Spec list
declare -a SPEC_BATCHES_ARRAY=("UserIsAbleToDownloadAnAssetSpec CanCreateDataTypeFromCreateDataElementWizardSpec AddAndRemoveManyDataElementsSpec"
                               "CheckCreateElementFromClassWizardSpec MaxOccursShowsInHistorySpec NewDraftEditFromImportedModelsAreUpdatedSpec"
                               "VerifyMinOccursCanBeZeroSpec CheckDataModelCanBeFinalizedSpec CheckDataModelPoliciesSpec"
                               "CheckDataModelPolicyEnumeratedTypeSpec CheckDataModelPolicyTagSpec CloneUnauthorizedElementSpec"
                               "CreateNewVersionOfDataModelSpec CannotCreateDataElementWithUnauthorizedDataTypeSpec CheckDataTypeAddedToNewVersionSpec"
                               "ValidateValueAgainstDataTypeSpec CannotAddDataElementsToFinalizedDataModelSpec CannotAddElementToFinalizedModelSpec"
                               "AbstractModelCatalogueGebSpec ImportXmlAndExcelDataSpec CuratorCanGenerateSuggestionsUsingMappingUtilitySpec"
                               "AbleToNavigateToOldVersionOfAModelThroughTreeSpec CanCreateDataElementAndCloneDataTypeSpec CanCreateDataTypeSpec"
                               "CanImportDataModelSpec CannotDeleteFinalizedDataModelSpec CanSelectPoliciesWhileCreatingDataModelSpec"
                               "HistoryIsPopulatedAccordingToModelActivitySpec ImportAndRemoveDataModelReflectsInHistorySpec UnableToImportIfReadAccessSpec"
                               "UserCanFinalizeDataModelSpec AdminCanCreateModelAndPolicySpec AdminUserCannotDeleteFinalizedItemsSpec"
                               "CheckAdminCanDeleteImportedModelSpec CuratorCanCreateANewDataClassSpec CuratorCanImportFinalizedDataModelSpec"
                               "CuratorCannotCreateClassInFinalizedModelSpec CuratorCannotEditFinalizedModelSpec CuratorWithAdminCanDeleteClassInDraftModelSpec"
                               "DisableUserSpec UserCannotEditReadOnlyDataModelSpec VerifyCuratorCannotDeleteFinalizedDataModelSpec"
                               "VerifyRegularUserCanSeeApiKeySpec VerifySupervisorCanActionSettingsSpec VerifyViewerCannotAccessFactActionsSpec"


    )


TEST_SUMMARY_FILE="${TEST_REPORT_DIR}/testSummary.txt"
touch "${TEST_SUMMARY_FILE}"
BATCHES_ARRAY_SIZE="${#SPEC_BATCHES_ARRAY[*]}"
for i2 in $(seq ${#SPEC_BATCHES_ARRAY[*]})
do
    i="$(($i2-1))"
    batch="${SPEC_BATCHES_ARRAY[$i]}"
    echo "index/batch: $i ${batch}"

    batchArr=($(echo ${batch}))
    allTestArgs=("${GRAILS_TEST_COMMAND_ARGS[@]}" "${batchArr[@]}")
    echo "batchArr[0]: ${batchArr[0]}"
    echo "allTestArgs: ${allTestArgs[@]}"

    batchUnderscore="$(echo ${batch} | sed "s/ /_/g")"
    echo "batchUnderscore: ${batchUnderscore}"
    batchReportDir="${TEST_REPORT_DIR}/${batchUnderscore}"
    echo "batchReportDir: ${batchReportDir}"

    "${GRAILS_COMMAND}" "${allTestArgs[@]}" | tee -a "${COLLATED_CONSOLE_OUTPUT_FILE}"
    printf "Just ran batch ${i}/${BATCH_ARRAY_SIZE}: <batch>${batch}</batch>\n" >> "${COLLATED_CONSOLE_OUTPUT_FILE}"


    mkdir "${batchReportDir}"
    mv target/test-reports "${batchReportDir}"


    sed -n "
        /Completed [0-9][0-9]* spock test/ p
        /<batch>/ p
    " "${COLLATED_CONSOLE_OUTPUT_FILE}" > "${TEST_SUMMARY_FILE}"

    CURRENT_TIME="$(date)"
    echo "CURRENT TIME: ${CURRENT_TIME}" >> "${COLLATED_CONSOLE_OUTPUT_FILE}"
done


CURRENT_TIME="$(date)"
echo "END TIME: ${CURRENT_TIME}" >> "${COLLATED_CONSOLE_OUTPUT_FILE}"


ALL_FAILED_FILE="${TEST_REPORT_DIR}/allFailed.html"
touch "${ALL_FAILED_FILE}"
echo "" > "${ALL_FAILED_FILE}"

echo "<h1> Failures: </h1><br/>" >> "${ALL_FAILED_FILE}"

echo "$(cat "${TEST_SUMMARY_FILE}" | sed -n "
    /[1-9][1-9]* failed in/ {
        N
        s_.*\(Completed[^|]*\)|.*\(<batch>.*</batch>\)_\1<br/>\
\2_
        p
    }
" | sed "
    /<batch>/ {
        s/ /_/g
        s|.*<batch>\(.*\)</batch>.*|<br/><a href=\"\1/test-reports/html/failed.html\">Failed Tests: \1</a><br/>|
    }
")" >> "${ALL_FAILED_FILE}"



