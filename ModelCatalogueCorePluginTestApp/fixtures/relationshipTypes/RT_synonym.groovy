import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.RelationshipType

fixture{
    RT_synonym(RelationshipType, name:"Synonym",
            sourceToDestination: "SynonymousWith",
            destinationToSource: "SynonymousWith",
            sourceClass: DataElement,
            destinationClass: DataElement)
}