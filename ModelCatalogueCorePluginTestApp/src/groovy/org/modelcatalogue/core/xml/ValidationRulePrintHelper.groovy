package org.modelcatalogue.core.xml

import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.ValidationRule

@Singleton
class ValidationRulePrintHelper extends CatalogueElementPrintHelper<ValidationRule> {

    @Override
    String getTopLevelName() {
        "validationRule"
    }

    @Override
    void processElements(Object mkp, ValidationRule rule, PrintContext context, Relationship relationship) {
        super.processElements(mkp, rule, context, relationship)
        if (rule.component) {
            mkp.component rule.component
        }
        if (rule.ruleFocus) {
            mkp.ruleFocus rule.ruleFocus
        }
        if (rule.trigger) {
            mkp.trigger rule.trigger
        }
        if (rule.rule) {
            mkp.rule rule.rule
        }
        if (rule.errorCondition) {
            mkp.errorCondition rule.errorCondition
        }
        if (rule.issueRecord) {
            mkp.issueRecord rule.issueRecord
        }
        if (rule.notification) {
            mkp.notification rule.notification
        }
        if (rule.notificationTarget) {
            mkp.notificationTarget rule.notificationTarget
        }
        for (Relationship rel in rule.involvesRelationships) {
            printElement(mkp, rel.destination, context, rel)
        }
    }
}
