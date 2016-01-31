package org.modelcatalogue.core.gel

import java.util.Collection;
import java.util.List;

import grails.transaction.Transactional
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import org.apache.commons.collections.map.MultiValueMap
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValueDomain

@Transactional
class GelXmlService {
    static final String XSD_STUDY_NAME = "http://xsd.modelcatalogue.org/metadata#study"
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
    static final String METADATA_MIN_OCCURS="Min Occurs"
    static final String METADATA_MAX_OCCURS="Max Occurs"
    static final String XSL_TABLE_NAME="http://xsl.modelcatalogue.org/tableName"
    static final String XSD_DEFAULT_NAMESPACE="http://xsd.modelcatalogue.org/defaultNamespace"
    static final String XSD_TARGET_NAMESPACE="http://xsd.modelcatalogue.org/targetNamespace"
    static final String XSD_PREFIX_NAMESPACE="http://xsd.modelcatalogue.org/prefixNamespace"

    static final def XSD_RESTRICTION_LIST=[XSD_RESTRICTION_LENGTH,XSD_RESTRICTION_MIN_LENGTH,XSD_RESTRICTION_MAX_LENGTH,XSD_RESTRICTION_MAX_INCLUSIVE,
        XSD_RESTRICTION_MIN_INCLUSIVE,XSD_RESTRICTION_MAX_EXCLUSIVE,XSD_RESTRICTION_MIN_EXCLUSIVE,XSD_RESTRICTION_TOTAL_DIGITS,XSD_RESTRICTION_FRACTION_DIGITS,XSD_RESTRICTION_PATTERN]

    static final def XSD_BUILTIN_DATA_TYPES=["xs:decimal","xs:float","xs:double","xs:integer","xs:positiveInteger","xs:negativeInteger","xs:nonPositiveInteger",
        "xs:nonNegativeInteger","xs:long","xs:int","xs:short","xs:byte","xs:unsignedLong","xs:unsignedInt","xs:unsignedShort","xs:unsignedByte","xs:dateTime","xs:date",
        "xs:gYearMonth","xs:gYear","xs:duration","xs:gMonthDay","xs:gDay","xs:gMonth","xs:string","xs:normalizedString","xs:token","xs:language","xs:NMTOKEN","xs:NMTOKENS",
        "xs:Name","xs:NCName","xs:ID","xs:IDREFS","xs:ENTITY","xs:ENTITIES","xs:QName","xs:boolean","xs:hexBinary","xs:base64Binary","xs:anyURI","xs:notation"]

    private static final int MAX_COLUMN_NAME_63 = 63

    
    protected List<Model> listChildren(Model model,List results = [],Boolean isRoot=false) {
        if (model && !results.contains(model)){
            //if we send root model as parameter means that this is a child and we have to add in the list
            if (isRoot==false){
                results.add(model)


            }
            for(Relationship r : model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)){
                listChildren(r.destination,results,false)
            }              
        }
        
        return results
    }

    
    /**
     * Return  xml  for schema specific for XML Shredder used by GEL.
     * @param model
     * @return a string with xml formed
     */
    def printXmlModelShredder(Model model) {
        log.info("++++beginning to generate xml shredder model for  '${model.name}'   ++++++++++ " )
        def time=System.currentTimeMillis()
        def subModels = listChildren(model,[],true)
      
 
        log.debug("getting all childrens counting  ${subModels.size()} in ${(System.currentTimeMillis()-time)/1000} s" )
   
        def childsRelationship=model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)
        //validate to see if have the element schema metadata
        time=System.currentTimeMillis()
        validateFormMetadata(model, subModels)
        log.debug(" validating  metadata for   '${model.name}'  in ${(System.currentTimeMillis()-time)/1000} s" )
        time=System.currentTimeMillis()
        validateMetadataOccurs(childsRelationship)
        log.debug(" validating occurences  for  '${model.name}'  in ${(System.currentTimeMillis()-time)/1000} s" )
        time=System.currentTimeMillis()
        validateTableNameCompliance(model)
        log.debug(" validateTableNameCompliance for  '${model.name}' in ${(System.currentTimeMillis()-time)/1000} s" )
        time=System.currentTimeMillis()

        def writer = new StringWriter()
        def builder = new MarkupBuilder(writer)
        builder.context('xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance") {
            setOmitEmptyAttributes(true)
            setOmitNullAttributes(true)
            form(id: printXSDFriendlyString(model.name)) {
                name model.ext.get(XSD_SCHEMA_NAME)
                instructions model?.ext?.instructions
                version   model.ext.get(XSD_SCHEMA_VERSION)
                versionDescription model.ext.get(XSD_SCHEMA_VERSION_DESCRIPTION)
                //order all elements based on their ext.order
                //these are actually CRFs
                subModels.get(0).outgoingRelationships.sort({ it.ext?.order }).each { Relationship rel ->
                
                    if (rel.relationshipType == RelationshipType.containmentType) {
                        
                        printQuestion(rel, builder)
                    }
                    if (rel.relationshipType == RelationshipType.hierarchyType) {
                        printSection(rel, builder)
                    }
                }
            }
        }
        log.debug(" converted elements string in  ${model.name} in ${System.currentTimeMillis()-time} ms" )
  
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + writer.toString()
    }

    def printSection(Relationship relationship, MarkupBuilder builder){
        Model model=relationship.destination
        Map ext=[(METADATA_MIN_OCCURS):fromDestination(relationship, METADATA_MIN_OCCURS),(METADATA_MAX_OCCURS):fromDestination(relationship, METADATA_MAX_OCCURS)]

        
        if(model.ext.repeating=='true') {
            return builder.repeatingGroup(id: printXSDFriendlyString(model.name), minRepeat: defaultMinOccurs(ext.get(METADATA_MIN_OCCURS)), maxRepeat: defaultMaxOccurs(ext.get(METADATA_MAX_OCCURS))) {
                setOmitEmptyAttributes(true)
                setOmitNullAttributes(true)
                name model.name
                tableName getXSLTableName(relationship)
                
                //validate occurs
                def elements=model.getOutgoingRelationshipsByType(RelationshipType.containmentType)
                def childrens=model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)
                validateMetadataOccurs(elements+childrens)

                //recursive printing 
                model.outgoingRelationships.each { Relationship rel ->
                    if (rel.relationshipType == RelationshipType.hierarchyType) this.printSection(rel, builder)
                    if (rel.relationshipType == RelationshipType.containmentType) this.printQuestion(rel, builder)


                }
            }
        }else{
            return builder.section(id: model.name, minRepeat: ext.get(METADATA_MIN_OCCURS), maxRepeat: ext.get(METADATA_MAX_OCCURS),) {
                setOmitEmptyAttributes(true)
                setOmitNullAttributes(true)
                name model.name
                tableName getXSLTableName(relationship)
                instructions model.ext.instructions
                
                //validate occurs
                def childrens=model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)
                def elements=model.getOutgoingRelationshipsByType(RelationshipType.containmentType)
                validateMetadataOccurs(elements+childrens)
                
                model.outgoingRelationships.each { Relationship rel ->
                    if (rel.relationshipType == RelationshipType.hierarchyType) this.printSection(rel, builder)
                    if (rel.relationshipType == RelationshipType.containmentType) this.printQuestion(rel, builder)
                }
            }

        }
    }

    /**
     * Print as a question for Relationship which contains DataElements
     * @param rel
     * @param builder
     * @return
     */
    def printQuestion(Relationship rel, MarkupBuilder builder){
        DataElement dataElement=rel.destination
        Map ext=[(METADATA_MIN_OCCURS):fromDestination(rel, METADATA_MIN_OCCURS),(METADATA_MAX_OCCURS):fromDestination(rel, METADATA_MAX_OCCURS)]

        return builder.question(id: "R_${dataElement.id}", minRepeat: ext.get(METADATA_MIN_OCCURS), maxRepeat:  ext.get(METADATA_MAX_OCCURS)){
            setOmitEmptyAttributes(true)
            setOmitNullAttributes(true)
            name dataElement.name
            if ((ext.get(METADATA_MAX_OCCURS).isInteger()&&ext.get(METADATA_MAX_OCCURS).toInteger()>1)||"unbounded".equals(ext.get(METADATA_MAX_OCCURS))){
             tableName getXSLTableName(rel)
            }
            text dataElement.ext.text
            instructions dataElement.description
            
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

    def getXSLTableName(CatalogueElement element){
        def tableName= (element.ext.get(XSL_TABLE_NAME))?element.ext.get(XSL_TABLE_NAME):element.name
        tableName=tableName.replaceAll("\\s+", " ").replaceAll("[()?*.;!]", "").replaceAll("[^a-zA-Z0-9]+","_").toLowerCase().replaceAll("_+", "_")
        return tableName
    }
    def getXSLTableName(Relationship relationship){
        def candidateName=fromDestination(relationship, XSL_TABLE_NAME)
        def tableName=candidateName?:relationship.destination.name
        tableName=tableName.replaceAll("\\s+", " ").replaceAll("[()?*.;!]", "").replaceAll("[^a-zA-Z0-9]+","_").toLowerCase().replaceAll("_+", "_")
        return tableName
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
        text = text.replaceAll("[\\(\\)]", "").replaceAll("[^a-zA-Z0-9]+","-").toLowerCase()
        if(text[0].matches("[0-9]")) text = "_" + text
        return text
    }
    
    protected printXSDFriendlyString(CatalogueElement el){
        if (el.name==null) return "null"
        String text="${printXSDFriendlyString(el.name)}-${getModelCatalogueId(el)}"
        return text;
 
    }


    protected getModelCatalogueId(CatalogueElement ce){
        def id = ce.getLatestVersionId() ? ce.getLatestVersionId() + "." + ce.getVersionNumber()  :  ce.getId() + "." + ce.getVersionNumber()
        return id


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
        def valueDomains = new TreeSet({ k1, k2 -> printXSDFriendlyString(k1) <=> printXSDFriendlyString(k2) } as Comparator)
        StringWriter writer = new StringWriter()
        def xmlSchema = Classification.findByName("XMLSchema")
        def xml = new MarkupBuilder(writer)
        def childRelations = targetModel.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)
        def subModels = listChildren(targetModel,[],true)

        //check if it's a good candidate for xsd schema
        validateFormMetadata(targetModel,subModels)
        //check if the metadata occurs it's already filled in for models
        validateMetadataOccurs(childRelations)

    
        def namespace=targetModel.ext.get(XSD_STUDY_NAME)=='cancer'?'gelCAN':'gelRD'
        def study=(targetModel.ext.get(XSD_STUDY_NAME)=='cancer')?'cancer':'rarediseases'
        def version = targetModel.ext.get(XSD_SCHEMA_VERSION)?:''


        xml.'xs:schema'('xmlns:xs': 'http://www.w3.org/2001/XMLSchema',
                "xmlns:vc": "http://www.w3.org/2007/XMLSchema-versioning",
                "xmlns:${namespace}" : "https://genomicsengland.co.uk/xsd/${study}/${version}",
                "xmlns": "https://genomicsengland.co.uk/xsd/${study}/${version}",
                "targetNamespace":"https://genomicsengland.co.uk/xsd/${study}/${version}",
                'vc:minVersion': '1.1',
                'elementFormDefault':'qualified') {
            'xs:annotation'{
                'xs:documentation'{
                    'h1'("Title:"+targetModel.ext.get(XSD_SCHEMA_NAME)? targetModel.ext.get(XSD_SCHEMA_NAME):targetModel.name)
                    'p'("Version:"+XmlUtil.escapeXml(targetModel.ext.get(XSD_SCHEMA_VERSION)))
                    'p'("Description:"+XmlUtil.escapeXml(targetModel.ext.get(XSD_SCHEMA_VERSION_DESCRIPTION)))
                    'p'("Generated: "+new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
                }
            }

            'xs:element'(name:printXSDFriendlyString(targetModel.name)){
                if(targetModel.description){
                    'xs:annotation'{
                        'xs:documentation'{
                            'p'(XmlUtil.escapeXml(targetModel.description))
                        }
                    }
                }
                'xs:complexType'{
                    'xs:sequence'{
                        'xs:element'(name:'metadata',type:'metadata',minOccurs:'1',maxOccurs:'1')
                        targetModel.getOutgoingRelationshipsByType(RelationshipType.hierarchyType).each { Relationship r ->
                            'xs:element'(name:printXSDFriendlyString(r.destination.name),type:printXSDFriendlyString(r.destination),
                                
                            minOccurs:defaultMinOccurs(fromDestination(r,METADATA_MIN_OCCURS)),
                            maxOccurs:defaultMinOccurs(fromDestination(r,METADATA_MAX_OCCURS)))

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
                    'xs:element'(name:'source-organisation',minOccurs:'1',maxOccurs:'1'){
                        'xs:annotation'{
                            'xs:documentation'{
                                'p'("ODS code of the source organisation within the GMC sending the message")
                            }
                        }
                        'xs:simpleType'{
                            'xs:restriction'(base: "xs:string"){
                                'xs:minLength'(value: 1)
                            }
                        }
                    }
                    'xs:element'(name:'source-system',type:"xs:string",minOccurs:'0',maxOccurs:'1'){
                        'xs:annotation'{
                            'xs:documentation'{
                                'p'("Source system. Optional but may be used to supply source with information if there are any issues with the submitted XML.")
                            }
                        }
                    }
                    'xs:element'(name:'local-report-id',type:"xs:string",minOccurs:'0',maxOccurs:'1'){
                        'xs:annotation'{
                            'xs:documentation'{
                                'p'("Source system report Id. Optional but may be used to supply source with information if there are any issues with the submitted XML.")
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
        
        //validate for required metadata occurs
        def containementRelationships=model.getOutgoingRelationshipsByType(RelationshipType.containmentType)
        def hieracrchyRelationships=model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)
        validateMetadataOccurs(hieracrchyRelationships+containementRelationships)
         
        return xml.'xs:complexType'(name: printXSDFriendlyString(model)){
            "${sectionType}"{

                hieracrchyRelationships.each { Relationship relationship ->
                    printModelElements(xml, relationship.destination, fromDestination(relationship,METADATA_MIN_OCCURS), fromDestination(relationship,METADATA_MAX_OCCURS))
                }
                  
                containementRelationships.each { Relationship relationship ->
                    printDataElements(xml, relationship.destination, fromDestination(relationship,METADATA_MIN_OCCURS), fromDestination(relationship,METADATA_MAX_OCCURS),valueDomains,xmlSchema,relationship.ext)
                }

            }
        }
    }

    /**
     * Check if it's a built in type and have no restrictions and it;s a basic type
     * @return
     */
    protected def isXsdBasicDataType(ValueDomain valueDomain){
        return  (XSD_BUILTIN_DATA_TYPES.find{it==valueDomain.dataType?.name}!=null && (!valueDomain.ext.keySet().any { idx->
            (idx in XSD_RESTRICTION_LIST) && (valueDomain.ext.get(idx)!=null)
        }&&(!valueDomain.regexDef)))
    }


    /**
     * Check specific GEL  elements for metadata section  for xml model or xsd
     * @param xml
     * @param model
     * @param valueDomains
     * @param xmlSchema
     * @return void
     * @throw Exception with a text message for missing fields
     */
    protected void validateFormMetadata (Model model,List subModels){
        String exceptionMessages="";
        if (!model.ext.get(XSD_SCHEMA_NAME)){
            exceptionMessages+="missing required field for xsd form 'schema-name', "
        }
        if (!model.ext.get(XSD_SCHEMA_VERSION)){
            exceptionMessages+="missing required field for xsd form 'schema-version', "
            
        }else{
            if (model.ext.get(XSD_SCHEMA_VERSION).split("\\.").length!=3){
                exceptionMessages+="schema-version must be in format X.X.X',"
            }
        }

        if (!model.ext.get(XSD_SCHEMA_VERSION_DESCRIPTION)){
            exceptionMessages+="missing required field for xsd form 'schema-name', "
        }
        if (subModels.findAll{it.name=="metadata"}) exceptionMessages+="duplicate 'metadata' element, "
        
        if (model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType).size()>1) exceptionMessages+="no root element.below the chosen model you need to have exactly one submodel model,"
        
        if (!exceptionMessages.empty) throw new Exception(exceptionMessages)
        

    }
    
    
    protected void validateTableNameCompliance(Model currModel){
        //take submodels with override  table names if any from relationship
        def subModels=findAllTableCandidates(currModel)
        log.debug(" all possible tables for shredder are  ${subModels.size}" )
        
        
        String exceptionMessages="";
        MultiValueMap mappedModels=subModels.collect{[it,getXSLTableName(it)]}.collectEntries( new  MultiValueMap())
        def tableNames=mappedModels.values()
        println "msdpprf"+ mappedModels.values()
        
        def duplicates=tableNames.findAll{tableNames.count(it) > 1}.unique()
        
        duplicates.each {  duplTableName ->
            def duplIds=[]
            mappedModels.each{  model, tableArray->
                for(def table:tableArray){
                    if (duplTableName==table){
                        duplIds.add getModelCatalogueId(model)
                    }
                }

            }
            exceptionMessages+="element name or it's chosen tableName with value  '${duplTableName}' is present more than once for models ${duplIds};  ";
        }
        
        mappedModels.each{ model, tableArray->
            for(def table:tableArray){

                if (table[0].matches("[0-9]")){
                    exceptionMessages+="model '${model}'  with further table name '${table}' must not begin with numbers;";
                }

                if (table.length()>MAX_COLUMN_NAME_63){
                    exceptionMessages+="mode  '${model}' with further table name '${table}' exceded in maximum allowed name size of ${MAX_COLUMN_NAME_63};";
                }
            }
        }

        if (!exceptionMessages.empty) throw new Exception(exceptionMessages)
    }

    
    protected void validateMetadataOccurs(List rels){
        String exceptionMessages="";
        for (Relationship rel in rels) {
            if (!fromDestination(rel,METADATA_MIN_OCCURS)){
                exceptionMessages+="metadata 'Min Occurs' for model '${rel.destination.name}'  is missing, "
            }else{
                if ((!fromDestination(rel,METADATA_MIN_OCCURS)?.isLong())){
                    exceptionMessages+="metadata 'Min Occurs' for model '${rel.destination.name}'  is not a number, "
                }
            }
            if (!fromDestination(rel,METADATA_MAX_OCCURS)){
                exceptionMessages+="metadata 'Max Occurs' for model '${rel.destination.name}'  is missing, ";
            }else{
                if (fromDestination(rel,METADATA_MAX_OCCURS)?.isLong()==false){
                    if(fromDestination(rel,METADATA_MAX_OCCURS)!='unbounded'){
                        exceptionMessages+="metadata 'Max Occurs' for model '${rel.destination.name}'  is not accepted notation, "
                    }
                }
            }
            if (!exceptionMessages.empty) throw new Exception(exceptionMessages)
        }
    }

    protected printModelElements(MarkupBuilder xml, Model model, String minOccurs, String maxOccurs){
        return xml.'xs:element'(name: printXSDFriendlyString(model.name), type: printXSDFriendlyString(model), minOccurs: defaultMinOccurs(minOccurs), maxOccurs: defaultMaxOccurs(maxOccurs)){
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
                //copy or overwrite all ext entries from dataElemnt to value domain
                valueDomain.ext.putAll(dataElement.ext.subMap(XSD_RESTRICTION_LIST))
                //copy or overwrite   all ext entries from relationship to value domain
                valueDomain.ext.putAll(relationshipMetadata.subMap(XSD_RESTRICTION_LIST))
                def  dataType=null;
                if (isXsdBasicDataType(valueDomain)){
                    dataType=valueDomain.dataType.name
                }else{
                    valueDomains.add(valueDomain)
                    dataType=printXSDFriendlyString(valueDomain)
                }
                return printDataElementSimpleType(xml, dataElement, dataType, minOccurs, maxOccurs)
            }
        }
    }

    protected printDataElementSchemaType(MarkupBuilder xml, DataElement dataElement, String type, String minOccurs = "0", String maxOccurs = "unbounded"){

        if(type=="xs:string" && ( minOccurs.contains("unbounded")||minOccurs.toInteger()>=1)){

            return xml.'xs:element'(name: printXSDFriendlyString(dataElement.name), minOccurs: defaultMinOccurs(minOccurs), maxOccurs: defaultMaxOccurs(maxOccurs)) {
                if (dataElement?.description) {
                    'xs:annotation' {
                        'xs:documentation' {
                            'p'(XmlUtil.escapeXml(dataElement?.description))
                        }
                    }
                }
                'xs:simpleType'{
                    'xs:restriction'(base: "xs:string"){
                        'xs:minLength'(value: 1)
                    }
                }

            }
        }else{
                return xml.'xs:element'(name: printXSDFriendlyString(dataElement.name), type: dataElement.valueDomain.dataType.name, minOccurs: defaultMinOccurs(minOccurs), maxOccurs: defaultMaxOccurs(maxOccurs)) {
                    if (dataElement?.description) {
                        'xs:annotation' {
                            'xs:documentation' {
                                'p'(XmlUtil.escapeXml(dataElement?.description))
                            }
                        }
                    }
                }
            }
    }




    protected printDataElementSimpleType(MarkupBuilder xml, DataElement dataElement, String type, String minOccurs = "0", String maxOccurs = "unbounded"){

        if(type=="xs:string" && ( minOccurs.contains("unbounded")||minOccurs.toInteger()>=1)){

            return xml.'xs:element'(name: printXSDFriendlyString(dataElement.name), minOccurs: defaultMinOccurs(minOccurs), maxOccurs: defaultMaxOccurs(maxOccurs)) {
                if (dataElement?.description) {
                    'xs:annotation' {
                        'xs:documentation' {
                            'p'(XmlUtil.escapeXml(dataElement?.description))
                        }
                    }
                }
                'xs:simpleType'{
                    'xs:restriction'(base: "xs:string"){
                        'xs:minLength'(value: 1)
                    }
                }

            }

        }else {

            return xml.'xs:element'(name: printXSDFriendlyString(dataElement.name), type: type, minOccurs: defaultMinOccurs(minOccurs), maxOccurs: defaultMaxOccurs(maxOccurs)) {
                if (dataElement?.description) {
                    'xs:annotation' {
                        'xs:documentation' {
                            'p'(XmlUtil.escapeXml(dataElement?.description))
                        }
                    }
                }
            }
        }
    }


    protected printSimpleType(MarkupBuilder xml, ValueDomain valueDomain){

        if(valueDomain?.dataType instanceof EnumeratedType){
            return xml.'xs:simpleType'(name: printXSDFriendlyString(valueDomain), final: 'restriction'){
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
            return xml.'xs:simpleType'(name: printXSDFriendlyString(valueDomain), final: 'restriction'){
                'xs:simpleContent'
                    def dataType='xs:string'
                    if (valueDomain.ext.get(XSD_RESTRICTION_PATTERN)|| valueDomain?.regexDef){
                        //we force to token all regular expressions
                        dataType='xs:token'
                    }else if (XSD_BUILTIN_DATA_TYPES.contains(valueDomain.dataType?.name)){
                        dataType=valueDomain.dataType.name
                    }
                'xs:restriction'(base: dataType ) {
                    printXsdDataTypeRestrictions(xml,valueDomain)
                }
            }
        }
    }



/**
 * Standard restrictions + GEL restrictions for the forms
 * check this link for viewing how to form xml restrictions http://www.xml.dvint.com/docs/SchemaDataTypesQR-2.pdf
 * @param xml
 * @param valueDomain
 * @return MarkupBuilder completed
 */
  private String printXsdDataTypeRestrictions(MarkupBuilder xml,ValueDomain valueDomain){
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

        if (valueDomain.ext.get(XSD_RESTRICTION_TOTAL_DIGITS)){
            xml.'xs:totalDigits'(value:valueDomain.ext.get(XSD_RESTRICTION_TOTAL_DIGITS))
        }
        
        if (valueDomain.ext.get(XSD_RESTRICTION_FRACTION_DIGITS)){
            xml.'xs:fractionDigits'(value:valueDomain.ext.get(XSD_RESTRICTION_FRACTION_DIGITS))
        }

        if (valueDomain.ext.get(XSD_RESTRICTION_PATTERN)|| valueDomain?.regexDef){
            xml.'xs:pattern'(value:valueDomain.ext.get(XSD_RESTRICTION_PATTERN)?valueDomain.ext.get(XSD_RESTRICTION_PATTERN):valueDomain?.regexDef)
        }
    }

 
  private  String fromDestination(Relationship rel, String extensionName, String defaultValue = null) {
      String value = rel.ext[extensionName]
      if (value) {
          return value
      }
      value = rel.destination.ext[extensionName]
      if (value) {
          return value
      }
      return defaultValue
  }  
  
    protected List<CatalogueElement> findAllTableCandidates(CatalogueElement model,List results = [],Boolean isRoot=false) {    
        if (model){ 
           //if we send root model as parameter means that this is a child and we have to add in the list
            if (isRoot==false){
                results.add(model)
            }

            for (Relationship r: model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)){
                //override table name to take from relation if any
                def tableName=fromDestination(r,XSL_TABLE_NAME)
                if(tableName) r.destination.ext.put(XSL_TABLE_NAME, tableName)
                findAllTableCandidates(r.destination,results,false)
            }

            //search in data elements
            for ( Relationship r:model.getOutgoingRelationshipsByType(RelationshipType.containmentType)) {
                def maxOccurs=fromDestination(r,METADATA_MAX_OCCURS)
               
                if ( maxOccurs.contains("unbounded")|| (maxOccurs.isInteger()&&maxOccurs.toInteger()>1)){
                    //override table name to take from relation if any                    
                    def tableName=fromDestination(r,XSL_TABLE_NAME)
                    if(tableName) r.destination.ext.put(XSL_TABLE_NAME, tableName)
                    results.add(r.destination)
                }
            }
            

        }             
        return results;
    }

}
