package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.delete
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save

@Stepwise
class RelationshipRelatedToSpec extends AbstractModelCatalogueGebSpec{

    private static final String dataModelMenuBar ="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String dataModel ="ul.catalogue-element-treeview-list-root>li>div>span>span"
    private static final String dataMenuBar ="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String createRelationship ="a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String  destination ="h3.panel-title"
    private static final String  destinationIcon="span.input-group-addon"
    private static final String   related="ul.nav-tabs>li:nth-child(3)>a>span:nth-child(1)"
    private static final String   relatedDataElement="ul.nav-tabs>li:nth-child(7)>a>span:nth-child(1)"
    private static final String   relatedTo ="#type > option:nth-child(5)"
    private static final String   saveAndCreateAnother ="a#role_modal_modal-save-and-add-anotherBtn"
    private static final String   deleteButton="a#delete-menu-item-link>span:nth-child(3)"
    private static final String  search ="input#value"
    private static final String  newDataType ="tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a"
    private static final String  newDataElement ="tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a"
    private static final String  elementButton ="a#role_list_create-catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String   removeRelationshipButton ="a#role_item_remove-relationshipBtn"
    private static final String  plusButton ="span.fa-plus-square-o"
    private static final String   editButton ="a#role_item-detail_edit-catalogue-elementBtn"
    private static final String  table2 ="tr.inf-table-item-row>td:nth-child(2)"
    private static final String  relatedToButton ="ul.nav-tabs>li:nth-child(3)>a"
    private static final String  dataTypesLink ="ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(3)>div>span>span"
    private static final String  table ="#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-7 > span > span > code"
    private static final int TIME_TO_REFRESH_SEARCH_RESULTS = 3000


    def "login to model catalogue and select a data model"(){

        when:
        loginAdmin()
        select'Test 3'

        then:'verify title of the page '
        check rightSideTitle contains 'Test 3'
    }
    def"navigate to the top menu and select create relationship "(){

        when:'navigate to createRelationship page'
        click dataModelMenuBar
        click createRelationship

        then:'verify that the text Destination is displayed'
        check destination displayed

    }
    def"select related to,destination and create relationship"(){

        when: 'select relation'
        click  relatedTo
        and: ' select destination'
        click destinationIcon
        fill search with '' and  pick first item
        click ModalPrimaryButton


        then:'verify that related to is displayed inside table'
        check table contains "related to"
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

    }

    def" remove the related to relationship that was created"(){

        when:'click on the related to tag'
        click relatedToButton

        and:'select the plus button'
        click plusButton

        and:
        click removeRelationshipButton

        and:'confirm you action'
        click modalPrimaryButton

        then:'Check that the first row is gone'
        check table2 isGone()
    }
    def"create a data type"(){

        when:
        click'span.mc-name'
        select 'Test 3' open'Data Types'

        then:
        check rightSideTitle is 'Active Data Types'

        when:
        click elementButton

        fill nameLabel with'Simple Type'
        fill modelCatalogueId with 'MET-23'
        fill description  with 'TESTING'
        click save

        then:
        check newDataType contains 'Simple Type'

    }
    def"create a data element"(){

        when:
        click'span.mc-name'
        select 'Test 3' open'Data Elements'

        then:
        check rightSideTitle  contains 'Active Data Elements'

        when:
        click elementButton

        fill nameLabel with'RelatedTo'
        fill modelCatalogueId with 'MET 45'
        fill description with 'TESTING DATA ELEMENT'
        click save

        then:
        check newDataElement contains 'RelatedTo'
    }

    def"created a relationship related to"(){

        when:
        driver.navigate().refresh()
        click dataModel

        then:
        check rightSideTitle contains 'Test 3 299@0.0.1'

        when:
        click dataModelMenuBar

        click createRelationship

        then:
        check 'h4.ng-binding' is'Test 3'

        when:
        fill 'select#type' with 'related to'
        fill 'input#element' with 'RelatedTo' and pick first item
        click modalPrimaryButton

        then:
        check rightSideTitle contains 'Test 3'

        when:
        click related

        then:
        check { infTableCell(1, 2) } is'RelatedTo (Test 3 0.0.1)'

    }

    def "edit data element "(){

        when:
        click plusButton
        click editButton

        then:
        check modalHeader is'Edit Data Element'

        when:
        fill 'input#dataType' with'Simple Type'and pick first item
        click save

        then:
        check { infTableCell(1, 3) } is 'Data Element'

    }

    def "check that the data type and related to are displayed"(){

        when:
        click'span.mc-name'
        select 'Test 3'
        selectInTree 'Data Elements'
        click newDataElement

        then:
        check 'span.unit-name' contains 'Simple Type'


        expect:
        click relatedDataElement
        check { infTableCell(1, 2) }is 'Test 3'
        check { infTableCell(1, 3) }is'Data Model'

    }
    def"delete the created data Element"(){

        when:
        click'span.mc-name'
        select 'Test 3'
        selectInTree 'Data Elements'
        click newDataElement

        then:
        check rightSideTitle contains 'RelatedTo MET 45@0.0.1'

        when:
        Thread.sleep(1000l)
        click dataMenuBar
        click deleteButton

        then:
        check modalHeader is 'Do you really want to delete Data Element RelatedTo?'

        when:
        click modalPrimaryButton

        then:
        check rightSideTitle contains 'Data Elements'

    }

    def"delete data types that was created"(){

        when:
        driver.navigate().refresh()
        click dataTypesLink

        then:
        check rightSideTitle is 'Active Data Types'

        when:
        click newDataType

        then:
        check rightSideTitle contains 'Simple Type MET-23@0.0.1'

        when:
        Thread.sleep(1000l)
        click dataMenuBar
        click deleteButton

        then:
        check modalHeader is 'Do you really want to delete Data Type Simple Type?'


        when:
        click modalPrimaryButton

        then:
        check rightSideTitle is 'Data Types'

    }

}
