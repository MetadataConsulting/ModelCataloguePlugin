package org.modelcatalogue.core.actions

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Extendible
import org.springframework.validation.ObjectError

/**
 * Action Runner to create new catalogue elements.
 */
class CreateCatalogueElement extends AbstractActionRunner {

    static String description = """
        Creates new catalogue element of type specified in 'type' parameter.
        It updates all the bindable values from the parameters map (except 'type').

        The validity of 'type' and 'name' parameters are validated immediately. Entity constraints are validated while
        performing the action.

        Parameters:
            name: the name of the newly created element
            type: the catalogue element class name

            any other bindable parameter

            to assign metadata, prefix the parameter name with 'ext:' (do not put any space after the colon)
    """

    String getMessage() {
        normalizeDescription """
            Create new ${GrailsNameUtils.getNaturalName(type.simpleName)} '$name' with following parameters:



            ${parameters.findAll { key, value -> key != 'type' }.collect { key, value -> "${GrailsNameUtils.getNaturalName(key)}: $value"}.join('\n\n')}
        """
    }

    @Override
    Map<String, String> validate(Map<String, String> params) {
        Map<String, String> ret = [:]

        if (!params.name) {
            ret.name = 'Missing name'
        }

        if (!params.type) {
            ret.type = 'Missing type'
        } else {
            try {
                def type = Class.forName(params.type)
                if (!CatalogueElement.isAssignableFrom(type)) {
                    ret.type = "Type $params.type does not belong to any catalogue element"
                }
            } catch(ClassNotFoundException ignored) {
                ret.type = "Type $params.type not found"
            }
        }

        ret.putAll super.validate(params)

        ret
    }

    @Override void run() {
        Map<String, String> properties = new LinkedHashMap<String, String>(parameters)
        properties.remove('type')
        CatalogueElement element = createCatalogueElement()
        element.properties = properties
        if (element.save(flush: true)) {
            if (element instanceof Extendible) {
                properties.findAll {key, value -> key.startsWith('ext:')}.each { key, value ->
                    element.addExtension key.substring(4), value
                }
            }
            out << "New <a href='#/catalogue/${GrailsNameUtils.getPropertyName(type)}/${element.id}'>${GrailsNameUtils.getNaturalName(type.simpleName)} '$name'</a> created"
            result = encodeEntity element
        } else {
            fail("Unable to create new ${GrailsNameUtils.getNaturalName(type.simpleName)} using parameters ${parameters}")
            for (ObjectError error in element.errors.allErrors) {
                out << "$error\n"
            }
        }
    }

    /**
     * Returns the type parameter as class.
     * @return the type parameter as class
     */
    Class getType() {
        if (!parameters.type) return null
        try {
            return Class.forName(parameters.type)
        } catch(ClassNotFoundException ignored) {
            return null
        }
    }

    /**
     * Returns the name of the created element.
     * @return the name of the created element
     */
    String getName() {
        parameters.name
    }

    /**
     * Creates new catalogue element instance.
     * @return new catalogue element instance
     */
    CatalogueElement createCatalogueElement() {
        if (!type) throw new IllegalStateException("Type is not set")
        type.newInstance()
    }

    @Override
    List<String> getRequiredParameters() {
        ['name', 'type']
    }
}
