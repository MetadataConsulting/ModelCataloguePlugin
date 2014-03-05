import org.modelcatalogue.core.PublishedElement
import org.modelcatalogue.core.RelationshipType

fixture {
    RT_pubRelationship(RelationshipType, name: "pubRelationship", sourceClass: PublishedElement, destinationClass: PublishedElement, sourceToDestination: "relates to", destinationToSource: "is relationship of")
}