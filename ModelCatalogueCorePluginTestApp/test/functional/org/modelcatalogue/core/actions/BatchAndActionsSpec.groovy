package org.modelcatalogue.core.actions

import geb.waiting.WaitTimeoutException
import org.modelcatalogue.core.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.pages.BatchActionsPage
import org.modelcatalogue.core.pages.BatchListPage
import spock.lang.Stepwise

@Stepwise
class BatchAndActionsSpec extends AbstractModelCatalogueGebSpec {


    def "see test batch in action "() {
        go "#/"
        loginAdmin()

        when:
        go "#/catalogue/batch/all"

        then:
        at BatchListPage

        waitFor {
            linkToTestBatch.displayed
            actionButton('generate-merge-models', 'list').displayed
        }
    }

    def "generate suggestions"() {
        waitUntilModalClosed()
        when:
        actionButton('generate-merge-models', 'list').click()

        then:
        waitFor {
            confirmDialog.displayed
        }
        when:
        confirmOk.click()

        then:
        waitFor(120) {
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
            actionButton('reload-actions').click()
            waitFor (5) {
                performedActions.size() == 1
            }
            return true
        }
    }

}