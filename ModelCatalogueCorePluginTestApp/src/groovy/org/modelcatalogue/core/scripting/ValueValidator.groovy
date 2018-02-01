package org.modelcatalogue.core.scripting


/**
 * Helper class to validate Validating objects.
 */
class ValueValidator {

    static boolean validateRule(Validating ruleSource, Object x) {
        if (ruleSource.explicitRule && !evaluateRule(ruleSource.explicitRule, x)) {
            return false
        }

        for (Validating validating in ruleSource.bases) {
            if (!validateRule(validating, x)) {
                return false
            }
        }

        if (ruleSource.implicitRule) {
            return evaluateRule(ruleSource.implicitRule, x)
        }

        return true
    }

    static boolean evaluateRule(String rule, Object x) {
        def result = new SecuredRuleExecutor(ValidatingRuleScript, new Binding(x: x)).execute(rule)
        if (result != null && (!(result instanceof Boolean) || result.is(false))) {
            return result
        }
        return true
    }

}
