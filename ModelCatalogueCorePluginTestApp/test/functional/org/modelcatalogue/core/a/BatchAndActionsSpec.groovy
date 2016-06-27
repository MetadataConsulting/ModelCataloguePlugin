package org.modelcatalogue.core.a

import geb.waiting.WaitTimeoutException
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.pages.BatchActionsPage
import org.modelcatalogue.core.pages.BatchListPage
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getCloseGrowlMessage

@Stepwise
class BatchAndActionsSpec extends AbstractModelCatalogueGebSpec {

    private static final CatalogueAction showBatches = CatalogueAction.runFirst('navigation-right', 'admin-menu', 'action-batches')

    def "see test batch in action "() {
        loginAdmin()

        // FIXME: temporary workaround of https://metadata.atlassian.net/browse/MET-949
        // should be using action menu item
        go "#/dataModels"
        refresh browser

        go "#/catalogue/relationshipType/all"
        refresh browser

        go "#/catalogue/batch/all"
        refresh browser

        expect:
        at BatchListPage

        waitFor {
            linkToTestBatch.first().displayed
        }
        waitFor {
            menuItem('generate-merge-models', 'list').displayed
        }
    }

    def "generate suggestions"() {
        waitUntilModalClosed()
        when:
        menuItem('generate-merge-models', 'list').click()

        then:
        waitFor {
            modalDialog.first().displayed
        }
        when:
        modalPrimaryButton.click()

        then:
        waitFor(120) {
            menuItem('refresh-batches', 'list').click()
            Thread.sleep(100)
            linkToRename.first().displayed
        }
    }

    def "go to detail page and execute few actions"() {
        waitUntilModalClosed()
        when:
        linkToTestBatch.click()

        then:
        at BatchActionsPage
        waitFor {
            batchName.first().displayed
        }
        batchName.first().text().startsWith 'Test Batch'
        waitFor {
            noPerformedActions.first().displayed
        }
        pendingActions.size() > 10
        'alert-danger' in pendingActions[0].classes()
        pendingActions[0].find('.glyphicon-edit').click()

        when:
        nameProperty = 'BrandNewModel'
        $('button#update-parameters').click()


        then:
        check '.modal-body' gone
        check closeGrowlMessage gone

        waitFor {
            pendingActions[0].text().contains('BrandNewModel')
        }

        when:
        pendingActions[0].find('.glyphicon-repeat').click()


        then:
        waitFor {
            pendingActions[0].find('.glyphicon-play').first().displayed
        }

        when:
        pendingActions[0].find('.glyphicon-play').click()

        then:
        actionsPerformed
    }

    protected boolean isActionsPerformed() {
        try {
            waitFor (5) {
                performedActions.size() == 1
            }
            return true
        } catch (WaitTimeoutException ignored) {
            menuItem('reload-actions', 'item').click()
            waitFor (5) {
                performedActions.size() == 1
            }
            return true
        }
    }

}
