package org.modelcatalogue.core

abstract class PublishedElement extends ExtendibleElement  {

    //version number - this gets iterated every time a new version is created from a finalized version
    Integer versionNumber = 1

    //status: once an object is finalized it cannot be changed
    //it's version number is updated and any subsequent update will
    //be mean that the element is superceded. We will provide a supercede function
    //to do this
    PublishedElementStatus status = PublishedElementStatus.DRAFT

    static searchable = {
        except = ['versionNumber']
    }

    @Override
    boolean isArchived() {
        if (!status) return false
        !status.modificable
    }
    static constraints = {
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

    Integer countVersions() {
        getClass().countByModelCatalogueIdLike "$bareModelCatalogueId%"
    }

}
