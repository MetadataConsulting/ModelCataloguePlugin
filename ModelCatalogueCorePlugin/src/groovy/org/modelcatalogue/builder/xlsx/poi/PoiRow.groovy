package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.modelcatalogue.builder.xlsx.Cell
import org.modelcatalogue.builder.xlsx.CellStyle
import org.modelcatalogue.builder.xlsx.Row

class PoiRow implements Row {

    private final XSSFRow xssfRow
    private final PoiSheet sheet

    private String styleName
    private Closure<Object> styleDefinition = { return null }

    PoiRow(PoiSheet sheet, XSSFRow xssfRow) {
        this.sheet = sheet
        this.xssfRow = xssfRow
    }

    @Override
    void cell() {
        xssfRow.createCell(xssfRow.lastCellNum, org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK)
    }

    @Override
    void cell(Object value) {
        XSSFCell xssfCell = xssfRow.createCell(nextCellNum)

        PoiCell poiCell = new PoiCell(this, xssfCell)
        poiCell.style styleDefinition
        poiCell.value value
    }

    @Override
    void cell(@DelegatesTo(Cell.class) Closure<Object> cellDefinition) {
        XSSFCell xssfCell = xssfRow.createCell(nextCellNum)

        PoiCell poiCell = new PoiCell(this, xssfCell)
        poiCell.style styleDefinition
        poiCell.with cellDefinition

        handleSpans(xssfCell, poiCell)
    }

    private void handleSpans(XSSFCell xssfCell, PoiCell poiCell) {
        if (poiCell.colspan > 1 || poiCell.rowspan > 1) {
            xssfRow.sheet.addMergedRegion(new CellRangeAddress(
                    xssfCell.rowIndex,
                    xssfCell.rowIndex + poiCell.rowspan,
                    xssfCell.columnIndex,
                    xssfCell.columnIndex + poiCell.colspan
            ));
        }
    }

    @Override
    void cell(int column, @DelegatesTo(Cell.class) Closure<Object> cellDefinition) {
        XSSFCell xssfCell = xssfRow.createCell(column)

        PoiCell poiCell = new PoiCell(this, xssfCell)
        if (styleName) {
            poiCell.style styleName
        }
        poiCell.style styleDefinition
        poiCell.with cellDefinition
    }

    @Override
    void style(@DelegatesTo(CellStyle.class) Closure<Object> styleDefinition) {
        this.styleDefinition = styleDefinition
    }

    @Override
    void style(String name) {
        this.styleName = name
    }

    private short getNextCellNum() {
        Math.max(xssfRow.lastCellNum, 0)
    }

    protected PoiSheet getSheet() {
        return sheet
    }
}
