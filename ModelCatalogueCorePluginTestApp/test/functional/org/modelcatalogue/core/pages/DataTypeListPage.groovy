package org.modelcatalogue.core.pages

class DataTypeListPage extends ModelCataloguePage {

    static url = "#/catalogue/dataType/all"

    static at = {
        url == "#/catalogue/dataType/all"
    }

    static content={
        dataWizard          { $('div.basic-edit-modal-prompt') }
        classifications     { dataWizard.find('input[id=dataModel]') }
    }
}
