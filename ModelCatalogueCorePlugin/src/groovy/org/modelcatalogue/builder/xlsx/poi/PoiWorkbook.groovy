package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.modelcatalogue.builder.xlsx.Sheet
import org.modelcatalogue.builder.xlsx.Workbook


class PoiWorkbook implements Workbook {

    private final XSSFWorkbook workbook

    PoiWorkbook(XSSFWorkbook workbook) {
        this.workbook = workbook
    }

    @Override
    void sheet(String name, @DelegatesTo(Sheet.class) Closure<Object> sheetDefinition) {
        XSSFSheet xssfSheet = workbook.getSheet(name) ?: workbook.createSheet(name)

        PoiSheet sheet = new PoiSheet(xssfSheet)
        sheet.with sheetDefinition
    }
}
