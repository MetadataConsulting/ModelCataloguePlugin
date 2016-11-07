package org.modelcatalogue.core

class DataModelPolicyController extends AbstractRestfulController<DataModelPolicy>{

    @Override
    protected boolean allowSaveAndEdit() {
        modelCatalogueSecurityService.hasRole('ADMIN')
    }

    DataModelPolicyController() {
        super(DataModelPolicy)
    }
}
