package uk.co.mc.core.util

import uk.co.mc.core.Relationship

/**
 * Wrapper used for easier marshalling of relations result lists
 */
class Relationships {
    String next
    String previous
    int total
    List<Relationship> relationships
    String direction
}
