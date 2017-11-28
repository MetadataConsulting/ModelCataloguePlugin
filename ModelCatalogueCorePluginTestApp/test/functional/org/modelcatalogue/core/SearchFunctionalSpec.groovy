package org.modelcatalogue.core

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class SearchFunctionalSpec extends AbstractModelCatalogueGebSpec {


    private static final CatalogueAction search = CatalogueAction.runFirst('navigation-right', 'search-menu')
    private static final String searchInput = '.modal-body .input-group-lg input'
    private static final String firstItemFound = '.list-group-item.item-found:first-of-type'
    private static final String firstItemFoundId = '.list-group-item.item-found:first-of-type .search-model-catalogue-id'
    private static final String DATA_ELEMENT_NAME_WITH_DATA_MODEL = 'TESTCER SYMPTOMS FIRST NOTED DATE (NHIC 0.0.1)'
    private static final String NAME_LOWER_CASE = 'testcer symptoms first note date'
    private static final String PART_OF_DESCRIPTION = 'the time when the symptoms were first noted'
    private static final String UNIQUE_EXTENSION_VALUE = 'TEST_27'
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 1000


    def "search for patient identity details"() {
        login admin

        go "#/dataModels"


        when:
        click search

        then:
        check modalDialog displayed

        when:
        fill searchInput with NAME_LOWER_CASE
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check firstItemFound contains DATA_ELEMENT_NAME_WITH_DATA_MODEL
        check firstItemFoundId displayed


        when:
        String id = $(firstItemFoundId).first().text() // i.e. http://localhost:8080/catalogue/dataClass/78@0.0.1
        fill searchInput with id
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check firstItemFound contains DATA_ELEMENT_NAME_WITH_DATA_MODEL

        when:
        String combinedVersion = id[(id.lastIndexOf('/') + 1)..-1]
        fill searchInput with combinedVersion
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check firstItemFound contains DATA_ELEMENT_NAME_WITH_DATA_MODEL

        when:
        String latestVersion = combinedVersion[0..(combinedVersion.lastIndexOf('@'))]
        fill searchInput with latestVersion
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check firstItemFound contains DATA_ELEMENT_NAME_WITH_DATA_MODEL

        when:
        fill searchInput with PART_OF_DESCRIPTION
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check firstItemFound contains DATA_ELEMENT_NAME_WITH_DATA_MODEL

        when:
        fill searchInput with UNIQUE_EXTENSION_VALUE
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check firstItemFound contains DATA_ELEMENT_NAME_WITH_DATA_MODEL


    }

}
