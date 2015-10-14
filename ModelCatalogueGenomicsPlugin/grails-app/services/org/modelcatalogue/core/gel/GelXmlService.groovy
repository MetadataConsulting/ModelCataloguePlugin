package org.modelcatalogue.core.gel

import grails.transaction.Transactional
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType

@Transactional
class GelXmlService {

    static final String XSD_SCHEMA_NAME = "http://xsd.modelcatalogue.org/metadata#schemaName"
    static final String XSD_SCHEMA_VERSION = "http://xsd.modelcatalogue.org/metadata#schemaVersion"
    static
    final String XSD_SCHEMA_VERSION_DESCRIPTION = "http://xsd.modelcatalogue.org/metadata#schemaVersionDescription"
    static final String XSD_SCHEMA_SECTION_TYPE = "http://xsd.modelcatalogue.org/section#type"

    static final String XSD_RESTRICTION_LENGTH = "http://xsd.modelcatalogue.org/restrictions#length"
    static final String XSD_RESTRICTION_MIN_LENGTH = "http://xsd.modelcatalogue.org/restrictions#minLength"
    static final String XSD_RESTRICTION_MAX_LENGTH = "http://xsd.modelcatalogue.org/restrictions#maxLength"
    static final String XSD_RESTRICTION_MAX_INCLUSIVE = "http://xsd.modelcatalogue.org/restrictions#maxInclusive"
    static final String XSD_RESTRICTION_MIN_INCLUSIVE = "http://xsd.modelcatalogue.org/restrictions#minInclusive"
    static final String XSD_RESTRICTION_MAX_EXCLUSIVE = "http://xsd.modelcatalogue.org/restrictions#maxExclusive"
    static final String XSD_RESTRICTION_MIN_EXCLUSIVE = "http://xsd.modelcatalogue.org/restrictions#minExclusive"
    static final String XSD_RESTRICTION_TOTAL_DIGITS = "http://xsd.modelcatalogue.org/restrictions#totalDigits"
    static final String XSD_RESTRICTION_FRACTION_DIGITS = "http://xsd.modelcatalogue.org/restrictions#fractionDigits"
    static final String XSD_RESTRICTION_PATTERN = "http://xsd.modelcatalogue.org/restrictions#pattern"
    static final String METADATA_MIN_OCCURS = "Min Occurs"
    static final String METADATA_MAX_OCCURS = "Max Occurs"
    static final String XSL_TABLE_NAME = "http://xsl.modelcatalogue.org/tableName"

    static final
    def XSD_RESTRICTION_LIST = [XSD_RESTRICTION_LENGTH, XSD_RESTRICTION_MIN_LENGTH, XSD_RESTRICTION_MAX_LENGTH, XSD_RESTRICTION_MAX_INCLUSIVE,
                                XSD_RESTRICTION_MIN_INCLUSIVE, XSD_RESTRICTION_MAX_EXCLUSIVE, XSD_RESTRICTION_MIN_EXCLUSIVE, XSD_RESTRICTION_TOTAL_DIGITS, XSD_RESTRICTION_FRACTION_DIGITS, XSD_RESTRICTION_PATTERN]

    static final
    def XSD_BUILTIN_DATA_TYPES = ["xs:decimal", "xs:float", "xs:double", "xs:integer", "xs:positiveInteger", "xs:negativeInteger", "xs:nonPositiveInteger",
                                  "xs:nonNegativeInteger", "xs:long", "xs:int", "xs:short", "xs:byte", "xs:unsignedLong", "xs:unsignedInt", "xs:unsignedShort", "xs:unsignedByte", "xs:dateTime", "xs:date",
                                  "xs:gYearMonth", "xs:gYear", "xs:duration", "xs:gMonthDay", "xs:gDay", "xs:gMonth", "xs:string", "xs:normalizedString", "xs:token", "xs:language", "xs:NMTOKEN", "xs:NMTOKENS",
                                  "xs:Name", "xs:NCName", "xs:ID", "xs:IDREFS", "xs:ENTITY", "xs:ENTITIES", "xs:QName", "xs:boolean", "xs:hexBinary", "xs:base64Binary", "xs:anyURI", "xs:notation"]

    private static final int MAX_COLUMN_NAME_63 = 63

    protected List<DataClass> listChildren(DataClass model, List<DataClass> results = [], Boolean isRoot = false) {
        if (model && !results.contains(model)) {
            //if we send root model as parameter means that this is a child and we have to add in the list
            if (!isRoot) {
                results += model
            }
            model.parentOf.each { DataClass child ->
                results += listChildren(child, results, false)
            }
        }
        results.unique()
    }

    /**
     * Return  xml  for schema specific for XML Shredder used by GEL.
     * @param model
     * @return a string with xml formed
     */
    String printXmlModelShredder(DataClass model) {
        List<DataClass> subModels = listChildren(model, [], true)
        List<Relationship> childRelationships = model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)
        //validate to see if have the element schema metadata
        validateFormMetadata(model, subModels)
        validateMetadataOccurs(childRelationships)
        validateModelsNameLength(subModels)

        StringWriter writer = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(writer)
        builder.context('xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance") {
            setOmitEmptyAttributes(true)
            setOmitNullAttributes(true)
            form(id: printXSDFriendlyString(model.name)) {
                name model.ext.get(XSD_SCHEMA_NAME)
                instructions model?.ext?.instructions
                version model.ext.get(XSD_SCHEMA_VERSION)
                versionDescription model.ext.get(XSD_SCHEMA_VERSION_DESCRIPTION)
                //order all elements based on their ext.order
                //these are actually CRFs
                subModels[0].outgoingRelationships.sort({ it.ext?.order }).each { Relationship rel ->
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

    def printSection(DataClass model, Map<String, String> ext, MarkupBuilder builder) {
        if (model.ext.repeating == 'true') {
            return builder.repeatingGroup(id: printXSDFriendlyString(model.name), minRepeat: defaultMinOccurs(ext.get(METADATA_MIN_OCCURS)), maxRepeat: defaultMaxOccurs(ext.get(METADATA_MAX_OCCURS))) {
                setOmitEmptyAttributes(true)
                setOmitNullAttributes(true)
                name model.name
                tableName getXSLTableName(model)

                //validate occurs
                validateMetadataOccurs(model.getOutgoingRelationshipsByType(RelationshipType.containmentType))
                validateMetadataOccurs(model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType))
                //recursive printing 
                model.outgoingRelationships.each { Relationship rel ->
                    if (rel.relationshipType == RelationshipType.containmentType) this.printQuestion(rel.destination, rel.ext, builder)
                    if (rel.relationshipType == RelationshipType.hierarchyType) this.printSection(rel.destination, rel.ext, builder)
                }
            }
        }

        return builder.section(id: model.name, minRepeat: ext.get(METADATA_MIN_OCCURS), maxRepeat: ext.get("Max Occurs")) {
            setOmitEmptyAttributes(true)
            setOmitNullAttributes(true)
            name model.name
            tableName getXSLTableName(model)
            instructions model.ext.instructions

            //validate occurs
            validateMetadataOccurs(model.getOutgoingRelationshipsByType(RelationshipType.containmentType))
            validateMetadataOccurs(model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType))

            model.outgoingRelationships.each { Relationship rel ->
                if (rel.relationshipType == RelationshipType.containmentType) this.printQuestion(rel.destination, rel.ext, builder)
                if (rel.relationshipType == RelationshipType.hierarchyType) this.printSection(rel.destination, rel.ext, builder)
            }
        }


    }

    def printQuestion(DataElement dataElement, Map<String, String> ext, MarkupBuilder builder) {
        return builder.question(id: "R_${dataElement.id}", minRepeat: ext.get(METADATA_MIN_OCCURS), maxRepeat: ext.get(METADATA_MAX_OCCURS)) {
            setOmitEmptyAttributes(true)
            setOmitNullAttributes(true)
            name dataElement.name
            if (ext.get(METADATA_MIN_OCCURS) > 0 || "unbounded".equals(ext.get(METADATA_MAX_OCCURS))) {
                tableName getXSLTableName(dataElement)
            }
            text dataElement.ext.text
            instructions dataElement.description

            if (dataElement?.dataType instanceof EnumeratedType) {

                enumeration(id: printXSDFriendlyString(dataElement.dataType.name), style: dataElement.ext.style) {
                    dataElement.dataType.enumerations.each { key, val ->
                        value(control: key, val)
                    }
                }
            } else {
                if (dataElement?.dataType) {
                    simpleType transformDataType(dataElement?.dataType?.name)
                } else {
                    simpleType 'string'
                }
            }

        }
    }

    def getXSLTableName(CatalogueElement element) {
        return (element.ext.get(XSL_TABLE_NAME)) ? element.ext.get(XSL_TABLE_NAME) : element.name
    }

    protected static String transformDataType(String dataType) {
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


    protected static printXSDFriendlyString(String text) {
        if (text == null) return "null"
        return text.replaceAll(" ", "-").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("/", "-").toLowerCase()
    }

    protected static defaultMinOccurs(String min) {
        if (min == null) min = '0'
        return min
    }

    protected static defaultMaxOccurs(String max) {
        if (max == null) max = 'unbounded'
        return max
    }

    /**
     * Print xsd model with additional metadatada element. The current target model should contains at least 2 metadata fields
     * with corespondence for schema-name, schema-version,schema-versionDescription
     * @param targetModel
     * @return an String with formed xml
     * @throw Exception with text error messages corresponding for missing fields
     */
    def printXSDModel(DataClass targetModel) {
        Set<DataType> dataTypes = new TreeSet<DataType>({ k1, k2 -> k1.name + k1.id <=> k2.name + k1.id } as Comparator)
        StringWriter writer = new StringWriter()
        DataModel xmlSchema = DataModel.findByName("XMLSchema")
        MarkupBuilder xml = new MarkupBuilder(writer)
        List<Relationship> childRelations = targetModel.getOutgoingRelationshipsByType(RelationshipType.hierarchyType)
        List<DataClass> subModels = listChildren(targetModel, [], true)

        //check if it's a good candidate for xsd schema
        validateFormMetadata(targetModel, subModels)
        //check if the metadata occurs it's already filled in for models
        validateMetadataOccurs(childRelations)

        xml.'xs:schema'('xmlns:xs': 'http://www.w3.org/2001/XMLSchema', 'xmlns:vc': 'http://www.w3.org/2007/XMLSchema-versioning', 'xmlns:gel': 'https://genomicsengland.co.uk/xsd/', 'vc:minVersion': '1.1') {
            'xs:annotation' {
                'xs:documentation' {
                    'h1'("Title:" + targetModel.ext.get(XSD_SCHEMA_NAME) ? targetModel.ext.get(XSD_SCHEMA_NAME) : targetModel.name)
                    'p'("Version:" + XmlUtil.escapeXml(targetModel.ext.get(XSD_SCHEMA_VERSION)))
                    'p'("Description:" + XmlUtil.escapeXml(targetModel.ext.get(XSD_SCHEMA_VERSION_DESCRIPTION)))
                    'p'("Generated: " + new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
                }
            }

            'xs:element'(name: printXSDFriendlyString(targetModel.name)) {
                if (targetModel.description) {
                    'xs:annotation' {
                        'xs:documentation' {
                            'p'(XmlUtil.escapeXml(targetModel.description))
                        }
                    }
                }
                'xs:complexType' {
                    'xs:sequence' {
                        'xs:element'(name: 'metadata', type: 'metadata', minOccurs: '1', maxOccurs: '1')
                        targetModel.getOutgoingRelationshipsByType(RelationshipType.hierarchyType).each { Relationship r ->
                            'xs:element'(name: printXSDFriendlyString(r.destination.name), type: printXSDFriendlyString(r.destination.name),
                                    minOccurs: defaultMinOccurs(r.ext.get(METADATA_MIN_OCCURS)),
                                    maxOccurs: defaultMinOccurs(r.ext.get(METADATA_MAX_OCCURS)))
                        }
                    }
                }
            }
            //print metadata as a standard required option
            printSchemaMetadata(xml, targetModel)


            subModels.each { DataClass model ->
                printComplexType(xml, model, dataTypes, xmlSchema)
            }
            //print datatypes
            dataTypes.each { DataType dataType ->
                printSimpleType(xml, dataType)
            }
        }

        return '<?xml version="1.0" encoding="UTF-8"?>\n' + writer.toString()

    }

    protected static printSchemaMetadata(MarkupBuilder xml, DataClass model) {
        return xml.'xs:complexType'(name: 'metadata') {
            'xs:sequence' {
                'xs:element'(name: 'schema-name', minOccurs: '1', maxOccurs: '1') {
                    'xs:simpleType' {
                        'xs:restriction'(base: 'xs:string') {
                            'xs:enumeration'(value: model.ext.get(XSD_SCHEMA_NAME))
                        }
                    }
                }
                'xs:element'(name: 'schema-version', minOccurs: '1', maxOccurs: '1') {
                    'xs:simpleType' {
                        'xs:restriction'(base: 'xs:string') {
                            'xs:enumeration'(value: model.ext.get(XSD_SCHEMA_VERSION))
                        }
                    }
                }
                'xs:element'(name: 'date', type: "xs:date", minOccurs: '1', maxOccurs: '1') {
                    'xs:annotation' {
                        'xs:documentation' {
                            'p'("The date that this file was generated")
                        }
                    }
                }
                'xs:element'(name: 'source-organisation', type: "xs:string", minOccurs: '1', maxOccurs: '1') {
                    'xs:annotation' {
                        'xs:documentation' {
                            'p'("ODS code of the source organisation within the GMC")
                        }
                    }
                }
            }
        }
    }


    protected static printComplexType(MarkupBuilder xml, DataClass model, Set<DataType> dataTypes, DataModel xmlSchema) {
        def sectionType = 'xs:sequence'
        if (model.ext.get(XSD_SCHEMA_SECTION_TYPE)?.compareToIgnoreCase("choice") == 0) {
            sectionType = 'xs:choice'
        }

        //validate for required metadata occurs
        validateMetadataOccurs(model.getOutgoingRelationshipsByType(RelationshipType.containmentType))
        validateModelsNameLength(model.getOutgoingRelationshipsByType(RelationshipType.containmentType).collect {
            it.destination
        })

        validateMetadataOccurs(model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType))
        validateModelsNameLength(model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType).collect {
            it.destination
        })

        return xml.'xs:complexType'(name: printXSDFriendlyString(model.name)) {
            "${sectionType}" {


                model.getOutgoingRelationshipsByType(RelationshipType.containmentType).each { Relationship relationship ->
                    printDataElements(xml, relationship.destination, relationship.ext.get(METADATA_MIN_OCCURS), relationship.ext.get(METADATA_MAX_OCCURS), dataTypes, xmlSchema, relationship.ext)
                }

                model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType).each { Relationship relationship ->
                    printModelElements(xml, relationship.destination, relationship.ext.get(METADATA_MIN_OCCURS), relationship.ext.get(METADATA_MAX_OCCURS))
                }
            }
        }
    }

    /**
     * Check if it's a built in type and have no restrictions and it;s a basic type
     * @return
     */
    protected static def isXsdBasicDataType(DataType dataType) {
        return (XSD_BUILTIN_DATA_TYPES.find {
            it == dataType?.name
        } != null && (!dataType.ext.keySet().any { idx -> (idx in XSD_RESTRICTION_LIST) } && (!dataType.regexDef)))

    }

    /**
     * Check specific GEL  elements for metadata section  for xml model or xsd
     * @param xml
     * @param model
     * @param dataTypes
     * @param xmlSchema
     * @return void
     * @throw Exception with a text message for missing fields
     */
    protected static void validateFormMetadata(DataClass model, List<DataClass> subModels) {
        String exceptionMessages = "";
        if (!model.ext.get(XSD_SCHEMA_NAME)) {
            exceptionMessages += "missing required field for xsd form 'schema-name', "
        }
        if (!model.ext.get(XSD_SCHEMA_VERSION)) {
            exceptionMessages += "missing required field for xsd form 'schema-version', "

        } else {
            if (model.ext.get(XSD_SCHEMA_VERSION).split("\\.").length != 3) {
                exceptionMessages += "schema-version must be in format X.X.X',"
            }
        }

        if (!model.ext.get(XSD_SCHEMA_VERSION_DESCRIPTION)) {
            exceptionMessages += "missing required field for xsd form 'schema-name', "
        }
        if (subModels.findAll { it.name == "metadata" }) exceptionMessages += "duplicate 'metadata' element, "

        if (model.getOutgoingRelationshipsByType(RelationshipType.hierarchyType).size() > 1) exceptionMessages += "no root element.below the chosen model you need to have exactly one submodel model,"

        if (!exceptionMessages.empty) throw new Exception(exceptionMessages)


    }

    protected static void validateModelsNameLength(List subModels) {
        String exceptionMessages = "";
        for (def model in subModels) {
            if (model.name.length() > MAX_COLUMN_NAME_63) {
                exceptionMessages += "element '${model.name}' exceded in maximum allowed name size of ${MAX_COLUMN_NAME_63}\n,";
            }
        }
        if (!exceptionMessages.empty) throw new Exception(exceptionMessages)
    }

    protected static void validateMetadataOccurs(List<Relationship> rels) {
        String exceptionMessages = ""
        for (Relationship rel in rels) {
            if (!rel.ext.get(METADATA_MIN_OCCURS)) {
                exceptionMessages += "metadata 'Min Occurs' for model '${rel.destination.name}'  is missing, "
            } else {
                if ((!rel.ext.get(METADATA_MIN_OCCURS).isLong())) {
                    exceptionMessages += "metadata 'Min Occurs' for model '${rel.destination.name}'  is not a number, "
                }
            }
            if (!rel.ext.get(METADATA_MAX_OCCURS)) {
                exceptionMessages += "metadata 'Max Occurs' for model '${rel.destination.name}'  is missing, ";
            } else {
                if (!rel.ext.get(METADATA_MAX_OCCURS).isLong()) {
                    if (rel.ext.get(METADATA_MAX_OCCURS) != 'unbounded') {
                        exceptionMessages += "metadata 'Max Occurs' for model '${rel.destination.name}'  is not a number, "
                    }
                }
            }
            if (!exceptionMessages.empty) throw new Exception(exceptionMessages)
        }
    }

    protected static printModelElements(MarkupBuilder xml, DataClass model, String minOccurs, String maxOccurs) {
        return xml.'xs:element'(name: printXSDFriendlyString(model.name), type: printXSDFriendlyString(model.name), minOccurs: defaultMinOccurs(minOccurs), maxOccurs: defaultMaxOccurs(maxOccurs)) {

        }
    }

    protected static printDataElements(MarkupBuilder xml, DataElement dataElement, String minOccurs, String maxOccurs, Set<DataType> dataTypes, DataModel xmlSchema, Map<String, String> relationshipMetadata) {
        DataType dataType = dataElement?.dataType
        if (!dataType) {
            return printDataElementSchemaType(xml, dataElement, minOccurs, maxOccurs)
        } else {
            if (dataType.dataModels.contains(xmlSchema)) {
                return printDataElementSchemaType(xml, dataElement, minOccurs, maxOccurs)
            } else {
                //copy or overwrite all ext entries from dataElemnt to value domain
                dataType.ext.putAll(dataElement.ext.subMap(XSD_RESTRICTION_LIST))
                //copy or overwrite   all ext entries from relationship to value domain
                dataType.ext.putAll(relationshipMetadata.subMap(XSD_RESTRICTION_LIST))
                def dataTypeName = null;
                if (isXsdBasicDataType(dataType)) {
                    dataTypeName = dataType.name
                } else {
                    dataTypes.add(dataType)
                    dataTypeName = dataType.name + dataType.id
                }
                return printDataElementSimpleType(xml, dataElement, dataTypeName, minOccurs, maxOccurs)
            }
        }
    }

    protected static printDataElementSchemaType(MarkupBuilder xml, DataElement dataElement, String minOccurs = "0", String maxOccurs = "unbounded") {
        return xml.'xs:element'(name: printXSDFriendlyString(dataElement.name), type: dataElement.dataType.name, minOccurs: defaultMinOccurs(minOccurs), maxOccurs: defaultMaxOccurs(maxOccurs)) {
            if (dataElement?.description) {
                'xs:annotation' {
                    'xs:documentation' {
                        'p'(XmlUtil.escapeXml(dataElement?.description))
                    }
                }
            }
        }
    }


    protected static printDataElementSimpleType(MarkupBuilder xml, DataElement dataElement, String type, String minOccurs = "0", String maxOccurs = "unbounded") {
        return xml.'xs:element'(name: printXSDFriendlyString(dataElement.name), type: printXSDFriendlyString(type), minOccurs: defaultMinOccurs(minOccurs), maxOccurs: defaultMaxOccurs(maxOccurs)) {
            if (dataElement?.description) {
                'xs:annotation' {
                    'xs:documentation' {
                        'p'(XmlUtil.escapeXml(dataElement?.description))
                    }
                }
            }
        }
    }


    protected static printSimpleType(MarkupBuilder xml, DataType dataType) {

        if (dataType instanceof EnumeratedType) {
            return xml.'xs:simpleType'(name: printXSDFriendlyString(dataType.name + dataType.id), final: 'restriction') {
                'xs:restriction'(base: 'xs:string') {
                    dataType.enumerationsAsBeans.each { enumeration ->
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
        } else {
            return xml.'xs:simpleType'(name: printXSDFriendlyString(dataType.name + dataType.id), final: 'restriction') {
                'xs:simpleContent'
                def dataTypeName = 'xs:string'
                if (dataType.ext.get(XSD_RESTRICTION_PATTERN) || dataType?.regexDef) {
                    //we force to token all regular expressions
                    dataTypeName = 'xs:token'
                } else if (XSD_BUILTIN_DATA_TYPES.contains(dataType?.name)) {
                    dataTypeName = dataType.name
                }
                'xs:restriction'(base: dataTypeName) {
                    printXsdDataTypeRestrictions(xml, dataType)
                }
            }
        }
    }

/**
 * Standard restrictions + GEL restrictions for the forms
 * check this link for viewing how to form xml restrictions http://www.xml.dvint.com/docs/SchemaDataTypesQR-2.pdf
 * @param xml
 * @param dataType
 * @return MarkupBuilder completed
 */
    protected static def printXsdDataTypeRestrictions(MarkupBuilder xml, DataType dataType) {
        if (dataType.ext.get(XSD_RESTRICTION_MAX_LENGTH)) {
            xml.'xs:maxLength'(value: dataType.ext.get(XSD_RESTRICTION_MAX_LENGTH))
        }
        if (dataType.ext.get(XSD_RESTRICTION_MIN_LENGTH)) {
            xml.'xs:minLength'(value: dataType.ext.get(XSD_RESTRICTION_MIN_LENGTH))
        }
        if (dataType.ext.get(XSD_RESTRICTION_LENGTH)) {
            xml.'xs:length'(value: dataType.ext.get(XSD_RESTRICTION_LENGTH))
        }

        if (dataType.ext.get(XSD_RESTRICTION_MIN_INCLUSIVE)) {
            xml.'xs:minInclusive'(value: dataType.ext.get(XSD_RESTRICTION_MIN_INCLUSIVE))
        }
        if (dataType.ext.get(XSD_RESTRICTION_MAX_INCLUSIVE)) {
            xml.'xs:maxInclusive'(value: dataType.ext.get(XSD_RESTRICTION_MAX_INCLUSIVE))
        }

        if (dataType.ext.get(XSD_RESTRICTION_MIN_EXCLUSIVE)) {
            xml.'xs:minExclusive'(value: dataType.ext.get(XSD_RESTRICTION_MIN_EXCLUSIVE))
        }
        if (dataType.ext.get(XSD_RESTRICTION_MAX_EXCLUSIVE)) {
            xml.'xs:maxExclusive'(value: dataType.ext.get(XSD_RESTRICTION_MAX_EXCLUSIVE))
        }

        if (dataType.ext.get(XSD_RESTRICTION_TOTAL_DIGITS)) {
            xml.'xs:totalDigits'(value: dataType.ext.get(XSD_RESTRICTION_TOTAL_DIGITS))
        }

        if (dataType.ext.get(XSD_RESTRICTION_FRACTION_DIGITS)) {
            xml.'xs:fractionDigits'(value: dataType.ext.get(XSD_RESTRICTION_FRACTION_DIGITS))
        }

        if (dataType.ext.get(XSD_RESTRICTION_PATTERN) || dataType?.regexDef) {
            xml.'xs:pattern'(value: dataType.ext.get(XSD_RESTRICTION_PATTERN) ? dataType.ext.get(XSD_RESTRICTION_PATTERN) : dataType?.regexDef)
        }
    }

}
