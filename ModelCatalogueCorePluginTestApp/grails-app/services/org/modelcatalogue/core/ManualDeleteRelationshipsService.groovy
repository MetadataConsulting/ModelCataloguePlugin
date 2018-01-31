package org.modelcatalogue.core

import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.persistence.CatalogueElementGormService
import org.modelcatalogue.core.persistence.ColumnTransformationDefinitionGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.PrimitiveTypeGormService
import org.modelcatalogue.core.persistence.ReferenceTypeGormService
import org.modelcatalogue.core.security.User

@CompileStatic
class ManualDeleteRelationshipsService {

    CatalogueElementGormService catalogueElementGormService

    PrimitiveTypeGormService primitiveTypeGormService

    ColumnTransformationDefinitionGormService columnTransformationDefinitionGormService

    ReferenceTypeGormService referenceTypeGormService

    DataElementGormService dataElementGormService

    @Transactional
    List<CatalogueElementDeleteBlocker> manualDeleteRelationships(DataModel instance) {

        // inspect declarations
        List<CatalogueElementDeleteBlocker> catalogueElementDeleteBlockerList = []
        List<CatalogueElement> catalogueElementList = catalogueElementGormService.findAllByDataModel(instance)
        for ( CatalogueElement catalogueElement : catalogueElementList ) {
            // check the element for the same - if manual delete is needed
            List<DeleteBlocker> deleteBlockerList = manualDeleteRelationshipsAtCatalogueElement(catalogueElement, instance)
            if ( deleteBlockerList ) {
                catalogueElementDeleteBlockerList << new CatalogueElementDeleteBlocker(elementTargetedToDeletion: catalogueElement, deleteBlockerList: deleteBlockerList)
            }
        }
        // inspect relationships
        List<DeleteBlocker> relationshipDeleteBlockers = []
        for ( Relationship relationship : instance.outgoingRelationships ) {
            if ( relationship.dataModel && relationship.dataModel != instance ) {
                relationshipDeleteBlockers << new DeleteBlocker(elementPreventsDeletion: relationship, reason: DeleteBlockerReason.OUTGOING_RELATION_AT_DIFFERENT_DATAMODEL)
            }
        }
        for ( Relationship relationship : instance.incomingRelationships ) {
            if ( relationship.dataModel && relationship.dataModel != instance ) {
                relationshipDeleteBlockers << new DeleteBlocker(elementPreventsDeletion: relationship, reason: DeleteBlockerReason.INCOMING_RELATION_AT_DIFFERENT_DATAMODEL)
            }
        }
        if ( relationshipDeleteBlockers ) {
            catalogueElementDeleteBlockerList << new CatalogueElementDeleteBlocker(elementTargetedToDeletion: instance, deleteBlockerList: relationshipDeleteBlockers)
        }
        catalogueElementDeleteBlockerList

    }

    @Transactional
    List<DeleteBlocker> manualDeleteRelationshipsAtCatalogueElement(CatalogueElement catalogueElement, DataModel toBeDeleted) {

        if (
                ( catalogueElement instanceof Asset ) ||
                ( catalogueElement instanceof Tag ) ||
                ( catalogueElement instanceof ValidationRule ) ||
                ( catalogueElement instanceof User )
        ) {
            return [] as List<DeleteBlocker>
        }
        if ( catalogueElement instanceof PrimitiveType ) {
        return manualDeleteRelationshipsAtPrimitiveType(catalogueElement as PrimitiveType, toBeDeleted)

        } else if ( catalogueElement instanceof DataType ) {
            return manualDeleteRelationshipsAtDataType(catalogueElement as DataType, toBeDeleted)

        } else if ( catalogueElement instanceof DataClass ) {
            return manualDeleteRelationshipsAtDataClass(catalogueElement as DataClass, toBeDeleted)

        } else if ( catalogueElement instanceof MeasurementUnit ) {
            return manualDeleteRelationshipsAtMeasurementUnit(catalogueElement as MeasurementUnit, toBeDeleted)

        } else if ( catalogueElement instanceof DataElement ) {
            return manualDeleteRelationshipsAtDataElement(catalogueElement as DataElement, toBeDeleted)

        }
        []
    }

    @Transactional
    List<DeleteBlocker> manualDeleteRelationshipsAtMeasurementUnit(MeasurementUnit measurementUnit, DataModel toBeDeleted) {

        List<DeleteBlocker> deleteBlockerList = []

        List<PrimitiveType> primitiveTypeList = primitiveTypeGormService.findAllByMeasurementUnit(measurementUnit)
        for ( PrimitiveType primitiveType : primitiveTypeList ) {
            if (toBeDeleted) {
                if (primitiveType.dataModel != measurementUnit.dataModel) {
                    deleteBlockerList << new DeleteBlocker(elementPreventsDeletion: primitiveType, reason: DeleteBlockerReason.BELONGS_TO_DIFFERENT_DATAMODEL)
                }
            } else {
                deleteBlockerList << new DeleteBlocker(elementPreventsDeletion: primitiveType, reason: DeleteBlockerReason.CATALOGUE_ELEMENT)
            }
        }

        deleteBlockerList
    }

    List<DeleteBlocker> manualDeleteRelationshipsAtPrimitiveType(PrimitiveType primitiveType, DataModel toBeDeleted) {
        [] as List<DeleteBlocker>
    }

    @Transactional
    List<DeleteBlocker> manualDeleteRelationshipsAtDataClass(DataClass dataClass, DataModel toBeDeleted) {
        List<DeleteBlocker> deleteBlockerList = []

        List<ReferenceType> referenceTypeList = referenceTypeGormService.findAllByDataClass(dataClass)
        for (  ReferenceType referenceType : referenceTypeList ) {
            if (toBeDeleted) {
                if (referenceType.dataModel != toBeDeleted) {
                    deleteBlockerList << new DeleteBlocker(elementPreventsDeletion: referenceType, reason: DeleteBlockerReason.BELONGS_TO_DIFFERENT_DATAMODEL)
                }
            } else {
                deleteBlockerList << new DeleteBlocker(elementPreventsDeletion: referenceType, reason: DeleteBlockerReason.CATALOGUE_ELEMENT)
            }
        }
        deleteBlockerList
    }

    @Transactional
    List<DeleteBlocker> manualDeleteRelationshipsAtDataElement(DataElement dataElement, DataModel toBeDeleted) {
        List<DeleteBlocker> deleteBlockerList = []

        List<ColumnTransformationDefinition> columnTransformationDefinitionList = columnTransformationDefinitionGormService.findAllBySourceOrDestination(dataElement, dataElement)
        for ( ColumnTransformationDefinition columnTransformationDefinition : columnTransformationDefinitionList ) {
            if (toBeDeleted) {
                // source is in different data model
                if (columnTransformationDefinition.source && columnTransformationDefinition.source.dataModel != toBeDeleted) {
                    deleteBlockerList << new DeleteBlocker(elementPreventsDeletion: columnTransformationDefinition, reason: DeleteBlockerReason.SOURCE_BELONGS_TO_DIFFERENT_DATAMODEL)

                } else if (columnTransformationDefinition.destination && columnTransformationDefinition.destination.dataModel != toBeDeleted) {
                    deleteBlockerList << new DeleteBlocker(elementPreventsDeletion: columnTransformationDefinition, reason: DeleteBlockerReason.DESTINATION_BELONGS_TO_DIFFERENT_DATAMODEL)
                }
            } else {
                deleteBlockerList << new DeleteBlocker(elementPreventsDeletion: columnTransformationDefinition, reason: DeleteBlockerReason.CATALOGUE_ELEMENT)
            }
        }
        deleteBlockerList
    }

    @Transactional
    List<DeleteBlocker> manualDeleteRelationshipsAtDataType(DataType dataType, DataModel toBeDeleted) {
        List<DeleteBlocker> deleteBlockerList = []

        List<DataElement> dataElementList = dataElementGormService.findAllByDataType(dataType)
        for ( DataElement dataElement : dataElementList) {
            if (toBeDeleted) {
                if (dataElement.dataModel != dataType.dataModel) {
                    deleteBlockerList << new DeleteBlocker(elementPreventsDeletion: dataElement, reason: DeleteBlockerReason.BELONGS_TO_DIFFERENT_DATAMODEL)
                }
            } else {
                deleteBlockerList << new DeleteBlocker(elementPreventsDeletion: dataElement, reason: DeleteBlockerReason.CATALOGUE_ELEMENT)
            }
        }
        deleteBlockerList
    }

}
