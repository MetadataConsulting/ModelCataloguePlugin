package uk.co.mc.core

class ValueDomainController extends CatalogueElementController<ValueDomain>{

    static responseFormats = ['json', 'xml']

    ValueDomainController() {
        super(ValueDomain)
    }

}
