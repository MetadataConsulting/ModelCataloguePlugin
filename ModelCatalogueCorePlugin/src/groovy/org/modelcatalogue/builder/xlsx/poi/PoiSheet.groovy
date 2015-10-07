package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.modelcatalogue.builder.xlsx.Row
import org.modelcatalogue.builder.xlsx.Sheet

class PoiSheet implements Sheet {

    private final XSSFSheet xssfSheet
    private final PoiWorkbook workbook

    private int nextRowNumber = 0

    PoiSheet(PoiWorkbook workbook, XSSFSheet xssfSheet) {
        this.workbook = workbook
        this.xssfSheet = xssfSheet
    }

    @Override
    void row() {
        xssfSheet.createRow(nextRowNumber++)
    }

    @Override
    void row(@DelegatesTo(Row.class) Closure<Object> rowDefinition) {
        XSSFRow xssfRow = xssfSheet.createRow(nextRowNumber++)

        PoiRow row = new PoiRow(this, xssfRow)
        row.with rowDefinition
    }

    @Override
    void row(int row, @DelegatesTo(Row.class) Closure<Object> rowDefinition) {
        XSSFRow xssfRow = xssfSheet.createRow(row)

        nextRowNumber = row + 1

        PoiRow poiRow = new PoiRow(this, xssfRow)
        poiRow.with rowDefinition
    }

    protected PoiWorkbook getWorkbook() {
        return workbook
    }

    @Override
    void freeze(int column, int row) {
        xssfSheet.createFreezePane(column, row)
    }
}
