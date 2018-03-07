package org.modelcatalogue.core

import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.DataModelPolicyGormService
import org.modelcatalogue.core.persistence.RelationshipGormService
import org.modelcatalogue.core.persistence.RelationshipTypeGormService
import org.modelcatalogue.core.util.IdName

@Slf4j
@CompileStatic
class DataModelCreateService {

    DataModelPolicyGormService dataModelPolicyGormService
    DataModelGormService dataModelGormService
    RelationshipTypeGormService relationshipTypeGormService
    RelationshipGormService relationshipGormService

    List<IdName> findAllDataModelPolicyGormService() {
        dataModelPolicyGormService.findAllToBean(IdName) as List<IdName>
    }

    List<IdName> findAllDataModelWhichMaybeImported() {
        dataModelGormService.findAllInStatus(ElementStatus.FINALIZED).collect { DataModel dataModel ->
            new IdName(id: dataModel.id, name: dataModel.name)
        }
    }

    List<Long> findDefaultDataModelPolicyIds() {
        List<DataModelPolicy> dataModelPolicyList = []
        DataModelPolicy dataModelPolicy = dataModelPolicyGormService.findByName('Unique of Kind')
        if ( dataModelPolicy ) {
            dataModelPolicyList << dataModelPolicy
        }
        dataModelPolicyList*.id
    }

    @Transactional(readOnly = true)
    boolean isDataModelUsedAlready(String nameParam) {
        DataModel.where { name == nameParam }.count() as Boolean
    }

    @Transactional
    def createRelationships(DataModel sourceDataModel, List<Long> destinationIds) {
        if ( sourceDataModel && destinationIds ) {
            final String type = 'import'
            RelationshipType relationshipType = relationshipTypeGormService.findByName(type)
            if ( relationshipType ) {
                for ( Long dataModelId : destinationIds ) {
                    DataModel destination = dataModelGormService.findById(dataModelId)
                    relationshipGormService.save(sourceDataModel, destination, relationshipType)
                }
            }
        }
    }

    @CompileDynamic
    @Transactional
    DataModel saveDataModel(DataModel dataModel, List<Long> policyIds, List<Long> dataModelIds) {
        List<DataModelPolicy> policies = dataModelPolicyGormService.findAllByIds(policyIds)
        if ( policies ) {
            for (DataModelPolicy policy : policies) {
                dataModel.addToPolicies(policy)
            }
        }
        if ( !dataModel.save() ) {
            log.error('Error while saving data Model')
        }
        dataModel
    }
}
