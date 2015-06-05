package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.xml.MarkupBuilder

import org.modelcatalogue.core.util.ClassificationFilter
import org.modelcatalogue.core.util.ListCountAndType
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists

@Transactional
class ModelService {

    static final String XSD_SCHEMA_NAME = "http://xsd.modelcatalogue.org/metadata#schemaName"
    static final String XSD_SCHEMA_VERSION = "http://xsd.modelcatalogue.org/metadata#schemaVersion"
    static final String XSD_SCHEMA_VERSION_DESCRIPTION = "http://xsd.modelcatalogue.org/metadata#schemaVersionDescription"
    static final String XSD_SCHEMA_SECTION_TYPE = "http://xsd.modelcatalogue.org/section#type"

    static final String XSD_RESTRICTION_LENGTH="http://xsd.modelcatalogue.org/restrictions#length"
    static final String XSD_RESTRICTION_MIN_LENGTH="http://xsd.modelcatalogue.org/restrictions#minLength"
    static final String XSD_RESTRICTION_MAX_LENGTH="http://xsd.modelcatalogue.org/restrictions#maxLength"
    static final String XSD_RESTRICTION_MAX_INCLUSIVE="http://xsd.modelcatalogue.org/restrictions#maxInclusive"
    static final String XSD_RESTRICTION_MIN_INCLUSIVE="http://xsd.modelcatalogue.org/restrictions#minInclusive"
    static final String XSD_RESTRICTION_MAX_EXCLUSIVE="http://xsd.modelcatalogue.org/restrictions#maxExclusive"
    static final String XSD_RESTRICTION_MIN_EXCLUSIVE="http://xsd.modelcatalogue.org/restrictions#minExclusive"
    static final String XSD_RESTRICTION_TOTAL_DIGITS="http://xsd.modelcatalogue.org/restrictions#totalDigits"
    static final String XSD_RESTRICTION_FRACTION_DIGITS="http://xsd.modelcatalogue.org/restrictions#fractionDigits"
    static final String XSD_RESTRICTION_PATTERN="http://xsd.modelcatalogue.org/restrictions#pattern"

    static final def XSD_RESTRICTION_LIST=[XSD_RESTRICTION_LENGTH,XSD_RESTRICTION_MIN_LENGTH,XSD_RESTRICTION_MAX_LENGTH,XSD_RESTRICTION_MAX_INCLUSIVE,
        XSD_RESTRICTION_MIN_INCLUSIVE,XSD_RESTRICTION_MAX_EXCLUSIVE,XSD_RESTRICTION_MIN_EXCLUSIVE,XSD_RESTRICTION_TOTAL_DIGITS,XSD_RESTRICTION_FRACTION_DIGITS,XSD_RESTRICTION_PATTERN]

    static final def XSD_BUILTIN_DATA_TYPES=["xs:decimal","xs:float","xs:double","xs:integer","xs:positiveInteger","xs:negativeInteger","xs:nonPositiveInteger",
        "xs:nonNegativeInteger","xs:long","xs:int","xs:short","xs:byte","xs:unsignedLong","xs:unsignedInt","xs:unsignedShort","xs:unsignedByte","xs:dateTime","xs:date",
        "xs:gYearMonth","xs:gYear","xs:duration","xs:gMonthDay","xs:gDay","xs:gMonth","xs:string","xs:normalizedString","xs:token","xs:language","xs:NMTOKEN","xs:NMTOKENS",
        "xs:Name","xs:NCName","xs:ID","xs:IDREFS","xs:ENTITY","xs:ENTITIES","xs:QName","xs:boolean","xs:hexBinary","xs:base64Binary","xs:anyURI","xs:notation"]

    SecurityService modelCatalogueSecurityService
    ClassificationService classificationService

    ListWithTotalAndType<Model> getTopLevelModels(Map params) {
        getTopLevelModels(classificationService.classificationsInUse, params)
    }

    ListWithTotalAndType<Model> getTopLevelModels(ClassificationFilter classifications, Map params) {
        RelationshipType hierarchy = RelationshipType.hierarchyType
        ElementStatus status = ElementService.getStatusFromParams(params)
        RelationshipType classification = RelationshipType.classificationType

        DetachedCriteria<Model> criteria = new DetachedCriteria<Model>(Model)




        if (classifications.unclassifiedOnly) {
            // language=HQL
            return Lists.fromQuery(params, Model, """
                select distinct m
                from Model as m left join m.incomingRelationships as rel
                where m.status = :status
                    and (
                        (
                            m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                            and rel.relationshipType != :classificationType
                        )
                        or m.incomingRelationships is empty
                     )
                group by m.name, m.id
                order by m.name
            """, """
                select count(m.id)
                from Model as m left join m.incomingRelationships as rel
                where m.status = :status
                    and (
                        (
                            m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                            and rel.relationshipType != :classificationType
                        )
                        or m.incomingRelationships is empty
                     )
            """, [type: hierarchy, status: status, classificationType: classification])
        }
        if (classifications.excludes && !classifications.includes) {
            // language=HQL
            return Lists.fromQuery(params, Model, """
                select distinct m
                from Model as m
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:classifications))
                group by m.name, m.id
                order by m.name
            """, """
                select count(m.id)
                from Model as m
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:classifications))
            """, [type: hierarchy, status: status, classifications: classifications.excludes, classificationType: classification])
        }
        if (classifications.excludes && classifications.includes) {
            // language=HQL
            return Lists.fromQuery(params, Model, """
                select distinct m
                from Model as m
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:excludes))
                    and m.id in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:includes))
                group by m.name, m.id
                order by m.name
            """, """
                select count(m.id)
                from Model as m
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:excludes))
                    and m.id in (select distinct r.destination.id from Relationship r where r.relationshipType = :classificationType and r.source.id in (:includes))
            """, [type: hierarchy, status: status, includes: classifications.includes, excludes: classifications.excludes, classificationType: classification])
        }
        if (classifications.includes && !classifications.excludes) {
            // language=HQL
            return Lists.fromQuery(params, Model, """
                select distinct m
                from Model as m join m.incomingRelationships as rel
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and rel.source.id in (:classifications)
                    and rel.relationshipType = :classificationType
                group by m.name, m.id
                order by m.name
            """, """
                select count(m.id)
                from Model as m join m.incomingRelationships as rel
                where m.status = :status
                    and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
                    and rel.source.id in (:classifications)
                    and rel.relationshipType = :classificationType
            """, [type: hierarchy, status: status, classifications: classifications.includes, classificationType: classification])
        }
        // language=HQL
        Lists.fromQuery params, Model, """
            select distinct m
            from Model m
            where m.status = :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
            group by m.name, m.id
            order by m.name
        """, """
            select count(m.id)
            from Model m
            where m.status = :status and m.id not in (select distinct r.destination.id from Relationship r where r.relationshipType = :type)
        """, [type: hierarchy, status: status]
    }

    ListWithTotalAndType<Model> getSubModels(Model model) {
        List<Model> models = listChildren(model)
        new ListCountAndType<Model>(count: models.size(), list: models, itemType: Model)

    }

    ListWithTotalAndType<DataElement> getDataElementsFromModels(List<Model> models) {
        def results = []
        models.each { model ->
            results.addAll(model.contains)
        }
        new ListCountAndType<DataElement>(count: results.size(), list: results, itemType: DataElement)
    }


    protected List<Model> listChildren(Model model, results = []) {
        if (model && !results.contains(model)) {
            results += model
            model.parentOf?.each { child ->
                results += listChildren(child, results)
            }
        }
        results.unique()
    }

    /**
     * Return  xml  for schema specific for XML Shredder used by GEL.
     * @param model
     * @return a string with xml formed
     */
    def gelXmlModelShredder(Model model) {
        def writer = new StringWriter()
        def builder = new MarkupBuilder(writer)
        builder.context('xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance") {
            setOmitEmptyAttributes(true)
            setOmitNullAttributes(true)
            //dataset('fileLocation': "../GEL_CAN_Master.xml", 'name': "Master")
            form(id: "${printXSDFriendlyString("Master")}") {
                name model.name
                instructions model?.ext?.instructions
                version {
                    major model.versionNumber
                    minor 0
                    patch 0
                }
                versionDescription 'Alpha version'
                revisionNotes 'Alpha version'
                formTitle printXSDFriendlyString(model.name)
                formInitials printXSDFriendlyString(model.name)

                //order all elements based on their ext.order
                //these are actually CRFs
                model.outgoingRelationships.sort({ it.ext?.order }).each { Relationship rel ->
                    if (rel.relationshipType == RelationshipType.containmentType) {
                        printQuestion(rel.destination, rel.ext, builder)
                    }
                    if (rel.relationshipType == RelationshipType.hierarchyType) {
                        printSection(rel.destination, rel.ext, builder)
                    }
                }
            }
        }

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + writer.toString()
    }

    def printSection(Model model, Map ext, MarkupBuilder builder){
        if(model.ext.repeating=='true') {

            return builder.repeatingGroup(id: printXSDFriendlyString(model.name), minRepeat: defaultMinOccurs(ext.get("Min Occurs")), maxRepeat: defaultMaxOccurs(ext.get("Max Occurs"))) {
                setOmitEmptyAttributes(true)
                setOmitNullAttributes(true)
                name model.name

                model.outgoingRelationships.each { Relationship rel ->
                    if (rel.relationshipType == RelationshipType.containmentType) this.printQuestion(rel.destination, rel.ext, builder)
                    if (rel.relationshipType == RelationshipType.hierarchyType) this.printSection(rel.destination, rel.ext, builder)
                }
            }

        }else{
            return builder.section(id: model.name, minRepeat: ext.get("Min Occurs"), maxRepeat: ext.get("Max Occurs")) {
                setOmitEmptyAttributes(true)
                setOmitNullAttributes(true)
                name model.name
                instructions model.ext.instructions

                model.outgoingRelationships.each { Relationship rel ->
                    if (rel.relationshipType == RelationshipType.containmentType) this.printQuestion(rel.destination, rel.ext, builder)
                    if (rel.relationshipType == RelationshipType.hierarchyType) this.printSection(rel.destination, rel.ext, builder)
                }
            }

        }
    }

    def printQuestion(DataElement dataElement, Map ext, MarkupBuilder builder){
        return builder.question(id: "R_${dataElement.id}", minRepeat: ext.get("Min Occurs"), maxRepeat: ext.get("Max Occurs")){
            setOmitEmptyAttributes(true)
            setOmitNullAttributes(true)
            name dataElement.name
            text dataElement.ext.text
            instructions dataElement.description

            if(dataElement.ext.serviceLookupName){
                'service-lookup'(id: dataElement.ext.serviceLookupId, style: dataElement.ext.serviceLookupStyle){
                    name dataElement.ext.serviceLookupName
                }
            }

            if(dataElement?.valueDomain?.dataType instanceof EnumeratedType) {

                enumeration(id:printXSDFriendlyString(dataElement.valueDomain.name), style:dataElement.ext.style){
                    dataElement.valueDomain.dataType.enumerations.each{ key, val ->
                        value (control: key, val)
                    }
                }
            }else{
                if(dataElement?.valueDomain?.dataType) {
                    simpleType transformDataType(dataElement?.valueDomain.dataType.name)
                }else{
                    simpleType 'string'
                }
            }

        }
    }

    protected transformDataType(String dataType) {
        def dataType2 = dataType.replace('xs:', '')

        def basicOnes = [
                "string", "boolean",
                "integer", "decimal",
                "float", "date",
                "pdate", "an10 date",
                "time", "datetime",
                "textarea", "file",
                "email", "phone",
                "NHSNumber"];

        if (dataType2.toLowerCase() == "nonnegativeinteger" || dataType2.toLowerCase() == "positiveinteger") {
            dataType2 = "integer"
        } else if (dataType2.toLowerCase() == "double") {
            dataType2 = "decimal"
        } else if (dataType2.toLowerCase() == "dateTime") {
            dataType2 = "datetime"
        } else if (dataType2.toLowerCase() == "base64binary") {
            dataType2 = "file"
        } else if (!basicOnes.contains(dataType2.toLowerCase())) {
            dataType2 = "string"
        }

        return printXSDFriendlyString(dataType2)

    }



    protected printXSDFriendlyString(String text){
        if (text==null) return "null"
        return text.replaceAll(" ", "-").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("/", "-").toLowerCase()
    }

    protected defaultMinOccurs(String min){
        if(min==null) min = '0'
        return min
    }

    protected defaultMaxOccurs(String max){
        if(max==null) max = 'unbounded'
        return max
    }


    protected defaultEnumerationStyle(String style) {
        if (style == null) style = 'single-select'
        return style
    }

    /**
     * Print xsd model with additional metadatada element. The current target model should contains at least 2 metadata fields
     * with corespondence for schema-name, schema-version,schema-versionDescription
     * @param targetModel
     * @return an String with formed xml
     * @throw Exception with text error messages corresponding for missing fields
     */
    def printXSDModel(Model targetModel) {
        def valueDomains = new TreeSet({ k1, k2 -> k1.name+k1.id <=> k2.name+k1.id } as Comparator)
        StringWriter writer = new StringWriter()
        def xmlSchema = Classification.findByName("XMLSchema")
        def xml = new MarkupBuilder(writer)
        def childRelations = targetModel.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)
        def subModels = listChildren(targetModel)

        //check if it's a good candidate for xsd schema
        validateGelXsdFormMetadata(targetModel,subModels)

        xml.'xs:schema'('xmlns:xs': 'http://www.w3.org/2001/XMLSchema', 'xmlns:vc': 'http://www.w3.org/2007/XMLSchema-versioning', 'xmlns:gel': 'https://genomicsengland.co.uk/xsd/', 'vc:minVersion': '1.1') {
            'xs:annotation'{
                'xs:documentation'{
                    'h1'("Title:"+targetModel.ext.get(XSD_SCHEMA_NAME)? targetModel.ext.get(XSD_SCHEMA_NAME):targetModel.name)
                    'p'("Version:"+targetModel.ext.get(XSD_SCHEMA_VERSION))
                    'p'("Description:"+targetModel.ext.get(XSD_SCHEMA_VERSION_DESCRIPTION))
                    'p'("Generated: "+new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
                }
            }

            'xs:element'(name:printXSDFriendlyString(targetModel.name)){
                if(targetModel.description){
                    'xs:annotation'{
                        'xs:documentation'{
                            'p'(targetModel.description)
                        }
                    }
                }
                'xs:complexType'{
                    'xs:sequence'{
                        'xs:element'(name:'metadata',type:'metadata',minOccurs:'1',maxOccurs:'1')
                        targetModel.getOutgoingRelationshipsByType(RelationshipType.hierarchyType).each { Relationship r ->
                            'xs:element'(name:printXSDFriendlyString(r.destination.name),type:printXSDFriendlyString(r.destination.name),
                            minOccurs:defaultMinOccurs(r.destination.ext.get("Min Occurs")),
                            maxOccurs:defaultMinOccurs(r.destination.ext.get("Max Occurs")))
                        }
                    }
                }
            }
            //print metadata as a standard required option
            printSchemaMetadata(xml,targetModel)


            subModels.each { Model model ->
                printComplexType(xml, model,valueDomains,xmlSchema)
            }
            //print datatypes
            valueDomains.each { ValueDomain valueDomain ->
                printSimpleType(xml, valueDomain)
            }
        }

        log.debug('<?xml version="1.0" encoding="UTF-8"?>' + writer.toString())

        return '<?xml version="1.0" encoding="UTF-8"?>\n' + writer.toString()

    }

    protected printSchemaMetadata(MarkupBuilder xml, Model model){
        return xml.'xs:complexType'(name:'metadata'){
                'xs:sequence'{
                    'xs:element'(name:'schema-name',minOccurs:'1',maxOccurs:'1'){
                        'xs:simpleType'{
                            'xs:restriction'(base:'xs:string'){
                                'xs:enumeration'(value:model.ext.get(XSD_SCHEMA_NAME))
                            }
                        }
                    }
                    'xs:element'(name:'schema-version',minOccurs:'1',maxOccurs:'1'){
                        'xs:simpleType'{
                            'xs:restriction'(base:'xs:string'){
                                'xs:enumeration'(value:model.ext.get(XSD_SCHEMA_VERSION))
                            }
                        }
                    }
                    'xs:element'(name:'date',type:"xs:date",minOccurs:'1',maxOccurs:'1'){
                        'xs:annotation'{
                            'xs:documentation'{
                                'p'("The date that this file was generated")
                            }
                        }
                    }
                    'xs:element'(name:'source-organisation',type:"xs:string",minOccurs:'1',maxOccurs:'1'){
                        'xs:annotation'{
                            'xs:documentation'{
                                'p'("ODS code of the source organisation within the GMC")
                            }
                        }
                    }
                }

          }
    }


    protected printComplexType(MarkupBuilder xml, Model model,def valueDomains,def xmlSchema){
        def sectionType='xs:sequence'
        if (model.ext.get(XSD_SCHEMA_SECTION_TYPE)?.compareToIgnoreCase("choice")==0){
            sectionType='xs:choice'
        }
        return xml.'xs:complexType'(name: printXSDFriendlyString(model.name)){
            "${sectionType}"{
                model.getOutgoingRelationshipsByType(RelationshipType.containmentType).each { Relationship relationship ->
                    printDataElements(xml, relationship.destination, relationship.ext.get("Min Occurs"), relationship.ext.get("Max Occurs"),valueDomains,xmlSchema,relationship.ext)
                }

                model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType).each { Relationship relationship ->
                    printModelElements(xml, relationship.destination, relationship.ext.get("Min Occurs"), relationship.ext.get("Max Occurs"))
                }
            }
        }
    }

    /**
     * Check if it's a built in type and have no restrictions and it;s a basic type
     * @return
     */
    protected def isXsdBasicDataType(ValueDomain valueDomain){
        return  (XSD_BUILTIN_DATA_TYPES.find{it==valueDomain.dataType?.name}!=null && (!valueDomain.ext.keySet().any {idx->(idx in XSD_RESTRICTION_LIST)}&&(!valueDomain.regexDef)))

    }


    /**
     * Check specific GEL  elements for metadata section
     * @param xml
     * @param model
     * @param valueDomains
     * @param xmlSchema
     * @return void
     * @throw Exception with a text message for missing fields
     */
    protected void validateGelXsdFormMetadata (Model model,List subModels){
        String exceptionMessages="";
        if (!model.ext.get(XSD_SCHEMA_NAME)){
            exceptionMessages+="missing required field for xsd form 'schema-name',"
        }
        if (!model.ext.get(XSD_SCHEMA_VERSION)){
            exceptionMessages+="missing required field for xsd form 'schema-version',"
        }

        if (!model.ext.get(XSD_SCHEMA_VERSION_DESCRIPTION)){
            exceptionMessages+="missing required field for xsd form 'schema-name',"
        }
        if (subModels.findAll{it.name=="metadata"}) exceptionMessages+="duplicate 'metadata' element"
        
        if (!exceptionMessages.empty) throw new Exception(exceptionMessages)       

    }
    

    protected printModelElements(MarkupBuilder xml, Model model, String minOccurs, String maxOccurs){
        return xml.'xs:element'(name: printXSDFriendlyString(model.name), type: printXSDFriendlyString(model.name), minOccurs: defaultMinOccurs(minOccurs), maxOccurs: defaultMaxOccurs(maxOccurs)){
            }

    }

    protected printDataElements(MarkupBuilder xml, DataElement dataElement, String minOccurs, String maxOccurs,def valueDomains,def xmlSchema,Map relationshipMetadata){
        def valueDomain = dataElement?.valueDomain
        if(!valueDomain){
            return printDataElementSchemaType(xml, dataElement, "xs:string", minOccurs, maxOccurs)
        }else{
            if(valueDomain.classifications.contains(xmlSchema)){
                return printDataElementSchemaType(xml, dataElement, valueDomain?.name, minOccurs, maxOccurs)
            }else{
                //copy all ext entries from dataElemnt to value domain
                valueDomain.ext.putAll(dataElement.ext.subMap(XSD_RESTRICTION_LIST))
                //copy and overide all ext entries from relationship to value domain
                valueDomain.ext.putAll(relationshipMetadata.subMap(XSD_RESTRICTION_LIST))
                def  dataType=null;
                if (isXsdBasicDataType(valueDomain)){
                    dataType=valueDomain.dataType.name
                }else{
                    valueDomains.add(valueDomain)
                    dataType=valueDomain.name+valueDomain.id
                }
                return printDataElementSimpleType(xml, dataElement, dataType, minOccurs, maxOccurs)
            }
        }
    }

    protected printDataElementSchemaType(MarkupBuilder xml, DataElement dataElement, String type, String minOccurs = "0", String maxOccurs = "unbounded"){
        return xml.'xs:element'(name: printXSDFriendlyString(dataElement.name), type:dataElement.valueDomain.dataType.name, minOccurs: defaultMinOccurs(minOccurs), maxOccurs: defaultMaxOccurs(maxOccurs)){
            if (dataElement?.description){
                'xs:annotation'{
                    'xs:documentation'{
                        'p'(dataElement?.description)
                    }
                }
            }
        }
    }




    protected printDataElementSimpleType(MarkupBuilder xml, DataElement dataElement, String type, String minOccurs = "0", String maxOccurs = "unbounded"){
        return xml.'xs:element'(name: printXSDFriendlyString(dataElement.name), type: printXSDFriendlyString(type), minOccurs: defaultMinOccurs(minOccurs), maxOccurs: defaultMaxOccurs(maxOccurs)){
            if (dataElement?.description){
                'xs:annotation'{
                    'xs:documentation'{
                        'p'(dataElement?.description)
                    }
                }
            }
        }
    }


    protected printSimpleType(MarkupBuilder xml, ValueDomain valueDomain){

        if(valueDomain?.dataType instanceof EnumeratedType){
            return xml.'xs:simpleType'(name: printXSDFriendlyString(valueDomain.name+valueDomain.id), final: 'restriction'){
                'xs:restriction'(base:'xs:string'){
                        valueDomain?.dataType.enumerationsAsBeans.each { enumeration ->
                            'xs:enumeration'(value: enumeration.code) {
                                'xs:annotation'() {
                                    'xs:appinfo'() {
                                        'display-text'("${enumeration.name}")
                                    }
                                }
                            }
                        }
                }
            }


        }else{
            return xml.'xs:simpleType'(name: printXSDFriendlyString(valueDomain.name+valueDomain.id), final: 'restriction'){
                'xs:simpleContent'
                'xs:restriction'(base: XSD_BUILTIN_DATA_TYPES.find{it==valueDomain.dataType?.name}?valueDomain.dataType.name: 'xs:string') {
                    printXsdDataTypeRestrictions(xml,valueDomain)
                }
            }
        }
    }



/**
 * Standard restrictions + GEL restrictions for the forms
 * check this link for viewing how to form xml restricitons http://www.xml.dvint.com/docs/SchemaDataTypesQR-2.pdf
 * @param xml
 * @param valueDomain
 * @return
 */
   def printXsdDataTypeRestrictions(MarkupBuilder xml,ValueDomain valueDomain){
        if (valueDomain.ext.get(XSD_RESTRICTION_MAX_LENGTH)){
            xml.'xs:maxLength'(value:valueDomain.ext.get(XSD_RESTRICTION_MAX_LENGTH))
        }
        if (valueDomain.ext.get(XSD_RESTRICTION_MIN_LENGTH)){
            xml.'xs:minLength'(value:valueDomain.ext.get(XSD_RESTRICTION_MIN_LENGTH))
        }
        if (valueDomain.ext.get(XSD_RESTRICTION_LENGTH)){
            xml.'xs:length'(value:valueDomain.ext.get(XSD_RESTRICTION_LENGTH))
        }

        if (valueDomain.ext.get(XSD_RESTRICTION_MIN_INCLUSIVE)){
            xml.'xs:minInclusive'(value:valueDomain.ext.get(XSD_RESTRICTION_MIN_INCLUSIVE))
        }
        if (valueDomain.ext.get(XSD_RESTRICTION_MAX_INCLUSIVE)){
            xml.'xs:maxInclusive'(value:valueDomain.ext.get(XSD_RESTRICTION_MAX_INCLUSIVE))
        }

        if (valueDomain.ext.get(XSD_RESTRICTION_MIN_EXCLUSIVE)){
            xml.'xs:minExclusive'(value:valueDomain.ext.get(XSD_RESTRICTION_MIN_EXCLUSIVE))
        }
        if (valueDomain.ext.get(XSD_RESTRICTION_MAX_EXCLUSIVE)){
            xml.'xs:maxExclusive'(value:valueDomain.ext.get(XSD_RESTRICTION_MAX_EXCLUSIVE))
        }

        if (valueDomain.ext.get(XSD_RESTRICTION_MAX_EXCLUSIVE)){
            xml.'xs:maxExclusive'(value:valueDomain.ext.get(XSD_RESTRICTION_MAX_EXCLUSIVE))
        }

        if (valueDomain.ext.get(XSD_RESTRICTION_PATTERN)|| valueDomain?.regexDef){
            xml.'xs:pattern'(value:valueDomain.ext.get(XSD_RESTRICTION_PATTERN)?valueDomain.ext.get(XSD_RESTRICTION_PATTERN):valueDomain?.regexDef)
        }
    }

}
