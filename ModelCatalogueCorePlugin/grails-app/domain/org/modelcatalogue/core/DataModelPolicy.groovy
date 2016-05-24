package org.modelcatalogue.core

import org.modelcatalogue.core.policy.Policy
import org.modelcatalogue.core.policy.PolicyBuilder
import org.modelcatalogue.core.policy.PolicyBuilderScript
import org.modelcatalogue.core.util.SecuredRuleExecutor

class DataModelPolicy {

    // cached policy
    Policy policy

    String name
    String policyText

    static hasMany = [dataModel: DataModel]
    static transients = ['policy']
    static belongsTo = DataModel

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
    }

    void afterInsert() {
        policy = null
    }

}
