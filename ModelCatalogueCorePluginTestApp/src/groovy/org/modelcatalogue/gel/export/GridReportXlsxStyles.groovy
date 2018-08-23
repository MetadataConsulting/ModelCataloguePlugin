package org.modelcatalogue.gel.export

import builders.dsl.spreadsheet.builder.api.CanDefineStyle
import builders.dsl.spreadsheet.builder.api.Stylesheet

class GridReportXlsxStyles implements Stylesheet {
    public static final String H1 = 'h1'
    public static final String TOP_LEFT_BORDER = 'top-left-border'
    public static final String TOP_BORDER = 'top-border'
    public static final String LEFT_BORDER = 'left-border'
    public static final String ANALYSIS = 'analysis'
    public static final String STANDARD = 'standard'

    public static final Closure h1CellStyle = {
        align center, center
        font {
            style bold
            size 22
            color cornflowerBlue
        }
    }
    public static final Closure analysisStyle = {
        align center, center
        font {
            style bold
            size 16
            color black
        }
    }
    public static final Closure standardCellStyle = {
        wrap text
        border top, {
            color black
            style medium
        }
    }
    public static final Closure topLeftBorderCellStyle = {
        wrap text
        border top, left, {
            color black
            style medium
        }
    }
    public static final Closure topBorderCellStyle = {
        wrap text
        border top, {
            color black
            style medium
        }
    }
    public static final Closure leftBorderCellStyle = {
        wrap text
        border left, {
            color black
            style medium
        }
    }

    @Override
    void declareStyles(CanDefineStyle stylable) {
        stylable.style(H1, h1CellStyle)
        stylable.style(TOP_BORDER, topBorderCellStyle)
        stylable.style(LEFT_BORDER, leftBorderCellStyle)
        stylable.style(TOP_LEFT_BORDER, topLeftBorderCellStyle)
        stylable.style(ANALYSIS, analysisStyle)
        stylable.style(STANDARD, standardCellStyle)
    }

    static void debugLine(String s) {
        System.out.println(s)
    }
}

