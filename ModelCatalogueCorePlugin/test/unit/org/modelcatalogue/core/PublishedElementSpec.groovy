package org.modelcatalogue.core

import spock.lang.Specification

class PublishedElementSpec extends Specification {


    def "get bare model catalogue id"() {

		when:
        PublishedElement el = new Model()
        el.id = 15

		then:
		el.modelCatalogueId.endsWith("_1")
		el.bareModelCatalogueId == el.modelCatalogueId.replaceAll('_1$', '')

		when:
		el.updateModelCatalogueId()

        then:
        el.modelCatalogueId.endsWith("_2")
        el.bareModelCatalogueId == el.modelCatalogueId.replaceAll('_2$', '')

    }


}
