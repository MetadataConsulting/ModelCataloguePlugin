package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.modelcatalogue.builder.xlsx.Cell
import org.modelcatalogue.builder.xlsx.CellStyle
import org.modelcatalogue.builder.xlsx.Row

class PoiRow implements Row {

    private final XSSFRow xssfRow

    private Closure<Object> styleDefinition = { return null }

    PoiRow(XSSFRow xssfRow) {
        this.xssfRow = xssfRow
    }

    @Override
    void cell() {
        xssfRow.createCell(xssfRow.lastCellNum, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK)
    }

    @Override
    void cell(Object value) {
        XSSFCell xssfCell = xssfRow.createCell(nextCellNum)

        PoiCell poiCell = new PoiCell(xssfCell)
        poiCell.style styleDefinition
        poiCell.value value
    }

    @Override
    void cell(@DelegatesTo(Cell.class) Closure<Object> cellDefinition) {
        XSSFCell xssfCell = xssfRow.createCell(nextCellNum)

        PoiCell poiCell = new PoiCell(xssfCell)
        poiCell.style styleDefinition
        poiCell.with cellDefinition
    }

    @Override
    void cell(int column, @DelegatesTo(Cell.class) Closure<Object> cellDefinition) {
        XSSFCell xssfCell = xssfRow.createCell(column)

        PoiCell poiCell = new PoiCell(xssfCell)
        poiCell.style styleDefinition
        poiCell.with cellDefinition
    }

    @Override
    void style(@DelegatesTo(CellStyle.class) Closure<Object> styleDefinition) {
        this.styleDefinition = styleDefinition
    }

    private short getNextCellNum() {
        Math.max(xssfRow.lastCellNum, 0)
    }
}
