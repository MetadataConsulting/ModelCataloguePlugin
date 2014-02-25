import uk.co.mc.core.CatalogueElement
import uk.co.mc.core.PublishedElement
import uk.co.mc.core.RelationshipType

fixture {
    RT_pubRelationship(RelationshipType, name: "pubRelationship", sourceClass: PublishedElement, destinationClass: PublishedElement, sourceToDestination: "relates to", destinationToSource: "is relationship of")
}