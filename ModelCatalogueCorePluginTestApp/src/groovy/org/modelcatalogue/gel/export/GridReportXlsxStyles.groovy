package org.modelcatalogue.gel.export

import builders.dsl.spreadsheet.builder.api.CanDefineStyle
import builders.dsl.spreadsheet.builder.api.Stylesheet
import groovy.transform.CompileStatic

@CompileStatic
class GridReportXlsxStyles implements Stylesheet {

    public static final String H1 = 'h1'
    public static final String TOP_LEFT_BORDER = 'top-left-border'
    public static final String TOP_BORDER = 'top-border'
    public static final String LEFT_BORDER = 'left-border'
    public static final String ANALYSIS = 'analysis'
    public static final String STANDARD = 'standard'
    @Override
    void declareStyles(CanDefineStyle stylable) {
        stylable.with {
            style(H1) {
                align center, center
                font {
                    style bold
                    size 22
                    color cornflowerBlue
                }
            }
            style(TOP_BORDER) {
                wrap text
                border top, {
                    color black
                    style medium
                }
            }
            style(LEFT_BORDER) {
                wrap text
                border left, {
                    color black
                    style medium
                }
            }
            style(TOP_LEFT_BORDER) {
                wrap text
                border top, left, {
                    color black
                    style medium
                }
            }
            style(ANALYSIS) {
                align center, center
                font {
                    style bold
                    size 16
                    color black
                }
            }
            style(STANDARD) {
                wrap text
                border top, {
                    color black
                    style medium
                }
            }
        }
    }

    static void debugLine(String s) {
        System.out.println(s)
    }
}

