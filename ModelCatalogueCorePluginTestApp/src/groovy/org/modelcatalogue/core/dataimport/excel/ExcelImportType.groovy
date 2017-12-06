package org.modelcatalogue.core.dataimport.excel

/**
 * Created by james on 01/12/2017.
 */
enum ExcelImportType {
    LOINC('LOINC'),
    GOSH_LAB_TEST_CODES('GOSH Lab Test Codes'),
    NORTH_THAMES_DATA_SOURCE_MAPPING('North Thames Data Source Mapping'),
    REIMPORT_FROM_EXCEL_EXPORTER('Re-import from Excel Exporter'),
    STANDARD('Standard')

    final String humanReadableName

    ExcelImportType(String humanReadableName) {
        this.humanReadableName = humanReadableName
    }

    // list of human readable names for the front-end, which when passed back can be used to find the enum.
    static List<String> humanReadableNames = values().collect {
        it.humanReadableName
    }

    private static final Map<String, ExcelImportType> humanReadableNameToEnum = new HashMap()
    static {
        for (ExcelImportType eIT: values()) {
            humanReadableNameToEnum.put(eIT.humanReadableName, eIT)
        }
    }

    static ExcelImportType fromHumanReadableName(String humanReadableName) {
        return humanReadableNameToEnum.get(humanReadableName)
    }


}

