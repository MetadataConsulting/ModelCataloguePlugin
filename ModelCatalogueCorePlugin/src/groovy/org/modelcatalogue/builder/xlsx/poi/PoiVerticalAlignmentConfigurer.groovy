package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.ss.usermodel.VerticalAlignment
import org.modelcatalogue.builder.xlsx.VerticalAlignmentConfigurer

class PoiVerticalAlignmentConfigurer implements VerticalAlignmentConfigurer {

    final PoiCellStyle style

    PoiVerticalAlignmentConfigurer(PoiCellStyle style) {
        this.style = style
    }

    @Override
    Object getTop() {
        style.setVerticalAlignment(VerticalAlignment.TOP)
    }

    @Override
    Object getCenter() {
        style.setVerticalAlignment(VerticalAlignment.CENTER)
    }

    @Override
    Object getBottom() {
        style.setVerticalAlignment(VerticalAlignment.BOTTOM)
    }

    @Override
    Object getJustify() {
        style.setVerticalAlignment(VerticalAlignment.JUSTIFY)
    }
}
