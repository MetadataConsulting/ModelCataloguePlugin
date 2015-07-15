package org.modelcatalogue.core.pages

/**
 * Created by david on 04/11/14.
 */
class DataModelListPage extends ModelCataloguePage {

    static url = "#/catalogue/dataModel/all"

    static at = {
        url == "#/catalogue/dataModel/all"
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
