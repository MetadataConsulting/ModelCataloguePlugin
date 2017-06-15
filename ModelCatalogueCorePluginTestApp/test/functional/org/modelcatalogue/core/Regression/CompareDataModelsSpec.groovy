package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import spock.lang.Timeout
import spock.lang.Title

import java.util.concurrent.TimeUnit

import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.getRightSideTitle

@Stepwise @Title("")
class CompareDataModelsSpec extends AbstractModelCatalogueGebSpec{


    static final String stepImports = "#step-imports"
    static final String wizardName = 'div.create-classification-wizard #name'
    private static final String  createButton='a#role_data-models_create-data-modelBtn'
    private static final String  closeButton='div.modal-footer>button:nth-child(2)'
    private static final String  finishButton='button#step-finish'
    private static final String  editButton='a#role_item-detail_inline-editBtn'
    private static final String  revisionNotes='form.ng-pristine>div:nth-child(7)>div>ng-include>div>span>div>textarea'
    private static final String  organisation='form.ng-pristine>div:nth-child(8)>div>ng-include>div:nth-child(2)>span>div>input'
    private static final String  namespace='form.ng-pristine>div:nth-child(8)>div>ng-include>div:nth-child(4)>span>div>input'
    private static final String  catalogueID='h3.ce-name>span:nth-child(5)>div>input'
    private static final String  policy='input#dataModelPolicy'
    private static final String  submitButton='button#role_item-detail_inline-edit-submitBtn'
    private static final String  name='form.ng-valid>div:nth-child(8)>div>ng-include>div:nth-child(4)>small'
    private static final String  organisationName='form.ng-valid>div:nth-child(8)>div>ng-include>div:nth-child(2)>small'
    private static final String  revision='form.ng-valid>div:nth-child(7)>div>ng-include>div>small'
    private static final String   finalize='a#finalize-menu-item-link'
    private static final String   dataModelMenuButton='a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'
    private static final String   table='tbody.ng-scope>tr:nth-child(1)>td:nth-child(4)'
    private static final String   finalizeButton='a#role_modal_modal-finalize-data-modalBtn'
    private static final String   versionNote='textarea#revisionNotes'



   // @Timeout(value = 10, unit =TimeUnit.MINUTES)
    def" login to the model catalogue and create a data model"(){
        when:
        loginAdmin()


        then:
        check createButton isDisplayed()

        when:
        click createButton

        and:'fill the form '
        fill nameLabel with 'COMPARE_MODEL_1'
        fill modelCatalogueId with 'MET-123'
        fill description with 'this is my testing data'

        then:
        check stepImports enabled

        when:
        click stepImports

        then:
        check stepImports has 'btn-primary'

        when:'import  MET-523'
        fill wizardName with 'MET-523'
        selectCepItemIfExists()

        and:'create the dat class'
        click finishButton
        click closeButton

        then:
        check rightSideTitle contains 'COMPARE_MODEL_1 MET-123@0.0.1'
    }

    def"fill the metadata form "(){

        when:
        click editButton

        and:
        fill catalogueID with 'MET-67'
        fill policy with 'Unique of Kind' and pick first item
        fill revisionNotes with 'THIS THE TESTING REVISION'
        fill organisation with 'metadata consulting'
        fill namespace with 'Paul Clarke'

        and:
        click submitButton

        then:
        check name is 'Paul Clarke'

        and:
        check organisationName is'metadata consulting'

        and:
        check revision is 'THIS THE TESTING REVISION'

    }

    def"finalized the data model"(){


        when:'navigate to the top menu and select finalized'
        click dataModelMenuButton
        click finalize

        then:
        check  modalHeader is 'Finalize Data Model'

        when:
        fill versionNote with 'THIS IS THE VERSION NOTE'

        click finalizeButton

        click modalPrimaryButton

        then:
        check table contains 'TESTING_DATA_MODEL_NEW_VERSION (0.0.1) finalized'

    }




}
