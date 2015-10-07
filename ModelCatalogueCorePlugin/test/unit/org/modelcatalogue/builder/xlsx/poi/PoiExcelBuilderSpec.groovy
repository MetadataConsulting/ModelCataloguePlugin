package org.modelcatalogue.builder.xlsx.poi

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.xlsx.ExcelBuilder
import spock.lang.Specification

import java.awt.Desktop


class PoiExcelBuilderSpec extends Specification {

    @Rule TemporaryFolder tmp

    def "create sample spreadsheet"() {
        when:
        File tmpFile = tmp.newFile("sample${System.currentTimeMillis()}.xlsx")

        ExcelBuilder builder = new PoiExcelBuilder()

        tmpFile.withOutputStream { OutputStream out ->
            builder.build(out) {
                sheet('One') {
                    row {
                        cell 'First Row'
                    }

                    row()

                    row {
                        style {
                            align left center
                            border {
                                color '#abcdef'
                                style dashDotDot
                            }
                            border right, {
                                color '#00ff00'
                            }
                        }
                        cell 'Hello'
                        cell {
                            style {
                                font {
                                    bold
                                    size 12
                                    color '#dddddd'
                                }
                                background '#123456'
                                foreground '#654321'
                                fill thinBackwardDiagonal
                            }
                            value 'World'
                            comment {
                                text 'This cell has some fancy fg/bg'
                                author 'musketyr'
                            }
                        }
                        cell {
                            style {
                                format 'd.m.y'
                            }
                            value new Date()
                            comment 'This is a date!'
                        }
                    }
                }
            }
        }

        open tmpFile

        then:
        noExceptionThrown()
    }

    /**
     * Tries to open the file in Word. Only works locally on Mac at the moment. Ignored otherwise.
     * Main purpose of this method is to quickly open the generated file for manual review.
     * @param file file to be opened
     */
    private static void open(File file) {
        try {
            if (Desktop.desktopSupported && Desktop.desktop.isSupported(Desktop.Action.OPEN)) {
                Desktop.desktop.open(file)
                Thread.sleep(10000)
            }
        } catch(ignored) {
            // CI
        }
    }



}
