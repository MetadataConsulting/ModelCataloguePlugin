package org.modelcatalogue.core.actions

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils

/**
 * Created by ladin on 04.08.14.
 */
abstract class AbstractActionRunner implements ActionRunner {
    PrintWriter out
    protected Map<String,String> parameters = [:]

    private boolean failed = false


    @Override
    void initWith(Map<String, String> params) {
        parameters = params
    }

    @Override
    Map<String, String> validate(Map<String, String> params) { [:] }

    @Override
    boolean isFailed() {
        return failed
    }

    protected void fail(String message = null) {
        if (message && out) {
            out << message
            out << '\n\n'
        }
        failed = true
    }

    @Override
    String getNaturalName() {
        return GrailsNameUtils.getNaturalName(getClass().simpleName - 'Action')
    }

    String getDescription() {
        normalizeDescription GrailsClassUtils.getStaticFieldValue(getClass(), 'description')
    }

    /**
     * Simple normalization function which trims, strips the indent, collapses single new lines but keeps new lines
     * with whitespace after them.
     *
     * @param description the description to be normalized
     * @return
     */
    static String normalizeDescription(Object description) {
        if (!description) {
            return ""
        }
        description.toString().stripIndent().trim().replaceAll(/(\S)\n(?=\S)/, '$1 ').replaceAll(/\n\n/, '\n')
    }
}
