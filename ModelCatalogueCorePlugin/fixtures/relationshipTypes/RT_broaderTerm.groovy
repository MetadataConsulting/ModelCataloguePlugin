import uk.co.mc.core.DataElement
import uk.co.mc.core.RelationshipType

fixture{
    RT_broaderTerm(RelationshipType, name: "BroaderTerm",
            sourceClass: DataElement,
            destinationClass: DataElement,
            destinationToSource: "narrower term for",
            sourceToDestination: "broader term for")
}