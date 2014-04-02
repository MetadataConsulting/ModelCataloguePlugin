import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.RelationshipType

fixture{
    RT_antonym(RelationshipType, name:"Antonym",
            sourceToDestination: "AntonymousWith",
            destinationToSource: "AntonymousWith",
            sourceClass: DataElement,
            destinationClass: DataElement)
}