package org.modelcatalogue

import org.modelcatalogue.core.dataimport.excel.ExcelImportType

enum Client {
    NORTH_THAMES([
        ExcelImportType.NORTH_THAMES_DATA_SOURCE_MAPPING,
        ExcelImportType.LOINC,
        ExcelImportType.GOSH_LAB_TEST_CODES,
    ]),
    GEL([]),
    NHS_DIGITAL([])

    final List<ExcelImportType> excelImportTypes

    Client(List<ExcelImportType> excelImportTypes) {
        this.excelImportTypes = excelImportTypes
    }
    Client clientFromSystemProperty() {
        return Client.fromString(System.properties['mc.client'])
    }
    List<ExcelImportType> getExcelImportTypes() {
        return excelImportTypes + [ExcelImportType.REIMPORT_FROM_EXCEL_EXPORTER, ExcelImportType.STANDARD]
    }
    private static final Map<String, Client> strings = new HashMap()
    static {
        for (Client client: values()) {
            strings.put(client.toString(), client)
        }
    }

    static Client fromString(String str) {
        return strings.get(str)
    }
}
