package org.modelcatalogue.core

abstract class PublishedElement extends CatalogueElement {

    //version number - this gets iterated every time a new version is created from a finalized version

    String modelCatalogueId
    Integer versionNumber = 1

    //status: once an object is finalized it cannot be changed
    //it's version number is updated and any subsequent update will
    //be mean that the element is superceded. We will provide a supercede function
    //to do this
    PublishedElementStatus status = PublishedElementStatus.DRAFT

    static searchable = true

    @Override
    boolean isArchived() {
        if (!status) return false
        !status.modificable
    }
    static constraints = {
        modelCatalogueId nullable:true, unique:true, maxSize: 255, matches: 'MC_\\d+_\\d+'
        status validator: { val , obj->
            if(!val){ return true}
            def oldStatus = null
            if(obj.version!=null){ oldStatus = obj.getPersistentValue('status')}
            if (oldStatus == PublishedElementStatus.FINALIZED && val != PublishedElementStatus.FINALIZED) {
                return ['validator.finalized']
            }
            return true
         }
    }

    static relationships = [
            incoming: [supersession: 'supersedes'],
            outgoing: [supersession: 'supersededBy']
    ]



    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, version: ${version}, status: ${status}]"
    }

    def afterInsert(){
        if(!getModelCatalogueId()){
            updateModelCatalogueId()
        }
    }

    def updateModelCatalogueId() {
        if (!getId()) { throw new IllegalStateException("Cannot assign the model catalogue id before the entity is persisted. Please, persist the entity first.") }
        modelCatalogueId = "MC_" + getId() + "_" + getVersionNumber()
    }


    def getBareModelCatalogueId() {
        afterInsert()
        (modelCatalogueId =~ /(MC_(.+))_(\d+)/)[0][1]
    }

}
