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
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
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
import org.modelcatalogue.core.util.lists.ListWithTotal
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeImpl

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
        return angularLink(dataModelAclService.dataModelFromInstance(catalogueElement).id,
                            catalogueElement.id,
                            catalogueElement.getClass())
    }

    D3JSON basicD3JSON(CatalogueElement catalogueElement) {
        D3JSON ret = new D3JSON(
            name: catalogueElement.name,
            description: catalogueElement.description,
            id: catalogueElement.id,
            angularLink: angularLink(catalogueElement),
            type: lowerCamelCaseDomainName(catalogueElement.getClass()),

            status: catalogueElement.status.toString(),
            dateCreated: catalogueElement.dateCreated,
            lastUpdated: catalogueElement.lastUpdated
        )

        if (catalogueElement.ext.size() > 0) {
            ret.metadata = catalogueElement.ext
        }
        ret = addRelationships(catalogueElement, ret)

        return ret
    }

    //// Data Models:

    /**
     * No children; load them later
     * @param dataModel
     * @return D3JSON
     */
    D3JSON dataModelD3Json(DataModel dataModel) {

        D3JSON ret = basicD3JSON(dataModel)


        return ret
    }

    /**
     * Get "children" of data model for display (top level data classes and data elements
     * @param dataModel
     * @return List<D3JSON>
     */
    ListWithTotal<D3JSON> dataModelD3JsonChildren(DataModel dataModel, long offset, long max) {
        DataModelFilter filter = DataModelFilter.create(ImmutableSet.<DataModel> of(dataModel), ImmutableSet.<DataModel> of())
        // This doesn't include imports, maybe it should.
        Map<String, Integer> stats = dataModelService.getStatistics(filter)


        ListWithTotalAndType<DataClass> dataClasses = dataClassService.getTopLevelDataClasses(filter, [offset: offset, max: max, toplevel: true, status: dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''])


        def dataClassChildrenJson = dataClasses.items.collect {dataClass ->
            dataClassD3Json(dataClass)

        }
//        List<DataElement> unDataClassedDataElements = DataElement.findAllByDataModel(dataModel).findAll{
//            dataElementService.countDataClassesOf(it) == 0
//        }
//        def dataElementChildrenJson = unDataClassedDataElements.collect {
//            dataElementD3Json(it)
//        }



        return (new ListWithTotalAndTypeImpl(D3JSON, dataClassChildrenJson, dataClasses.getTotal())) //+ dataElementChildrenJson

        // the dataElementChildrenJson seems to take a long time
        // TODO: Handle case where there are just DataTypes listed not connected to any DataElements

    }

    //// Data Classes:


    /**
     * @return DataClass D3JSON (children will be loaded later)
     */
    D3JSON dataClassD3Json(DataClass dataClass) {

        D3JSON ret = basicD3JSON(dataClass)


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

        D3JSON ret = basicD3JSON(dataElement)

        ret.loadedChildren= true // Just load data type, no laziness

        if (dataElement.dataType) {
            ret.children = [dataTypeD3Json(dataElement.dataType)]
        }



        return ret
    }

    D3JSON dataTypeD3Json(DataType dataType) {
        D3JSON ret = basicD3JSON(dataType)
        ret.loadedChildren = true // No children, so "already loaded children."


        if (dataType.rule) {
            ret.rule = dataType.rule
        }
        if (dataType instanceof EnumeratedType) {
            ret.enumerations = ((EnumeratedType) dataType).enumerations
        }
        if (dataType instanceof PrimitiveType) {
            ret.measurementUnitName = dataType.measurementUnit.name
            ret.measurementUnitSymbol = dataType.measurementUnit.symbol
        }
        if (dataType instanceof ReferenceType) {
            ret.referenceName = dataType.dataClass.name
            ret.referenceAngularLink = angularLink(dataType.dataClass)
        }

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
 *  D3 view json recursive format representing a catalogue element and also info for displaying on D3.
 *
 *
 */
class D3JSON {
    // corresponding to fields on Catalogue Element
    String name
    long id
    String description

    /**
     * String of format e.g. "http://localhost:8080/#/82467/dataClass/82470/", which is a link to the angular application view for data model 82467, data class 82470
     */
    String angularLink
    /**
     * String from MetadataDomain.lowerCamelCaseDomainName e.g. 'dataClass'
     */
    String type

    // corresponding to fields on Catalogue Element
    String status
    String dateCreated
    String lastUpdated
    Map<String, String> metadata // from Catalogue Element ext

    boolean loadedChildren = false // false by default; children will be loaded later.
    boolean loading = false

    List<D3JSON> children

    /**
     * Map whose keys are the directional names of relationshipTypes,
     * values being lists representing the Catalogue Elements ("Relations") on the other end
     * of the relationships of that type.
     */
    Map<String, List<RelationJson>> relationships

    // more information which may be on certain Catalogue Elements
    Map<String,String> enumerations
    String rule
    String measurementUnitName
    String measurementUnitSymbol
    String referenceName
    String referenceAngularLink




}

/**
 * JSON representing a "relation", a Catalogue Element somehow related to the one under consideration. Just the name and a link.
 */
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
    boolean paginationParametersFound
    /**
     * For a paginated query this should be the total possible results (not the size of actually returned results)
     */
    long total
}
