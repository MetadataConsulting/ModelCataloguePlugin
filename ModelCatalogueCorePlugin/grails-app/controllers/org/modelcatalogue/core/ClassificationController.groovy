package org.modelcatalogue.core

import org.hibernate.Criteria
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.PublishedElements
import org.modelcatalogue.core.util.Relationships
import org.modelcatalogue.core.util.SimpleListWrapper


class ClassificationController<T> extends AbstractCatalogueElementController<Classification> {

    ClassificationController() {
        super(Classification)
    }

    def classifies(Integer max){
        handleParams(max)
        Classification classification = queryForResource(params.id)
        if (!classification) {
            notFound()
            return
        }

        reportCapableRespond new PublishedElements(list: Lists.fromCriteria(params, PublishedElement, "/${resourceName}/${params.id}/classifies", "classifies"){
            classifications{
                eq 'id', classification.id
            }
        })

    }

    @Override
    protected bindRelations(Classification instance) {
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
