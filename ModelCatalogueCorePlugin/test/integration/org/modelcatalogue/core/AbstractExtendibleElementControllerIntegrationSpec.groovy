package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.Elements
import spock.lang.Unroll

/**
 * Created by ladin on 28.04.14.
 */
abstract class AbstractExtendibleElementControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {


    def "update and set metadata"() {
        if (controller.readOnly) return
        ExtendibleElement another        = ExtendibleElement.get(anotherLoadItem.id)
        String newName                  = "UPDATED NAME"
        String currentName              = another.name
        String currentModelCatalogueId  = another.modelCatalogueId
        Map keyValue = new HashMap()
        keyValue.put('testKey', 'testValue')

        when:
        controller.request.method       = 'PUT'
        controller.params.id            = another.id
        controller.params.newVersion    = true
        controller.request.json         = [name: newName, ext: keyValue]
        controller.response.format      = "json"

        controller.update()
        def json = controller.response.json

        then:
        json.name                   == newName
        json.ext == keyValue

    }

}
