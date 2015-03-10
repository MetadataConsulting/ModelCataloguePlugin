package org.modelcatalogue.core.actions

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Extendible
import org.modelcatalogue.core.Extension
import org.springframework.validation.ObjectError

/**
 * Action runner to update existing catalogue element.
 */
class UpdateCatalogueElement extends AbstractActionRunner {

    static String description = """
        Updates the catalogue element of type specified in 'type' parameter fetched by the id specified in 'id' parameter.
        It updates all the bindable values from the parameters map (except 'type' and 'id').

        The validity of 'type' and 'id' parameters are validated immediately. Entity constraints are validated while
        performing the action.

        Parameters:
            id: the id of the element to be updated
            type: the catalogue element class name

            any other bindable parameter

            to update metadata, prefix the parameter name with 'ext:' (do not put any space after the colon)
    """

    String getMessage() {
        CatalogueElement fetched = queryForCatalogueElement()
        normalizeDescription """
            Update the ${GrailsNameUtils.getNaturalName(type.simpleName)} '$fetched.name' with following parameters:



            ${parameters.findAll { key, value -> key != 'type' && key != 'id' }.collect { key, value -> "${GrailsNameUtils.getNaturalName(key)}: $value"}.join('\n\n')}
        """
    }

    @Override
    Map<String, String> validate(Map<String, String> params) {
        Map<String, String> ret = [:]

        if (!params.id) {
            ret.id = 'Missing ID'
        }

        if (!(params.id ==~ /\d+/)) {
            ret.id = 'ID must contain numbers only'
        }

        if (!params.type) {
            ret.type = 'Missing type'
        } else {
            try {
                def type = Class.forName(params.type)
                if (!CatalogueElement.isAssignableFrom(type)) {
                    ret.type = "Type $params.type does not belong to any catalogue element"
                } else if (!ret.id && !type.exists(params.id as Long)) {
                    ret.id = "There is no $params.type with id $params.id"
                }
            } catch(ClassNotFoundException ignored) {
                ret.type = "Type $params.type not found"
            }
        }

        if (params.size() == 2 && params.id && params.type) {
            ret.properties = "Specify at least one property to update"
        }

        ret.putAll super.validate(params)

        ret
    }

    @Override void run() {
        Map<String, String> properties = new LinkedHashMap<String, String>(parameters)
        properties.remove('type')
        properties.remove('id')

        CatalogueElement element = queryForCatalogueElement()

        element.properties = properties
        if (element.save()) {
            if (element instanceof Extendible) {
                properties.findAll {key, value -> key.startsWith('ext:')}.each { key, value ->
                    if (value) {
                        element.addExtension key.substring(4), value
                    } else {
                        Extension ext = element.listExtensions().find { "ext:$it.name" == key }
                        if (ext) {
                            element.removeExtension(ext)
                        }
                    }
                }
            }
            out << "<a href='#/catalogue/${GrailsNameUtils.getPropertyName(type)}/${element.id}'>${GrailsNameUtils.getNaturalName(type.simpleName)} '$element.name'</a> updated"
            result = encodeEntity element
        } else {
            fail("Unable to update ${GrailsNameUtils.getNaturalName(type.simpleName)}:${id} using parameters ${parameters}")
            for (ObjectError error in element.errors.allErrors) {
                out << "$error\n"
            }
        }
    }

    /**
     * Returns the type parameter converted to class.
     * @return the type parameter converted to class
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
     * Returns the ID of the element being updated.
     * @return the ID of the element being updated
     */
    Long getId() {
        parameters.id as Long
    }

    /**
     * Returns the catalogue element fetched by given type and ID.
     * @return the catalogue element fetched by given type and ID
     */
    CatalogueElement queryForCatalogueElement() {
        if (!type) throw new IllegalStateException("Type is not set")
        if (!id) throw new IllegalStateException("ID is not set")
        type.get(id)
    }


    @Override
    List<String> getRequiredParameters() {
        ['id', 'type']
    }

}
