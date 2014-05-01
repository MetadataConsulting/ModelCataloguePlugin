package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import spock.lang.Unroll

/**
 * Created by ladin on 28.04.14.
 */
abstract class AbstractPublishedElementControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    def publishedElementService

    @Unroll
    def "get json history: #no where max: #max offset: #offset\""() {
        CatalogueElement first = loadItem
        createArchiveVersions(first)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "json"
        controller.history(max)
        def json = controller.response.json

        recordResult "history$no", json


        then:
        checkJsonCorrectListValues(json, total, size, offset, max, next, previous)
        json.listType == Elements.name
        json.itemType == resource.name

        // TODO: add more verification

        where:
        [no, size, max, offset, total, next, previous] << getHistoryPaginationParameters("/${resourceName}/${loadItem.id}/history")
    }


    @Unroll
    def "get xml history: #no where max: #max offset: #offset"() {
        CatalogueElement first = loadItem
        createArchiveVersions(first)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "xml"
        controller.history(max)
        def xml = controller.response.xml

        recordResult "history$no", xml

        then:
        checkXmlCorrectListValues(xml, total, size, offset, max, next, previous)
        xml.element.size() == size


        // todo add more verification

        where:
        [no, size, max, offset, total, next, previous] << getHistoryPaginationParameters("/${resourceName}/${loadItem.id}/history")
    }

    def getHistoryPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 12, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, 12, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, 12, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 4, 4, 8, 12, "", "${baseLink}?max=4&offset=4"],
                [5, 2, 10, 10, 12, "", "${baseLink}?max=10&offset=0"],
                [6, 2, 2, 10, 12, "", "${baseLink}?max=2&offset=8"]
        ]
    }

    void createArchiveVersions(PublishedElement el) {
        while (!el.modelCatalogueId.endsWith('_12')) {
            publishedElementService.archiveAndIncreaseVersion(el)
        }
    }
}
