package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.modelcatalogue.builder.xlsx.api.ExcelBuilder
import org.modelcatalogue.builder.xlsx.api.Sheet


class PoiExcelBuilder implements ExcelBuilder {

    @Override
    void build(OutputStream outputStream, @DelegatesTo(Sheet.class) Closure workbookDefinition) {
        XSSFWorkbook workbook = new XSSFWorkbook()

        PoiWorkbook poiWorkbook = new PoiWorkbook(workbook)
        poiWorkbook.with workbookDefinition

        workbook.write(outputStream)
    }
}
