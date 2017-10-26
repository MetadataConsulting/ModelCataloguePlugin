package org.modelcatalogue.core.generalTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.gebUtils.CatalogueAction
import org.modelcatalogue.core.gebUtils.CatalogueContent
import org.modelcatalogue.core.gebUtils.Common
import spock.lang.Stepwise

@Stepwise
class BatchAndActionsSpec extends AbstractModelCatalogueGebSpec {

    private static final CatalogueAction showBatches = CatalogueAction.runFirst('navigation-right', 'admin-menu', 'action-batches')

    private static final String batchName                   = 'h3.ce-name'
    private static final String performedActions            = 'div.performed-actions .alert:not(.alert-info)'
    private static final String pendingActions              = 'div.pending-actions .alert:not(.alert-info)'
    private static final String noPerformedActions          = 'div.performed-actions .alert.alert-info'
    private static final CatalogueContent nameProperty      = CatalogueContent.create('td.soe-table-property-value input', 'data-for-property': 'name')

    private static final CatalogueContent linkToTestBatch   = CatalogueContent.create('td.inf-table-item-cell a', text: 'Test Batch')
    private static final CatalogueContent linkToRename      = CatalogueContent.create('td.inf-table-item-cell a', text: "Suggested DataElement Exact Matches for 'Test 1 (0.0.1)' and 'Test 2 (0.0.1)'")
    private static final CatalogueAction generateSuggestions = CatalogueAction.runFirst('list', 'generate-suggestions')
    private static final CatalogueAction refreshList        = CatalogueAction.runFirst('list', 'refresh-batches')
    private static final CatalogueAction reloadActions      = CatalogueAction.runFirst('item', 'reload-actions')
    private static final String  search1 ="input#data-model-1"
    private static final String  search2 ="input#data-model-2"
    private static final String  dropdown ="select.form-control"
    private static final String  minScore ="input#min-score"


    def "see test batch in action "() {
        loginAdmin()

        click showBatches

        expect:
        check linkToTestBatch displayed
    }

    def "generate suggestions"() {
        check Common.backdrop gone

        when:
        click generateSuggestions

        then:
        check Common.modalDialog displayed

        when:
        fill search1 with "test 1" and Common.pick first Common.item
        fill search2 with "test 2" and Common.pick first Common.item
        click dropdown
        $(dropdown).find('option').find{it.value() =='string:Data Element Exact Match'}.click()

        fill minScore with "10"

        click Common.modalPrimaryButton

        while (check(linkToRename).missing) {
            click refreshList
            Thread.sleep(300)
        }

        then:
        check linkToRename displayed
    }

    def "go to detail page and execute few actions"() {
        check Common.backdrop gone

        when:
        click linkToTestBatch

        then:
        check batchName displayed
        check batchName contains 'Test Batch'
        check noPerformedActions displayed
        check pendingActions present 10 or Common.more
        check pendingActions has 'alert-danger'

        when:
        click { $(pendingActions).first().find('.glyphicon-edit') }
        fill nameProperty with 'BrandNewModel'
        click 'button#update-parameters'

        then:
        check '.modal-body' gone
        remove Common.messages

        check pendingActions contains 'BrandNewModel'

        when:
        click { $(pendingActions).first().find('.glyphicon-repeat') }

        then:
        check { $(pendingActions).first().find('.glyphicon-play') } displayed

        when:
        click { $(pendingActions).first().find('.glyphicon-play') }

        while (check(pendingActions).missing) {
            click reloadActions
        }

        then:
        noExceptionThrown()
    }

}
