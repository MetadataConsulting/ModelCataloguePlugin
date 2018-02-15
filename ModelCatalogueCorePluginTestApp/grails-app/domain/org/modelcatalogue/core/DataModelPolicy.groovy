package org.modelcatalogue.core

import org.modelcatalogue.core.policy.Policy
import org.modelcatalogue.core.policy.PolicyBuilder
import org.modelcatalogue.core.policy.PolicyBuilderScript
import org.modelcatalogue.core.rx.ErrorSubscriber
import org.modelcatalogue.core.scripting.SecuredRuleExecutor

class DataModelPolicy {

    def modelCatalogueSearchService

    // cached policy
    Policy policy

    String name
    String policyText

    static transients = ['policy']

    static constraints = {
        name size: 1..255, unique: true
        policyText size: 1..10000, validator: { val,obj ->
            if(!val){return true}
            SecuredRuleExecutor.ValidationResult result = new SecuredRuleExecutor(PolicyBuilderScript, new Binding()).validate(val)
            result ? true : ['wontCompile', result.compilationFailedMessage]
        }
    }

    Policy getPolicy() {
        if (policy == null) {
            policy = PolicyBuilder.build(policyText)
        }
        return policy
    }

    void afterUpdate() {
        policy = null
        modelCatalogueSearchService.index(this).subscribe(ErrorSubscriber.create("Exception indexing data model policy after update"))
    }

    void afterInsert() {
        policy = null
        modelCatalogueSearchService.index(this).subscribe(ErrorSubscriber.create("Exception indexing data model policy after insert"))
    }

    void beforeDelete() {
        modelCatalogueSearchService.unindex(this).subscribe(ErrorSubscriber.create("Exception unindexing data model policy before delete"))
    }



}
