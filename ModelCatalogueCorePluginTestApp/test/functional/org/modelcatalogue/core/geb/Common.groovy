package org.modelcatalogue.core.geb

class Common {

    static final ApplicationUser admin = ApplicationUser.create('admin')
    static final ApplicationUser viewer = ApplicationUser.create('viewer')
    static final ApplicationUser curator = ApplicationUser.create('curator')

    static final CatalogueAction save = CatalogueAction.runLast('modal', 'modal-save-element')
    static final CatalogueAction create = CatalogueAction.runLast('list', 'create-catalogue-element')
    static final CatalogueAction finalize = CatalogueAction.runLast('item', 'change-element-state', 'finalize')
    static final CatalogueAction newVersion = CatalogueAction.runLast('item', 'change-element-state', 'create-new-version')
    static final CatalogueAction archive = CatalogueAction.runLast('item', 'change-element-state', 'archive')
    static final CatalogueAction delete = CatalogueAction.runLast('item', 'change-element-state', 'delete')
    static final CatalogueAction merge = CatalogueAction.runLast('item', 'change-element-state', 'merge')

    static final String name = 'name'
    static final String description = 'description'

    static final String status = "h3 small span.label"
    static final String rightSideTitle = "h3:not(.ng-hide):not(.data-model-heading)"
    static final String wizard = 'div.basic-edit-modal-prompt'
    static final String subviewStatus = "h3 small span.label"
    static final String modalDialog = "div.modal"
    static final String modalHeader = "div.modal-header h4"
    static final String modalCloseButton = 'div.modal button.close'
    static final String backdrop = '.modal-backdrop'
    static final String confirm = '.modal.messages-modal-confirm'
    static final String OK = '.modal.messages-modal-confirm .btn-primary'
    static final String nameInTheFirstRow = 'div.inf-table-body tbody tr:nth-child(1) td:nth-child(3)'
    static final String firstRowLink = 'div.inf-table-body tbody tr:nth-child(1) td:nth-child(3) a'
    static final String createActionInInfiniteList = 'a.infinite-list-create-action'
    static final String tableFooterAction = 'tr.inf-table-footer-action'
    static final String closeGrowlMessage = "div.messages-panel.growl div.alert button.close"

    static final Keywords item = Keywords.ITEM
    static final Keywords pick = Keywords.SELECT
    static final Keywords prefer = Keywords.PREFER
    static final Keywords existing = Keywords.EXISTING
    static final Keywords last = Keywords.LAST
    static final Keywords first = Keywords.FIRST
}
