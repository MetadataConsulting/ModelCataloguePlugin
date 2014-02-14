package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import grails.converters.XML
import uk.co.mc.core.Relationship

/**
 * Created by ladin on 14.02.14.
 */
class RelationshipMarshallers implements MarshallersProvider {

    static registered = false

    void register() {
        if (registered) return

        JSON.registerObjectMarshaller(Relationship) { Relationship rel ->
            [
                    id: rel.id,
                    source: rel.source.info,
                    destination: rel.destination.info,
                    type: rel.relationshipType.info
            ]
        }
        XML.registerObjectMarshaller(Relationship) { Relationship rel, XML xml ->
            def renderInfo = { String what, Map info ->
                xml.build {
                    "$what" {
                        for (e in info.entrySet()) {
                            if (e.key == 'id') {
                                xml.attribute('id', "$e.value")
                            } else {
                                "$e.key" e.value
                            }
                        }
                    }
                }
            }
            xml.attribute('id', "$rel.id")
            xml.build {
                renderInfo('source', rel.source.info)
                renderInfo('destination', rel.destination.info)
                renderInfo('type', rel.relationshipType.info)
            }
        }
        registered = true
    }
}
