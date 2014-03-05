import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.RelationshipType

fixture{
    RT_broaderTerm(RelationshipType, name: "BroaderTerm",
            sourceClass: DataElement,
            destinationClass: DataElement,
            destinationToSource: "narrower term for",
            sourceToDestination: "broader term for")
}