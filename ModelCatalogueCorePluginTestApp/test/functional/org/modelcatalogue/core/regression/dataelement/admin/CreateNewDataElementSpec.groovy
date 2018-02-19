package org.modelcatalogue.core.regression.dataelement.admin

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.messages
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.save
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteA')  })
@Stepwise
class CreateNewDataElementSpec extends AbstractModelCatalogueGebSpec {

    private static final String  delete = "a#delete-menu-item-link"
    private static final String  modelCatalogue = "span.mc-name"
    private static final String  saveElement = "a#role_modal_modal-save-elementBtn"
    private static final String search = "input#dataType"
    private static final String dataElement = "a#role_item_catalogue-element-menu-item-link"
    private static final String table = "td.col-md-4"
    private static final String  myElement = "td.col-md-4>span>span>a"
    static String myName = " testing data element "
    static String myCatalogue = "324"
    static String myDescription = "This a test element"

    def "login and navigate to Data Model"() {
        when:
        loginAdmin()

        select 'Test 3'
        selectInTree 'Data Elements'

        then:
        check Common.rightSideTitle is 'Active Data Elements'
    }

    def "navigate to data element creation page"() {
        when:
        click Common.create
        then:
        check Common.modalHeader contains 'Create Data Element'
    }

    @Ignore
    def "fill the create data element form"() {
        when:
        fill Common.nameLabel with myName

        fill Common.modelCatalogueId with myCatalogue

        fill Common.description with myDescription

        and: 'select a data type'

        fill search with ' boolean'
        Thread.sleep(2000l)
        remove Common.messages

        and: 'click on the save button'
        click saveElement

        then: 'verify that data is created'
        check table contains 'testing data element'
    }

    @Ignore
    def "delete the created data model"() {

        when: 'click on the model catalogue'
        click modelCatalogue

        and: 'select the created data element'
        select('Test 3') select 'Data Elements'
        click myElement

        and: 'navigate to the top menu and click on the data element'
        click dataElement

        and: 'click on the delete button'
        click delete
        click Common.modalPrimaryButton

        then:
        check table gone
    }
}
