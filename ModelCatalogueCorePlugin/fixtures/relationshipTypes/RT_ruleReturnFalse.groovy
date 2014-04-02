import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.RelationshipType

fixture{
    RT_ruleReturnFalse(RelationshipType, name: "falseRuleReturn",
            sourceClass: DataElement,
            destinationClass: DataElement,
            destinationToSource: "narrower term for",
            sourceToDestination: "broader term for",
            rule: "return false")
}