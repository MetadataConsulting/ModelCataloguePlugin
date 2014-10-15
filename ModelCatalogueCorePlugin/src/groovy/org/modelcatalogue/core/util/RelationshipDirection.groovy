package org.modelcatalogue.core.util

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

enum RelationshipDirection {

    INCOMING {

        @Override
        DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type, Classification classification) {
            DetachedCriteria<Relationship> criteria = new DetachedCriteria<Relationship>(Relationship)
            criteria.eq('destination', element)
            if (!element.archived) {
                criteria.eq('archived', false)
            }
            if (type) {
                criteria.eq('relationshipType', type)
            }
            if (classification) {
                criteria.or {
                    eq('classification', classification)
                    isNull('classification')
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
    },
    OUTGOING {

        @Override
        DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type, Classification classification) {
            DetachedCriteria<Relationship> criteria = new DetachedCriteria<Relationship>(Relationship)
            criteria.eq('source', element)
            if (!element.archived) {
                criteria.eq('archived', false)
            }
            if (type) {
                criteria.eq('relationshipType', type)
            }
            if (classification) {
                criteria.or {
                    eq('classification', classification)
                    isNull('classification')
                }
            }

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

    },
    BOTH {
        @Override
        DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type, Classification classification) {
            DetachedCriteria<Relationship> criteria = new DetachedCriteria<Relationship>(Relationship)
            criteria.or {
                eq('source', element)
                eq('destination', element)
            }
            if (!element.archived) {
                criteria.eq('archived', false)
            }
            if (type) {
                criteria.eq('relationshipType', type)
            }
            if (classification) {
                criteria.or {
                    eq('classification', classification)
                    isNull('classification')
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
            owner == relationship.destination ? relationship.source : relationship.destination
        }

        @Override
        String getActionName() {
            "relationships"
        }
    }

    abstract DetachedCriteria<Relationship> composeWhere(CatalogueElement element, RelationshipType type, Classification classification)
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
