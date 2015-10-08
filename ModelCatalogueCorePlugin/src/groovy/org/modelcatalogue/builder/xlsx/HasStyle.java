package org.modelcatalogue.builder.xlsx;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface HasStyle {
    void style(@DelegatesTo(CellStyle.class) Closure styleDefinition);
    void style(String name);
}
