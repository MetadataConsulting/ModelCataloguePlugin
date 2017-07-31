package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.Stepwise
import spock.lang.Title

import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle


@Stepwise @Title("https://metadata.atlassian.net/browse/MET-1552")
class CreateNewDraftAndAddDataTypeFromAnotherModelSpec  extends AbstractModelCatalogueGebSpec {

    private static final String createButton = "a#role_data-models_create-data-modelBtn"
    private static final String finalized = "a#finalize-menu-item-link>span:nth-child(3)"
    private static final String importStep = "button#step-imports"
    private static final String saveButton = "a#role_modal_modal-save-elementBtn"
    private static final String finishStep = "button#step-finish"
    private static final String closeStep = "div.modal-footer>button:nth-child(2)"
    private static final String dataMenuBarButton = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String createNewDataType = "a#catalogue-element-create-dataType-menu-item-link>span:nth-child(3)"
    private static final String referenceTypeButton = "input#pickReferenceType"
    private static final String dataClassLink = "form.ng-dirty>div:nth-child(11)>div>label"
    private static final String hideButton = "button.btn-primary"
    private static final String modelCatalogueButton = "span.mc-name"
    private static final String semanticVersion = "input#semanticVersion"
    private static final String revisionNotes = "textarea#revisionNotes"
    private static final String finalizedButton = "a#role_modal_modal-finalize-data-modalBtn"
    private static final String newVersionLink = "a#create-new-version-menu-item-link>span:nth-child(3)"
    private static final String createVersionButton = "a#role_modal_modal-create-new-versionBtn"
    private static final String newVersion = "#history-changes > div.inf-table-body > table > tbody > tr.inf-table-item-row.ng-scope.warning > td:nth-child(1) > a.preserve-new-lines.ng-binding.ng-scope"
    private static final String Version = "ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(10)>div>span>span"
    private static final String dataTypeVersion = "#metadataCurator > div.container-fluid.container-main > div > div > div.ng-scope > div > div.split-view-right.data-model-detail-pane > ui-view > ui-view > div > div > div > div > form > h3 > small.text-muted.ng-scope.ng-binding.editable"
    private static final String dataTypeTreeView = "ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(3)>div>span>span"
    private static final String newDataType = "tr.inf-table-item-row>td:nth-child(1)>span>span>a"
    private static final String dataModelTreeView = "ul.catalogue-element-treeview-list-root>li>div>span>span"
    private static final String deleteButton = "a#delete-menu-item-link>span:nth-child(3)"


    def "login to model catalogue and create a data model"(){

        when:
        loginAdmin()

        then:
        check createButton isDisplayed()

        when:
        click createButton

        then:
        check modalHeader is 'Data Model Wizard'

        when:
        fill nameLabel with 'Draft Data Model'
        fill modelCatalogueId with 'MET-22234'
        fill description with 'THIS IS DATA FROM ANOTHER MODEL'
        Thread.sleep(2000L)
        click importStep

        then:
        check 'div.form-group>label' is 'Import Existing Data Models'

        when:
        fill 'input#name' with 'MET-523' and pick first item

        then:
        check 'span.with-pointer' isDisplayed()

        when:
        click finishStep

        then:
        check 'div#summary>h4' is 'Data Model Draft Data Model created'

        when:
        click closeStep

        then:
        check rightSideTitle contains 'Draft Data Model MET-22234@0.0.1'
    }

    def"create a Reference data type"(){

        when:
        click dataMenuBarButton
        click createNewDataType

        then:
        check 'h4.ng-binding' is 'Create Data Type'

        when:
        fill nameLabel with 'REFERENCE TYPE'
        fill modelCatalogueId with 'MD-002'
        fill description with 'TESTING DATA TYPE'
        click referenceTypeButton
        Thread.sleep(2000L)

        then:
        check dataClassLink  is 'Data Class'

        when:
        fill 'input#dataClass' with 'MET-523.M1' and pick first item
        click saveButton

        then:
        check rightSideTitle contains 'Draft Data'

    }

    def" finalized the data model"(){

        when:
        click modelCatalogueButton
        select 'Draft Data Model'

        then:
        check rightSideTitle contains 'Draft Data Model MET-22234@0.0.1'

        when:
        click dataMenuBarButton
        click finalized

        then:
        check modalHeader is 'Finalize Data Model'

        when:
        fill semanticVersion with '2.0.0'
        fill revisionNotes with 'TESTING FINALIZED'
        Thread.sleep(3000l)
        click finalizedButton


        then:
        Thread.sleep(3000L)
        check 'h4.ng-binding'  contains 'Finalizing Draft Data Model'

        when:
        Thread.sleep(3000L)
        click modalPrimaryButton

        then:
        check rightSideTitle contains 'Draft Data Model MET-22234@2.0.0'


    }

    def"create a new version and tick the checkbox next to the imported data model"(){

        when:
        click dataMenuBarButton
        click newVersionLink

        then:
        check modalHeader is 'New Version of Data Model'

        when:
        fill semanticVersion with '2.0.1'

        then:
        $("body > div.modal.fade.ng-isolate-scope.in > div > div > div.modal-body.ng-scope > form > div.checkbox.ng-scope > label > input").value(true)

        when:
        click createVersionButton

        then:
        check 'h4.ng-binding' contains 'Create new version of Draft Data Model'


        when:
        Thread.sleep(3000L)
        click hideButton

        then:
        check rightSideTitle contains 'Draft Data Model MET-22234@2.0.0'

    }

    def"verify the imported data class carries the new version"(){

        when:
        click Version


        then:
        check rightSideTitle contains 'Draft Data Model History'

        and:
        check { infTableCell(1, 1) } contains '2.0.1'


        when:
        click newVersion

        then:
        check rightSideTitle contains 'Draft Data Model MET-22234@2.0.1'


        when:
        click dataTypeTreeView
        click newDataType

        then:
        check dataTypeVersion contains 'MD-002@2.0.1'

    }

    def"delete the created data model"(){

        when:
        click dataModelTreeView
        click dataMenuBarButton
        click deleteButton

        then:
        check modalHeader is 'Do you really want to delete Data Model Draft Data Model?'
        and:
        click modalPrimaryButton

    }


}
