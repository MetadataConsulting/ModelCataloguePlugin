package org.modelcatalogue.core.pages

import geb.Page

class ModalTreeViewPage extends ModelCataloguePage {

    static url = "#/catalogue/model/all"

    static at = {
        url == "#/catalogue/model/all"
    }
    static content = {

        addModelButton      { $('span.glyphicon.glyphicon-plus-sign') }

        basicEditDialog     { $('div.basic-edit-modal-prompt') }

        name                { basicEditDialog.find('input[id=name]') }
        description         { basicEditDialog.find('textarea[id=description]') }
        saveButton          { basicEditDialog.find("button.btn-success") }

    }
}
