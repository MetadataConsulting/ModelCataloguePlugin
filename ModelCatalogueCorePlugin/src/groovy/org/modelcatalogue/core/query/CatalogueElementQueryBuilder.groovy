package org.modelcatalogue.core.query

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.RelationshipType

class CatalogueElementQueryBuilder<T extends CatalogueElement> {

    // TODO: classification filter

    final DetachedCriteria<T> criteria

    private CatalogueElementQueryBuilder(Class<T> resource) {
        this.criteria = new DetachedCriteria(resource)
    }

    static <T extends CatalogueElement> CatalogueElementQueryBuilder<T> create(Class<T> resource, @DelegatesTo(CatalogueElementQueryBuilder) Closure query = {}) {
        CatalogueElementQueryBuilder<T> builder = new CatalogueElementQueryBuilder<T>(resource)
        builder.with query
        builder
    }


    CatalogueElementQueryBuilder<T> property(@DelegatesTo(DetachedCriteria) Closure criteriaBuilder) {
        criteria.with criteriaBuilder
        this
    }

    CatalogueElementQueryBuilder<T> metadata(@DelegatesTo(DetachedCriteria) Closure criteriaBuilder) {
        criteria.extensions criteriaBuilder
        this
    }

    CatalogueElementQueryBuilder<T> incoming(RelationshipType type, Classification classification = null, @DelegatesTo(DetachedCriteria) Closure criteriaBuilder) {
        criteria.incomingRelationships {
            eq 'relationshipType', type
            source criteriaBuilder
            if (classification) {
                or {
                    eq 'classification', classification
                    isNull 'classification'
                }
            }
        }
        this
    }

    CatalogueElementQueryBuilder<T> outgoing(RelationshipType type, Classification classification = null, @DelegatesTo(DetachedCriteria) Closure criteriaBuilder) {
        criteria.outgoingRelationships {
            eq 'relationshipType', type
            destination criteriaBuilder
            if (classification) {
                or {
                    eq 'classification', classification
                    isNull 'classification'
                }
            }
        }
        this
    }

}
