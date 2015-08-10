package org.modelcatalogue.core.pages

class DataViewPage extends ModelCataloguePage {

    static url = "#/catalogue/dataElement/all"

    static vdUrl = "#/catalogue/valueDomain/all"



    static at = {
        url == "#/catalogue/dataElement/all"
    }
    static content = {

        addNewDataElementButton      { $('#role_list_create-catalogue-elementBtn') }

        dataWizard         { $('div.basic-edit-modal-prompt') }

        name                { dataWizard.find('input[id=name]') }
        classifications     { dataWizard.find('input[id=dataModel-]') }
        description         { dataWizard.find('textarea[id=description]') }

        saveButton          { dataWizard.find("button.btn-success") }
        exitButton          { $("#exit-wizard") }

        newDataElement       {$('div.inf-table-body tbody tr:nth-child(1) td:nth-child(3)')}

        newDataElementTitle      { $('#ce-name')}
        pageTitle             { $("h3") }



    }

    def getCell(int row, int col) {
        $('div.inf-table-body tbody tr:nth-child(' + row + ') td:nth-child(' + col + ')')
    }




}
