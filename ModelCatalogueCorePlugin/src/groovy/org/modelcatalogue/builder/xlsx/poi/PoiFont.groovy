package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.ss.usermodel.FontUnderline
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.modelcatalogue.builder.xlsx.Font

class PoiFont implements Font {

    private final XSSFFont font

    PoiFont(XSSFWorkbook workbook, XSSFCellStyle style) {
        font = workbook.createFont()
        style.font = font
    }

    @Override
    void color(String hexColor) {
        font.setColor(PoiCellStyle.parseColor(hexColor))
    }

    @Override
    void size(int size) {
        font.fontHeightInPoints = (short) size
    }

//    @Override
    Object getItalic() {
        font.italic = true
    }

    @Override
    Object getBold() {
        font.bold = true
    }

    @Override
    Object getStrikeout() {
        font.strikeout = true
    }

    @Override
    Object getUnderline() {
        // TODO: support all variants
        font.underline = FontUnderline.SINGLE.value
    }
}
