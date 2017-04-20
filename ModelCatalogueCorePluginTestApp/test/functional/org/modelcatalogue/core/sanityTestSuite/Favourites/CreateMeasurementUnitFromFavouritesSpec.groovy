package org.modelcatalogue.core.sanityTestSuite.Favourites

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.ScrollDirection
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.*

@Stepwise
class CreateMeasurementUnitFromFavouritesSpec extends AbstractModelCatalogueGebSpec {

    private static final String favourite = "a#role_item-detail_favorite-elementBtn"
    private static final String user = "a#role_navigation-right_user-menu-menu-item-link>span:nth-child(1)"
    private static final String favourites_menu = "a#user-favorites-menu-item-link>span:nth-child(3)"
    private static final String Model_Catalogue_ID = "tr.inf-table-header-row>th:nth-child(1)>span"
    private static final String plus_button = "span.fa-plus-square-o"
    private static final String data_model = "button#role_item_catalogue-elementBtn"
    public static final String measurement_button = "a#catalogue-element-create-measurementUnitBtn>span:nth-child(2)"
    private static final String name = "input#name"
    private static final String symbol = "input#symbol"
    private static final String icon="span.input-group-addon"
    public static final String  search ="input#value"
    private static final String  model_link="tr.inf-table-item-row>td:nth-child(2)>a"



    def " Login to model catalogue and select a data model"() {
        when:
        loginAdmin()
        select 'Test 6'
        click favourite

        then:
        //check(favourite)displayed
        noExceptionThrown()
    }

    def "navigate to favourite tag ,click on plus button and click  on data model"() {

        when:
        remove messages
        click user
        Thread.sleep(2000l)
        click favourites_menu

        then:
        check Model_Catalogue_ID displayed


        when: 'click on plus button to expand model'
        click plus_button
        then:
        check data_model displayed

        and: 'click on data model and select measurement'
        click data_model
        scroll(ScrollDirection.DOWN)
        click measurement_button


        then:
        check modalHeader contains('Create Measurement Unit')
    }

    def" create Measurement unit"(){
        when:
        click icon
        Thread.sleep(3000L)
        fill search with("Test 6")
        Thread.sleep(1000L)
         selectInSearch(2)
        Thread.sleep(2000L)
        fill name with("MEASUREMENT FROM FAVOURITE ${System.currentTimeMillis()}")
        fill symbol with("kilogram")
        fill modelCatalogueId with("METT-${System.currentTimeSeconds()}")
        fill description with(" this is my measurement ${System.currentTimeSeconds()}")
        click save
        Thread.sleep(1000L)
        click model_link
        Thread.sleep(1000L)
        selectTreeView 'Measurement Units '

        then:
         check rightSideTitle  is 'Active Measurement Units'

 }

}
