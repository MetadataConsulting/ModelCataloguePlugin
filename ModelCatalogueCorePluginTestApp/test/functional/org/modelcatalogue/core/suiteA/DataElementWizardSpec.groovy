package org.modelcatalogue.core.suiteA

import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataElementsPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore
import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.Common
import spock.lang.Stepwise
import spock.lang.IgnoreIf


@Ignore
class DataElementWizardSpec extends AbstractModelCatalogueGebSpec {

    static final String detailSectionFormItemContent = ".metadata-form-item-content"

    def "login and select Data Element"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search('Test 1')
        dashboardPage.select('Test 2')

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Elements')

        then:
        at DataElementsPage

        when:
        select 'No tags'

        then:
        check Common.rightSideTitle is 'Active Data Elements'
    }

    def "Add new data element"() {
        when: 'I click the add model button'
        click Common.create

        then: 'the data element dialog opens'
        check Common.wizard displayed

        when:
        fill Common.nameLabel with "NewDE1"
        fill Common.description with "NT1 Description"

        and: 'save button clicked'
        click Common.save

        then: 'the data element is saved and displayed at the top of the table'
        check { infTableCell(1, 1) } contains "NewDE1"
    }

    def "Check the data element shows up with own details"() {
        expect:
        check Common.backdrop gone

        when: 'Data Element is located'
        check { infTableCell(1, 1) } contains "NewDE1"

        then: 'Click the element'
        click { infTableCell(1, 1).find('a:not(.inf-cell-expand)') }
        check Common.rightSideTitle contains 'NewDE1'
    }

    def "Check Form (Item) detail section is present and collapsed"() {
        expect:
        check Common.detailSectionFormMetadata present Common.once
        check detailSectionFormItemContent gone

        when: "Click the title"
        click Common.detailSectionFormMetadata.find('.title .btn')

        then: "Content is displayed"
        check detailSectionFormItemContent displayed
    }
}
