package org.modelcatalogue.builder.xlsx;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface Workbook {

    /**
     * Declare a named style.
     * @param name name of the style
     * @param styleDefinition definition of the style
     */
    void style(String name, @DelegatesTo(CellStyle.class) Closure<Object> styleDefinition);
    void sheet(String name, @DelegatesTo(Sheet.class) Closure<Object> sheetDefinition);
    

}
