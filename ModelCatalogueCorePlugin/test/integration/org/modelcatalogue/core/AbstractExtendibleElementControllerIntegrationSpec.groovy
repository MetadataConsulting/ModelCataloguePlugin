package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import spock.lang.Unroll

/**
 * Created by ladin on 28.04.14.
 */
abstract class AbstractExtendibleElementControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {


    def "update and create new version"() {
        if (controller.readOnly) return

        String newName                  = "UPDATED NAME WITH NEW VERSION"
        PublishedElement another        = PublishedElement.get(anotherLoadItem.id)
        String currentName              = another.name
        String currentModelCatalogueId  = another.modelCatalogueId
        Integer currentVersionNumber    = another.versionNumber
        Integer numberOfCurrentVersions = another.countVersions()

        when:
        controller.params.id            = another.id
        controller.params.newVersion    = true
        controller.request.json         = [name: newName, ext: [['testkey', 'testvalue']]]
        controller.response.format      = "json"

        controller.update()

        PublishedElement oldVersion    = PublishedElement.findByModelCatalogueId(currentModelCatalogueId)

        def json = controller.response.json

        then:
        json.versionNumber          == currentVersionNumber + 1
        json.modelCatalogueId       != currentModelCatalogueId
        json.modelCatalogueId       == another.bareModelCatalogueId + '_' + (currentVersionNumber + 1)
        json.name                   == newName

        another.countVersions()     == numberOfCurrentVersions + 1

        oldVersion.versionNumber    == currentVersionNumber
        oldVersion.modelCatalogueId == currentModelCatalogueId
        oldVersion.name             == currentName
        oldVersion.name             != json.name
    }

}
