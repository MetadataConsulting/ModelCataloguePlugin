package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import static org.modelcatalogue.core.geb.Common.getCreate
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.getSave
import static org.modelcatalogue.core.geb.Common.messages
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class CreateDataTypeAndSelectReferenceSpec extends AbstractModelCatalogueGebSpec {
    private static final String reference= "input#pickReferenceType"
    private static final String  dataClass="form.ng-dirty>div:nth-child(11)>div>span>span"
    private static final String addImport="div.search-lg>p>span>a"
    private static final String  search ="input#elements"
    private static final String OK = "div.messages-modal-prompt>div>div>div:nth-child(3)>button:nth-child(1)"
    private static final String clickX ="div.input-group-addon"
    private static final String table ="tr.inf-table-item-row>td:nth-child(1)"
    private static final String referenceType="a#role_item_catalogue-element-menu-item-link"
    private static final String deleteButton="a#delete-menu-item-link>span:nth-child(3)"
    private static final String dataType="tr.inf-table-item-row>td:nth-child(1)>span>span>a"

    def "login to Model Catalogue and select Model"() {
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

    def "fill the create data type form"() {
        when:
        fill nameLabel with "TESTING_DATA_TYPE"

        fill modelCatalogueId with "MET-333"

        fill description with "my description of data type${System.currentTimeMillis()}"

        and: 'select references button and save'
        click reference
        click dataClass

        and: 'import a data'
        click addImport
        fill search with("clinical Tags 0.0.1")
        remove messages

        and:
        click OK
        click clickX

        and:
        click save

        then:
        check table contains 'TESTING_DATA_TYPE'
    }

    def "delete the created data type"() {
        when:'click on the created data type'
        click dataType

        and:'navigate to the top menu and click on the reference type button'
        click referenceType

        and:
        click deleteButton

        and:'confirmation'
        click modalPrimaryButton

        then:
        check table gone
    }
}
