import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.RelationshipType

fixture {
    RT_relationship(RelationshipType, name: "relationship", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "relates to", destinationToSource: "is relationship of")
}