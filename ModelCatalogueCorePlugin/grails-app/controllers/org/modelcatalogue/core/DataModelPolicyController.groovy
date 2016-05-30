package org.modelcatalogue.core

class DataModelPolicyController extends AbstractRestfulController<DataModelPolicy>{

    @Override
    protected String getRoleForSaveAndEdit() {
        "ADMIN"
    }

    DataModelPolicyController() {
        super(DataModelPolicy)
    }
}
