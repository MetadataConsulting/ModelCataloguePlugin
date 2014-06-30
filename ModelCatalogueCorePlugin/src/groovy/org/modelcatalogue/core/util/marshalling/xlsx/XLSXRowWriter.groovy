package org.modelcatalogue.core.util.marshalling.xlsx

import grails.rest.render.RenderContext
import org.modelcatalogue.core.util.ListWrapper

interface XLSXRowWriter<T> {

    /**
     * Returns <code>true</code> if this row writer can be applied on given list wrapper and render context.
     * @param container     the container which holds the items representing the rows
     * @param context       the current renderer context containing information such as controller or action name
     * @return <code>true</code> if this row writer can be applied on given list wrapper and render context
     */
    boolean isApplicableOn(ListWrapper<T> container, RenderContext context)

    /**
     * Identifier of this row writer so it can be easily distinguish by name.
     * Can be <code>null</code> for the default row writer.
     * @return the name of this writer
     */
    String getName()

    /**
     * Human readable title.
     * Can be <code>null</code>. The name or the word <code>Excel</code> is used instead if not available.
     * @return the title of this writer
     */
    String getTitle()

    /**
     * Returns the name of the excel file or {@code null} if default name should be used.
     * @param context the renderer context
     * @return the name of the excel file or {@code null} if default name should be used
     */
    String getFileName(RenderContext context)

    /**
     * Return list of list of strings which represents one or more row to be added to the spreadsheet.
     * @param item item to be converted to one or more rows
     * @return list of list of strings which represents one or more row to be added to the spreadsheet
     */
    List<List<Object>> getRows(T item)

    /**
     * Returns the columns headers as list of string.
     * @return the columns headers as list of string
     */
    List<Object> getHeaders()


	String getLayoutResourceName()

}
