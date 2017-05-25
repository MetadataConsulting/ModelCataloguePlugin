package org.modelcatalogue.core.cytoscape.json

import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.ValidationRule

/**
 * Helper for printing ValidationRule to CytoscapeJSON. Still need to adapt.
 * Created by james on 28/04/2017.
 */
@Singleton
class ValidationRuleCJPrintHelper extends CatalogueElementCJPrintHelper<ValidationRule> {

    final String typeName = "ValidationRule"
    @Override
    void printElement(ValidationRule validationRule,
                      CJPrintContext context,
                      String typeName,
                      Relationship relationship = null,
                      boolean recursively = true) {

        super.printElement(validationRule, context, this.typeName, relationship, recursively)
        // The following comment block is code from the XML Printer. It should be adapted for JSON printing.
        // TODO: adapt rule fields for JSON printing.
        /*
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
        }*/
        if (recursively) {
            for (Relationship rel in validationRule.involvesRelationships) {
                dispatch(rel.destination, context, rel)
            }
        }

    }
}

