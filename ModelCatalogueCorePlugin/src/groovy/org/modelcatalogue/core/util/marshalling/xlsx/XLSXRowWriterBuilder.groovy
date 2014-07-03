package org.modelcatalogue.core.util.marshalling.xlsx

import grails.rest.render.RenderContext
import groovy.util.logging.Log4j
import org.modelcatalogue.core.util.ListWrapper

/**
 * Created by ladin on 09.04.14.
 */
@Log4j
class XLSXRowWriterBuilder {

    private String name
    private String title
    private List<String> headers
    private Closure condition   = {container, condition -> true}
    private Closure writer      = {[]}
    private Closure fileName    = { null }

    /**
     * Creates new builder for writer of given name.
     * @param name optional identifier of the writer
     * @return the builder for given name
     */
    static XLSXRowWriterBuilder writer(String name = null) {
        new XLSXRowWriterBuilder(name: name)
    }

    /**
     * Allows to create condition for the writer. If not specified
     * the resulted writer can be applied on any container and context
     * @param condition the closure representing the condition, container and context are passed as argumnents
     * @return the builder with given condition
     */
    XLSXRowWriterBuilder when(Closure condition) {
        this.condition = condition
        this
    }

    /**
     * Allows to specify columns headers.
     * @param headers the columns headers
     * @return the builder with given headers
     */
    XLSXRowWriterBuilder headers(String... headers) {
        this.headers = headers
        this
    }


    /**
     * Specifies the name of the exported file omitting the extension.
     * @param fileName the name of the exported file omitting the extension
     * @return the builder with given file name
     */
    XLSXRowWriterBuilder file(String fileName) {
        this.fileName = { fileName }
        this
    }

    /**
     * Specifies the name of the exported file omitting the extension in a closure.
     * @param fileName
     *                  the clousujre returning the name of the exported file omitting the extension,
     *                  render context is passed as single argument
     * @return the builder with given file name
     */
    XLSXRowWriterBuilder file(Closure fileName) {
        this.fileName = fileName
        this
    }

    /**
     * Defines how the items are converted to the rows.
     *
     * Single item is passed to the closure as argument.
     *
     * @param writer    closure accepting the single item to be written and returning the list of lists of strings
     *                  representing the rows to be written
     * @return the builder with given headers
     */
    XLSXRowWriterBuilder write(Closure writer) {
        this.writer = writer
        this

    }


    /**
     * Defines human readable title.
     *
     * @param title human readable title
     * @return the builder with given title
     */
    XLSXRowWriterBuilder title(String title) {
        this.title = title
        this

    }

    /**
     * Synonym for {@code write} method.
     * @see #write(groovy.lang.Closure)
     */
    XLSXRowWriterBuilder then(Closure writer) {
        this.writer = writer
        this

    }


    XLSXRowWriter build() {
        XLSXRowWriterBuilder self = this
        new XLSXRowWriter() {
            @Override
            boolean isApplicableOn(ListWrapper container, RenderContext context) {
                try {
                    return self.condition(container, context)
                } catch (Exception e) {
                    log.error("Exception testing row writer $name", e)
                    return false
                }
            }

            @Override
            String getName() {
                return self.name
            }

            @Override
            List<List<String>> getRows(Object item) {
                return self.writer(item)
            }

            @Override
            String getTitle() {
                return self.title ?: self.name ?: 'Excel'
            }

            @Override
            List<String> getHeaders() {
                return self.headers
            }

            @Override
            String toString() {
                "Writer '${name ?: 'default' }' with headers $headers"
            }

            @Override
            String getFileName(RenderContext context) {
                return self.fileName(context)
            }
        }
    }




}
