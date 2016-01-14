package org.modelcatalogue.core.a

import geb.waiting.WaitTimeoutException
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.pages.BatchActionsPage
import org.modelcatalogue.core.pages.BatchListPage
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getCloseGrowlMessage

@Stepwise
class BatchAndActionsSpec extends AbstractModelCatalogueGebSpec {


    def "see test batch in action "() {
        loginAdmin()

        when:
        waitFor {
            menuItem('admin-menu', 'navigation-right').displayed
        }

        menuItem('admin-menu', 'navigation-right').click()

        then:
        waitFor {
            menuItem('action-batches', "").displayed
        }

        when:
        menuItem('action-batches', "").click()

        then:
        at BatchListPage

        waitFor {
            linkToTestBatch.displayed
            menuItem('generate-merge-models', 'list').displayed
        }
    }

    def "generate suggestions"() {
        waitUntilModalClosed()
        when:
        menuItem('generate-merge-models', 'list').click()

        then:
        waitFor {
            modalDialog.displayed
        }
        when:
        modalPrimaryButton.click()

        then:
        waitFor(120) {
            menuItem('refresh-batches', 'list').click()
            Thread.sleep(100)
            linkToRename.displayed
        }
    }

    def "go to detail page and execute few actions"() {
        waitUntilModalClosed()
        when:
        linkToTestBatch.click()

        then:
        at BatchActionsPage
        waitFor {
            batchName.displayed
        }
        batchName.text().startsWith 'Test Batch'
        waitFor {
            noPerformedActions.displayed
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
            pendingActions[0].find('.glyphicon-play').displayed
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