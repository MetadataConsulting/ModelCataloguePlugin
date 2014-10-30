package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists

class ClassificationController<T> extends AbstractCatalogueElementController<Classification> {

    ClassificationController() {
        super(Classification)
    }

    protected bindRelations(Classification instance, Object objectToBind) {
        if (objectToBind.classifies != null) {
            for (domain in instance.classifies.findAll { !(it.id in objectToBind.classifies*.id) }) {
                instance.removeFromClassifies(domain)
                domain.removeFromClassifications(instance)
            }
            for (domain in objectToBind.classifies) {
                PublishedElement publishedElement = PublishedElement.get(domain.id as Long)
                instance.addToClassifies publishedElement
                publishedElement.addToClassifications instance
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
