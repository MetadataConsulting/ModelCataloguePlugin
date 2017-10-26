package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.gebUtils.CatalogueAction
import spock.lang.Stepwise
import static org.modelcatalogue.core.gebUtils.Common.getItem
import static org.modelcatalogue.core.gebUtils.Common.getModalHeader
import static org.modelcatalogue.core.gebUtils.Common.getPick
import static org.modelcatalogue.core.gebUtils.Common.modalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.more


@Stepwise
class GenerateSuggestionAndDismissSpec extends AbstractModelCatalogueGebSpec{

    private static final String  Admin= "#role_navigation-right_admin-menu-menu-item-link > span.fa.fa-cog.fa-fw.fa-2x-if-wide"
    private static final String  actionBatches= "#action-batches-menu-item-link > span.action-label.ng-binding.ng-scope"
    private static final String  deleteSuggestion= "a#role_list_delete-suggestions-menu-item-link>span:nth-child(3)"
    private static final String  table= "tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)"
    private static final String  refresh= "a#role_list_refresh-batches-menu-item-link>span:nth-child(3)"
    private static final String  generateSuggestion= "a#role_list_generate-suggestions-menu-item-link>span:nth-child(3)"
    private static final String  EnumDuplicatesAndSynonyms= "select.form-control>option:nth-child(4)"
    private static final String  suggestion= "tbody.ng-scope>tr:nth-child(1)>td:nth-child(2)>a"
    private static final String  runAllPending= "a#role_item_run-all-actions-in-batch-menu-item-link>span:nth-child(3)"
    private static final String pendingActions = 'div.pending-actions .alert:not(.alert-info)'
    private static final String  dismiss = "#role_action_dismiss-actionBtn"
    private static final CatalogueAction reloadActions = CatalogueAction.runFirst('item', 'reload-actions')


    def "Login to model catalogue and navigate to Actions Batches"(){

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
        click EnumDuplicatesAndSynonyms
        fill 'input#min-score' with '10'
        click modalPrimaryButton
        click refresh

        then:
        check table isDisplayed()

    }

    def"select suggestion and dismiss pending actions"(){

        when:
        click suggestion
        Thread.sleep(2000)

        then:
        check 'div.performed-actions>div' contains("There no actions performed or failed")

        and:
        check pendingActions present 6 or more

        when:
        click { $(dismiss).first()}
        Thread.sleep(3000l)
        click{$(dismiss).last()}

        then:
        check pendingActions has 'alert-danger'

    }

    def"run all pending actions and check dismissed is pending"(){

        when:
        Thread.sleep(2000l)
        click runAllPending
        click modalPrimaryButton
        Thread.sleep(2000l)
        click reloadActions

        then:
        check pendingActions has 'alert-danger'
        check pendingActions present 2

    }


}
