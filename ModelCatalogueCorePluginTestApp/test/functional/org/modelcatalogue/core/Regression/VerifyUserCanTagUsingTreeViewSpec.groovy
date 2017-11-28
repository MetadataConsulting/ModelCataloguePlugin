package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class VerifyUserCanTagUsingTreeViewSpec extends AbstractModelCatalogueGebSpec{

    private static final String table='tr.inf-table-item-row>td:nth-child(2)'
    private static final String menuButton ='a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'
    private static final String  modelCatalogue='span.mc-name'
    private static final String  dataElement='td.col-md-4>span>span>a'
    private static final String  dataElementTable='td.col-md-4'
    private static final String  addTags='span.fa-plus-circle'
    private static final String  search='input#element'
    private static final String  deleteButton='a#delete-menu-item-link>span:nth-child(3)'
    private static final String  archiveButton='a#archive-menu-item-link>span:nth-child(3)'
    private static final String   tag ='tr.warning>td:nth-child(1)'
    private static final String   removeButton ='#role_item_remove-relationshipBtn'
    private static final String   plusButton ='span.fa-plus-square-o'
    private static final String   deprecatedTable ='tr.inf-table-item-row>td:nth-child(2)'
    private static final String  createdTag='ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(2)>ul>li:nth-child(3)>div>span>span'
    private static final String  createdDataElement='tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a'
    public static final int TIME_TO_REFRESH_SEARCH_RESULTS = 2000

    def "login to model catalogue and select a draft data model"() {
        when:
        loginAdmin()
        select 'Test 3' select 'Tags'


        then:
        check rightSideTitle contains 'Active Tags'
        noExceptionThrown()

        when:
        click create
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        noExceptionThrown()
        check modalHeader is 'Create Tag'
    }

    def "create a tag "() {
        when:
        fill nameLabel with 'TESTING_TAG'
        fill modelCatalogueId with 'MET-002'
        fill description with 'this my testing data'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        click save

        then:
        check table contains 'TESTING_TAG'
    }

    def "create a data element"() {
        when:
        click modelCatalogue

        and:
        select 'Test 3' open 'Data Elements'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check rightSideTitle contains ' Active Data Elements'

        when:
        click create

        and:
        fill nameLabel with 'TESTING_ELEMENT'
        fill modelCatalogueId with 'MET-00234'
        fill description with 'this my testing data'

        and:
        click save
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check dataElement is 'TESTING_ELEMENT'

    }

    def "add the tag to the data element created"() {

        when:
        click dataElement

        and:
        click addTags
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        fill search with 'TESTING_TAG' and pick first item
        click modalPrimaryButton

        then:
        check tag contains 'TESTING_TAG'

    }

    def "check that tag is added to data element tag"() {

        when:
        click modelCatalogue

        and:
        select 'Test 3' open 'Data Elements' select 'No tags'

        then:
        check rightSideTitle contains ' Active Data Elements'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        check createdTag contains 'TESTING_TAG'
    }

    def "delete the created tag"() {

        when:
        click modelCatalogue

        and:
        select 'Test 3' open 'Data Elements' select 'TESTING_TAG'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check rightSideTitle contains 'TESTING_TAG'

        when:
        click plusButton

        and:
        click removeButton
        click modalPrimaryButton

        then:
        check table isGone()

        when:
        click modelCatalogue

        and:
        select 'Test 3' open 'Tags' select 'TESTING_TAG'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check rightSideTitle contains 'TESTING_TAG'


        when:
        click menuButton

        and:
        click archiveButton

        and:
        click modalPrimaryButton

        and:
        select 'Test 3' open 'Deprecated Items'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check rightSideTitle contains 'Deprecated Catalogue Elements'

        and:
        check deprecatedTable contains 'TESTING_TAG'

    }

    def "delete the created data model"() {

        when:
        click modelCatalogue

        and:
        select 'Test 3' select 'Data Elements'

        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click createdDataElement

        then:
        check rightSideTitle contains 'TESTING_ELEMENT'

        when:
        click menuButton

        and:
        click deleteButton

        and:
        click modalPrimaryButton

        then:

        check dataElementTable isGone()


    }
}
