package org.modelcatalogue.core.actions

import geb.spock.GebSpec
import org.modelcatalogue.core.pages.BatchActionsPage
import org.modelcatalogue.core.pages.BatchListPage
import org.modelcatalogue.core.pages.ModalTreeViewPage

class BatchAndActionsSpec extends GebSpec {


    def "execute few actions"() {
        when:
        go "#/catalogue/model/all"

        then:
        at ModalTreeViewPage
        waitFor {
            viewTitle.displayed
        }

        when:
        loginAdmin()

        then:
        waitFor {
            addModelButton.displayed
        }

        when:
        go "#/catalogue/batch/all"

        then:
        at BatchListPage
        waitFor {
            linkToTestBatch.displayed
        }

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
        waitFor {
            performedActions.size() == 1
        }
    }

}