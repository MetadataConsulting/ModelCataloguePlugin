package org.modelcatalogue.core.pages

/**
 * Created by david on 05/11/14.
 */
class ValueDomainList extends ModelCataloguePage {

    static url = "#/catalogue/valuedomain/all"

    static at = {
        url == "#/catalogue/batch/all"
    }
    static content = {

        addBatchButton      { $('span.glyphicon.glyphicon-plus-sign') }

        basicEditDialog     { $('div.basic-edit-modal-prompt') }

        name                { basicEditDialog.find('input[id=name]') }
        description         { basicEditDialog.find('textarea[id=description]') }
        saveButton          { basicEditDialog.find("button.btn-success") }

        batchList           { $('table.dl-table') }
        linkToTestBatch     { $('td.inf-table-item-cell a', text: 'Test Batch') }

    }
}
