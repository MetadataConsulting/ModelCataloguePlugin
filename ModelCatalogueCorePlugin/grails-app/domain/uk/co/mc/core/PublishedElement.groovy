package uk.co.mc.core

abstract class PublishedElement extends CatalogueElement{

    enum Status {
        DRAFT, PENDING, FINALIZED, REMOVED
    }

    //version number - this gets iterated every time a new version is created from a finalized version

    Double versionNumber = 0.1

    //status: once an object is finalized it cannot be changed
    //it's version number is updated and any subsequent update will
    //be mean that the element is superceded. We will provide a supercede function
    //to do this
    Status status = Status.DRAFT

    static constraints = {

        status validator: { val , obj->
            if(!val){ return true}
            def oldStatus
            if(obj.version!=null){ oldStatus = obj.getPersistentValue('status')}
            if(oldStatus==Status.FINALIZED && val!=Status.FINALIZED){
                return ['validator.finalized']
            }
            return true
         }

    }


    /******************************************************************************************************************/
    /******   this method allows you to supercede any catalogue element, cloning it and creating ********
     ******   new relationships mirroring the old relationships  ******************************************************/
    /******************************************************************************************************************/

    def supercede(){


       /* def clonedElement = this.getClass().newInstance()
        def properties = new HashMap(this.properties)

        //remove any relations form the properties map
        properties.remove('relations')
        properties.remove('version')

        //copy properties over to new object
        clonedElement.properties = properties

        //increment versionNumber of new object and reset status to draft

        clonedElement.status = CatalogueElement.Status.DRAFT

        clonedElement.save(flush:true, failOnError: true)

        def relations = this.relations()

        relations.each{ relation ->


            Relationship.link(clonedElement, relation, relation.relationshipType)

        }

        def supersession = RelationshipType.findByName("Supersession")
        Relationship.link(clonedElement, this, supersession)


        // Grant the current user principal administrative permission
        if(springSecurityService.authentication.name!='admin'){
            aclUtilService.addPermission clonedElement, springSecurityService.authentication.name, BasePermission.ADMINISTRATION
        }

        //Grant admin user administrative permissions

        aclUtilService.addPermission clonedElement, 'admin', BasePermission.ADMINISTRATION

        return clonedElement
*/
    }


}
