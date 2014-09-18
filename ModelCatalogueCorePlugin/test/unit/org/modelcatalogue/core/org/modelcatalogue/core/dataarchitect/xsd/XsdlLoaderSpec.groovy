package org.modelcatalogue.core.org.modelcatalogue.core.dataarchitect.xsd

import grails.test.mixin.Mock
import groovy.xml.QName
import org.modelcatalogue.core.ValueDomain
import spock.lang.Specification
import org.modelcatalogue.core.dataarchitect.xsd.*

/**
 * Created by sus_avi on 11/07/2014.
 */

@Mock(ValueDomain)
class XsdlLoaderSpec extends Specification {


    String filename = "test/unit/resources/SACT/XSD_Example.xsd"
    XsdLoader loader
    //SimpleDataTypes
    def setup(){
        loader = new XsdLoader(filename)
    }

    void "Test that an Xsd file is loaded properly"() {


        String logErrors =""
        ArrayList<XsdSimpleType> simpleDataTypes =[]
        ArrayList<XsdComplexType>  complexDataTypes =[]
        ArrayList<XsdGroup> topLevelElements =[]
        ArrayList<QName> xsdNamespaces =[]
        def schema

        when:"An XSD file is loaded and the file is parsed"
        (topLevelElements, simpleDataTypes, complexDataTypes, schema, xsdNamespaces, logErrors) = loader.parse()

        then: "File content is loaded"


        assert simpleDataTypes.size() == 9
        assert complexDataTypes.size() == 19
        assert logErrors == ""

        simpleDataTypes[1].name == "ts"
        simpleDataTypes[1].description == "This data type supports the representation of a timestamp."
        simpleDataTypes[1].restriction.base == "xs:string"
        simpleDataTypes[1].restriction.patterns[0].value == "[0-9]{1,8}|([0-9]{9,14}|[0-9]{14,14}\\.[0-9]+)([+\\-][0-9]{1,4})?"

        complexDataTypes[0].name == "TS.GB-en-NHS.Date"
        complexDataTypes[0].description == "This data type supports the representation of a date of the form: yyyy-mm-dd."
        complexDataTypes[0].complexContent.restriction.base == "TS"
        complexDataTypes[0].complexContent.restriction.attributes[0].name == "value"
        complexDataTypes[0].complexContent.restriction.attributes[0].use == "required"

    }

    void "Test that readElement successfully reads an element with attributes only"(){
        String elementText = "<element name=\"SACTRecord\" type=\"SACTSACTRecordType\" minOccurs=\"1\" maxOccurs=\"unbounded\"/>"
        when: "I have a valueNode for an Element and I call the readElement"

        XmlParser parser = new XmlParser()
        def elementParser = parser.parseText(elementText)
        elementParser
        XsdElement xsdElement = loader.readElement(elementParser, "")

        then: "I get the associated XsdElement structure for the given valueNode"
        assert xsdElement.name == "SACTRecord"
        assert xsdElement.type == "SACTSACTRecordType"
        assert xsdElement.minOccurs == "1"
        assert xsdElement.maxOccurs == "unbounded"
    }


    //  ANNOTATION
    void "Test that readAnnotation reads an annotation node properly"(){
        when: "I have a valueNode for an annotation node and I call the readAnnotation"

        String annotationText = "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "<xs:annotation>\n" +
                "\t\t\t<xs:documentation>This data type supports the representation of an address.</xs:documentation>\n" +
                "\t\t</xs:annotation>" +
                "</xs:schema>"
        XmlParser parser = new XmlParser()
        def annotationParser = parser.parseText(annotationText)
        String annotation = loader.readAnnotation(annotationParser.value()[0])

        then: "I get the associated Annotation for the given valueNode"
        assert  annotation == "This data type supports the representation of an address."
    }

    // ANY
    void "Test that readAny reads an any node properly" (){
        when: "I have a valueNode for an any node and I call the readAny"

        String anyText = "<any namespace=\"##other\" processContents=\"skip\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>"
        XmlParser parser = new XmlParser()
        def anyParser = parser.parseText(anyText)
        XsdAny xsdAny = loader.readAny(anyParser)

        then: "I get the associated XsdAny structure for the given valueNode"
        assert xsdAny.namespace == "##other"
        assert xsdAny.processContents == "skip"
        assert xsdAny.minOccurs == "0"
        assert xsdAny.maxOccurs == "unbounded"
    }

    //Attribute
    void "Test that readAttribute reads an attribute node properly" (){
        when: "I have a valueNode for an Attribute node and I call the readAny"

        String attributeParserText = "<attribute name=\"integrityCheckAlgorithm\" type=\"cs_IntegrityCheckAlgorithm\" use=\"optional\" default=\"SHA-1\"/>"
        XmlParser parser = new XmlParser()
        def attributeParser = parser.parseText(attributeParserText)
        XsdAttribute xsdAttribute = loader.readAttribute(attributeParser, "")

        then: "I get the associated XsdAttribute structure for the given valueNode"
        assert xsdAttribute.name == "integrityCheckAlgorithm"
        assert xsdAttribute.type == "cs_IntegrityCheckAlgorithm"
        assert xsdAttribute.use == "optional"
        assert xsdAttribute.defaultValue == "SHA-1"
    }

    // XsdChoice
    void "Test that readChoice reads a choice node properly" () {
        when: "I have a valueNode for a Choice node and I call the readChoice"

        String choiceText =  "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "\t\t\t<xs:choice>\n" +
                "\t\t\t\t<xs:element name=\"UnstructuredAddress\">\n" +
                "\t\t\t\t\t<xs:complexType>\n" +
                "\t\t\t\t\t\t<xs:sequence>\n" +
                "\t\t\t\t\t\t\t<xs:element name=\"streetAddressLine\">\n" +
                "\t\t\t\t\t\t\t\t<xs:simpleType>\n" +
                "\t\t\t\t\t\t\t\t\t<xs:restriction base=\"st\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<xs:maxLength value=\"175\"/>\n" +
                "\t\t\t\t\t\t\t\t\t</xs:restriction>\n" +
                "\t\t\t\t\t\t\t\t</xs:simpleType>\n" +
                "\t\t\t\t\t\t\t</xs:element>\n" +
                "\t\t\t\t\t\t</xs:sequence>\n" +
                "\t\t\t\t\t</xs:complexType>\n" +
                "\t\t\t\t</xs:element>\n" +
                "\t\t\t\t<xs:element name=\"StructuredAddress\">\n" +
                "\t\t\t\t\t<xs:complexType>\n" +
                "\t\t\t\t\t\t<xs:sequence>\n" +
                "\t\t\t\t\t\t\t<xs:element name=\"streetAddressLine\" minOccurs=\"5\" maxOccurs=\"5\">\n" +
                "\t\t\t\t\t\t\t\t<xs:simpleType>\n" +
                "\t\t\t\t\t\t\t\t\t<xs:restriction base=\"st\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<xs:maxLength value=\"35\"/>\n" +
                "\t\t\t\t\t\t\t\t\t</xs:restriction>\n" +
                "\t\t\t\t\t\t\t\t</xs:simpleType>\n" +
                "\t\t\t\t\t\t\t</xs:element>\n" +
                "\t\t\t\t\t\t</xs:sequence>\n" +
                "\t\t\t\t\t</xs:complexType>\n" +
                "\t\t\t\t</xs:element>\n" +
                "\t\t\t</xs:choice>\n" +
                "</xs:schema>"

        XmlParser parser = new XmlParser()
        def choiceParser = parser.parseText(choiceText)
        XsdChoice xsdChoice = loader.readChoice(choiceParser.value()[0], "")

        then: "I get the associated XsdChoice structure for the given valueNode"
        assert xsdChoice.elements[0].name == "UnstructuredAddress"
        assert xsdChoice.elements[0].complexType.sequence.elements[0].name == "streetAddressLine"
        assert xsdChoice.elements[0].complexType.sequence.elements[0].simpleType.restriction.base == "st"
        assert xsdChoice.elements[0].complexType.sequence.elements[0].simpleType.restriction.maxLength == "175"
        assert xsdChoice.elements[1].name == "StructuredAddress"
        assert xsdChoice.elements[1].complexType.sequence.elements[0].name == "streetAddressLine"
        assert xsdChoice.elements[1].complexType.sequence.elements[0].simpleType.restriction.base == "st"
        assert xsdChoice.elements[1].complexType.sequence.elements[0].simpleType.restriction.maxLength == "35"


    }

    // XsdComplexContent
    void "Test that readComplexContent reads a complexContent node properly" () {
        when: "I have a valueNode for a complexContent node and I call the readComplexContent"

        String complexContentText = "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "<xs:complexContent>\n" +
                "\t\t\t<xs:restriction base=\"AD.NHS.Internal\">\n" +
                "\t\t\t\t<xs:sequence>\n" +
                "\t\t\t\t\t<xs:element name=\"postalCode\" type=\"ADXP\"/>\n" +
                "\t\t\t\t\t<xs:element name=\"streetAddressLine\" type=\"ADXP\" minOccurs=\"0\" maxOccurs=\"0\"/>\n" +
                "\t\t\t\t</xs:sequence>\n" +
                "\t\t\t\t<xs:attribute name=\"nullFlavor\" type=\"cs_NullFlavor\" use=\"prohibited\"/>\n" +
                "\t\t\t</xs:restriction>\n" +
                "\t\t</xs:complexContent>\n" +
                "</xs:schema>"

        XmlParser parser = new XmlParser()
        def choiceParser = parser.parseText(complexContentText)
        XsdComplexContent xsdComplexContent = loader.readComplexContent(choiceParser.value()[0], "", "")

        then: "I get the associated XsdComplexContent structure for the given valueNode"
        assert xsdComplexContent.restriction.base == "AD.NHS.Internal"
        assert xsdComplexContent.restriction.sequence.elements[0].name == "postalCode"
        assert xsdComplexContent.restriction.sequence.elements[0].type == "ADXP"
        assert xsdComplexContent.restriction.sequence.elements[1].name == "streetAddressLine"
        assert xsdComplexContent.restriction.sequence.elements[1].type == "ADXP"
        assert xsdComplexContent.restriction.sequence.elements[1].minOccurs == "0"
        assert xsdComplexContent.restriction.sequence.elements[1].maxOccurs == "0"
        assert xsdComplexContent.restriction.attributes[0].name == "nullFlavor"
        assert xsdComplexContent.restriction.attributes[0].type == "cs_NullFlavor"
        assert xsdComplexContent.restriction.attributes[0].use == "prohibited"


    }

    //  COMPLEXTYPES
    void "Test that readComplexType reads a complexType with a complexContent node properly"(){

        when: "I have a valueNode for a ComplexType containing a ComplexContent and I call the readComplexType"
        String complexElementText = "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "<xs:complexType name=\"AD.NHS.Internal\" abstract=\"true\" mixed=\"true\">\n" +
                "\t\t<xs:annotation>\n" +
                "\t\t\t<xs:documentation>This data type supports the representation of an address.</xs:documentation>\n" +
                "\t\t</xs:annotation>\n" +
                "\t\t<xs:complexContent>\n" +
                "\t\t\t<xs:extension base=\"ANY\">\n" +
                "\t\t\t\t<xs:sequence>\n" +
                "\t\t\t\t\t<xs:element name=\"postalCode\" type=\"ADXP\" minOccurs=\"0\"/>\n" +
                "\t\t\t\t\t<xs:element name=\"streetAddressLine\" type=\"ADXP\" minOccurs=\"0\" maxOccurs=\"5\"/>\n" +
                "\t\t\t\t</xs:sequence>\n" +
                "\t\t\t\t<xs:attribute name=\"use\" type=\"set_cs_PostalAddressUse\" use=\"optional\"/>\n" +
                "\t\t\t</xs:extension>\n" +
                "\t\t</xs:complexContent>\n" +
                "\t</xs:complexType>\n"  +
                "</xs:schema>"
        XmlParser parser = new XmlParser()
        def complexParser = parser.parseText(complexElementText)
        XsdComplexType complexDataType = loader.readComplexType(complexParser.value()[0],"")

        then: "I get the associated XsdElement structure for the given valueNode"
        assert complexDataType.name == "AD.NHS.Internal"
        assert complexDataType.description == "This data type supports the representation of an address."
        assert complexDataType.mixed == "true"
        assert complexDataType.abstractAttr == "true"
        assert complexDataType.complexContent.extension.base == "ANY"
        assert complexDataType.complexContent.extension.sequence.elements[0].name=="postalCode"
        assert complexDataType.complexContent.extension.sequence.elements[0].type == "ADXP"
        assert complexDataType.complexContent.extension.sequence.elements[0].minOccurs == "0"
        assert complexDataType.complexContent.extension.sequence.elements[1].name=="streetAddressLine"
        assert complexDataType.complexContent.extension.sequence.elements[1].type == "ADXP"
        assert complexDataType.complexContent.extension.sequence.elements[1].minOccurs == "0"
        assert complexDataType.complexContent.extension.sequence.elements[1].maxOccurs == "5"
        assert complexDataType.complexContent.extension.attributes[0].name == "use"
        assert complexDataType.complexContent.extension.attributes[0].type == "set_cs_PostalAddressUse"
        assert complexDataType.complexContent.extension.attributes[0].use == "optional"

    }
    void "Test that readComplexType reads a complexType with a Sequence node properly"(){

        String complexElementText ="<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "\t\t<xs:complexType name=\"PN.GB-en-NHS.PersonNameType5\">\n" +
                "\t\t\t<xs:sequence>\n" +
                "\t\t\t\t<xs:element name=\"prefix\" type=\"en.prefix\" minOccurs=\"0\" maxOccurs=\"0\"/>\n" +
                "\t\t\t\t<xs:element name=\"family\" type=\"en.family\" minOccurs=\"0\" maxOccurs=\"0\"/>\n" +
                "\t\t\t</xs:sequence>\n" +
                "\t\t</xs:complexType>\n" +
                "</xs:schema>"

        when: "I have a valueNode for a complexType that contains a Sequence node and I call the readComplexType"
        XmlParser parser = new XmlParser()
        def complexParser = parser.parseText(complexElementText)
        XsdComplexType complexDataType = loader.readComplexType(complexParser.value()[0],"")
        then: "I get the associated XsdComplexType structure for the given valueNode"
        assert complexDataType.name == "PN.GB-en-NHS.PersonNameType5"
        assert complexDataType.sequence.elements[0].name=="prefix"
        assert complexDataType.sequence.elements[0].type == "en.prefix"
        assert complexDataType.sequence.elements[0].minOccurs == "0"
        assert complexDataType.sequence.elements[0].maxOccurs == "0"
        assert complexDataType.sequence.elements[1].name=="family"
        assert complexDataType.sequence.elements[1].type == "en.family"
        assert complexDataType.sequence.elements[1].minOccurs == "0"
        assert complexDataType.sequence.elements[1].maxOccurs == "0"

    }
    void "Test that readComplexType reads a complexType with Attribute nodes properly"(){

        String complexElementText = "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "\t\t<xs:complexType name=\"PN.GB-en-NHS.PersonNameType5\">\n" +
                "\t\t\t<xs:attribute name=\"root\" type=\"uid\" use=\"prohibited\" fixed=\"2\"/>\n" +
                "\t\t\t<xs:attribute name=\"extension\" use=\"required\"/>\n" +
                "\t\t</xs:complexType>\n" +
                "</xs:schema>"


        when: "I have a valueNode for a complexType that contains a Sequence node and I call the readComplexType"

        XmlParser parser = new XmlParser()
        def complexParser = parser.parseText(complexElementText)
        XsdComplexType complexDataType = loader.readComplexType(complexParser.value()[0],"")
        then: "I get the associated XsdComplexType structure for the given valueNode"
        assert complexDataType.name == "PN.GB-en-NHS.PersonNameType5"
        assert complexDataType.attributes[0].name =="root"
        assert complexDataType.attributes[0].type == "uid"
        assert complexDataType.attributes[0].use == "prohibited"
        assert complexDataType.attributes[0].fixed == "2"
        assert complexDataType.attributes[1].name =="extension"
        assert complexDataType.attributes[1].use == "required"
    }


    // XsdExtension
    void "Test that readExtension reads an Extension node properly" () {
        when: "I have a valueNode for an Extension node and I call the readExtension"

        String extensionText = "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "<xs:extension base=\"ANY\">\n" +
                "\t\t\t\t<xs:sequence>\n" +
                "\t\t\t\t\t<xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
                "\t\t\t\t\t\t<xs:element name=\"delimiter\" type=\"en.delimiter\"/>\n" +
                "\t\t\t\t\t\t<xs:element name=\"suffix\" type=\"en.suffix\"/>\n" +
                "\t\t\t\t\t</xs:choice>\n" +
                "\t\t\t\t\t<xs:element name=\"validTime\" type=\"IVL_TS\" minOccurs=\"0\"/>\n" +
                "\t\t\t\t</xs:sequence>\n" +
                "\t\t\t\t<xs:attribute name=\"use\" type=\"set_cs_EntityNameUse\" use=\"optional\"/>\n" +
                "\t\t\t</xs:extension>\n" +
                "</xs:schema>"
        XmlParser parser = new XmlParser()
        def extensionParser = parser.parseText(extensionText)
        XsdExtension xsdExtension = loader.readExtension(extensionParser.value()[0], "", "")

        then: "I get the associated XsdComplexContent structure for the given valueNode"
        assert xsdExtension.base == "ANY"
        assert xsdExtension.sequence.choiceElements[0].minOccurs == "0"
        assert xsdExtension.sequence.choiceElements[0].maxOccurs == "unbounded"
        assert xsdExtension.sequence.choiceElements[0].elements[0].name == "delimiter"
        assert xsdExtension.sequence.choiceElements[0].elements[0].type == "en.delimiter"
        assert xsdExtension.sequence.choiceElements[0].elements[1].name == "suffix"
        assert xsdExtension.sequence.choiceElements[0].elements[1].type == "en.suffix"
        assert xsdExtension.sequence.elements[0].name == "validTime"
        assert xsdExtension.sequence.elements[0].type == "IVL_TS"
        assert xsdExtension.sequence.elements[0].minOccurs == "0"
        assert xsdExtension.attributes[0].name == "use"
        assert xsdExtension.attributes[0].type == "set_cs_EntityNameUse"
        assert xsdExtension.attributes[0].use == "optional"

    }

    // XsdGroup
    void "Test that readGroup reads a Group node properly" () {
        when: "I have a valueNode for a Group node and I call the readGroup"

        String groupText = "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "<xs:group name=\"OrganisationPersonIdentity\">\n" +
                "\t\t<xs:sequence>\n" +
                "<xs:element name=\"NHSNumberStatusIndicatorCode\" type=\"NHSNumberStatusIndicatorCodeType\"/>" +
                "\t\t\t<xs:choice>\n" +
                "\t\t\t\t<xs:sequence>\n" +
                "\t\t\t\t\t<xs:element name=\"NHSNumber\" type=\"NHSNumberType\" minOccurs=\"0\">\n" +
                "\t\t\t\t\t</xs:element>\n" +
                "\t\t\t\t</xs:sequence>\n" +
                "\t\t\t\t<xs:element name=\"NHSNumber\" type=\"NHSNumberType\">\n" +
                "\t\t\t\t</xs:element>\n" +
                "\t\t\t\t<xs:element name=\"PatientIdentityWithheld\"/>\n" +
                "\t\t\t</xs:choice>\n" +
                "\t\t</xs:sequence>\n" +
                "\t</xs:group>\n" +
                "</xs:schema>"

        XmlParser parser = new XmlParser()
        def groupParser = parser.parseText(groupText)
        XsdGroup xsdGroup = loader.readGroup(groupParser.value()[0], "")
        then: "I get the associated xsdGroup structure for the given valueNode"
        assert xsdGroup.name == "OrganisationPersonIdentity"
        assert xsdGroup.sequence.elements[0].name == "NHSNumberStatusIndicatorCode"
        assert xsdGroup.sequence.elements[0].type == "NHSNumberStatusIndicatorCodeType"
        assert xsdGroup.sequence.choiceElements[0].sequenceElements[0].elements[0].name == "NHSNumber"
        assert xsdGroup.sequence.choiceElements[0].sequenceElements[0].elements[0].type == "NHSNumberType"
        assert xsdGroup.sequence.choiceElements[0].sequenceElements[0].elements[0].minOccurs ==  "0"
        assert xsdGroup.sequence.choiceElements[0].elements[0].name ==  "NHSNumber"
        assert xsdGroup.sequence.choiceElements[0].elements[0].type == "NHSNumberType"
        assert xsdGroup.sequence.choiceElements[0].elements[1].name == "PatientIdentityWithheld"

    }


    // XsdList
    void "Test that a list node is read properly"(){

        String listText = "<list itemType=\"cs_EntityNamePartQualifier\"/>"

        when: "I have a valueNode for an  a list node"
        XmlParser parser = new XmlParser()
        def listTypeParser = parser.parseText(listText)
        XsdList list = loader.readList(listTypeParser, "")

        then: "I get the associated XsdList structure for the given valueNode"

        assert list.itemType == "cs_EntityNamePartQualifier"
    }


    // XsdPattern
    void "Test that readPattern reads a pattern node properly" () {
        when: "I have a valueNode for a pattern node and I call the readPattern"
        String patternText = "<pattern value=\"\\d\\.\\d{1,2}\"/>"
        XmlParser parser = new XmlParser()
        def patternParser = parser.parseText(patternText)
        XsdPattern xsdPattern = loader.readPattern(patternParser)

        then: "I get the XsdPattern structure for the node"
        assert xsdPattern.value == "\\d\\.\\d{1,2}"

    }

    // XsdRestriction
    void "Test that readRestriction reads a restriction node properly" () {
        when: "I have a valueNode for a restriction node and I call the readRestriction"
        String restrictionText = "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "<xs:restriction base=\"ED\">\n" +
                "\t\t\t\t<xs:sequence>\n" +
                "\t\t\t\t\t<xs:element name=\"reference\" type=\"TEL\" minOccurs=\"0\"/>\n" +
                "\t\t\t\t\t<xs:any namespace=\"##other\" processContents=\"skip\"/>\n" +
                "\t\t\t\t</xs:sequence>\n" +
                "\t\t\t\t<xs:attribute name=\"mediaType\" type=\"cs\" />\n" +
                "\t\t\t</xs:restriction>\n" +
                "</xs:schema>"

        XmlParser parser = new XmlParser()
        def restrictionParser = parser.parseText(restrictionText)
        XsdRestriction xsdRestriction = loader.readRestriction(restrictionParser.value()[0], "")

        then: "I get the XsdPattern structure for the node"
        assert xsdRestriction.sequence.elements[0].name == "reference"
        assert xsdRestriction.sequence.elements[0].type == "TEL"
        assert xsdRestriction.sequence.elements[0].minOccurs == "0"
        assert xsdRestriction.sequence.any.namespace == "##other"
        assert xsdRestriction.sequence.any.processContents == "skip"
        assert xsdRestriction.attributes[0].name == "mediaType"
        assert xsdRestriction.attributes[0].type == "cs"
    }
        //XsdSequence
    void "Test that readSequence reads a sequence node properly" () {
        when: "I have a valueNode for a sequence node and I call the readSequence"
        String sequenceText = "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "\t\t\t\t<xs:sequence>\n" +
                "\t\t\t\t\t<xs:element name=\"reference\" type=\"TEL\" minOccurs=\"0\"/>\n" +
                "\t\t\t\t\t<xs:any namespace=\"##other\" processContents=\"skip\"/>\n" +
                "\t\t\t\t</xs:sequence>\n" +
                "</xs:schema>"

        XmlParser parser = new XmlParser()
        def sequenceParser = parser.parseText(sequenceText)
        XsdSequence xsdSequence = loader.readSequence(sequenceParser.value()[0], "")

        then: "I get the XsdSequence structure for the node"
        assert xsdSequence.elements[0].name == "reference"
        assert xsdSequence.elements[0].type == "TEL"
        assert xsdSequence.elements[0].minOccurs == "0"
        assert xsdSequence.any.namespace == "##other"
        assert xsdSequence.any.processContents == "skip"
    }

    //  SIMPLETYPES
    void "Test that a simpleType containing a list node is read properly"(){

        when: "I have a valueNode for an SimpleType that contains a list node"

        String simpleTypeText = "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "<xs:simpleType name=\"set_cs_EntityNamePartQualifier\">\n" +
                "\t\t<xs:annotation>\n" +
                "\t\t\t<xs:documentation>This data type supports the representation of a set of entity name part qualifiers.</xs:documentation>\n" +
                "\t\t</xs:annotation>\n" +
                "\t\t<xs:list itemType=\"cs_EntityNamePartQualifier\"/>\n" +
                "\t</xs:simpleType>" +
                "</xs:schema>"
        XmlParser parser = new XmlParser()
        def simpleTypeParser = parser.parseText(simpleTypeText)
        XsdSimpleType simpleType = loader.readSimpleType(simpleTypeParser.value()[0], "")

        then: "I get the associated XsdSimpleType structure for the given valueNode"

        assert simpleType.name == "set_cs_EntityNamePartQualifier"
        assert simpleType.description == "This data type supports the representation of a set of entity name part qualifiers."
        assert simpleType.list.itemType == "cs_EntityNamePartQualifier"
    }
    void "Test that a simpleType containing a restriction node is read properly"(){

        when: "I have a valueNode for an SimpleType that contains a restriction node"

        String simpleTypeText = "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "<xs:simpleType name=\"cs_EntityNameUse\">\n" +
                "\t\t<xs:annotation>\n" +
                "\t\t\t<xs:documentation>This data type supports the representation of a specific use of an entity name.</xs:documentation>\n" +
                "\t\t</xs:annotation>\n" +
                "\t\t<xs:restriction base=\"cs\">\n" +
                "\t\t\t<xs:enumeration value=\"L\"/>\n" +
                "\t\t\t<xs:enumeration value=\"A\"/>\n" +
                "\t\t\t<xs:enumeration value=\"I\"/>\n" +
                "\t\t\t<xs:enumeration value=\"R\"/>\n" +
                "\t\t</xs:restriction>\n" +
                "\t</xs:simpleType>\n" +
                "</xs:schema>"
        XmlParser parser = new XmlParser()
        def simpleTypeParser = parser.parseText(simpleTypeText)
        XsdSimpleType simpleType = loader.readSimpleType(simpleTypeParser.value()[0], "")

        then: "I get the associated XsdSimpleType structure for the given valueNode"

        assert simpleType.name == "cs_EntityNameUse"
        assert simpleType.description == "This data type supports the representation of a specific use of an entity name."
        assert simpleType.restriction.base == "cs"
        assert simpleType.restriction.enumeration == "L:L\r\nA:A\r\nI:I\r\nR:R\r\n"
    }
    void "Test that a simpleType containing a union node is read properly"(){


        when: "I have a valueNode for an SimpleType that contains a union node"

        String simpleTypeText =  "<?xml version=\"1.0\"?>\n" +
                "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" +
                "<xs:simpleType name=\"real\">\n" +
                "\t\t<xs:annotation>\n" +
                "\t\t\t<xs:documentation>This data type supports the representation of a quantity defined at a given point of a continuum.</xs:documentation>\n" +
                "\t\t</xs:annotation>\n" +
                "\t\t<xs:union memberTypes=\"xs:decimal xs:double\"/>\n" +
                "\t</xs:simpleType>\n"  +
                "</xs:schema>"
        XmlParser parser = new XmlParser()
        def simpleTypeParser = parser.parseText(simpleTypeText)
        XsdSimpleType simpleType = loader.readSimpleType(simpleTypeParser.value()[0], "")

        then: "I get the associated XsdSimpleType structure for the given valueNode"

        assert simpleType.name == "real"
        assert simpleType.description == "This data type supports the representation of a quantity defined at a given point of a continuum."
        assert simpleType.union.memberTypes == "xs:decimal xs:double"
    }


    //XsdUnion
    void "Test that readUnion reads an element properly"(){

        when: "I have a valueNode for an Element and I call the readUnion"
        String unionXsdText = "<union memberTypes=\"xs:decimal xs:double\"/>"

        XmlParser parser = new XmlParser()
        def unionParser = parser.parseText(unionXsdText)
        XsdUnion union = loader.readUnion(unionParser, "")

        then: "I get the associated XsdElement structure for the given valueNode"
        assert union.memberTypes == "xs:decimal xs:double"
    }

}

