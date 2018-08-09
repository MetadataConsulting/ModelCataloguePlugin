#!/bin/bash
# Run functional tests in specified batches.
# Depends on testReport.sh to create a directory for test results to go into.
# Takes chromedriver path as an argument.

TEST_REPORT_DIR="$(./scripts/testing/testReport.sh)"

if [ ! -d "${TEST_REPORT_DIR}" ]
then
    echo "TEST_REPORT_DIR "${TEST_REPORT_DIR}" needs to be a directory path"
    exit 1
fi


CHROME_DRIVER_PATH="$1"
if [ -z "${CHROME_DRIVER_PATH}" ]
then
    echo "Missing first argument. Command usage: $0 CHROME_DRIVER_PATH [DOWNLOAD_FILE_PATH]"
    exit 1
fi
# /usr/local/lib/node_modules/chromedriver/bin/chromedriver

DOWNLOAD_FILE_PATH="$2"
if [ -z "${DOWNLOAD_FILE_PATH}" ]
then
  DOWNLOAD_FILE_PATH="${TEST_REPORT_DIR}/downloads"
  if [ ! -e "${DOWNLOAD_FILE_PATH}" ]
  then
    mkdir "${DOWNLOAD_FILE_PATH}"
  fi
  echo "Using download path at ${DOWNLOAD_FILE_PATH}"
fi


COLLATED_CONSOLE_OUTPUT_FILE="${TEST_REPORT_DIR}/collatedConsoleOutput.txt"
touch "${COLLATED_CONSOLE_OUTPUT_FILE}"


START_TIME="$(date)"
echo "START TIME: ${START_TIME}" >> "${COLLATED_CONSOLE_OUTPUT_FILE}"

GRAILS_TEST_COMMAND_ARGS=(test-app -Xmx8G -Dgeb.env=chrome -DdownloadFilepath=${DOWNLOAD_FILE_PATH} -Dwebdriver.chrome.driver=${CHROME_DRIVER_PATH} functional:)
# GRAILS_TEST_COMMAND_ARGS=(test-app)

Full Functional Spec list
declare -a SPEC_BATCHES_ARRAY=("UserIsAbleToDownloadAnAssetSpec CanCreateDataTypeFromCreateDataElementWizardSpec AddAndRemoveManyDataElementsSpec"
                               "CheckCreateElementFromClassWizardSpec MaxOccursShowsInHistorySpec NewDraftEditFromImportedModelsAreUpdatedSpec"
                               "VerifyMinOccursCanBeZeroSpec CheckDataModelCanBeFinalizedSpec CheckDataModelPoliciesSpec"
                               "CheckDataModelPolicyEnumeratedTypeSpec CheckDataModelPolicyTagSpec CloneUnauthorizedElementSpec"
                               "CreateNewVersionOfDataModelSpec CannotCreateDataElementWithUnauthorizedDataTypeSpec CheckDataTypeAddedToNewVersionSpec"
                               "ValidateValueAgainstDataTypeSpec CannotAddAssetToFinalizedDataModelSpec CannotAddBusinessRulesToFinalizedDataModelSpec"
                               "CannotAddDataClassesToFinalizedDataModelSpec CannotAddDataElementsToFinalizedDataModelSpec CannotAddDataTypesToFinalizedDataModelSpec"
                               "CannotAddElementToFinalizedModelSpec CannotAddMeasurementUnitToFinalizedDataModelSpec CannotAddTagsToFinalizedDataModelSpec"
                               "AbstractModelCatalogueGebSpec ImportMcSpec ImportXmlAndExcelDataSpec"
                               "CuratorCanGenerateSuggestionsUsingMappingUtilitySpec CloneAanClassIntoAnotherModelSpec CompareTwoDataModelSpec"
                               "CreateDataModelAndCreatePolicesSpec CreateNewVersionFromFinalisedToDraftSpec CustomMetadataNotCarriedNewVersionSpec"
                               "InvalidRegistrationSpec MaxOccursIsShowingInHistorySpec VerifyResetPasswordPresentOnLoginPageSpec"
                               "VerifyUserCanTagUsingTreeViewSpec AbleToNavigateToOldVersionOfAModelThroughTreeSpec CanCreateDataElementAndCloneDataTypeSpec"
                               "CanCreateDataTypeSpec CanImportDataModelSpec CannotAddAssetToFinalizedDataModelSpec"
                               "CannotAddBusinessRulesToFinalizedDataModelSpec CannotAddDataClassesToFinalizedDataModelSpec CannotAddDataTypesToFinalizedDataModelSpec"
                               "CannotAddMeasurementUnitToFinalizedDataModelSpec CannotAddTagsToFinalizedDataModelSpec CannotDeleteFinalizedDataModelSpec"
                               "CanSelectPoliciesWhileCreatingDataModelSpec FinalizedDataModelIsMarkedAsFinalizedInXMLSpec HistoryIsPopulatedAccordingToModelActivitySpec"
                               "ImportAndRemoveDataModelReflectsInHistorySpec UnableToImportIfReadAccessSpec UserCanFinalizeDataModelSpec"
                               "AdminCanCreateModelAndPolicySpec AdminUserCannotDeleteFinalizedItemsSpec CheckAdminCanDeleteImportedModelSpec"
                               "CuratorCanCreateANewDataClassSpec CuratorCanEditDataClassesForAdminDataModelSpec CuratorCanImportFinalizedDataModelSpec"
                               "CuratorCannotCreateClassInFinalizedModelSpec CuratorCannotEditFinalizedModelSpec CuratorWithAdminCanDeleteClassInDraftModelSpec"
                               "DisableUserSpec FinalizedDataModelMenuVisibilitySpec UserCannotEditReadOnlyDataModelSpec"
                               "UsersDontSeeUnauthorizedDataModelsSpec VerifyCuratorCannotDeleteFinalizedDataModelSpec VerifyRegularUserCanSeeApiKeySpec"
                               "VerifySupervisorCanActionSettingsSpec VerifyViewerCannotAccessFactActionsSpec ApiKeySpec"
                               "CreateAssetsAndImportDataSpec CreateBusinessRulesSpec CreateDataClassSpec"
                               "CreateDataModelSpec CreateDataTypeAndSelectEnumeratedSpec CreateDataTypeAndSelectReferenceSpec"
                               "CreateDataTypeAndSelectSubsetSpec CreateMeasurementUnitSpec CreateNewDataElementSpec"
                               "CreateRelationshipSpec CreateTagSpec SearchMoreOptionPolicySpec"
                               "CreateDataTypeAndSelectPrimitiveSpec DataModelSearchSpec CreateMeasurementUnitFromFavouritesSpec"
                               "DevSupportedLinkSpec AddDataInToFavouritesSpec AddDataModelImportSpec"
                               "AddUsernameToFavouriteSpec CodeVersionSpec EditDataElementSpec"
                               "LastSeenSpec ModelCatalogueDevelopmentSpec NavItemVisibilitySpec"
                               "QuickSearchSpec RelationshipHasAttachmentOfSpec RelationshipImportsSpec"
                               "RelationshipIsBaseForSpec RelationshipIsImportedBySpec RelationshipIsSynonymForSpec"
                               "RelationshipRelatedToSpec SearchCatalogueModelsSpec LoginAsViewerSpec"
                               "LoginInAndClickOnCancelSpec LoginSpec ResetPasswordSpec"
                               "ValidateRegistrationSpec LogoutSpec ApiKeyUrlMappingsSecuredSpec"
                               "CatalogueElementUrlMappingsSecuredSpec ClassificationUrlMappingsSecuredSpec CsvTransformationUrlMappingsSecuredSpec"
                               "DataArchitectUrlMappingsSecuredSpec DataClassUrlMappingsSecuredSpec DataElementUrlMappingsSecuredSpec"
                               "DataImportCreateUrlMappingsSecuredSpec DataModelPolicyUrlMappingsSecuredSpec DataModelUrlMappingsSecuredSpec"
                               "DataTypeUrlMappingsSecuredSpec EnumeratedTypeUrlMappingsSecuredSpec LastSeenUrlMappingsSecuredSpec"
                               "LogsUrlMappingsSecuredSpec MeasurementUnitUrlMappingsSecuredSpec ModelCatalogueCorePluginUrlMappingsSecuredSpec"
                               "ModelCatalogueFormsUrlMappingsSecuredSpec ModelCatalogueGenomicsUrlMappingsSecuredSpec ModelCatalogueNorthThamesUrlMappingsSecuredSpec"
                               "ModelCatalogueVersionUrlMappingsSecuredSpec ReindexCatalogueUrlMappingsSecuredSpec UserUrlMappingSecuredSpec"
                               "GrantRoleCuratorSpec AssetWizardSpec BatchAndActionsSpec"
                               "ChangeLogForEligibilitySpec ChangesSpec DataClassWizardSpec"
                               "DataElementWizardSpec DataModelWizardSpec DataTypeWizardSpec"
                               "MeasurementUnitWizardSpec RegisterSpec SearchFunctionalSpec"
                               "UsersDontSeeUnauthorizedDataModelsSpec ValidationRuleWizardSpec VersionVerificationSpec"

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

    ./grailsw "${allTestArgs[@]}" | tee -a "${COLLATED_CONSOLE_OUTPUT_FILE}"
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


