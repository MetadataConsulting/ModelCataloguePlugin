package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFDataFormat
import org.modelcatalogue.builder.xlsx.AbstractCellStyle
import org.modelcatalogue.builder.xlsx.Border
import org.modelcatalogue.builder.xlsx.BorderSide
import org.modelcatalogue.builder.xlsx.BorderSideAndHorizontalAlignment
import org.modelcatalogue.builder.xlsx.Font
import org.modelcatalogue.builder.xlsx.ForegroundFill
import org.modelcatalogue.builder.xlsx.HorizontalAlignment
import org.modelcatalogue.builder.xlsx.PureBorderSide
import org.modelcatalogue.builder.xlsx.PureHorizontalAlignment
import org.modelcatalogue.builder.xlsx.VerticalAlignmentConfigurer

class PoiCellStyle extends AbstractCellStyle {

    private final XSSFCell xssfCell
    private final XSSFCellStyle style

    PoiCellStyle(XSSFCell xssfCell) {
        this.xssfCell = xssfCell
        style = xssfCell.row.sheet.workbook.createCellStyle()
        xssfCell.cellStyle = style
    }

    @Override
    void background(String hexColor) {
        style.setFillBackgroundColor(parseColor(hexColor))
    }

    @Override
    void foreground(String hexColor) {
        style.setFillForegroundColor(parseColor(hexColor))
    }

    @Override
    void fill(ForegroundFill fill) {
        switch (fill) {
            case ForegroundFill.NO_FILL:
                style.fillPattern = CellStyle.NO_FILL
                break
            case ForegroundFill.SOLID_FOREGROUND:
                style.fillPattern = CellStyle.SOLID_FOREGROUND
                break
            case ForegroundFill.FINE_DOTS:
                style.fillPattern = CellStyle.FINE_DOTS
                break
            case ForegroundFill.ALT_BARS:
                style.fillPattern = CellStyle.ALT_BARS
                break
            case ForegroundFill.SPARSE_DOTS:
                style.fillPattern = CellStyle.SPARSE_DOTS
                break
            case ForegroundFill.THICK_HORZ_BANDS:
                style.fillPattern = CellStyle.THICK_HORZ_BANDS
                break
            case ForegroundFill.THICK_VERT_BANDS:
                style.fillPattern = CellStyle.THICK_VERT_BANDS
                break
            case ForegroundFill.THICK_BACKWARD_DIAG:
                style.fillPattern = CellStyle.THICK_BACKWARD_DIAG
                break
            case ForegroundFill.THICK_FORWARD_DIAG:
                style.fillPattern = CellStyle.THICK_FORWARD_DIAG
                break
            case ForegroundFill.BIG_SPOTS:
                style.fillPattern = CellStyle.BIG_SPOTS
                break
            case ForegroundFill.BRICKS:
                style.fillPattern = CellStyle.BRICKS
                break
            case ForegroundFill.THIN_HORZ_BANDS:
                style.fillPattern = CellStyle.THIN_HORZ_BANDS
                break
            case ForegroundFill.THIN_VERT_BANDS:
                style.fillPattern = CellStyle.THIN_VERT_BANDS
                break
            case ForegroundFill.THIN_BACKWARD_DIAG:
                style.fillPattern = CellStyle.THIN_BACKWARD_DIAG
                break
            case ForegroundFill.THIN_FORWARD_DIAG:
                style.fillPattern = CellStyle.THIN_FORWARD_DIAG
                break
            case ForegroundFill.SQUARES:
                style.fillPattern = CellStyle.SQUARES
                break
            case ForegroundFill.DIAMONDS:
                style.fillPattern = CellStyle.DIAMONDS
                break
        }
    }

    @Override
    void font(@DelegatesTo(Font.class) Closure<Object> fontConfiguration) {
        PoiFont poiFont = new PoiFont(xssfCell, style)
        poiFont.with fontConfiguration
    }

    @Override
    void indent(int indent) {
        style.indention = (short) indent
    }

    @Override
    Object getLocked() {
        style.locked = true
    }

    @Override
    Object getWrapped() {
        style.wrapText = true
    }

    @Override
    Object getHidden() {
        style.hidden = true
    }

    @Override
    void rotation(int rotation) {
        style.rotation = (short) rotation
    }

    @Override
    void format(String format) {
        XSSFDataFormat dataFormat = xssfCell.row.sheet.workbook.createDataFormat()
        style.dataFormat = dataFormat.getFormat(format)
    }

    @Override
    VerticalAlignmentConfigurer align(HorizontalAlignment alignment) {
        switch (alignment) {
            case PureHorizontalAlignment.GENERAL:
                style.alignment = CellStyle.ALIGN_GENERAL
                break
            case BorderSideAndHorizontalAlignment.LEFT:
                style.alignment = CellStyle.ALIGN_LEFT
                break
            case PureHorizontalAlignment.CENTER:
                style.alignment = CellStyle.ALIGN_CENTER
                break
            case BorderSideAndHorizontalAlignment.RIGHT:
                style.alignment = CellStyle.ALIGN_RIGHT
                break
            case PureHorizontalAlignment.FILL:
                style.alignment = CellStyle.ALIGN_FILL
                break
            case PureHorizontalAlignment.JUSTIFY:
                style.alignment = CellStyle.ALIGN_JUSTIFY
                break
            case PureHorizontalAlignment.CENTER_SELECTION:
                style.alignment = CellStyle.ALIGN_CENTER_SELECTION
                break
            default:
                throw new IllegalArgumentException("$alignment is not supported!")
        }
        return new PoiVerticalAlignmentConfigurer(this)
    }

    @Override
    void border(@DelegatesTo(Border.class) Closure<Object> borderConfiguration) {
        PoiBorder poiBorder = new PoiBorder(style)
        poiBorder.with borderConfiguration

        [*PureBorderSide.values(), *BorderSideAndHorizontalAlignment.values()].each {
            poiBorder.applyTo(it)
        }
    }

    @Override
    void border(BorderSide location, @DelegatesTo(Border.class) Closure<Object> borderConfiguration) {
        PoiBorder poiBorder = new PoiBorder(style)
        poiBorder.with borderConfiguration
        poiBorder.applyTo(location)
    }

    @Override
    void border(BorderSide first, BorderSide second,
                @DelegatesTo(Border.class) Closure<Object> borderConfiguration) {

        PoiBorder poiBorder = new PoiBorder(style)
        poiBorder.with borderConfiguration
        poiBorder.applyTo(first)
        poiBorder.applyTo(second)

    }

    @Override
    void border(BorderSide first, BorderSide second, BorderSide third,
                @DelegatesTo(Border.class) Closure<Object> borderConfiguration) {

        PoiBorder poiBorder = new PoiBorder(style)
        poiBorder.with borderConfiguration
        poiBorder.applyTo(first)
        poiBorder.applyTo(second)
        poiBorder.applyTo(third)
    }


    protected setVerticalAlignment(VerticalAlignment alignment) {
        style.setVerticalAlignment(alignment)
    }

    static XSSFColor parseColor(String hex) {
        if (!hex) {
            throw new IllegalArgumentException("Please, provide the color in '#abcdef' hex string format")
        }
        def match = hex.toUpperCase() =~ /#([\dA-F]{2})([\dA-F]{2})([\dA-F]{2})/

        if (!match) {
            throw new IllegalArgumentException("Cannot parse color $hex. Please, provide the color in '#abcdef' hex string format")
        }


        byte red = Integer.parseInt(match[0][1], 16)
        byte green = Integer.parseInt(match[0][2], 16)
        byte blue = Integer.parseInt(match[0][3], 16)

        new XSSFColor([red, green, blue] as byte[])
    }
}
