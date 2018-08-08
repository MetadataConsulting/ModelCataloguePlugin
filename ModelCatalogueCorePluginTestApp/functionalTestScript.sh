#!/bin/bash

TEST_REPORT_DIR="$(./testReport.sh)"

if [ ! -d "${TEST_REPORT_DIR}" ]
then
    echo "TEST_REPORT_DIR "${TEST_REPORT_DIR}" needs to be a directory path"
    exit 1
fi

COLLATED_CONSOLE_OUTPUT="${TEST_REPORT_DIR}/collatedConsoleOutput.txt"
touch "${COLLATED_CONSOLE_OUTPUT}"


START_TIME="$(date)"
echo "START TIME: ${START_TIME}" >> "${COLLATED_CONSOLE_OUTPUT}"

DOWNLOAD_FILE_PATH=/home/james/Downloads/functionalTestDownloads
CHROME_DRIVER_PATH=/usr/local/lib/node_modules/chromedriver/bin/chromedriver
GRAILS_TEST_COMMAND_ARGS=(test-app -Xmx8G -Dgeb.env=chrome -DdownloadFilepath=${DOWNLOAD_FILE_PATH} -Dwebdriver.chrome.driver=${CHROME_DRIVER_PATH} functional:)


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


TEST_SUMMARY="${TEST_REPORT_DIR}/testSummary.txt"
touch "${TEST_SUMMARY}"
BATCHES_ARRAY_SIZE="${#SPEC_BATCHES_ARRAY[*]}"
for i2 in $(seq ${BATCH_ARRAY_SIZE})
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

    ./grailsw "${allTestArgs[@]}" | tee -a "${COLLATED_CONSOLE_OUTPUT}"
    printf "Just ran batch ${i}/${BATCH_ARRAY_SIZE}: ${batch}\n" >> "${COLLATED_CONSOLE_OUTPUT}"


    mkdir "${batchReportDir}"
    mv target/test-reports "${batchReportDir}"


    sed -n "
        /Completed [0-9][0-9]* spock test/ p
        /Just ran/ p
    " "${COLLATED_CONSOLE_OUTPUT}" > "${TEST_SUMMARY}"

    CURRENT_TIME="$(date)"
    echo "CURRENT TIME: ${CURRENT_TIME}" >> "${COLLATED_CONSOLE_OUTPUT}"
done


CURRENT_TIME="$(date)"
echo "END TIME: ${CURRENT_TIME}" >> "${COLLATED_CONSOLE_OUTPUT}"
