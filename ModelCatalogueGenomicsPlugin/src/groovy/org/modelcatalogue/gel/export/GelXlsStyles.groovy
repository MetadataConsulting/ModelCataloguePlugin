package org.modelcatalogue.gel.export

import org.modelcatalogue.builder.spreadsheet.api.CanDefineStyle
import org.modelcatalogue.builder.spreadsheet.api.Stylesheet

class GelXlsStyles implements Stylesheet {

    @Override
    void declareStyles(CanDefineStyle stylable) {
        stylable.with {
            style('h3') {
                background('#dbebf6')
                align center left
                font {
                    bold
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
