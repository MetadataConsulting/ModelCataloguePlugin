package org.modelcatalogue.core.sanityTestSuite.CreateDataModels


import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec

import spock.lang.Stepwise
import spock.lang.Title
import spock.lang.Unroll

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save



@Stepwise
@Title("This test is to create a data type base on enumeration")
class CreateDataTypeAndSelectEnumeratedSpec extends AbstractModelCatalogueGebSpec{

    private static final String enumerated ="input#pickEnumeratedType"
    private static final String  paste="button.btn-default"
    private static final String  deleteButton="a#delete-menu-item-link>span:nth-child(3)"
    private static final String  enumeratedType="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String  dataType="tr.inf-table-item-row>td:nth-child(1)>span>span>a"
    private static final String   createdDataType="tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a"
    private static final String   first_row="tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)"


    def "login and navigate to the model"() {
        when:
        loginCurator()
        select 'Test 3'
        selectInTree 'Data Types'

        then:
        check rightSideTitle contains 'Active Data Types'
    }

    def "navigate to data type creation page "() {
        when:
        click create
        then:
        check modalHeader contains 'Create Data Type'
    }

    def " fill the create data type form"() {

        when:
        fill nameLabel with "TESTING_DATA_TYPE "

        fill modelCatalogueId with "MET-345"

        fill description with "my description of data type"

        and: 'click on enumerated type'
        click enumerated
        fillMetadata '01': 'one', '02': 'two', '03': 'three', '04': 'four', '05': 'five'
        and:
        click paste

        then:
        check "button.btn-primary" displayed

        when:
        click save

        then:
        check { infTableCell(1, 1) } contains 'TESTING_DATA_TYPE'
        and:
        check { infTableCell(1, 2) } contains '01: one'

        when:
        click createdDataType

        then:
        check rightSideTitle  contains 'TESTING_DATA_TYPE MET-345@0.0.1 '



    }
    @Unroll
    def"check that the enumerations have been saved properly"(  int row , String colunm){

        expect:
        $("div.split-view-right.data-model-detail-pane > ui-view > ui-view > div > div > div > div > form > div:nth-child(5) > div > ng-include > div > div:nth-child(2) > div > table > tbody > tr:nth-child($row) > th > small").text() == colunm

        Thread.sleep(3000l)



        where:
        row      || colunm
        1        || '01'
        2        || '02'
        3        || '03'
        4        || '04'


    }
    @Unroll
    def"check that enumeration are saved"(int row,String colunm){


        expect:

        $("div.split-view-right.data-model-detail-pane > ui-view > ui-view > div > div > div > div > form > div:nth-child(5) > div > ng-include > div > div:nth-child(2) > div > table > tbody > tr:nth-child($row) > td:nth-child(2) > small").text()==colunm
        Thread.sleep(3000L)

        where:
        row      || colunm
        1        || 'one'
        2        || 'two'
        3        || 'three'

    }

    def" remove the created data type "(){

        when:
        click enumeratedType

        and:
        click deleteButton

        then:
        check modalHeader is 'Do you really want to delete Enumerated Type TESTING_DATA_TYPE?'

        when:
        click modalPrimaryButton

        then:
        check first_row isGone()
    }
}
