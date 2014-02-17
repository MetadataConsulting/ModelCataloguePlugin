package uk.co.mc.core

class ConceptualDomainController extends CatalogueElementController<ConceptualDomain>{

    static responseFormats = ['json', 'xml']

    ConceptualDomainController() {
        super(ConceptualDomain)
    }

}
