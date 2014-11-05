package org.modelcatalogue.core

class ClassificationController<T> extends AbstractPublishedElementController<Classification> {

    ClassificationController() {
        super(Classification, false)
    }

    protected bindRelations(Classification instance, Object objectToBind) {
        if (objectToBind.classifies != null) {
            for (domain in instance.classifies.findAll { !(it.id in objectToBind.classifies*.id) }) {
                instance.removeFromClassifies(domain)
                domain.removeFromClassifications(instance)
            }
            for (domain in objectToBind.classifies) {
                CatalogueElement catalogueElement = CatalogueElement.get(domain.id as Long)
                instance.addToClassifies catalogueElement
                catalogueElement.addToClassifications instance
            }
        }
    }

    @Override
    protected getIncludeFields(){
        def fields = super.includeFields
        fields.removeAll(['classifies'])
        fields
    }

    @Override
    protected Classification createResource() {
        Classification instance = resource.newInstance()
        bindData instance, getObjectToBind(), [include: includeFields]
        instance
    }


}
