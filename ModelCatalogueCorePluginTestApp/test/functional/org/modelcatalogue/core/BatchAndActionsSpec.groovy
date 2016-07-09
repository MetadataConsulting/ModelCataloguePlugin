package org.modelcatalogue.core

import geb.waiting.WaitTimeoutException
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.CatalogueContent
import org.modelcatalogue.core.geb.Common
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.*

@Stepwise
class BatchAndActionsSpec extends AbstractModelCatalogueGebSpec {

    private static final CatalogueAction showBatches = CatalogueAction.runFirst('navigation-right', 'admin-menu', 'action-batches')

    private static final String batchName                   = 'h3.ce-name'
    private static final String performedActions            = 'div.performed-actions .alert:not(.alert-info)'
    private static final String pendingActions              = 'div.pending-actions .alert:not(.alert-info)'
    private static final String noPerformedActions          = 'div.performed-actions .alert.alert-info'
    private static final CatalogueContent nameProperty      = CatalogueContent.create('td.soe-table-property-value input', 'data-for-property': 'name')

    private static final CatalogueContent linkToTestBatch   = CatalogueContent.create('td.inf-table-item-cell a', text: 'Test Batch')
    private static final CatalogueContent linkToRename      = CatalogueContent.create('td.inf-table-item-cell a', text: 'Create Synonyms for Enumerated Type \'personGenderCode(current)\'')
    private static final CatalogueAction generateMerge      = CatalogueAction.runFirst('list', 'generate-merge-models')
    private static final CatalogueAction refreshList        = CatalogueAction.runFirst('list', 'refresh-batches')
    private static final CatalogueAction reloadActions      = CatalogueAction.runFirst('item', 'reload-actions')

    def "see test batch in action "() {
        loginAdmin()

        click showBatches

        expect:
        check linkToTestBatch displayed
    }

    def "generate suggestions"() {
        check backdrop gone

        when:
        click generateMerge

        then:
        check modalDialog displayed

        when:
        click modalPrimaryButton

        while (check(linkToRename).missing) {
            click refreshList
            Thread.sleep(300)
        }

        then:
        check linkToRename displayed
    }

    def "go to detail page and execute few actions"() {
        check backdrop gone

        when:
        click linkToTestBatch

        then:
        check batchName displayed
        check batchName contains 'Test Batch'
        check noPerformedActions displayed
        check pendingActions present 10 or more
        check pendingActions has 'alert-danger'

        when:
        click { $(pendingActions).first().find('.glyphicon-edit') }
        fill nameProperty with 'BrandNewModel'
        click 'button#update-parameters'

        then:
        check '.modal-body' gone
        remove messages

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
