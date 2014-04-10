package org.modelcatalogue.core

import grails.transaction.Transactional
import org.hibernate.Criteria

@Transactional
class DataArchitectService {

    Collection<DataElement> uninstantiatedDataElements(){
        def instantiation = RelationshipType.findByName("instantiation")
        def c = DataElement.createCriteria()
        def uninstantiatedDataElements = c.list {
            createAlias('outgoingRelationships', 'outgoingRelationships', Criteria.LEFT_JOIN)
            or{
            isEmpty("outgoingRelationships")
            ne('outgoingRelationships.relationshipType', instantiation)
            }

        }
        return uninstantiatedDataElements
    }

    Collection<DataElement> metadataKeyCheck(String key){

        def c = DataElement.createCriteria()

        def missingMetadataKey = c.list {
            createAlias('extensions', 'extensions', Criteria.LEFT_JOIN)
            or{
                isEmpty("extensions")
                ne('extensions.name', key)
            }

        }
        return missingMetadataKey
    }
}
