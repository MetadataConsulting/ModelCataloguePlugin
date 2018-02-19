package org.modelcatalogue.core.regression.tags.admin

import org.modelcatalogue.core.geb.Common
import spock.lang.Ignore

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
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteB')  })
@Stepwise
class MET1434_VerifyUserCanTagUsingTreeViewSpec extends AbstractModelCatalogueGebSpec{

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
        check Common.rightSideTitle contains 'Active Tags'
        noExceptionThrown()

        when:
        click Common.create
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        noExceptionThrown()
        check Common.modalHeader is 'Create Tag'
    }

    def "create a tag "() {
        when:
        fill Common.nameLabel with 'TESTING_TAG'
        fill Common.modelCatalogueId with 'MET-002'
        fill Common.description with 'this my testing data'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        click Common.save

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
        check Common.rightSideTitle contains ' Active Data Elements'

        when:
        click Common.create

        and:
        fill Common.nameLabel with 'TESTING_ELEMENT'
        fill Common.modelCatalogueId with 'MET-00234'
        fill Common.description with 'this my testing data'

        and:
        click Common.save
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
        fill search with 'TESTING_TAG' and Common.pick first Common.item
        click Common.modalPrimaryButton

        then:
        check tag contains 'TESTING_TAG'

    }

    @Ignore
    def "check that tag is added to data element tag"() {

        when:
        click modelCatalogue

        and:
        select 'Test 3' open 'Data Elements' select 'No tags'

        then:
        check Common.rightSideTitle contains ' Active Data Elements'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        check createdTag contains 'TESTING_TAG' // TODO Fails here
    }

    @Ignore
    def "delete the created tag"() {

        when:
        click modelCatalogue

        and:
        select 'Test 3' open 'Data Elements' select 'TESTING_TAG'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check Common.rightSideTitle contains 'TESTING_TAG'

        when:
        click plusButton

        and:
        click removeButton
        click Common.modalPrimaryButton

        then:
        check table isGone()

        when:
        click modelCatalogue

        and:
        select 'Test 3' open 'Tags' select 'TESTING_TAG'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check Common.rightSideTitle contains 'TESTING_TAG'


        when:
        click menuButton

        and:
        click archiveButton

        and:
        click Common.modalPrimaryButton

        and:
        select 'Test 3' open 'Deprecated Items'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check Common.rightSideTitle contains 'Deprecated Catalogue Elements'

        and:
        check deprecatedTable contains 'TESTING_TAG'
    }

    @Ignore
    def "delete the created data model"() {

        when:
        click modelCatalogue

        and:
        select 'Test 3' select 'Data Elements'

        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click createdDataElement

        then:
        check Common.rightSideTitle contains 'TESTING_ELEMENT'

        when:
        click menuButton

        and:
        click deleteButton

        and:
        click Common.modalPrimaryButton

        then:

        check dataElementTable isGone()


    }
}
