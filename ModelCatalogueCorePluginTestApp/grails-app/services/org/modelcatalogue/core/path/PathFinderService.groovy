package org.modelcatalogue.core.path

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.persistence.CatalogueElementGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.PrimitiveTypeGormService

@CompileStatic
class PathFinderService {

    CatalogueElementGormService catalogueElementGormService

    PrimitiveTypeGormService primitiveTypeGormService

    DataElementGormService dataElementGormService

    List<String> findPath(CatalogueElement element, List<String> ret = [element.link]) {
        Long parentId = firstParentId(element)
        if (parentId) {
            CatalogueElement parent = catalogueElementGormService.findById(parentId)
            ret.add(0, parent.link)
            return findPath(parent, ret)
        }
        ret
    }

    @CompileDynamic
    Long firstParentIdAtDataClass(DataClass dataClass) {
        dataClass.getChildOf().find { it?.dataModel?.id  == dataClass?.dataModel?.id }?.id ?: dataClass?.dataModel?.id
    }

    Long firstParentIdAtMeasurementUnit(MeasurementUnit measurementUnit) {
        List<PrimitiveType> primitiveTypeList = primitiveTypeGormService.findAllByMeasurementUnit(measurementUnit)
        primitiveTypeList.find { PrimitiveType primitiveType ->
            primitiveType?.dataModel?.id == measurementUnit?.dataModel?.id
        }?.id ?: measurementUnit?.dataModel?.id
    }

    @CompileDynamic
    Long firstParentIdAtDataElement(DataElement dataElement) {
        dataElement.getContainedIn().find { it?.dataModel?.id == dataElement?.dataModel?.id }?.id ?: dataElement?.dataModel?.id
    }

    Long firstParentIdAtDataType(DataType dataType) {
        findAllRelatedDataElements(dataType).find { DataElement dataElement ->
            dataElement?.dataModel?.id == dataType?.dataModel?.id
        }?.id ?: dataType?.dataModel?.id
    }

    Long firstParentId(CatalogueElement element) {
        if ( element instanceof MeasurementUnit ) {
            return firstParentIdAtMeasurementUnit(element as MeasurementUnit)

        } else if ( element instanceof DataClass ) {
            return firstParentIdAtDataClass(element as DataClass)

        } else if ( element instanceof DataElement ) {
            return firstParentIdAtDataElement(element as DataElement)

        } else if ( element instanceof DataType ) {
            return firstParentIdAtDataType(element as DataType)
        }
        element?.dataModel?.id
    }

    List<DataElement> findAllRelatedDataElements(DataType dataType) {
        if (dataType.archived) {
            return dataElementGormService.findAllByDataType(dataType)
        }
        dataElementGormService.findAllByDataTypeAndStatusInList(dataType, [ElementStatus.FINALIZED, ElementStatus.DRAFT])
    }

}
