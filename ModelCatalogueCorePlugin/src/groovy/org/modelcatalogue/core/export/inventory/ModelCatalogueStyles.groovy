package org.modelcatalogue.core.export.inventory

import org.modelcatalogue.builder.spreadsheet.api.CanDefineStyle
import org.modelcatalogue.builder.spreadsheet.api.Stylesheet

class ModelCatalogueStyles implements Stylesheet {

    @Override
    void declareStyles(CanDefineStyle stylable) {
        stylable.with {
            style('h1') {
                align center center
                font {
                    bold
                    size 22
                    color cornflowerBlue
                }
            }
            style('h2') {
                align center center
                font {
                    bold
                    size 16
                    color cornflowerBlue
                }
            }
            style('description') {
                wrap text
                align top justify
                font {
                    color dimGray
                }
            }
            style('date') {
                format 'dd/mm/yy'
                align center center
                font {
                    size 12
                    color dimGray
                }
            }
            style('status') {
                align center center
                font {
                    size 12
                    color dimGray
                }
            }
            style('property-value') {
                align center center
                font {
                    color dimGray
                }
            }
            style('model-catalogue-id') {
                align center center
                font {
                    size 12
                    color dimGray
                }
            }
            style('inner-table-header') {
                font {
                    bold
                    size 12
                    font {
                        color cornflowerBlue
                    }
                }
                align center center
            }
            style('note') {
                font {
                    italic
                    color dimGray
                    align center center
                }
            }
            style('property-title') {
                font {
                    color dimGray
                    bold
                }
            }
            style('data-element') {
                foreground whiteSmoke
                font {
                    bold
                }
            }
            style('data-element-bottom-right') {
                align bottom right
                foreground whiteSmoke
                font {
                    bold
                }
            }
            style('data-element-center-center') {
                align center center
                foreground whiteSmoke
                font {
                    bold
                }
            }
            style('data-element-top-right') {
                align top right
                foreground whiteSmoke
                font {
                    bold
                }
            }
            style('data-element-description-row') {
                wrap text
                font { size 10 }
                align top justify
            }
            style('metadata-key') {
                font {
                    size 10
                    bold
                    indent 4
                }
            }

            style ('metadata-value') {
                font {
                    size 10
                }
            }
            style ('link') {
                font {
                    underline
                }
            }
        }
    }
}
