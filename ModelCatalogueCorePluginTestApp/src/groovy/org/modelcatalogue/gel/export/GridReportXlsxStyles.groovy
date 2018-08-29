package org.modelcatalogue.gel.export

import builders.dsl.spreadsheet.api.BorderStyle
import builders.dsl.spreadsheet.api.Color
import builders.dsl.spreadsheet.api.Configurer
import builders.dsl.spreadsheet.api.FontStyle
import builders.dsl.spreadsheet.api.Keywords
import builders.dsl.spreadsheet.builder.api.BorderDefinition
import builders.dsl.spreadsheet.builder.api.CanDefineStyle
import builders.dsl.spreadsheet.builder.api.CellStyleDefinition
import builders.dsl.spreadsheet.builder.api.FontDefinition
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
        stylable.style(H1, new Configurer<CellStyleDefinition>() {
            @Override
            void configure(CellStyleDefinition c) {
                c.align(Keywords.VerticalAlignment.CENTER, Keywords.HorizontalAlignment.CENTER)
                c.font(new Configurer<FontDefinition>() {
                    @Override
                    void configure(FontDefinition f) {
                        f.style(FontStyle.BOLD)
                        f.size(22)
                        f.color(Color.cornflowerBlue)
                    }
                })
            }
        })
        stylable.style(TOP_BORDER, new Configurer<CellStyleDefinition>() {
            @Override
            void configure(CellStyleDefinition c) {
                c.wrap(c.text)
                c.border(Keywords.BorderSide.TOP, new Configurer<BorderDefinition>() {
                    @Override
                    void configure(BorderDefinition b) {
                        b.color(Color.black)
                        b.style(BorderStyle.MEDIUM)
                    }
                })
            }
        })
        stylable.style(LEFT_BORDER, new Configurer<CellStyleDefinition>() {
            @Override
            void configure(CellStyleDefinition c) {
                c.wrap(c.text)
                c.border(Keywords.BorderSide.LEFT, new Configurer<BorderDefinition>() {
                    @Override
                    void configure(BorderDefinition b) {
                        b.color(Color.black)
                        b.style(BorderStyle.MEDIUM)
                    }
                })
            }
        })
        stylable.style(TOP_LEFT_BORDER, new Configurer<CellStyleDefinition>() {
            @Override
            void configure(CellStyleDefinition c) {
                c.wrap(c.text)
                c.border(Keywords.BorderSide.TOP, Keywords.BorderSide.LEFT, new Configurer<BorderDefinition>() {
                    @Override
                    void configure(BorderDefinition b) {
                        b.color(Color.black)
                        b.style(BorderStyle.MEDIUM)
                    }
                })
            }
        })
        stylable.style(ANALYSIS, new Configurer<CellStyleDefinition>() {
            @Override
            void configure(CellStyleDefinition c) {
                c.align(Keywords.VerticalAlignment.CENTER, Keywords.HorizontalAlignment.CENTER)
                c.font(new Configurer<FontDefinition>() {
                    @Override
                    void configure(FontDefinition f) {
                        f.style(FontStyle.BOLD)
                        f.size(16)
                        f.color(Color.black)
                    }
                })
            }
        })
        stylable.style(STANDARD, new Configurer<CellStyleDefinition>() {
            @Override
            void configure(CellStyleDefinition c) {
                c.wrap(c.text)
                c.border(Keywords.BorderSide.TOP, new Configurer<BorderDefinition>() {
                    @Override
                    void configure(BorderDefinition b) {
                        b.color(Color.black)
                        b.style(BorderStyle.MEDIUM)
                    }
                })
            }
        })
    }

    static void debugLine(String s) {
        System.out.println(s)
    }
}

