package org.modelcatalogue.core.util

import spock.lang.Specification
import spock.lang.Unroll

class LegacySpec extends Specification {

    @Unroll
    def "fixes model catalogue id #original to #fixed"() {
        expect:
        Legacy.fixModelCatalogueId(original) == fixed

        where:
        original                                                                                | fixed
        null                                                                                    | null
        'http://localhost:8080/ModelCatalogueCorePluginTestApp/catalogue/classification/24.1'   | 'http://localhost:8080/ModelCatalogueCorePluginTestApp/catalogue/dataModel/24.1'
        'http://localhost:8080/ModelCatalogueCorePluginTestApp/catalogue/model/22.1'            | 'http://localhost:8080/ModelCatalogueCorePluginTestApp/catalogue/dataClass/22.1'
        'http://localhost:8080/ModelCatalogueCorePluginTestApp/catalogue/dataElement/22.1'      | 'http://localhost:8080/ModelCatalogueCorePluginTestApp/catalogue/dataElement/22.1'


    }

}
