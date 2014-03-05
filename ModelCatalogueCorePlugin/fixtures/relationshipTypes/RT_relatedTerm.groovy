import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.RelationshipType

fixture{
    RT_relatedTerm(RelationshipType, name:"RelatedTerm",
            sourceToDestination: "relatedTo",
            destinationToSource: "relatedTo",
            sourceClass: DataElement,
            destinationClass: DataElement)

}