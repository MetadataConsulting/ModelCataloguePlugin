package org.modelcatalogue.core.pages

import org.openqa.selenium.By

/**
 * Created by david on 02/11/14.
 */
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
        classifications     { dataWizard.find('input[id=classification-]') }
        description         { dataWizard.find('textarea[id=description]') }
        valueDomain         { dataWizard.find('input[id=valueDomain]') }

        saveButton          { dataWizard.find("button.btn-success") }
        exitButton          { $("#exit-wizard") }

        //newDataElement       { $('td.inf-table-item-cell.ng-scope.col-md-3') }

        newDataElement       {$('div.inf-table-body tbody tr:nth-child(1) td:nth-child(3)')}


        newDataElementTitle      { $('div.element.ng-isolate-scope.h3')}
        pageTitle             { $("h3") }



    }

    def getCell(int row, int col) {
        $('div.inf-table-body tbody tr:nth-child(' + row + ') td:nth-child(' + col + ')')
    }




}
