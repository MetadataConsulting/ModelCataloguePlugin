package org.modelcatalogue.core

import grails.util.GrailsNameUtils

abstract class PublishedElement extends CatalogueElement  {

    //version number - this gets iterated every time a new version is created from a finalized version
    Integer versionNumber = 1

    //status: once an object is finalized it cannot be changed
    //it's version number is updated and any subsequent update will
    //be mean that the element is superseded. We will provide a supersede function
    //to do this
    ElementStatus status = ElementStatus.DRAFT

    Date versionCreated = new Date()

    // id of the latest version
    PublishedElement latestVersion

    Set<Classification> classifications = []

    static searchable = {
        except = ['versionNumber']
    }

    static hasMany = [classifications: Classification]

    static belongsTo = Classification

    static mapping = {
        tablePerHierarchy false
    }

    String getClassifiedName() {
        if (!classifications) {
            return name
        }
        "$name (${classifications*.name.sort().join(', ')})"
    }

    @Override
    boolean isArchived() {
        if (!status) return false
        !status.modificable
    }
    static constraints = {

        status validator: { val , obj->
            if(!val){ return true}
            def oldStatus = null
            if(obj.version!=null){ oldStatus = obj.getPersistentValue('status')}
            if (obj.instanceOf(Model) && oldStatus != ElementStatus.FINALIZED && val == ElementStatus.FINALIZED) {
                if(!checkChildItemsFinalized(obj)) {
                    return ['org.modelcatalogue.core.PublishedElement.status.validator.children']
                }
            }
            return true
         }
        versionNumber bindable: false
        latestVersion bindable: false, nullable: true
    }

    static relationships = [
            incoming: [supersession: 'supersedes'],
            outgoing: [supersession: 'supersededBy']
    ]


    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, version: ${version}, status: ${status}, modelCatalogueId: ${modelCatalogueId}]"
    }

    Integer countVersions() {
        if (!latestVersion) {
            return 1
        }
        getClass().countByLatestVersion(latestVersion)
    }

    static protected Boolean checkChildItemsFinalized(Model model, Collection<Model> tree = []){

        if(model.contains.any{it.status!=ElementStatus.FINALIZED && it.status!=ElementStatus.DEPRECATED }) return false

        if(!tree.contains(model)) tree.add(model)

        def parentOf = model.parentOf
        if(parentOf) {
            return model.parentOf.any { Model md ->
                if (md.status != ElementStatus.FINALIZED && md.status != ElementStatus.DEPRECATED) return false
                if(!tree.contains(md)) {
                    if (!checkChildItemsFinalized(md, tree)) return false
                }
                return true
            }
        }
        return true
    }

    String getDefaultModelCatalogueId() {
        if (!grailsLinkGenerator) {
            return null
        }
        String resourceName = fixResourceName GrailsNameUtils.getPropertyName(getClass())
        grailsLinkGenerator.link(absolute: true, uri: "/catalogue/${resourceName}/${latestVersionId ?: id}.${versionNumber}")
    }

}
