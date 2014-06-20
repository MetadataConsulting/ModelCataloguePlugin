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

    static searchable = {
        modelCatalogueId boost:10
        except = ['versionNumber']
    }

    @Override
    boolean isArchived() {
        if (!status) return false
        !status.modificable
    }
    static constraints = {
        modelCatalogueId nullable:true, unique:true, maxSize: 255, matches: '(?i)MC_([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12})_\\d+'
//        TODO we need to think about what restrictions we put on publishing elements
//        status validator: { val , obj->
//            if(!val){ return true}
//            def oldStatus = null
//            if(obj.version!=null){ oldStatus = obj.getPersistentValue('status')}
//            if (oldStatus == PublishedElementStatus.FINALIZED && val != PublishedElementStatus.FINALIZED) {
//                return ['validator.finalized']
//            }
//            return true
//         }
    }

    static relationships = [
            incoming: [supersession: 'supersedes'],
            outgoing: [supersession: 'supersededBy']
    ]



    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, version: ${version}, status: ${status}, modelCatalogueId: ${modelCatalogueId}]"
    }

    def afterInsert(){
        if(!getModelCatalogueId()) {
            createModelCatalogueId()
        }
    }

    def createModelCatalogueId(){
        modelCatalogueId = "MC_" + UUID.randomUUID() + "_" + 1
    }


    def updateModelCatalogueId() {
        if(getModelCatalogueId()) {
            def newCatalogueId = modelCatalogueId.split("_")
            newCatalogueId[-1] = newCatalogueId.last().toInteger() + 1
            modelCatalogueId = newCatalogueId.join("_")
        }else{
            createModelCatalogueId()
        }
    }


    def getBareModelCatalogueId() {
        afterInsert()
        (modelCatalogueId =~ /(?i)(MC_([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12}))/)[0][1]
    }

    Integer countVersions() {
        getClass().countByModelCatalogueIdLike "$bareModelCatalogueId%"
    }

}
