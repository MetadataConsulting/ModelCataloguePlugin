package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFName
import org.modelcatalogue.builder.xlsx.Cell
import org.modelcatalogue.builder.xlsx.CellStyle
import org.modelcatalogue.builder.xlsx.Comment
import org.modelcatalogue.builder.xlsx.LinkDefinition
import org.modelcatalogue.builder.xlsx.ToKeyword

class PoiCell implements Cell  {

    private final PoiRow row
    private final XSSFCell xssfCell

    private int colspan = 1
    private int rowspan = 1

    PoiCell(PoiRow row, XSSFCell xssfCell) {
        this.xssfCell = xssfCell
        this.row = row
    }

    @Override
    void value(Object value) {
        if (!value) {
            xssfCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK)
            return
        }

        if (value instanceof Number) {
            xssfCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC)
            xssfCell.setCellValue(value.doubleValue())
            return
        }

        if (value instanceof Date) {
            xssfCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC)
            xssfCell.setCellValue(value as Date)
            return
        }

        if (value instanceof Calendar) {
            xssfCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC)
            xssfCell.setCellValue(value as Calendar)
            return
        }

        if (value instanceof Boolean) {
            xssfCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN)
            xssfCell.setCellValue(value as Boolean)
            return
        }

        if (value instanceof Boolean) {
            xssfCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN)
            xssfCell.setCellValue(value as Boolean)
            return
        }

        xssfCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING)
        xssfCell.setCellValue(value.toString())
    }

    @Override
    void style(@DelegatesTo(CellStyle.class) Closure<Object> styleDefinition) {
        PoiCellStyle poiCellStyle = new PoiCellStyle(xssfCell)
        poiCellStyle.with styleDefinition
    }

    @Override
    void comment(String commentText) {
        comment {
            text commentText
        }
    }

    @Override
    void comment(@DelegatesTo(Comment.class) Closure<Object> commentDefinition) {
        PoiComment poiComment = new PoiComment()
        poiComment.with commentDefinition
        poiComment.applyTo xssfCell
    }

    @Override
    void colspan(int span) {
        this.colspan = span
    }

    @Override
    void rowspan(int span) {
        this.rowspan = span
    }

    @Override
    void style(String name) {
        xssfCell.cellStyle = row.sheet.workbook.getStyle(name)
    }

    @Override
    void name(String name) {
        XSSFName theName = xssfCell.row.sheet.workbook.createName()
        theName.setNameName(name)
        theName.setRefersToFormula("${xssfCell.sheet.sheetName}!${xssfCell.reference}")
    }

    @Override
    ToKeyword getTo() {
        return ToKeyword.TO
    }

    @Override
    LinkDefinition link(ToKeyword to) {
        return new PoiLinkDefintion(xssfCell)
    }

    protected int getColspan() {
        return colspan
    }

    protected int getRowspan() {
        return rowspan
    }
}
