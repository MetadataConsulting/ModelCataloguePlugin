package org.modelcatalogue.core.pages

class DataTypeListPage extends ModelCataloguePage {

    static url = "#/catalogue/dataType/all"

    static at = {
        url == "#/catalogue/dataType/all"
    }

    static content={
        dataWizard          { $('div.basic-edit-modal-prompt') }
        classifications     { dataWizard.find('input[id=dataModel]') }

        addBatchButton      { $('span.glyphicon.glyphicon-plus-sign') }

        basicEditDialog     { $('div.basic-edit-modal-prompt') }

        classification      { basicEditDialog.find('#dataModel') }
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

        mapping             { modalDialog.find('#mapping') }
        value               { modalDialog.find('#value') }

        type                { modalDialog.find('#type') }
        element             { modalDialog.find('#element') }
    }
}
