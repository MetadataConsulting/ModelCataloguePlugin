package org.modelcatalogue.builder.xlsx.api;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.io.OutputStream;

public interface ExcelBuilder {

    void build(OutputStream outputStream, @DelegatesTo(Workbook.class) Closure workbookDefinition);


}
