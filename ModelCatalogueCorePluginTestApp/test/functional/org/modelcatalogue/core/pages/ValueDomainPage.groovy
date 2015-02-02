package org.modelcatalogue.core.pages

/**
 * Created by david on 05/11/14.
 */
class ValueDomainPage extends ModelCataloguePage {

    static url = "#/catalogue/valueDomain/all"

    static at = {
        url == "#/catalogue/valueDomain/all"
    }
    static content = {

        addBatchButton      { $('span.glyphicon.glyphicon-plus-sign') }

        basicEditDialog     { $('div.basic-edit-modal-prompt') }

        classification      { basicEditDialog.find('#classification') }
        name                { basicEditDialog.find('#name') }
        modelCatalogueId    { basicEditDialog.find('#modelCatalogueId') }
        description         { basicEditDialog.find('#description') }
        dataType            { basicEditDialog.find('#dataType') }
        unitOfMeasure       { basicEditDialog.find('#unitOfMeasure') }
        rule                { basicEditDialog.find('#rule') }

        expandRuleButton    { basicEditDialog.find('.glyphicon-collapse-down') }

        saveButton          { basicEditDialog.find("button.btn-success") }

        newValueDomain      {$('div.inf-table-body tbody tr:nth-child(1) td:nth-child(3)')}

        dataTypeHeader      { $("td[data-value-for='Data Type']") }

        valueDomain         { modalDialog.find('#valueDomain') }
        mapping             { modalDialog.find('#mapping') }
        value               { modalDialog.find('#value') }

        type                { modalDialog.find('#type') }
        element             { modalDialog.find('#element') }


    }
}
