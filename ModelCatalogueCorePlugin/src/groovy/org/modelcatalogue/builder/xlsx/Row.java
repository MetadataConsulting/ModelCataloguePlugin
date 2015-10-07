package org.modelcatalogue.builder.xlsx;


import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface Row {

    void cell();
    void cell(Object value);
    void cell(@DelegatesTo(Cell.class) Closure<Object> cellDefinition);
    void cell(int column, @DelegatesTo(Cell.class) Closure<Object> cellDefinition);

    void style(@DelegatesTo(CellStyle.class) Closure<Object> styleDefinition);

}
