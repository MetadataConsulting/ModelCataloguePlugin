package org.modelcatalogue.core

import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.CatalogueContent
import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.ScrollDirection
import spock.lang.Ignore

import static org.modelcatalogue.core.geb.Common.*

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import spock.lang.IgnoreIf
@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class DataModelWizardSpec extends AbstractModelCatalogueGebSpec {

    static final String classificationWizzard = 'div.create-classification-wizard'
    static final String wizardName = 'div.create-classification-wizard #name'
    static final String description ='div.create-classification-wizard #description'
    static final String modelCatalogueId ='div.create-classification-wizard #modelCatalogueId'
    static final String stepImports = "#step-imports"
    static final String stepFinish = "#step-finish"
    static final String exitButton = "#exit-wizard"
    static final CatalogueContent metadataReviewed = CatalogueContent.create('*[name=metadata-reviewed]')
    static final CatalogueContent metadataApproved = CatalogueContent.create('*[name=metadata-approved]')
    static final CatalogueAction modalFinalize = CatalogueAction.runLast('modal', 'modal-finalize-data-modal')
    static final CatalogueAction modalCreateNewVersion = CatalogueAction.runLast('modal', 'modal-create-new-version')
    static final String modalFeedback = '.messages-modal-feedback'
    static final CatalogueAction refreshFeedback = CatalogueAction.runFirst('feedback', 'refresh-feedback')
    static final CatalogueContent feedback = CatalogueContent.create('.messages-modal-feedback pre') {
        click refreshFeedback
    }

    def "go to login"() {
        login admin

        expect:
        waitFor(120) { browser.title == 'Data Models' }
    }

    def "add new data model"() {

        def uuid = UUID.randomUUID().toString()

        click createNewDataModel

        expect: 'the model dialog opens'
        check classificationWizzard displayed

        when:
        fill wizardName with "New Data Model $uuid"
        fill modelCatalogueId with "http://www.example.com/$uuid"
        fill description with "Description of Data Model"

        then:
        // TODO: check does not work even if the button is enabled
        //check stepImports enabled
        true

        when:
        click stepImports

        then:
        check stepImports has 'btn-primary'

        when:
        fill wizardName with 'NHIC'
        selectCepItemIfExists()


        and:
        click stepFinish

        then:
        check '#summary' is "Data Model New Data Model $uuid created"

        when:
        click exitButton
        remove messages

        then:
        check rightSideTitle contains "New Data Model $uuid"
    }

    def "finalize element"() {
        check backdrop gone

        setup: "fill required data for finalization"
        click inlineEdit
        click Common.detailSectionMetadata.find('.title .btn')
        fill 'metadata-authors' with 'Neville Chamberlain'
        fill 'metadata-reviewers' with 'Édouard Daladier'
        fill 'metadata-owner' with 'Jan Hus'
        fill metadataReviewed with '29/04/2016'
        fill metadataApproved with '29/04/2016'
        fill 'metadata-namespace' with 'Namespace'
        fill 'metadata-organization' with 'Organization'
        3.times { scroll up }
        click inlineEditSubmit
        check "input[name='name']" gone

        when: "finalize is clicked"
        click finalize

        then: "modal prompt is displayed"
        check modalDialog displayed

        when: "ok is clicked"
        fill 'semanticVersion' with '1.0.0'
        fill 'revisionNotes' with 'initial commit'
        click modalFinalize

        then:
        check modalFeedback displayed
        check feedback contains 'COMPLETED SUCCESSFULLY'

        when:
        click modalPrimaryButton

        then: "the element is finalized"
        check status has 'label-primary'
    }

    def "create new version of the element"() {
        check backdrop gone
        when: "new version is clicked"
        click newVersion

        then: "modal prompt is displayed"
        check modalDialog displayed

        when: "ok is clicked"
        fill 'semanticVersion' with '1.0.1'

        click modalCreateNewVersion

        then: "the element new draft version is created"
        check modalFeedback displayed
        check feedback contains 'COMPLETED SUCCESSFULLY'

        when:
        click '.new-version-link'
        click modalPrimaryButton

        then:
        check status has 'label-warning'
    }

    @Ignore('Draft versions cannot be deprecated')
    def "deprecate the data model"() {
        waitUntilModalClosed()
        when: "deprecate action is clicked"
        click archive

        then: "modal prompt is displayed"
        check confirm displayed

        when: "ok is clicked"
        click OK

        then: "the element is now deprecated"
        check status has 'label-danger'

    }

    def "create new data model, hard delete the data model and create new with the same name"() {
        given:
            def uuid = UUID.randomUUID().toString()

        when:
            login admin
            click createNewDataModel

        then: 'the model dialog opens'
            check classificationWizzard displayed

        when:
            fill wizardName with "New Data Model $uuid"
            fill modelCatalogueId with "http://www.example.com/$uuid"
            fill description with "Description of Data Model"

        then:
        // TODO: check does not work even if the button is enabled
        //check stepFinish enabled
        true

        when:
            click stepFinish

        then:
            check '#summary' is "Data Model New Data Model $uuid created"

        when:
            click exitButton
            remove messages

        then:
            check rightSideTitle contains "New Data Model $uuid"

        check backdrop gone
        when: "delete action is clicked"
            click delete

        then: "modal prompt is displayed"
            check confirm displayed

        when: "ok is clicked"
            click OK

        then: "you are redirected to the data models page"
            waitFor(120) { browser.title == 'Data Models' }

        when: "you try to create the data model with the same name"

            click createNewDataModel

        then: 'the model dialog opens'
            check classificationWizzard displayed

        when:
            fill wizardName with "New Data Model $uuid"
            fill modelCatalogueId with "http://www.example.com/$uuid"
            fill description with "Description of Data Model"

        then:
        // TODO: check does not work even if the button is enabled
        // check stepFinish enabled
        true

        when:
            click stepFinish

        then:
            check '#summary' is "Data Model New Data Model $uuid created"

        when:
            click exitButton
            remove messages

        then:
            check rightSideTitle contains "New Data Model $uuid"
    }
}
