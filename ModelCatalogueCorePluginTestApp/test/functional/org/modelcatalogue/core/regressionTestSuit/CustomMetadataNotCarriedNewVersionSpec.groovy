package org.modelcatalogue.core.regressionTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.gebUtils.Common.getRightSideTitle
import static org.modelcatalogue.core.gebUtils.Common.modalHeader

@Stepwise
class CustomMetadataNotCarriedNewVersionSpec extends AbstractModelCatalogueGebSpec {

    private static final String first_row ='tr.inf-table-item-row>td:nth-child(1)'
    private static final String adminTag ='span.fa-cog'
    private static final String  dataPolicies ='a#data-model-policies-menu-item-link'
    private static final String  header ='#metadataCurator > div.container-fluid.container-main > div > div > div.ng-scope > ui-view > ui-view > div > div > h3'
    private static final String  defaultChecks ='tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>a:nth-child(2)'
    private static final String  uniqueKinds ='tbody.ng-scope>tr:nth-child(3)>td:nth-child(1)>a:nth-child(2)'
    private static final String  editButton ='a#role_item-detail_edit-catalogue-elementBtn'
    private static final String  saveButton ='a#role_modal_modal-save-elementBtn'
    private static final String  logs ='a#logs-menu-item-link>span:nth-child(3)'
    private static final String  nameRow ='tr.inf-table-item-row>td:nth-child(2)'



    def"login to model catalogue and select version"(){

        when:
        loginAdmin()
        select 'Clinical Tags' open 'Versions'

        then:
        check rightSideTitle contains 'Clinical Tags History'

        and:
        check first_row contains '0.0.1'

    }

    def"verify that data Default Checks is displayed and editable"(){

        when:
        click 'span.mc-name'
        click adminTag
        and:
        click dataPolicies

        then:
        check header contains 'Data Model Policies'

        when:
        click defaultChecks

        then:
        check 'span.editable' is'Default Checks'

        when:
        click editButton

        then:
        check modalHeader is 'Edit Data Model Policy'

        when:
        fill 'input#name' with'Default Checks Testing'
        click saveButton

        then:
        check 'span.editable' is 'Default Checks Testing'

        when:
        Thread.sleep(2000L)
        click editButton

        then:
        check modalHeader is 'Edit Data Model Policy'

        when:
        fill 'input#name' with'Default Checks'
        click saveButton

        then:
        check 'span.editable' is 'Default Checks'
    }

    def"verify that data Unique of Kind is displayed and editable"(){

        when:
        click adminTag
        and:
        click dataPolicies

        then:
        check header contains ' Data Model Policies'

        when:
        click uniqueKinds

        then:
         check 'span.editable' is 'Unique of Kind'

        when:
        click editButton

        then:
        check modalHeader is 'Edit Data Model Policy'

        when:
        fill 'input#name' with'Unique of Kind Testing'
        click saveButton

        then:
        Thread.sleep(2000L)
        check 'span.editable' is 'Unique of Kind Testing'

        when:
        click editButton
        fill 'input#name' with'Unique of Kind'
        click saveButton

        then:
        check 'span.editable' is 'Unique of Kind'

    }
    def"check that users can create logs archive"(){

        when:
        click adminTag
        click logs

        then:
        check modalHeader is 'Do you want to create logs archive?'

        and:
        check 'div.modal-body' is'New asset containing the application logs will be created and accessible to all users.'

        when:
        click 'button.btn-primary'

        then:
        check 'span.editable' contains 'Logs from'
         check nameRow isDisplayed()


    }

}
