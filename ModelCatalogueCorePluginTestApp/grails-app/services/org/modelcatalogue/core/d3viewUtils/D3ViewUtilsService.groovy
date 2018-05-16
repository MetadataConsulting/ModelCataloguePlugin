package org.modelcatalogue.core.d3viewUtils

import grails.transaction.Transactional
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.util.MetadataDomain

@Transactional
/**
 *  Produces D3 view json.
 *  D3 view json is of the format D3JSON = ["name": NAME, "type": TYPE, "children": List<D3JSON>]
 *  TYPE is from MetadataDomain.lowerCamelCaseDomainName e.g. 'dataClass'
 */
class D3ViewUtilsService {

    DataClassService dataClassService

    def dataClassD3Json(DataClass dataClass) {
        def dataElementsJson = dataClassService.getDataElementsIn(dataClass).collect{[
            "name": it.name,
            "type": MetadataDomain.lowerCamelCaseDomainName(MetadataDomain.ofClass(DataElement)),
        ]}
        def childDataClassesJson = dataClassService.getChildDataClasses(dataClass).collect{
            dataClassD3Json(it) // recursive
        }

        def ret = ["name": dataClass.name,
                   "type": MetadataDomain.lowerCamelCaseDomainName(MetadataDomain.ofClass(DataClass)),
                   "children": dataElementsJson + childDataClassesJson]
        return ret
    }
}
