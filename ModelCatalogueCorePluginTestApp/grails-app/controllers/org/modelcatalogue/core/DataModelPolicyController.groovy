package org.modelcatalogue.core

import org.modelcatalogue.core.persistence.DataModelPolicyGormService

class DataModelPolicyController extends AbstractRestfulController<DataModelPolicy>{

    DataModelPolicyGormService dataModelPolicyGormService

    protected DataModelPolicy findById(long id) {
        dataModelPolicyGormService.findById(id)
    }

    DataModelPolicyController() {
        super(DataModelPolicy)
    }
}
