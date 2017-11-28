package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.curator
import static org.modelcatalogue.core.geb.Common.rightSideTitle

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class EditDataElementSpec extends AbstractModelCatalogueGebSpec {

    private static final String Element="td.col-md-4>span>span>a"
    private static final String editButton="a#role_item-detail_inline-editBtn"
    private static final String  description="#metadataCurator > div.container-fluid.container-main > div > div > div.ng-scope > div > div.split-view-right.data-model-detail-pane > ui-view > ui-view > div > div > div > div > form > div:nth-child(4) > div > ng-include > div > div > span > div > textarea"
    private static final String dataType="input#dataType"
    private static final String submit="button#role_item-detail_inline-edit-submitBtn"
    private static final String change="#history-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-8 > span > span > code"

    def "login to model catalogue and select a draft model"() {

        login curator
        select 'Test 3' open 'Data Elements'

        expect:
        check rightSideTitle is 'Active Data Elements'
    }

    def "select a data element"() {

        when:'select an element'
        click Element
        then:
        check editButton displayed
    }

    def "edit description ,data type and save"() {

        when:
        click editButton
        Thread.sleep(3000)
        fill description with '.i am describe my action'
        fill dataType with "var 1234"


        and:'save '
        click submit
        Thread.sleep(3000)

        then:
        $('span.unit-name').text() == "var 1234 "


    }
}
