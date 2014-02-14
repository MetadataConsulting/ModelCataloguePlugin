import uk.co.mc.core.DataElement
import uk.co.mc.core.RelationshipType

fixture{
    RT_relatedTerm(RelationshipType, name:"RelatedTerm",
            sourceToDestination: "relatedTo",
            destinationToSource: "relatedTo",
            sourceClass: DataElement,
            destinationClass: DataElement)
}