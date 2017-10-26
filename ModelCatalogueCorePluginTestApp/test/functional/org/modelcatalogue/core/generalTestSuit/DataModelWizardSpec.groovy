package org.modelcatalogue.core.generalTestSuit

import org.modelcatalogue.core.gebUtils.CatalogueAction
import org.modelcatalogue.core.gebUtils.CatalogueContent
import org.modelcatalogue.core.gebUtils.Common
import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@Stepwise
class DataModelWizardSpec extends AbstractModelCatalogueGebSpec {

    static final String classificationWizzard = 'div.create-classification-wizard'
    static final String  metadataLink = 'form.ng-pristine>div:nth-child(7)>p>span>span:nth-child(1)'
    static final String wizardName = 'div.create-classification-wizard #name'
    static final String description ='div.create-classification-wizard #description'
    static final String modelCatalogueId ='div.create-classification-wizard #modelCatalogueId'
    static final String stepImports = "#step-imports"
    static final String stepFinish = "#step-finish"
    static final String exitButton = "#exit-wizard"
    static final CatalogueContent metadataReviewed = CatalogueContent.create('*[name=metadata-reviewers]')
    static final CatalogueContent metadataApproved = CatalogueContent.create('*[name=metadata-approved]')
    static final CatalogueAction modalFinalize = CatalogueAction.runLast('modal', 'modal-finalize-data-modal')
    static final CatalogueAction modalCreateNewVersion = CatalogueAction.runLast('modal', 'modal-create-new-version')
    static final String modalFeedback = '.messages-modal-feedback'
    static final CatalogueAction refreshFeedback = CatalogueAction.runFirst('feedback', 'refresh-feedback')
    static final CatalogueContent feedback = CatalogueContent.create('.messages-modal-feedback pre') {
        click refreshFeedback
    }

    def "go to login"() {
        login Common.admin

        expect:
        waitFor(120) { browser.title == 'Data Models' }
    }

    def "add new data model"() {

        def uuid = UUID.randomUUID().toString()

        click Common.createNewDataModel

        expect: 'the model dialog opens'
        check classificationWizzard displayed

        when:
        fill wizardName with "New Data Model $uuid"
        fill modelCatalogueId with "http://www.example.com/$uuid"
        fill description with "Description of Data Model"

        then:
        check stepImports enabled

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
        remove Common.messages

        then:
        check Common.rightSideTitle contains "New Data Model $uuid"
    }

    def "finalize element"() {
        check Common.backdrop gone
        Thread.sleep(2000l)

        setup: "fill required data for finalization"
        click Common.inlineEdit
        Thread.sleep(3000L)
        click metadataLink
        Thread.sleep(3000l)
        fill 'metadata-authors' with 'Neville Chamberlain'
        fill 'metadata-reviewers' with 'Édouard Daladier'
        fill 'metadata-owner' with 'Jan Hus'
        fill metadataReviewed with '29/04/2016'
        fill metadataApproved with '29/04/2016'
        fill 'metadata-organization' with 'Namespace'
        fill 'metadata-namespace' with 'Organization'
        3.times { scroll Common.up }
        click Common.inlineEditSubmit
        check "input[name='name']" gone

        when: "finalize is clicked"
        click Common.finalize

        then: "modal prompt is displayed"
        check Common.modalDialog displayed

        when: "ok is clicked"
        fill 'semanticVersion' with '1.0.0'
        fill 'revisionNotes' with 'initial commit'
        click modalFinalize

        then:
        check modalFeedback displayed
        check feedback contains 'COMPLETED SUCCESSFULLY'

        when:
        click Common.modalPrimaryButton

        then: "the element is finalized"
        check Common.status has 'label-primary'
    }

    def "create new version of the element"() {
        check Common.backdrop gone
        when: "new version is clicked"
        click Common.newVersion

        then: "modal prompt is displayed"
        check Common.modalDialog displayed

        when: "ok is clicked"
        fill 'semanticVersion' with '1.0.1'

        click modalCreateNewVersion

        then: "the element new draft version is created"
        check modalFeedback displayed
        check feedback contains 'COMPLETED SUCCESSFULLY'

        when:
        click '.new-version-link'
        click Common.modalPrimaryButton

        then:
        check Common.status has 'label-warning'
    }

    def "deprecate the data model"() {
        waitUntilModalClosed()
        when: "deprecate action is clicked"
        click Common.archive

        then: "modal prompt is displayed"
        check Common.confirm displayed

        when: "ok is clicked"
        click Common.OK

        then: "the element is now deprecatedAndRestore"
        check Common.status has 'label-danger'

    }

    def "create new data model, hard delete the data model and create new with the same name"() {
        given:
            def uuid = UUID.randomUUID().toString()

        when:
            login Common.admin
            click Common.createNewDataModel

        then: 'the model dialog opens'
            check classificationWizzard displayed

        when:
            fill wizardName with "New Data Model $uuid"
            fill modelCatalogueId with "http://www.example.com/$uuid"
            fill description with "Description of Data Model"

        then:
            check stepFinish enabled

        when:
            click stepFinish

        then:
            check '#summary' is "Data Model New Data Model $uuid created"

        when:
            click exitButton
            remove Common.messages

        then:
            check Common.rightSideTitle contains "New Data Model $uuid"

        check Common.backdrop gone
        when: "delete action is clicked"
            click Common.delete

        then: "modal prompt is displayed"
            check Common.confirm displayed

        when: "ok is clicked"
            click Common.OK

        then: "you are redirected to the data models page"
            waitFor(120) { browser.title == 'Data Models' }

        when: "you try to create the data model with the same name"

            click Common.createNewDataModel

        then: 'the model dialog opens'
            check classificationWizzard displayed

        when:
            fill wizardName with "New Data Model $uuid"
            fill modelCatalogueId with "http://www.example.com/$uuid"
            fill description with "Description of Data Model"

        then:
            check stepFinish enabled

        when:
            click stepFinish

        then:
            check '#summary' is "Data Model New Data Model $uuid created"

        when:
            click exitButton
            remove Common.messages

        then:
            check Common.rightSideTitle contains "New Data Model $uuid"
    }
}
