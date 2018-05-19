package org.modelcatalogue.core.d3viewUtils

import com.google.common.collect.ImmutableSet
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataElementService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.lists.ListWithTotalAndType

@Transactional
/**
 *  Produces D3 hierarchical layout json augmented with model catalogue specific fields.
 *
 */
class D3ViewUtilsService {

    DataModelService dataModelService
    DataClassService dataClassService
    DataElementService dataElementService
    GrailsApplication grailsApplication

    static String lowerCamelCaseDomainName(Class clazz) {
        MetadataDomain.lowerCamelCaseDomainName(MetadataDomain.ofClass(clazz))
    }

    String angularLink(Long dataModelId, Long id, Class clazz) {
        return "${grailsApplication.config.grails.serverURL}/#/$dataModelId/${lowerCamelCaseDomainName(clazz)}/$id"
    }

    /**
     * No children; load them later
     * @param dataModel
     * @return D3JSON
     */
    D3JSON dataModelD3Json(DataModel dataModel) {

        D3JSON dataModelJson = new D3JSON(
            name: dataModel.name,
            id: dataModel.id,
            angularLink: angularLink(dataModel.id, dataModel.id, DataModel),
            type: lowerCamelCaseDomainName(DataModel)
        )
    }

    /**
     * Get "children" of data model for display (top level data classes and data elements
     * @param dataModel
     * @return List<D3JSON>
     */
    List<D3JSON> dataModelD3JsonChildren(DataModel dataModel) {
        DataModelFilter filter = DataModelFilter.create(ImmutableSet.<DataModel> of(dataModel), ImmutableSet.<DataModel> of())
        Map<String, Integer> stats = dataModelService.getStatistics(filter)


        ListWithTotalAndType<DataClass> dataClasses = dataClassService.getTopLevelDataClasses(filter, [toplevel: true, status: dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''])
        List<DataElement> unDataClassedDataElements = DataElement.findAllByDataModel(dataModel).findAll{
            dataElementService.countDataClassesOf(it) == 0
        }

        def dataClassChildrenJson = dataClasses.items.collect {dataClass ->
            dataClassD3Json(dataClass, 0)

        }
        def dataElementChildrenJson = unDataClassedDataElements.collect {
            dataElementD3Json(it)
        }



        return dataClassChildrenJson + dataElementChildrenJson
        // TODO: Handle case where there are just DataTypes listed not connected to any DataElements

    }

    D3JSON dataElementD3Json(DataElement dataElement) {
        D3JSON ret = new D3JSON(

            name: dataElement.name,
            angularLink: angularLink(dataElement.dataModel.id, dataElement.id, DataElement),
            type: lowerCamelCaseDomainName(DataElement)
        )

        if (dataElement.dataType) {
            ret.children = [dataTypeD3Json(dataElement.dataType)]
        }

        return ret
    }

    D3JSON dataTypeD3Json(DataType dataType) {
        D3JSON ret = new D3JSON(
            name: dataType.name,
            angularLink: angularLink(dataType.dataModel.id, dataType.id, DataType),
            loadedChildren: true, // No children, so "already loaded children."
            type: lowerCamelCaseDomainName(DataType),
        )

        if (dataType instanceof EnumeratedType) {
            ret.enumerations = ((EnumeratedType) dataType).enumerations
        }
        return ret
    }

    /**
     * depth -1 means go as far as possible
     * depth 0 means don't have any children
     * depth 1 means have one layer of children
     * @param dataClass
     * @param depth
     * @return
     */
    D3JSON dataClassD3Json(DataClass dataClass, int depth) {

        List<D3JSON> dataElementsJson = []
        List<D3JSON> childDataClassesJson = []

        if (depth != 0) {

            dataElementsJson = dataClassService.getDataElementsIn(dataClass).collect{
                dataElementD3Json(it)
            }

            childDataClassesJson = dataClassService.getChildDataClasses(dataClass).collect{
                dataClassD3Json(it, depth - 1) // recursive
            }

        } // otherwise depth == 0, no children


        D3JSON ret = new D3JSON(
            name: dataClass.name,
            angularLink: angularLink(dataClass.dataModel.id, dataClass.id, DataClass),
            type: lowerCamelCaseDomainName(DataClass),
        )


        List<D3JSON> children = dataElementsJson + childDataClassesJson

        if (children) {
            ret.children = children
        }

        return ret
    }
}
/**
 *  D3 view json recursive format
 *
 *  type is a String from MetadataDomain.lowerCamelCaseDomainName e.g. 'dataClass'
 *  angularLinkis a String of format e.g. "http://localhost:8080/#/82467/dataClass/82470/", which is a link to the angular application view for data model 82467, data class 82470
 *  enumerations is a Map of Strings to Strings for enumerated types.
 *
 */
class D3JSON {
    String name
    long id

    boolean loadedChildren = false
    boolean loading = false

    String angularLink
    String type
    Map<String,String> enumerations
    List<D3JSON> children

}

class ChildrenData {
    List<D3JSON> children
    boolean canAccessDataModel
}
