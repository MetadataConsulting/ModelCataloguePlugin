package org.modelcatalogue.core.pages

class BatchActionsPage extends ModelCataloguePage {

    static url = "#/catalogue/actions/batch/1"

    static at = {
        url ==~ "#/catalogue/actions/batch/\\d+"
    }
    static content = {
        batchName                   { $('h3.ce-name') }
        performedActionsContainer   { $('div.performed-actions')}
        performedActions            { performedActionsContainer.find('.alert:not(.alert-info)') }
        pendingActionsContainer     { $('div.pending-actions')}
        pendingActions              { pendingActionsContainer.find('.alert:not(.alert-info)') }
        noPerformedActions          { performedActionsContainer.find('.alert.alert-info') }

        nameProperty                { $('td.soe-table-property-value input', 'data-for-property': 'name') }
    }
}
