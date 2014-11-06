package org.modelcatalogue.core.pages

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





    }




}
