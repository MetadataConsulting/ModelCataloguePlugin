package org.modelcatalogue.core.d3viewUtils

import com.google.common.collect.ImmutableSet
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.lists.ListWithTotalAndType

@Transactional
/**
 *  Produces D3 view json.
 *  D3 view json is of the format D3JSON = ["name": NAME, "angularLink": ANGULARLINK, "type": TYPE, "children": List<D3JSON>]
 *  TYPE is from MetadataDomain.lowerCamelCaseDomainName e.g. 'dataClass'
 *  angularLink is e.g. http://localhost:8080/#/82467/dataClass/82470/, which is data model 82467, data class 82470
 */
class D3ViewUtilsService {

    DataModelService dataModelService
    DataClassService dataClassService
    GrailsApplication grailsApplication

    static String lowerCamelCaseDomainName(Class clazz) {
        MetadataDomain.lowerCamelCaseDomainName(MetadataDomain.ofClass(clazz))
    }

    String angularLink(Long dataModelId, Long id, Class clazz) {
        return "${grailsApplication.config.grails.serverURL}/#/$dataModelId/${lowerCamelCaseDomainName(clazz)}/$id"
    }

    /**
     * depth -1 means go as far as possible
     * depth 0 means don't have any children
     * depth 1 means have one layer of children
     * @param dataModel
     * @param depth
     */
    def dataModelD3Json(DataModel dataModel, int depth) {
        DataModelFilter filter = DataModelFilter.create(ImmutableSet.<DataModel> of(dataModel), ImmutableSet.<DataModel> of())
        Map<String, Integer> stats = dataModelService.getStatistics(filter)

        ListWithTotalAndType<DataClass> dataClasses = dataClassService.getTopLevelDataClasses(filter, [toplevel: true, status: dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''])
        def dataModelJson = [
            "name": dataModel.name,
            "angularLink": angularLink(dataModel.id, dataModel.id, DataModel),
            "type": lowerCamelCaseDomainName(DataModel),
            "children": (depth == 0) ? [] : dataClasses.items.collect {dataClass ->
                dataClassD3Json(dataClass, depth-1)

            } // TODO: If Data Model has Data Elements not in any Data Class add them
        ]
    }

    /**
     * depth -1 means go as far as possible
     * depth 0 means don't have any children
     * depth 1 means have one layer of children
     * @param dataClass
     * @param depth
     * @return
     */
    def dataClassD3Json(DataClass dataClass, int depth) {

        def dataElementsJson = []
        def childDataClassesJson = []

        if (depth != 0) {

            dataElementsJson = dataClassService.getDataElementsIn(dataClass).collect{[
                "name": it.name,
                "angularLink": angularLink(it.dataModel.id, it.id, DataElement),
                "type": lowerCamelCaseDomainName(DataElement),
            ]}

            childDataClassesJson = dataClassService.getChildDataClasses(dataClass).collect{
                dataClassD3Json(it, depth - 1) // recursive
            }

        } // otherwise depth == 0, no children

        def ret = ["name": dataClass.name,
                    "angularLink": angularLink(dataClass.dataModel.id, dataClass.id, DataClass),
                   "type": lowerCamelCaseDomainName(DataClass),
                   "children": dataElementsJson + childDataClassesJson]
        return ret
    }
}
