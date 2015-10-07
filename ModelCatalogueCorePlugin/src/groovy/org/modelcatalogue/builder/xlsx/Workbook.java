package org.modelcatalogue.builder.xlsx;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface Workbook {

    void sheet(String name, @DelegatesTo(Sheet.class) Closure<Object> sheetDefinition);
    

}
