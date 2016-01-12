package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

enum RelationshipDirection {

    INCOMING {

        @Override
        DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type, DataModelFilter filter) {
            DetachedCriteria<Relationship> criteria = new DetachedCriteria<Relationship>(Relationship)
            criteria.join 'source'
            criteria.eq('destination', element)
            if (type) {
                criteria.eq('relationshipType', type)
            }

            if (filter) {
                criteria.or {
                    isNull 'dataModel'
                    and {
                        if (filter.excludes) {
                            criteria.not {
                                criteria.'in' 'dataModel.id', filter.excludes
                            }
                        }
                        if (filter.includes) {
                            criteria.'in'  'dataModel.id', filter.includes
                        }
                    }
                }
            }

            criteria
        }

        @Override
        String getDirection(CatalogueElement owner, Relationship relationship) {
            "destinationToSource"
        }

        @Override
        CatalogueElement getRelation(CatalogueElement owner, Relationship relationship) {
            relationship.source
        }

        @Override
        CatalogueElement getElement(CatalogueElement owner, Relationship relationship) {
            relationship.destination
        }

        @Override
        String getActionName() {
            "incoming"
        }

        @Override
        String getSortProperty() {
            "incomingIndex"
        }

        @Override
        Long getIndex(Relationship rel) {
            return rel.incomingIndex
        }

        @Override
        boolean isOwnedBy(CatalogueElement owner, Relationship relationship) {
            relationship?.destination == owner
        }

        @Override
        Long getMinIndexAfter(CatalogueElement owner, RelationshipType relationshipType, Long current) {
            Relationship.executeQuery("""
                select min(r.incomingIndex) from Relationship r
                where r.destination = :destination
                and r.relationshipType = :type
                and r.incomingIndex > :current
            """, [destination: owner, type: relationshipType, current: current])[0] as Long
        }
    },
    OUTGOING {

        @Override
        DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type, DataModelFilter filter) {
            DetachedCriteria<Relationship> criteria = new DetachedCriteria<Relationship>(Relationship)
            criteria.join 'destination'
            criteria.eq('source', element)
            if (type) {
                criteria.eq('relationshipType', type)
            }

            if (filter) {
                criteria.or {
                    isNull 'dataModel'
                    and {
                        if (filter.excludes) {
                            criteria.not {
                                criteria.'in' 'dataModel.id', filter.excludes
                            }
                        }
                        if (filter.includes) {
                            criteria.'in'  'dataModel.id', filter.includes
                        }
                    }
                }
            }

            criteria.sort('outgoingIndex')

            criteria
        }

        @Override
        String getDirection(CatalogueElement owner, Relationship relationship) {
            "sourceToDestination"
        }

        @Override
        CatalogueElement getRelation(CatalogueElement owner, Relationship relationship) {
            relationship.destination
        }

        @Override
        CatalogueElement getElement(CatalogueElement owner, Relationship relationship) {
            relationship.source
        }

        @Override
        String getActionName() {
            "outgoing"
        }

        @Override
        String getSortProperty() {
            "outgoingIndex"
        }

        @Override
        Long getIndex(Relationship rel) {
            return rel.outgoingIndex
        }

        @Override
        boolean isOwnedBy(CatalogueElement owner, Relationship relationship) {
            relationship?.source == owner
        }

        @Override
        Long getMinIndexAfter(CatalogueElement owner, RelationshipType relationshipType, Long current) {
            Relationship.executeQuery("""
                select min(r.outgoingIndex) from Relationship r
                where r.source = :source
                and r.relationshipType = :type
                and r.outgoingIndex > :current
            """, [source: owner, type: relationshipType, current: current])[0] as Long
        }
    },
    BOTH {
        @Override
        DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type, DataModelFilter filter) {
            DetachedCriteria<Relationship> criteria = new DetachedCriteria<Relationship>(Relationship)
            criteria.join 'source'
            criteria.join 'destination'
            criteria.or {
                eq('source', element)
                eq('destination', element)
            }
            if (type) {
                criteria.eq('relationshipType', type)
            }
            if (filter) {
                criteria.or {
                    isNull 'dataModel'
                    and {
                        if (filter.excludes) {
                            criteria.not {
                                criteria.'in' 'dataModel.id', filter.excludes
                            }
                        }
                        if (filter.includes) {
                            criteria.'in'  'dataModel.id', filter.includes
                        }
                    }
                }
            }

            criteria
        }

        @Override
        String getDirection(CatalogueElement owner, Relationship relationship) {
            owner == relationship.source ? 'sourceToDestination' : 'destinationToSource'
        }

        @Override
        CatalogueElement getRelation(CatalogueElement owner, Relationship relationship) {
            owner == relationship.source ? relationship.destination : relationship.source
        }

        @Override
        CatalogueElement getElement(CatalogueElement owner, Relationship relationship) {
            owner
        }

        @Override
        String getActionName() {
            "relationships"
        }

        @Override
        String getSortProperty() {
            "combinedIndex"
        }

        @Override
        Long getIndex(Relationship rel) {
            return rel.combinedIndex
        }

        @Override
        boolean isOwnedBy(CatalogueElement owner, Relationship relationship) {
            relationship?.destination == owner || relationship?.source == owner
        }


        @Override
        Long getMinIndexAfter(CatalogueElement owner, RelationshipType relationshipType, Long current) {
            Relationship.executeQuery("""
                select min(r.combinedIndex) from Relationship r
                where (r.source = :owner or r.destination = :owner)
                and r.relationshipType = :type
                and r.combinedIndex > :current
            """, [owner: owner, type: relationshipType, current: current])[0] as Long
        }
    }

    /* GROOVY-7415 abstract */ DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type, DataModelFilter filter) { throw new UnsupportedOperationException("Not Yet Implemented")}
    /* GROOVY-7415 abstract */ String getDirection(CatalogueElement owner, Relationship relationship) { throw new UnsupportedOperationException("Not Yet Implemented")}
    /* GROOVY-7415 abstract */ CatalogueElement getRelation(CatalogueElement owner, Relationship relationship) { throw new UnsupportedOperationException("Not Yet Implemented")}
    /* GROOVY-7415 abstract */ CatalogueElement getElement(CatalogueElement owner, Relationship relationship) { throw new UnsupportedOperationException("Not Yet Implemented")}
    /* GROOVY-7415 abstract */ String getActionName() { throw new UnsupportedOperationException("Not Yet Implemented")}
    /* GROOVY-7415 abstract */ String getSortProperty() { throw new UnsupportedOperationException("Not Yet Implemented")}
    /* GROOVY-7415 abstract */ Long getIndex(Relationship rel) { throw new UnsupportedOperationException("Not Yet Implemented")}
    /* GROOVY-7415 abstract */ boolean isOwnedBy(CatalogueElement owner, Relationship relationship) { throw new UnsupportedOperationException("Not Yet Implemented")}
    /* GROOVY-7415 abstract */ Long getMinIndexAfter(CatalogueElement owner, RelationshipType relationshipType, Long current) { throw new UnsupportedOperationException("Not Yet Implemented")}

    void setIndex(Relationship rel, Long value) {
        rel.setProperty(sortProperty, value)
    }
    static RelationshipDirection parse(String direction) {
        switch (direction) {
            case ['incoming', 'destinationToSource']: return INCOMING
            case ['outgoing', 'sourceToDestination']: return OUTGOING
            default: return BOTH
        }

    }

}
