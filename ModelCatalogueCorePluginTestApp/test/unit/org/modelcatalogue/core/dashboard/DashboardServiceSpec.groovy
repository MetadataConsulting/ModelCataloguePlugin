package org.modelcatalogue.core.dashboard

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(DashboardService)
class DashboardServiceSpec extends Specification {

    @Unroll
    def "given #searchCatalogueElementScopeList #description #propertyName"(String propertyName,
                                                                            List<SearchCatalogueElementScope> searchCatalogueElementScopeList,
                                                                            boolean expected,
                                                                            String description) {
        expect:
        service.shouldSearchProperty(propertyName,searchCatalogueElementScopeList) == expected

        where:
        propertyName        | searchCatalogueElementScopeList                | expected
        'name'              | [SearchCatalogueElementScope.ALL]              | true
        'name'              | [SearchCatalogueElementScope.NAME]             | true
        'name'              | [SearchCatalogueElementScope.DESCRIPTION]      | false
        'description'       | [SearchCatalogueElementScope.ALL]              | true
        'description'       | [SearchCatalogueElementScope.DESCRIPTION]      | true
        'description'       | [SearchCatalogueElementScope.NAME]             | false
        'modelCatalogueId'  | [SearchCatalogueElementScope.ALL]              | true
        'modelCatalogueId'  | [SearchCatalogueElementScope.MODELCATALOGUEID] | true
        'modelCatalogueId'  | [SearchCatalogueElementScope.NAME]             | false
        'extensions.name'     | [SearchCatalogueElementScope.ALL]              | true
        'extensions.name'     | [SearchCatalogueElementScope.NAME]             | false
        'extensions.name'     | [SearchCatalogueElementScope.EXTENSIONNAME]    | true
        'extensions.extensionValue'    | [SearchCatalogueElementScope.ALL]              | true
        'extensions.extensionValue'    | [SearchCatalogueElementScope.EXTENSIONVALUE]   | true
        'extensions.extensionValue'    | [SearchCatalogueElementScope.NAME]             | false
        'foo'               | [SearchCatalogueElementScope.ALL]              | false
        description = expected ? 'should search' : 'should not be able to search'
    }
}
