package org.modelcatalogue.core.pages

class ModalTreeViewPage extends ModelCataloguePage {

    static url = "#/catalogue/model/all"

    static at = {
        url == "#/catalogue/model/all"
    }
    static content = {

        addModelButton      { $('span.glyphicon.glyphicon-plus-sign') }

        modelWizard         { $('div.create-model-wizard') }

        name                { modelWizard.find('#name') }
        description         { modelWizard.find('#description') }
        modelCatalogueId    { modelWizard.find('#modelCatalogueId') }

        stepTitle           { modelWizard.find('h4')}

        stepPrevious        { $("#step-previous") }
        stepModel           { $("#step-model") }
        stepMetadata        { $("#step-metadata") }
        stepParents         { $("#step-parents") }
        stepChildren        { $("#step-children") }
        stepElements        { $("#step-elements") }
        stepClassifications { $("#step-classifications") }
        stepNext            { $("#step-next") }
        stepFinish          { $("#step-finish") }


        saveButton          { modelWizard.find("button.btn-success") }
        exitButton          { $("#exit-wizard") }

        type                { modalDialog.find('#type') }
        element             { modalDialog.find('#element') }
    }
}
