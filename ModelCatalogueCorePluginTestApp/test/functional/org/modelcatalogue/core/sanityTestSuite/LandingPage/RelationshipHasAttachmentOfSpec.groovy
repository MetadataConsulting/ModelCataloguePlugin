package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AssetsPage
import org.modelcatalogue.core.geb.DataModelListPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage

import static org.modelcatalogue.core.geb.Common.getCreate
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalDialog
import static org.modelcatalogue.core.geb.Common.getModalDialog
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.getSave
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import org.modelcatalogue.core.AssetWizardSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

//@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class RelationshipHasAttachmentOfSpec extends AbstractModelCatalogueGebSpec{

    private static final String dataModel ="a#role_item_catalogue-element-menu-item-link"
    private static final String createRelationship ="a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String  destination ="h3.panel-title"
    private static final String  destinationIcon="span.input-group-addon"
    private static final String   hasAttachmentOf ="#type > option:nth-child(4)"
    private static final String  search ="input#value"
    private static final String  table ="#activity-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-7 > span > span > code"
    public static final String infiniteTableRow = '.inf-table tbody .inf-table-item-row'
    public static final String asset = 'asset'
    public static final String  modelCatalogue = 'span.mc-name'

    @Ignore
    def "login to model catalogue and select a data model"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DataModelListPage

        when:
        DataModelListPage dataModelListPage = browser.page DataModelListPage
        dataModelListPage.select("Test 3")

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Assets")

        then:
        at AssetsPage

        and:
        check rightSideTitle is 'Active Assets'
    }

    @Ignore
    def "upload new asset"() {
        when:
        click create

        then:
        check modalDialog displayed

        when:
        fill nameLabel with 'Sample TESTING'
        fill asset with file('example.xsd')

        click save

        then:
        check infiniteTableRow displayed

        and:
        check modalDialog gone
    }

    @Ignore
    def "navigate back to data model"() {
        when:
        click modelCatalogue

        and:
        select 'Test 3'

        then:
        check rightSideTitle contains 'Test 3'
    }

    @Ignore
    def "navigate to the top menu and select create relationship"() {

        when:'navigate to createRelationship page'
        click dataModel
        click createRelationship

        then:'verify that the text Destination is displayed'
        check destination displayed
    }

    @Ignore
    def "select based on,destination and create relationship"() {
        when: 'select relation'
        click hasAttachmentOf
        and: ' select destination'
        click destinationIcon
        fill search with "example TESTING" and pick first item
        click ModalPrimaryButton


        then:'verify that attachment is displayed inside table'
        check table contains "has attachment of"
    }

    String file(String name) {
        new File(AssetWizardSpec.getResource(name).toURI()).absolutePath
    }
}
