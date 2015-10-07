package org.modelcatalogue.builder.xlsx;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface Cell {

    void value(Object value);
    void style(@DelegatesTo(CellStyle.class) Closure<Object> styleDefinition);
    void comment(String comment);
    void comment(@DelegatesTo(Comment.class) Closure<Object> commentDefinition);

}
