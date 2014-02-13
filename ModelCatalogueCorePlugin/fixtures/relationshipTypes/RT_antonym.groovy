import uk.co.mc.core.DataElement
import uk.co.mc.core.RelationshipType

fixture{
    RT_antonym(RelationshipType, name:"Antonym",
            sourceToDestination: "AntonymousWith",
            destinationToSource: "AntonymousWith",
            sourceClass: DataElement,
            destinationClass: DataElement)
}