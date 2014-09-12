package org.modelcatalogue.core.pages

import geb.Page

class ModalTreeViewPage extends ModelCataloguePage {

    static url = "#/catalogue/model/all"

    static at = {
        url == "#/catalogue/model/all"
    }
    static content = {

        addModelButton      { $('span.glyphicon.glyphicon-plus-sign') }

        modelWizard         { $('div.create-model-wizard') }

        name                { modelWizard.find('input[id=name]') }
        description         { modelWizard.find('textarea[id=description]') }
        saveButton          { modelWizard.find("button.btn-success") }
        exitButton          { $("#exit-wizard") }

    }
}
