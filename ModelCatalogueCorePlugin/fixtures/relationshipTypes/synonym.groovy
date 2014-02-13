import uk.co.mc.core.DataElement
import uk.co.mc.core.RelationshipType

fixture{
    synonym(RelationshipType, name:"Synonym",
            sourceToDestination: "SynonymousWith",
            destinationToSource: "SynonymousWith",
            sourceClass: DataElement,
            destinationClass: DataElement)
}