package org.modelcatalogue.core

class DataModelPolicyController extends AbstractRestfulController<DataModelPolicy>{

    DataModelPolicyController dataModelPolicyController

    protected DataModelPolicy findById(long id) {
        dataModelPolicyController.findById(id)
    }

    @Override
    protected boolean allowSaveAndEdit() {
        modelCatalogueSecurityService.hasRole('ADMIN', getDataModel())
    }

    DataModelPolicyController() {
        super(DataModelPolicy)
    }
}
