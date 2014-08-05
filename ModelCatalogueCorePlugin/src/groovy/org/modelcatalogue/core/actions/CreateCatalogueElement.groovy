package org.modelcatalogue.core.actions

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError

class CreateCatalogueElement extends AbstractActionRunner {

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
        if (element.save()) {
            out << "New ${GrailsNameUtils.getNaturalName(type.simpleName)} '$name' created"
        } else {
            fail("Unable to create new ${GrailsNameUtils.getNaturalName(type.simpleName)} using parameters ${parameters}")
            for (ObjectError error in element.errors.allErrors) {
                out << "$error\n"
            }
        }
    }

    Class getType() {
        if (!parameters.type) return null
        try {
            return Class.forName(parameters.type)
        } catch(ClassNotFoundException ignored) {
            return null
        }
    }

    String getName() {
        parameters.name
    }

    CatalogueElement createCatalogueElement() {
        if (!type) throw new IllegalStateException("Type is not set")
        type.newInstance()
    }

}
