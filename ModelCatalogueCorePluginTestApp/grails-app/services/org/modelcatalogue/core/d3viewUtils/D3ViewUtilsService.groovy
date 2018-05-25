package org.modelcatalogue.core.d3viewUtils

import com.google.common.collect.ImmutableSet
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataElementService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipConfiguration
import org.modelcatalogue.core.RelationshipInfo
import org.modelcatalogue.core.RelationshipTypeName
import org.modelcatalogue.core.RelationshipTypeService
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.DataModelAclService
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.RelationshipDirection
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
    RelationshipTypeService relationshipTypeService
    DataModelAclService dataModelAclService

    //// Utilities:

    static String lowerCamelCaseDomainName(Class clazz) {
        MetadataDomain.lowerCamelCaseDomainName(MetadataDomain.ofClass(clazz))
    }

    String angularLink(Long dataModelId, Long id, Class clazz) {
        return "${grailsApplication.config.grails.serverURL}/#/$dataModelId/${lowerCamelCaseDomainName(clazz)}/$id"
    }
    String angularLink(CatalogueElement catalogueElement) {
        return angularLink(dataModelAclService.dataModelFromInstance(catalogueElement).dataModelId,
                            catalogueElement.id,
                            catalogueElement.getClass())
    }

    //// Data Models:

    /**
     * No children; load them later
     * @param dataModel
     * @return D3JSON
     */
    D3JSON dataModelD3Json(DataModel dataModel) {

        D3JSON ret = new D3JSON(
            name: dataModel.name,
            description: dataModel.description,
            id: dataModel.id,
            angularLink: angularLink(dataModel.id, dataModel.id, DataModel),
            type: lowerCamelCaseDomainName(DataModel)
        )

        ret = addRelationships(dataModel, ret)

        return ret
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


        def dataClassChildrenJson = dataClasses.items.collect {dataClass ->
            dataClassD3Json(dataClass)

        }
//        List<DataElement> unDataClassedDataElements = DataElement.findAllByDataModel(dataModel).findAll{
//            dataElementService.countDataClassesOf(it) == 0
//        }
//        def dataElementChildrenJson = unDataClassedDataElements.collect {
//            dataElementD3Json(it)
//        }



        return dataClassChildrenJson //+ dataElementChildrenJson

        // the dataElementChildrenJson seems to take a long time
        // TODO: Handle case where there are just DataTypes listed not connected to any DataElements

    }

    //// Data Classes:


    /**
     * @return DataClass D3JSON (children will be loaded later)
     */
    D3JSON dataClassD3Json(DataClass dataClass) {

        D3JSON ret = new D3JSON(
            name: dataClass.name,
            id: dataClass.id,
            description: dataClass.description,
            angularLink: angularLink(dataClass.dataModel.id, dataClass.id, DataClass),
            type: lowerCamelCaseDomainName(DataClass),
        )

        ret = addRelationships(dataClass, ret)

        return ret
    }

    List<D3JSON> dataClassD3JsonChildren(DataClass dataClass) {

        List<D3JSON> dataElementsJson = dataClassService.getDataElementsIn(dataClass).collect{
                dataElementD3Json(it)
        }

        List<D3JSON> childDataClassesJson = dataClassService.getChildDataClasses(dataClass).collect{
                dataClassD3Json(it) // recursive
        }


        List<D3JSON> children = dataElementsJson + childDataClassesJson

        return children
    }

    //// Data Elements and Data Types:


    D3JSON dataElementD3Json(DataElement dataElement) {
        D3JSON ret = new D3JSON(

            name: dataElement.name,
            id: dataElement.id,
            description: dataElement.description,
            angularLink: angularLink(dataElement.dataModel.id, dataElement.id, DataElement),
            loadedChildren: true, // Just load data type, no laziness
            type: lowerCamelCaseDomainName(DataElement)
        )

        if (dataElement.dataType) {
            ret.children = [dataTypeD3Json(dataElement.dataType)]
        }


        ret = addRelationships(dataElement, ret)

        return ret
    }

    D3JSON dataTypeD3Json(DataType dataType) {
        D3JSON ret = new D3JSON(
            name: dataType.name,
            id: dataType.id,
            description: dataType.description,
            angularLink: angularLink(dataType.dataModel.id, dataType.id, DataType),
            loadedChildren: true, // No children, so "already loaded children."
            type: lowerCamelCaseDomainName(DataType),
        )

        if (dataType instanceof EnumeratedType) {
            ret.enumerations = ((EnumeratedType) dataType).enumerations
        }

        ret = addRelationships(dataType, ret)
        return ret
    }

    static Map<String, Map<String, String>> relationshipTypes(Class type) {
        Map<String, Map<String, String>> relationships  = [incoming: [:], outgoing: [:], bidirectional: [:]]

        if (type.superclass && CatalogueElement.isAssignableFrom(type.superclass)) {
            Map<String, Map<String,String>> fromSuperclass = relationshipTypes(type.superclass)
            relationships.incoming.putAll(fromSuperclass.incoming ?: [:])
            relationships.outgoing.putAll(fromSuperclass.outgoing ?: [:])
            relationships.bidirectional.putAll(fromSuperclass.bidirectional ?: [:])
        }

        Map<String, Map<String, String>> fromType = GrailsClassUtils.getStaticFieldValue(type, 'relationships') ?: [incoming: [:], outgoing: [:], bidirectional: [:]]
        relationships.incoming.putAll(fromType.incoming ?: [:])
        relationships.outgoing.putAll(fromType.outgoing ?: [:])
        relationships.bidirectional.putAll(fromType.bidirectional ?: [:])
    }

    /**
     * add relationships to D3JSON, modifying and returning the D3JSON.
     * @param catalogueElement
     * @param d3JSON
     * @return
     */
    D3JSON addRelationships(CatalogueElement catalogueElement, D3JSON d3JSON) {
        RelationshipConfiguration relationshipConfiguration = relationshipTypeService.getRelationshipConfiguration(catalogueElement.getClass())
        relationshipConfiguration.each {
            Map<String, String> directionMap ->
                directionMap.remove RelationshipTypeName.HIERARCHY.name // relationship type names
                directionMap.remove RelationshipTypeName.CONTAINMENT.name
        }
        List<RelationshipInfo> relationshipInfos = relationshipTypeService.relationshipInfoOf(catalogueElement, relationshipConfiguration)
        Map<String, List<RelationJson>> relationshipsJson = [:]

        relationshipInfos.each {RelationshipInfo relationshipInfo ->

            String capitalizedSpacedName = lowerCamelCaseToSeparated(relationshipInfo.directionalRelationshipName, ' ').capitalize()

            relationshipsJson[capitalizedSpacedName] = relationshipInfo.relationships.collect {
                Relationship relationship ->
                    CatalogueElement otherElement = (relationshipInfo.relationshipDirection == RelationshipDirection.INCOMING) ? relationship.source : relationship.destination
                    new RelationJson(name: otherElement.name,
                        angularLink: angularLink(otherElement)
                    )
            }
        }

        if (relationshipsJson) {
            d3JSON.relationships = relationshipsJson
        }

        return d3JSON

    }
    /**
     * lower-camel-case to sep-separated
     * @param s
     * @return
     */
    String lowerCamelCaseToSeparated (String s, String sep) {
        return s.replaceAll(/\B[A-Z]/) { sep + it }
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
    String description

    boolean loadedChildren = false // false by default; children will be loaded later.
    boolean loading = false

    String angularLink
    String type
    Map<String,String> enumerations
    List<D3JSON> children

    Map<String, List<RelationJson>> relationships




}

class RelationJson {
    String name
    String angularLink
}

/**
 * Returned from DataModelController's basicViewChildrenData action
 */
class ChildrenData {
    List<D3JSON> children
    boolean canAccessDataModel
    boolean caseHandled
}
