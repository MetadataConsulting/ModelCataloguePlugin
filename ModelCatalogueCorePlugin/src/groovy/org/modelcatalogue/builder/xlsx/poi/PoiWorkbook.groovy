package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.modelcatalogue.builder.xlsx.api.CellStyle
import org.modelcatalogue.builder.xlsx.api.Sheet
import org.modelcatalogue.builder.xlsx.api.Workbook


class PoiWorkbook implements Workbook {

    private final XSSFWorkbook workbook
    private final Map<String, XSSFCellStyle> namedStyles = [:]

    PoiWorkbook(XSSFWorkbook workbook) {
        this.workbook = workbook
    }

    @Override
    void sheet(String name, @DelegatesTo(Sheet.class) Closure sheetDefinition) {
        XSSFSheet xssfSheet = workbook.getSheet(name) ?: workbook.createSheet(name)

        PoiSheet sheet = new PoiSheet(this, xssfSheet)
        sheet.with sheetDefinition

        sheet.processAutoColumns()
    }

    @Override
    void style(String name, @DelegatesTo(CellStyle.class) Closure styleDefinition) {
        XSSFCellStyle style = workbook.createCellStyle()
        PoiCellStyle poiCellStyle = new PoiCellStyle(workbook, style)
        poiCellStyle.with styleDefinition
        namedStyles[name] = style
    }

    protected XSSFCellStyle getStyle(String name) {
        XSSFCellStyle style = namedStyles[name]
        if (!style) {
            throw new IllegalArgumentException("Style '$name' not defined")
        }
        return style
    }
}
