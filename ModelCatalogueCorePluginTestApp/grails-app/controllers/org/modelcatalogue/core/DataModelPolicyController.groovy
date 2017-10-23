package org.modelcatalogue.core

import org.springframework.security.access.annotation.Secured

class DataModelPolicyController extends AbstractRestfulController<DataModelPolicy>{

    DataModelPolicyController dataModelPolicyController

    protected DataModelPolicy findById(long id) {
        dataModelPolicyController.findById(id)
    }


    DataModelPolicyController() {
        super(DataModelPolicy)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_SUPERVISOR'])
    def search(Integer max) {
        super.search(max)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_SUPERVISOR'])
    def index(Integer max) {
        super.index(max)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_SUPERVISOR'])
    def validate() {
        super.validate()
    }

    @Secured(['ROLE_ADMIN', 'ROLE_SUPERVISOR'])
    def save() {
        super.save()
    }

    @Secured(['ROLE_ADMIN', 'ROLE_SUPERVISOR'])
    def update() {
        super.update()
    }

    @Secured(['ROLE_ADMIN', 'ROLE_SUPERVISOR'])
    def delete() {
        super.delete()
    }
}
