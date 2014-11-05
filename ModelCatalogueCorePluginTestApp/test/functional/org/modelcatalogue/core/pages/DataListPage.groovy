package org.modelcatalogue.core.pages

/**
 * Created by david on 04/11/14.
 */
class DataListPage extends ModelCataloguePage {

    static url = "#/catalogue/dataElement/all"


    static content={
        name {$("input", id:"entry_0")}
        emailAddress {$("input", id:"entry_1")}
    }
}
