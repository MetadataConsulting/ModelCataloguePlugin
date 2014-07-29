package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.Relationships

class RelationshipsMarshaller extends ListWrapperMarshaller {

    RelationshipsMarshaller() {
        super(Relationships)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object relationsList) {
        def ret = super.prepareJsonMap(relationsList)
        ret.list = relationsList.items.collect {
            [id: it.id, type: it.relationshipType, ext: it.ext, relation: relationsList.direction.getRelation(relationsList.owner, it), direction: relationsList.direction.getDirection(relationsList.owner, it), removeLink: getDeleteLink(relationsList.owner, it)]
        }
        ret
    }

    @Override
    protected void buildItemsXml(Object relationsList, XML xml) {
        xml.build {
            for (Relationship rel in relationsList.items) {
                relationship(id: rel.id, removeLink: getDeleteLink(relationsList.owner, rel)) {
                    type rel.relationshipType
                    direction relationsList.direction.getDirection(relationsList.owner, rel)
                    relation(relationsList.direction.getRelation(relationsList.owner, rel))
                    if (rel.ext) {
                        extensions {
                            for (e in rel.ext.entrySet()) {
                                extension key: e.key, e.value
                            }
                        }
                    }
                }
            }
        }
    }

    protected static getDeleteLink(theOwner, Relationship rel) {
        "${theOwner.info.link}/${theOwner == rel.source ? 'outgoing' : 'incoming'}/${rel.relationshipType.name}"
    }
}
