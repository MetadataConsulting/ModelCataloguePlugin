package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.modelcatalogue.builder.xlsx.Row
import org.modelcatalogue.builder.xlsx.Sheet

class PoiSheet implements Sheet {

    private final XSSFSheet xssfSheet

    private int nextRowNumber = 0

    PoiSheet(XSSFSheet xssfSheet) {
        this.xssfSheet = xssfSheet
    }

    @Override
    void row() {
        xssfSheet.createRow(nextRowNumber++)
    }

    @Override
    void row(@DelegatesTo(Row.class) Closure<Object> rowDefinition) {
        XSSFRow xssfRow = xssfSheet.createRow(nextRowNumber++)

        PoiRow row = new PoiRow(xssfRow)
        row.with rowDefinition
    }

    @Override
    void row(int row, @DelegatesTo(Row.class) Closure<Object> rowDefinition) {
        XSSFRow xssfRow = xssfSheet.createRow(row)

        nextRowNumber = row + 1

        PoiRow poiRow = new PoiRow(xssfRow)
        poiRow.with rowDefinition
    }

}
