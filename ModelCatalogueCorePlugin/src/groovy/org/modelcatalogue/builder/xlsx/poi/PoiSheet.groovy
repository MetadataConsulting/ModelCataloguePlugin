package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.modelcatalogue.builder.xlsx.Row
import org.modelcatalogue.builder.xlsx.Sheet

class PoiSheet implements Sheet {

    private final XSSFSheet xssfSheet
    private final PoiWorkbook workbook

    private final List<Integer> startPositions = []
    private int nextRowNumber = 0
    private final Set<Integer> autoColumns = new HashSet<Integer>()

    PoiSheet(PoiWorkbook workbook, XSSFSheet xssfSheet) {
        this.workbook = workbook
        this.xssfSheet = xssfSheet
    }

    @Override
    void row() {
        xssfSheet.createRow(nextRowNumber++)
    }

    @Override
    void row(@DelegatesTo(Row.class) Closure rowDefinition) {
        XSSFRow xssfRow = xssfSheet.createRow(nextRowNumber++)

        PoiRow row = new PoiRow(this, xssfRow)
        row.with rowDefinition
    }

    @Override
    void row(int row, @DelegatesTo(Row.class) Closure rowDefinition) {
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

    @Override
    void collapse(@DelegatesTo(Sheet.class) Closure insideGroupDefinition) {
        createGroup(true, insideGroupDefinition)
    }

    @Override
    void group(@DelegatesTo(Sheet.class) Closure insideGroupDefinition) {
        createGroup(false, insideGroupDefinition)
    }

    private void createGroup(boolean collapsed, @DelegatesTo(Sheet.class) Closure insideGroupDefinition) {
        startPositions.push nextRowNumber
        with insideGroupDefinition

        int startPosition = startPositions.pop()

        if (nextRowNumber - startPosition > 1) {
            int endPosition = nextRowNumber - 1
            xssfSheet.groupRow(startPosition, endPosition)
            if (collapsed) {
                xssfSheet.setRowGroupCollapsed(endPosition, true)
            }
        }

    }

    protected XSSFSheet getSheet() {
        return xssfSheet
    }

    protected void addAutoColumn(int i) {
        autoColumns << i
    }

    protected void processAutoColumns() {
        for (int index in autoColumns) {
            xssfSheet.autoSizeColumn(index)
        }
    }
}
