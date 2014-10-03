package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

enum RelationshipDirection {

    INCOMING {

        @Override
        DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type) {
            if (element.archived) {
                if (type) {
                    return Relationship.where {
                        destination == element && relationshipType == type
                    }
                }
                return Relationship.where {
                    destination == element
                }
            }
            if (type) {
                return Relationship.where {
                    archived == false && destination == element && relationshipType == type
                }
            }
            Relationship.where {
                archived == false && destination == element
            }
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
    },
    OUTGOING {

        @Override
        DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type) {
            if (element.archived) {
                if (type) {
                    return Relationship.where {
                        source == element && relationshipType == type
                    }
                }
                return Relationship.where {
                    source == element
                }
            }
            if (type) {
                return Relationship.where {
                    archived == false && source == element && relationshipType == type
                }
            }
            Relationship.where {
                archived == false && source == element
            }
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

    },
    BOTH {
        @Override
        DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type) {
            if (element.archived) {
                if (type) {
                    return Relationship.where {
                        (source == element || destination == element) && relationshipType == type
                    }
                }
                return Relationship.where {
                    (source == element || destination == element)
                }
            }
            if (type) {
                return Relationship.where {
                    archived == false && (source == element || destination == element) && relationshipType == type
                }
            }
            Relationship.where {
                archived == false && (source == element || destination == element)
            }
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
            owner == relationship.destination ? relationship.source : relationship.destination
        }

        @Override
        String getActionName() {
            "relationships"
        }
    }

    abstract DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type)
    abstract String getDirection(CatalogueElement owner, Relationship relationship)
    abstract CatalogueElement getRelation(CatalogueElement owner, Relationship relationship)
    abstract CatalogueElement getElement(CatalogueElement owner, Relationship relationship)
    abstract String getActionName()

    static RelationshipDirection parse(String direction) {
        switch (direction) {
            case ['incoming', 'destinationToSource']: return INCOMING
            case ['outgoing', 'sourceToDestination']: return OUTGOING
            default: return BOTH
        }

    }

}
