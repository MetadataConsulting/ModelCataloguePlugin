import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.RelationshipType

fixture {
    RT_pubRelationship(RelationshipType, name: "pubRelationship", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "relates to", destinationToSource: "is relationship of")
}