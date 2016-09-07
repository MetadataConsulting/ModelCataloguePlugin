package org.modelcatalogue.core.export.inventory

import org.modelcatalogue.builder.spreadsheet.api.CanDefineStyle
import org.modelcatalogue.builder.spreadsheet.api.Stylesheet

class ModelCatalogueStyles implements Stylesheet {


    public static final String H1 = 'h1'
    public static final String H2 = 'h2'
    public static final String DESCRIPTION = 'description'
    public static final String DATE = 'date'
    public static final String DATE_NORMAL = 'date-normal'
    public static final String STATUS = 'status'
    public static final String PROPERTY_VALUE = 'property-value'
    public static final String MODEL_CATALOGUE_ID = 'model-catalogue-id'
    public static final String INNER_TABLE_HEADER = 'inner-table-header'
    public static final String NOTE = 'note'
    public static final String PROPERTY_TITLE = 'property-title'
    public static final String DATA_ELEMENT = 'data-element'
    public static final String DATA_ELEMENT_BOTTOM_RIGHT = 'data-element-bottom-right'
    public static final String DATA_ELEMENT_CENTER_CENTER = 'data-element-center-center'
    public static final String CENTER_CENTER = 'center-center'
    public static final String DATA_ELEMENT_TOP_RIGHT = 'data-element-top-right'
    public static final String DATA_ELEMENT_DESCRIPTION_ROW = 'data-element-description-row'
    public static final String METADATA_KEY = 'metadata-key'
    public static final String METADATA_VALUE = 'metadata-value'
    public static final String LINK = 'link'
    public static final String THIN_DARK_GREY_BORDER = 'thin-dark-grey-border'
    public static final String DIM_GRAY_FONT = 'dim-gray-font'

    @Override
    void declareStyles(CanDefineStyle stylable) {
        stylable.with {
            style(H1) {
                align center center
                font {
                    bold
                    size 22
                    color cornflowerBlue
                }
            }
            style(H2) {
                align center center
                font {
                    bold
                    size 16
                    color cornflowerBlue
                }
            }
            style(DESCRIPTION) {
                wrap text
                align top justify
                font {
                    color dimGray
                }
            }
            style(DATE) {
                format 'dd/mm/yy'
                align center center
                font {
                    size 12
                    color dimGray
                }
            }
            style(DATE_NORMAL) {
                format 'dd/mm/yy'
                align center center
                font {
                    color dimGray
                }
            }
            style(STATUS) {
                align center center
                font {
                    size 12
                    color dimGray
                }
            }
            style(PROPERTY_VALUE) {
                align center center
                font {
                    color dimGray
                }
            }
            style(MODEL_CATALOGUE_ID) {
                align center center
                font {
                    size 12
                    color dimGray
                }
            }
            style(INNER_TABLE_HEADER) {
                font {
                    bold
                    size 12
                    color cornflowerBlue
                }
                align center center
            }
            style(NOTE) {
                font {
                    italic
                    color dimGray
                    align center center
                }
            }
            style(PROPERTY_TITLE) {
                font {
                    color dimGray
                    bold
                }
            }
            style(DIM_GRAY_FONT) {
                font {
                    color dimGray
                    bold
                }
            }
            style(DATA_ELEMENT) {
                foreground whiteSmoke
                font {
                    bold
                }
            }
            style(CENTER_CENTER) {
                align center center
            }
            style(DATA_ELEMENT_BOTTOM_RIGHT) {
                align bottom right
                foreground whiteSmoke
                font {
                    bold
                }
            }
            style(DATA_ELEMENT_CENTER_CENTER) {
                align center center
                foreground whiteSmoke
                font {
                    bold
                }
            }
            style(DATA_ELEMENT_TOP_RIGHT) {
                align top right
                foreground whiteSmoke
                font {
                    bold
                }
            }
            style(DATA_ELEMENT_DESCRIPTION_ROW) {
                wrap text
                font { size 10 }
                align top justify
            }
            style(METADATA_KEY) {
                font {
                    size 10
                    bold
                    indent 4
                }
            }

            style (METADATA_VALUE) {
                font {
                    size 10
                }
            }
            style (LINK) {
                font {
                    underline
                }
            }
            style (THIN_DARK_GREY_BORDER) {
                border {
                    style thin
                    color darkGray
                }
            }
        }
    }
}
