package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.getRightSideTitle

@Stepwise
class RelationshipIsImportedBy extends AbstractModelCatalogueGebSpec{

    private static final String dataModel ="a#role_item_catalogue-element-menu-item-link"
    private static final String createRelationship ="a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String  destination ="h3.panel-title"
    private static final String  destinationIcon="span.input-group-addon"
    private static final String   isImportedBy ="#type > option:nth-child(8)"
    private static final String  search ="input#value"
    private static final String  imported_by="ul.nav-tabs>li:nth-child(2)>a"
    private static final String  table ="#importedBy-changes > div.inf-table-body > table > tbody > tr > td:nth-child(1) > span > span"



    def "login to model catalogue and select a data model"(){

        when:
        loginAdmin()
        select'TEST 7'

        then:'verify title of the page '
        check rightSideTitle contains 'TEST 7'
    }
    def"navigate to the top menu and select create relationship "(){

        when:'navigate to createRelationship page'
        click dataModel
        click createRelationship

        then:'verify that the text Destination is displayed'
        check destination displayed

    }
    def"select is imported by,destination and create relationship"(){

        when: 'select relation'
        click  isImportedBy
        and: ' select destination'
        click destinationIcon
        fill search with "" and pick first item
        click ModalPrimaryButton
        and:'click on the imported tag'
        click imported_by


        then:'verify that is imported by  is displayed inside table'
        check table contains "is imported by"


    }


}
