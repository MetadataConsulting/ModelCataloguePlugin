package org.modelcatalogue.builder.xlsx;


import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface Sheet {

    /**
     * Crates new empty row.
     */
    void row ();

    /**
     * Creates new row in the spreadsheet.
     * @param rowDefinition closure defining the content of the row
     */
    void row (@DelegatesTo(Row.class) Closure<Object> rowDefinition);

    /**
     * Creates new row in the spreadsheet.
     * @param row row number
     * @param rowDefinition closure defining the content of the row
     */
    void row (int row, @DelegatesTo(Row.class) Closure<Object> rowDefinition);

    /**
     * Freeze some column or row or both.
     * @param column last freeze column
     * @param row last freeze row
     */
    void freeze(int column, int row);

    void group(@DelegatesTo(Sheet.class) Closure<Object> insideGroupDefinition);
    void collapse(@DelegatesTo(Sheet.class) Closure<Object> insideGroupDefinition);

}
