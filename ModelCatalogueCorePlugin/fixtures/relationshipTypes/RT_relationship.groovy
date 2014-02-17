import uk.co.mc.core.CatalogueElement
import uk.co.mc.core.RelationshipType

fixture {
    RT_relationship(RelationshipType, name: "relationship", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "relates to", destinationToSource: "is relationship of")
}