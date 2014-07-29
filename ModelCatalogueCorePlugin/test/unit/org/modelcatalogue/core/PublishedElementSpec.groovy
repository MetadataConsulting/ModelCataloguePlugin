package org.modelcatalogue.core

import spock.lang.Specification

class PublishedElementSpec extends Specification {


    def "get bare model catalogue id"() {
        PublishedElement el = new Model()
        el.id = 15
        el.updateModelCatalogueId()


        expect:
        el.modelCatalogueId.endsWith("_1")
        el.bareModelCatalogueId == el.modelCatalogueId.replaceAll('_1$', '')

    }


}
