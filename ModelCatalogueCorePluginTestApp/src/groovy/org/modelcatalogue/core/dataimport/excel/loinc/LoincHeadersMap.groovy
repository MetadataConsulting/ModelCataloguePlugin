package org.modelcatalogue.core.dataimport.excel.loinc

import org.modelcatalogue.core.dataimport.excel.HeadersMap

class LoincHeadersMap extends HeadersMap {

    static String PROPERTY='PROPERTY'
    static String TIME_ASPCT='TIME_ASPCT'
    static String SYSTEM='SYSTEM'
    static String METHOD_TYP='METHOD_TYP'
    static String VersionLastChanged='VersionLastChanged'
    static String CHNG_TYPE='CHNG_TYPE'
    static String DefinitionDescription='DefinitionDescription'
    static String STATUS='STATUS'
    static String CONSUMER_NAME='CONSUMER_NAME'
    static String CLASSTYPE='CLASSTYPE'
    static String FORMULA='FORMULA'
    static String SPECIES='SPECIES'
    static String EXMPL_ANSWERS='EXMPL_ANSWERS'
    static String SURVEY_QUEST_TEXT='SURVEY_QUEST_TEXT'
    static String SURVEY_QUEST_SRC='SURVEY_QUEST_SRC'
    static String UNITSREQUIRED='UNITSREQUIRED'
    static String SUBMITTED_UNITS='SUBMITTED_UNITS'
    static String RELATEDNAMES2='RELATEDNAMES2'
    static String SHORTNAME='SHORTNAME'
    static String ORDER_OBS='ORDER_OBS'
    static String CDISC_COMMON_TESTS='CDISC_COMMON_TESTS'
    static String HL7_FIELD_SUBFIELD_ID='HL7_FIELD_SUBFIELD_ID'
    static String EXTERNAL_COPYRIGHT_NOTICE='EXTERNAL_COPYRIGHT_NOTICE'
    static String EXAMPLE_UNITS='EXAMPLE_UNITS'
    static String UnitsAndRange='UnitsAndRange'
    static String DOCUMENT_SECTION='DOCUMENT_SECTION'
    static String EXAMPLE_SI_UCUM_UNITS='EXAMPLE_SI_UCUM_UNITS'
    static String STATUS_REASON='STATUS_REASON'
    static String STATUS_TEXT='STATUS_TEXT'
    static String CHANGE_REASON_PUBLIC='CHANGE_REASON_PUBLIC'
    static String COMMON_TEST_RANK='COMMON_TEST_RANK'
    static String COMMON_ORDER_RANK='COMMON_ORDER_RANK'
    static String COMMON_SI_TEST_RANK='COMMON_SI_TEST_RANK'
    static String HL7_ATTACHMENT_STRUCTURE='HL7_ATTACHMENT_STRUCTURE'
    static String EXTERNAL_COPYRIGHT_LINK='EXTERNAL_COPYRIGHT_LINK'
    static String PanelType='PanelType'
    static String AskAtOrderEntry='AskAtOrderEntry'
    static String AssociatedObservations='AssociatedObservations'
    static String VersionFirstReleased='VersionFirstReleased'
    static String ValidHL7AttachmentRequest='ValidHL7AttachmentRequest'



    static Map<String,String> createForLoincExcelLoader(Map<String, Object> params = [:]){
        Map<String,String> headersMap = new LinkedHashMap<String,String>()
        headersMap.put(dataElementCode, params.get(dataElementCode) ?: 'LOINC_NUM')
        headersMap.put(dataElementName, params.get(dataElementName) ?: 'COMPONENT')
        headersMap.put(dataElementDescription, params.get(dataElementDescription) ?: 'LONG_COMMON_NAME')
        headersMap.put(dataTypeName, params.get(dataTypeName) ?: 'SCALE_TYP')
        headersMap.put(parentDataClassName, params.get(parentDataClassName) ?: 'Parent Data Class')
        headersMap.put(containingDataClassName, params.get(containingDataClassName) ?: 'CLASS')
        headersMap.put(measurementUnitName, params.get(measurementUnitName) ?: 'EXAMPLE_UCUM_UNITS')

        // for spreadsheet produced by ExcelExporter (10 Oct 2017):

        headersMap.put(multiplicity, params.get(multiplicity) ?: 'Multiplicity')
        headersMap.put(dataTypeEnumerations, params.get(dataTypeEnumerations) ?: 'Data Type Enumerations')
        headersMap.put(dataTypeRule, params.get(dataTypeRule) ?: 'Data Type Rule')
        headersMap.put(measurementUnitCode, params.get(measurementUnitCode) ?: 'Measurement Unit ID')



        headersMap.put(PROPERTY, params.get(PROPERTY) ?: 'PROPERTY')
        headersMap.put(TIME_ASPCT, params.get(TIME_ASPCT) ?: 'TIME_ASPCT')
        headersMap.put(SYSTEM, params.get(SYSTEM) ?: 'SYSTEM')
        headersMap.put(METHOD_TYP, params.get(METHOD_TYP) ?: 'METHOD_TYP')
        headersMap.put(VersionLastChanged, params.get(VersionLastChanged) ?: 'VersionLastChanged')
        headersMap.put(CHNG_TYPE, params.get(CHNG_TYPE) ?: 'CHNG_TYPE')
        headersMap.put(DefinitionDescription, params.get(DefinitionDescription) ?: 'DefinitionDescription')
        headersMap.put(STATUS, params.get(STATUS) ?: 'STATUS')
        headersMap.put(CONSUMER_NAME, params.get(CONSUMER_NAME) ?: 'CONSUMER_NAME')
        headersMap.put(CLASSTYPE, params.get(CLASSTYPE) ?: 'CLASSTYPE')
        headersMap.put(FORMULA, params.get(FORMULA) ?: 'FORMULA')
        headersMap.put(SPECIES, params.get(SPECIES) ?: 'SPECIES')
        headersMap.put(EXMPL_ANSWERS, params.get(EXMPL_ANSWERS) ?: 'EXMPL_ANSWERS')
        headersMap.put(SURVEY_QUEST_TEXT, params.get(SURVEY_QUEST_TEXT) ?: 'SURVEY_QUEST_TEXT')
        headersMap.put(SURVEY_QUEST_SRC, params.get(SURVEY_QUEST_SRC) ?: 'SURVEY_QUEST_SRC')
        headersMap.put(UNITSREQUIRED, params.get(UNITSREQUIRED) ?: 'UNITSREQUIRED')
        headersMap.put(SUBMITTED_UNITS, params.get(SUBMITTED_UNITS) ?: 'SUBMITTED_UNITS')
        headersMap.put(RELATEDNAMES2, params.get(RELATEDNAMES2) ?: 'RELATEDNAMES2')
        headersMap.put(SHORTNAME, params.get(SHORTNAME) ?: 'SHORTNAME')
        headersMap.put(ORDER_OBS, params.get(ORDER_OBS) ?: 'ORDER_OBS')
        headersMap.put(CDISC_COMMON_TESTS, params.get(CDISC_COMMON_TESTS) ?: 'CDISC_COMMON_TESTS')
        headersMap.put(HL7_FIELD_SUBFIELD_ID, params.get(HL7_FIELD_SUBFIELD_ID) ?: 'HL7_FIELD_SUBFIELD_ID')
        headersMap.put(EXTERNAL_COPYRIGHT_NOTICE, params.get(EXTERNAL_COPYRIGHT_NOTICE) ?: 'EXTERNAL_COPYRIGHT_NOTICE')
        headersMap.put(EXAMPLE_UNITS, params.get(EXAMPLE_UNITS) ?: 'EXAMPLE_UNITS')
        headersMap.put(UnitsAndRange, params.get(UnitsAndRange) ?: 'UnitsAndRange')
        headersMap.put(DOCUMENT_SECTION, params.get(DOCUMENT_SECTION) ?: 'DOCUMENT_SECTION')
        headersMap.put(EXAMPLE_SI_UCUM_UNITS, params.get(EXAMPLE_SI_UCUM_UNITS) ?: 'EXAMPLE_SI_UCUM_UNITS')
        headersMap.put(STATUS_REASON, params.get(STATUS_REASON) ?: 'STATUS_REASON')
        headersMap.put(STATUS_TEXT, params.get(STATUS_TEXT) ?: 'STATUS_TEXT')
        headersMap.put(CHANGE_REASON_PUBLIC, params.get(CHANGE_REASON_PUBLIC) ?: 'CHANGE_REASON_PUBLIC')
        headersMap.put(COMMON_TEST_RANK, params.get(COMMON_TEST_RANK) ?: 'COMMON_TEST_RANK')
        headersMap.put(COMMON_ORDER_RANK, params.get(COMMON_ORDER_RANK) ?: 'COMMON_ORDER_RANK')
        headersMap.put(COMMON_SI_TEST_RANK, params.get(COMMON_SI_TEST_RANK) ?: 'COMMON_SI_TEST_RANK')
        headersMap.put(HL7_ATTACHMENT_STRUCTURE, params.get(HL7_ATTACHMENT_STRUCTURE) ?: 'HL7_ATTACHMENT_STRUCTURE')
        headersMap.put(EXTERNAL_COPYRIGHT_LINK, params.get(EXTERNAL_COPYRIGHT_LINK) ?: 'EXTERNAL_COPYRIGHT_LINK')
        headersMap.put(PanelType, params.get(PanelType) ?: 'PanelType')
        headersMap.put(AskAtOrderEntry, params.get(AskAtOrderEntry) ?: 'AskAtOrderEntry')
        headersMap.put(AssociatedObservations, params.get(AssociatedObservations) ?: 'AssociatedObservations')
        headersMap.put(VersionFirstReleased, params.get(VersionFirstReleased) ?: 'VersionFirstReleased')
        headersMap.put(ValidHL7AttachmentRequest, params.get(ValidHL7AttachmentRequest) ?: 'ValidHL7AttachmentRequest')

        return headersMap
    }

}
