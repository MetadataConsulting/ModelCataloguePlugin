package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getCreate
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getMessages
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.getSave
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.messages
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.save

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class CreateDataTypeAndSelectSubsetSpec extends AbstractModelCatalogueGebSpec {

    private static final String  subset= "input#pickSubsetType"
    private static final String enumeratedTypeBase ="input#baseEnumeration"
    private static final String  deleteButton="a#delete-menu-item-link>span:nth-child(3)"
    private static final String  dataType ="tr.inf-table-item-row>td:nth-child(1)>span>span>a"
    private static final String  enumeratedType ="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String table = "tr.inf-table-item-row>td:nth-child(1)"

    def "login and navigate to Data model"() {

        when:
        loginCurator()
        select 'Test 3'
        selectInTree 'Data Types'

        then:
        check rightSideTitle contains 'Active Data Types'
    }

    def "Navigate to data type page"() {
        when:
        click create
        then:
        check modalHeader contains 'Create Data Type'
    }

    def " fill the create data type form"() {
        when:
        fill nameLabel with "TESTING_DATA_TYPE_SUBSET"

        fill modelCatalogueId with "MET-089"

        fill description with "my description of data type${System.currentTimeMillis()}"

        and: 'select references button and save'
        click subset
        fill enumeratedTypeBase with 'analysedSpecimenType UrineDip'
        Thread.sleep(2000l)
        remove messages

        and:
        click save

        then:
        check table contains 'TESTING_DATA_TYPE_SUBSET'


    }
    def" delete the created data type"(){

        when:'click on the created data type'
        click dataType

        and:'navigate to the top menu and click on the reference type button'
        click enumeratedType

        and:
        click deleteButton

        and:'confirmation'
        click modalPrimaryButton

        then:
        check table gone

    }
}
