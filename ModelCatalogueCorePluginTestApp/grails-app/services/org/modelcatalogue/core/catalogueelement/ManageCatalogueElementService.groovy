package org.modelcatalogue.core.catalogueelement

import groovy.transform.CompileStatic
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.util.DestinationClass
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.SearchParams

@CompileStatic
interface ManageCatalogueElementService {

    MetadataResponseEvent archive(Long catalogueElementId)

    MetadataResponseEvent restore(Long catalogueElementId)

    MetadataResponseEvent reorderInternal(RelationshipDirection direction,
                                          Long catalogueElementId,
                                          String type,
                                          Long movedId,
                                          Long currentId)

    MetadataResponseEvent addRelation(Long catalogueElementId,
                                      String type,
                                      Boolean outgoing,
                                      Object objectToBind,
                                      DestinationClass otherSide) throws ClassNotFoundException

    MetadataResponseEvent searchWithinRelationships(Long catalogueElementId,
                                                    String type,
                                                    RelationshipDirection direction,
                                                    SearchParams searchParams)
}
