package org.modelcatalogue.core

class DataModelPolicyController extends AbstractRestfulController<DataModelPolicy>{

    @Override
    protected String getRoleForSaveAndEdit() {
        "ADMIN"
    }

    DataModelPolicyController() {
        super(DataModelPolicy)
    }

    @Override
    protected bindRelations(DataModelPolicy instance, boolean newVersion, Object objectToBind) {
        Set<DataModelPolicy> policies = (objectToBind.policies ?: []).collect { DataModelPolicy.get(it.id) } as Set<DataModelPolicy>
        Set<DataModelPolicy> existing = instance.policies
        (policies - existing).each {
            instance.addToPolicies(it)
        }
        (existing - policies).each {
            instance.removeFromPolicies(it)
        }

    }
}
