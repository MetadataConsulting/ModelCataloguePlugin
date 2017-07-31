package org.modelcatalogue.core.actions

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.MessageSource
import org.springframework.validation.ObjectError

/**
 * Base class for ActionRunner interface.
 *
 * The description is taken from static description field of the implementation class.
 * Parameters are stored in parameters field after initialization.
 * Natural name is extracted from the name of the class, ignoring any
 *
 * To signalize failure, call fail method with optional string message which will be printed into out print writer.
 *
 */
abstract class AbstractActionRunner implements ActionRunner {

    @Autowired AutowireCapableBeanFactory autowireCapableBeanFactory
    @Autowired MessageSource messageSource

    PrintWriter out
    protected Map<String,String> parameters = [:]

    private boolean failed = false

    String result


    @Override
    void initWith(Map<String, String> params) {
        parameters = params
    }

    @Override
    Map<String, String> validate(Map<String, String> params) {
        def ret = [:]
        for(name in requiredParameters) {
            if(!params.containsKey(name)) {
                ret[name] = "Missing ${GrailsNameUtils.getNaturalName(name)}"
            }
        }
        ret
    }

    @Override
    boolean isFailed() {
        return failed
    }

    /**
     * Signalized failure to the runner executor. The optional message will be written to the out print writer.
     * @param message optional message to be written to the out print writer
     */
    protected void fail(String message = null) {
        if (message && out) {
            out << message
            out << '\n\n'
        }
        failed = true
    }

    @Override
    String getNaturalName() {
        return GrailsNameUtils.getNaturalName(getClass().simpleName - 'Action' - 'Runner')
    }

    String getDescription() {
        normalizeDescription(GrailsClassUtils.getStaticFieldValue(getClass(), 'description'))
    }

    @Override
    List<String> getRequiredParameters() {
        []
    }

    @Override
    String getMessage() {
        naturalName
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


    static String encodeEntity(entity) {
        if (!entity.hasProperty('id') || !entity) {
            return null
        }
        return "gorm://${entity.class.name}:${entity.id}"
    }

    Object decodeEntity(String encoded) {
        if (!encoded) {
            return null
        }
        def match = encoded =~ /gorm:\/\/(.*):(\d+)/
        if (match) {
            String className = match[0][1] as String
            Long id = match[0][2] as Long
            try {
                def entity = Class.forName(className).get(id)
                if (entity) {
                    autowireCapableBeanFactory.autowireBean(entity)
                }
                return entity
            } catch (ClassNotFoundException ignored) {
                return null
            }
        }
        return null
    }

    protected printErrors(List<ObjectError> errors) {
        for (ObjectError error in errors) {
            out << messageSource.getMessage(error, Locale.default)
        }
    }
}
