package org.modelcatalogue.builder.xlsx;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface Cell extends HasStyle {

    void value(Object value);
    void comment(String comment);
    void comment(@DelegatesTo(Comment.class) Closure<Object> commentDefinition);

    void colspan(int span);
    void rowspan(int span);

}
