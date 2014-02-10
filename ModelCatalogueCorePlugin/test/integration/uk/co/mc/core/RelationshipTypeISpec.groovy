package uk.co.mc.core

import spock.lang.Specification

/**
 * Created by ladin on 10.02.14.
 */
class RelationshipTypeISpec extends Specification {

    def "read by name returns read only instance"() {
        RelationshipType.initDefaultRelationshipTypes()
        RelationshipType containment = RelationshipType.readByName("containment")

        expect:
        containment

        when:
        containment.name = "foo"
        containment.save()

        then:
        IllegalStateException e = thrown(IllegalStateException)
        e
        e.message == "Cannot make an immutable entity modifiable."

    }

}
