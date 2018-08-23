package org.modelcatalogue.core.export.inventory

import org.modelcatalogue.spreadsheet.api.Color
import org.modelcatalogue.spreadsheet.api.HTMLColorProvider
import org.modelcatalogue.spreadsheet.builder.api.CanDefineStyle
import org.modelcatalogue.spreadsheet.builder.api.Stylesheet

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
    public static final String CENTER_RIGHT = 'center-right'
    public static final String CENTER_LEFT = 'center-left'
    public static final String BOTTOM_RIGHT = 'bottom-right'
    public static final String DATA_ELEMENT_TOP_RIGHT = 'data-element-top-right'
    public static final String DATA_ELEMENT_DESCRIPTION_ROW = 'data-element-description-row'
    public static final String METADATA_KEY = 'metadata-key'
    public static final String METADATA_VALUE = 'metadata-value'
    public static final String LINK = 'link'
    public static final String THIN_DARK_GREY_BORDER = 'thin-dark-grey-border'
    public static final String DIM_GRAY_FONT = 'dim-gray-font'
    public static final String CHANGE_NEW = 'change-new'
    public static final String CHANGE_UPDATE = 'change-update'
    public static final String CHANGE_REMOVAL = 'change-removal'
    public static final Color CHANGE_NEW_COLOR = new Color('#E2EFDB')
    public static final Color CHANGE_UPDATE_COLOR = new Color('#DDEBF6')
    public static final Color CHANGE_REMOVAL_COLOR = HTMLColorProvider.mistyRose


    @Override
    void declareStyles(CanDefineStyle stylable) {
        stylable.with {
            style(H1) {
                align center, center
                font {
                    make bold
                    size 22
                    color cornflowerBlue
                }
            }
            style(H2) {
                align center, center
                font {
                    make bold
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
                align center, center
                font {
                    size 12
                    color dimGray
                }
            }
            style(DATE_NORMAL) {
                format 'dd/mm/yy'
                align center, center
                font {
                    color dimGray
                }
            }
            style(STATUS) {
                align center, center
                font {
                    size 12
                    color dimGray
                }
            }
            style(PROPERTY_VALUE) {
                align center, center
                font {
                    color dimGray
                }
            }
            style(MODEL_CATALOGUE_ID) {
                align center, center
                font {
                    size 12
                    color dimGray
                }
            }
            style(INNER_TABLE_HEADER) {
                font {
                    make bold
                    size 12
                    color cornflowerBlue
                }
                align center, center
            }
            style(NOTE) {
                font {
                    make italic
                    color dimGray
                    align center, center
                }
            }
            style(PROPERTY_TITLE) {
                font {
                    color dimGray
                    make bold
                }
            }
            style(DIM_GRAY_FONT) {
                font {
                    color dimGray
                    make bold
                }
            }
            style(DATA_ELEMENT) {
                foreground whiteSmoke
                font {
                    make bold
                }
                indent 1
            }
            style(CENTER_CENTER) {
                align center, center
            }
            style(BOTTOM_RIGHT) {
                align bottom right
            }
            style(CENTER_RIGHT) {
                align center right
            }
            style(CENTER_LEFT) {
                align center left
            }
            style(DATA_ELEMENT_BOTTOM_RIGHT) {
                align bottom right
                foreground whiteSmoke
                font {
                    make bold
                }
            }
            style(DATA_ELEMENT_CENTER_CENTER) {
                align center, center
                foreground whiteSmoke
                font {
                    make bold
                }
            }
            style(DATA_ELEMENT_TOP_RIGHT) {
                align top right
                foreground whiteSmoke
                font {
                    make bold
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
                    make bold
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
                    make underline
                }
            }
            style (THIN_DARK_GREY_BORDER) {
                border {
                    style thin
                    color darkGray
                }
            }
            style (CHANGE_NEW) {
                foreground CHANGE_NEW_COLOR
                font {
                  color darkGreen
                }
            }
            style (CHANGE_UPDATE) {
                foreground CHANGE_UPDATE_COLOR
                font {
                    color darkBlue

                }
            }
            style (CHANGE_REMOVAL) {
                foreground CHANGE_REMOVAL_COLOR
                font {
                    make strikeout
                    color '#D2787B'
                }

            }
        }
    }
}
