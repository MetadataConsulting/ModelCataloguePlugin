package org.modelcatalogue.core.pages

/**
 * Created by david on 04/11/14.
 */
class ClassificationListPage extends ModelCataloguePage {

    static url = "#/catalogue/classification/all"

    static at = {
        url == "#/catalogue/classification/all"
    }

    static content={
        classificationWizzard { $('div.create-classification-wizard') }

        name                { classificationWizzard.find('#name') }
        description         { classificationWizzard.find('#description') }
        modelCatalogueId    { classificationWizzard.find('#modelCatalogueId') }

        stepPrevious        { $("#step-previous") }
        stepElements        { $("#step-elements") }
        stepNext            { $("#step-next") }
        stepFinish          { $("#step-finish") }
        exitButton          { $("#exit-wizard") }
    }
}
