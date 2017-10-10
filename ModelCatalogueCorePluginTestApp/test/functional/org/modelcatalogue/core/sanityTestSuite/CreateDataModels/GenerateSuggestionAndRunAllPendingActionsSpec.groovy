package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getMore
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.pick


@Stepwise
class GenerateSuggestionAndRunAllPendingActionsSpec extends AbstractModelCatalogueGebSpec {

    private static final String  Admin= "#role_navigation-right_admin-menu-menu-item-link > span.fa.fa-cog.fa-fw.fa-2x-if-wide"
    private static final String  actionBatches= "#action-batches-menu-item-link > span.action-label.ng-binding.ng-scope"
    private static final String  deleteSuggestion= "a#role_list_delete-suggestions-menu-item-link>span:nth-child(3)"
    private static final String  table= "tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)"
    private static final String  refresh= "a#role_list_refresh-batches-menu-item-link>span:nth-child(3)"
    private static final String  generateSuggestion= "a#role_list_generate-suggestions-menu-item-link>span:nth-child(3)"
    private static final String  fuzzyMatch= "select.form-control>option:nth-child(3)"
    private static final String  suggestion= "tbody.ng-scope>tr:nth-child(1)>td:nth-child(2)>a"
    private static final String  runAllPending= "a#role_item_run-all-actions-in-batch-menu-item-link>span:nth-child(3)"
    private static final String  reloadButton= "a#role_item_reload-actions-menu-item-link>span:nth-child(3)"


    def"login to model catalogue and click on admin tag"(){


        when:
        loginAdmin()

        then:
        check Admin isDisplayed()


        when:
        click Admin
        click actionBatches

        then:
        check 'h3.ng-binding' contains  'Batches'

    }

    def"delete suggestion and create new suggestion"(){

        when:
        click deleteSuggestion
        click modalPrimaryButton
        Thread.sleep(3000)
        and:
        click refresh

        then:
        check table isGone()


        when:
        click generateSuggestion

        then:
        check modalHeader is 'Generate Suggestions'


        when:
        fill 'input#data-model-1' with'NHIC'and pick first item
        fill 'input#data-model-2' with'NHIC'and pick first item
        click fuzzyMatch
        fill 'input#min-score' with '10'
        click modalPrimaryButton
        Thread.sleep(3000L)
        click refresh

        then:
        check table isDisplayed()

    }

    def"select the suggestion and run pending action"(){

        when:
        click suggestion
        Thread.sleep(2000)

        then:
        check 'div.performed-actions>div' contains("There no actions performed or failed")


        when:
        click runAllPending
        click modalPrimaryButton
        Thread.sleep(3000l)
        click reloadButton

        then:
        check 'div.pending-actions>div' contains("There are no pending actions")




    }
}
