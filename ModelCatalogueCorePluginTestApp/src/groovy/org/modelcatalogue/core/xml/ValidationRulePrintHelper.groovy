package org.modelcatalogue.core.xml

import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.ValidationRule

/** Helper for printing Validation Rules */
@Singleton
class ValidationRulePrintHelper extends CatalogueElementPrintHelper<ValidationRule> {

    @Override
    String getTopLevelName() {
        "validationRule"
    }

    @Override
    void processElement(Object markupBuilder, ValidationRule rule, PrintContext context, Relationship relationship) {
        super.processElement(markupBuilder, rule, context, relationship)
        if (rule.component) {
            markupBuilder.component rule.component
        }
        if (rule.ruleFocus) {
            markupBuilder.ruleFocus rule.ruleFocus
        }
        if (rule.trigger) {
            markupBuilder.trigger rule.trigger
        }
        if (rule.rule) {
            markupBuilder.rule rule.rule
        }
        if (rule.errorCondition) {
            markupBuilder.errorCondition rule.errorCondition
        }
        if (rule.issueRecord) {
            markupBuilder.issueRecord rule.issueRecord
        }
        if (rule.notification) {
            markupBuilder.notification rule.notification
        }
        if (rule.notificationTarget) {
            markupBuilder.notificationTarget rule.notificationTarget
        }
        for (Relationship rel in rule.involvesRelationships) {
            dispatch(markupBuilder, rel.destination, context, rel)
        }
    }
}
