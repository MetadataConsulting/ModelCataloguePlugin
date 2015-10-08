package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.modelcatalogue.builder.xlsx.ExcelBuilder
import org.modelcatalogue.builder.xlsx.Sheet


class PoiExcelBuilder implements ExcelBuilder {

    @Override
    void build(OutputStream outputStream, @DelegatesTo(Sheet.class) Closure workbookDefinition) {
        XSSFWorkbook workbook = new XSSFWorkbook()

        PoiWorkbook poiWorkbook = new PoiWorkbook(workbook)
        poiWorkbook.with workbookDefinition

        workbook.write(outputStream)
    }
}
