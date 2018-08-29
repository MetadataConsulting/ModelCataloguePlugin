package org.modelcatalogue.gel.export

import builders.dsl.spreadsheet.builder.api.CanDefineStyle
import builders.dsl.spreadsheet.builder.api.Stylesheet

class GelXlsStyles implements Stylesheet {

    @Override
    void declareStyles(CanDefineStyle stylable) {
        stylable.with {
            style('h3') {
                background('#dbebf6')
                align center left
                font {
                    make bold
                    size 12
                    color black
                }
            }
            style('h3-green') {
                background('#c2efcf')
                align center left
                font {
                    make bold
                    size 12
                    color black
                }
            }
            style('h3-wrap') {
                wrap text
                align center left
                font {
                    make bold
                    size 12
                    color black
                }
            }
            style('h3-wrap-thick') {
                background('#dbebf6')
                wrap text
                border {
                    thickVerticalBands
                    thickHorizontalBands
                }
                align center left
                font {
                    make bold
                    size 12
                    color black
                }
            }
            style('wrap-green') {
                wrap text
                background('#c2efcf')
            }

            style('property-value') {
                align center left
                font {
                    color black
                }
            }
            style('property-value-wrap') {
                wrap text
                font {
                    color black
                }
            }
            style('property-value-green') {
                wrap text
                background('#c2efcf')
                font {
                    color black
                }
            }
        }
    }
}
