package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.hibernate.annotations.FetchMode
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.dataarchitect.xsd.XsdAttribute
import org.modelcatalogue.core.dataarchitect.xsd.XsdComplexType
import org.modelcatalogue.core.dataarchitect.xsd.XsdElement
import org.modelcatalogue.core.dataarchitect.xsd.XsdExtension
import org.modelcatalogue.core.dataarchitect.xsd.XsdPattern
import org.modelcatalogue.core.dataarchitect.xsd.XsdRestriction
import org.modelcatalogue.core.dataarchitect.xsd.XsdSequence
import org.modelcatalogue.core.dataarchitect.xsd.XsdSimpleType
import org.modelcatalogue.core.dataarchitect.xsd.XsdUnion

import javax.persistence.criteria.JoinType

@Transactional
class XSDImportService {

    private static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]

    def createValueDomainsAndDataTypes(Collection<XsdSimpleType> simpleDataTypes, ConceptualDomain conceptualDomain){

        simpleDataTypes.each{ XsdSimpleType simpleDataType ->
            matchOrCreateValueDomain(simpleDataType, simpleDataTypes, conceptualDomain)
        }

    }


    def createModelsAndElements(Collection<XsdComplexType> complexDataTypes, Classification classification, ConceptualDomain conceptualDomain){
        complexDataTypes.each{ XsdComplexType complexType ->
            matchOrCreateModel(complexType, complexDataTypes, classification, conceptualDomain)
        }
    }

    def matchOrCreateModel(XsdComplexType complexType, Collection<XsdComplexType> complexDataTypes,  Classification classification, ConceptualDomain conceptualDomain){
        def model = findModel(complexType.name, classification)
        if(!model){
            model = new Model(name: complexType.name, description: complexType.description, classification: [classification]).save(flush:true, failOnError: true)

            def (elements, baseModel) = getRestrictionDetails(complexType.restriction, complexDataTypes, classification, complexType.name, conceptualDomain)
            //def moreElements = getComplexContentDetails(complexType.complexContent, complexDataTypes, classification, complexType.name, conceptualDomain)

            if(baseModel) model.addToBasedOn(baseModel)
            elements.each{ Element element ->
                def relationship = model.addToContains(element.dataElement)
                element.metadata.each{ metadata ->
                    relationship.ext.put(metadata.key, metadata.value)
                }
            }

        }
        return model
    }


    protected matchOrCreateValueDomain(XsdSimpleType simpleDataType, Collection<XsdSimpleType> simpleDataTypes, ConceptualDomain conceptualDomain){
        def valueDomain = findValueDomain(simpleDataType.name, conceptualDomain)
        if(!valueDomain) {
            def (dataType, rule, baseValueDomain) = getRestrictionDetails(simpleDataType.restriction, simpleDataTypes, conceptualDomain, simpleDataType.name)
            valueDomain = new ValueDomain(name: simpleDataType.name, description: simpleDataType.description, dataType: dataType, rule: rule, conceptualDomains: [conceptualDomain]).save(flush: true, failOnError: true)
            if (baseValueDomain) valueDomain.addToBasedOn(baseValueDomain)
            if (simpleDataType.union) valueDomain = addUnions(valueDomain, simpleDataType.union, simpleDataTypes, conceptualDomain)

            //TODO: get metadata(is there any?)
        }
        return valueDomain
    }

    protected addUnions(ValueDomain valueDomain, XsdUnion union, Collection<XsdSimpleType> simpleDataTypes, ConceptualDomain conceptualDomain){
        union.simpleTypes.each{simpleDataType ->
            def unionValueDomain = matchOrCreateValueDomain(simpleDataType, simpleDataTypes, conceptualDomain)
            unionValueDomain.addToIsUnitedIn(valueDomain)
        }
    }

    def findModel(String name, Classification classification){
        def models, model
        models = Model.findAllByName(name)
        models.each{ Model md ->
            if(md.classifications.contains(classification)) model = md
        }
        return model
    }

    protected findValueDomain(String name, ConceptualDomain conceptualDomain){
        def valueDomains, valueDomain
        valueDomains = ValueDomain.findAllByNameIlike(name)
        valueDomains.each{ ValueDomain vd->
            if(vd.conceptualDomains.contains(conceptualDomain)) valueDomain = vd
        }

        return valueDomain
    }

    protected findDataElement(String name, String description, ValueDomain valueDomain, Classification classification){
        def dataElements, dataElement
        dataElements = DataElement.findAllByNameAndDescriptionAndValueDomain(name, valueDomain)
        dataElements.each{ DataElement de->
            if(dataElements.classifcation.contains(classification)) dataElement = de
        }
        return dataElement
    }

    protected getRestrictionDetails(XsdRestriction restriction, Collection<XsdComplexType> complexDataTypes, Classification classification, ConceptualDomain conceptualDomain, String complexTypeName){

        ArrayList<Element> attributeElements
        ArrayList<Element> sequenceElements
        ArrayList<Element> elements
        String base = restriction.base
        def baseModel

        if(base){

            [elements, baseModel]

        }else{
            attributeElements = getElementsFromAttributes(restriction.attributes, classification, conceptualDomain)
            sequenceElements = getElementsFromSequence(restriction.sequence, classification, conceptualDomain, complexDataTypes)
            elements.addAll(attributeElements)
            elements.addAll(sequenceElements)
            [elements, ""]
        }

    }

    protected getElementsFromSequence(XsdSequence sequence, Classification classification, ConceptualDomain conceptualDomain, Collection<XsdComplexType> complexDataTypes){
        ArrayList<Element> elements

        sequence.elements.each{ XsdElement el ->
            def complexType = inXsdComplexTypes(el.type, complexDataTypes)
            if(complexType) matchOrCreateModel(complexType, complexDataTypes, classification, conceptualDomain) else
            elements.add(createElementFromXSDElement(el, classification, conceptualDomain))
        }

        return elements
    }

    protected inXsdComplexTypes(String type, Collection<XsdComplexType> complexDataTypes){
        XsdComplexType complexType = complexDataTypes.find{it.name==type}
        return complexType
    }

    protected getElementsFromAttributes(ArrayList <XsdAttribute> attributes, ConceptualDomain conceptualDomain){
        ArrayList<Element> elements

        attributes.each{ XsdAttribute attribute ->
            Element element = createElementFromAttribute(attribute, conceptualDomain, classification)
            elements.add(element)
        }

        return elements

    }



    protected createElementFromXSDElement(XsdElement xsdElement, Classification classification, ConceptualDomain conceptualDomain){
        Element element = new Element()
        ValueDomain valueDomain
        DataElement dataElement = new DataElement(name: attribute.name, description: attribute.description, classifications: [classification])
        if(xsdElement.type)  valueDomain = findValueDomain(xsdElement.type, conceptualDomain)
        else if(xsdElement.simpleType) valueDomain = findValueDomain(xsdElement.name, conceptualDomain)
        dataElement.save()
        def metadata = ["type":"xs:element"]
        if(xsdElement.minOccurs) metadata.put("Min Occurs", xsdElement.minOccurs)
        if(xsdElement.maxOccurs) metadata.put("Max Occurs", xsdElement.maxOccurs)
        element.metadata = metadata
        element.dataElement = dataElement
        return element
    }

    protected createElementFromAttribute(XsdAttribute attribute, ConceptualDomain conceptualDomain, Classification classification){

        Element element = new Element()
        ValueDomain valueDomain
        DataElement dataElement = new DataElement(name: attribute.name, description: attribute.description, classifications: [classification])
        if(attribute.defaultValue) dataElement.ext.put("defaultValue",attribute.defaultValue)
        if(attribute.fixed) dataElement.ext.put("fixed",attribute.fixed)
        if(attribute.id) dataElement.ext.put("id",attribute.id)
        if(attribute.form) dataElement.ext.put("defaultValue",attribute.form)
        if(attribute.ref) dataElement.ext.put("defaultValue",attribute.ref)
        if(attribute.type)  valueDomain = findValueDomain(attribute.type, conceptualDomain)
        else if(attribute.simpleType) valueDomain = findValueDomain(attribute.name, conceptualDomain)
        dataElement.valueDomain = valueDomain
        dataElement.save()
        def metadata = ["type":"xs:attribute"]
        if(attribute.use) metadata.put("use", attribute.use)
        element.metadata = metadata
        element.dataElement = dataElement
        return element

    }

//    protected getExtensionDetails(XsdExtension extension, Collection<XsdComplexType> complexDataTypes, Classification classification, String complexTypeName){
//        ArrayList<Element> elements
//        return elements
//    }

//    protected getComplexContentDetails(XsdRestriction restriction, Collection<XsdComplexType> complexDataTypes, Classification classification, String complexTypeName){
//        ArrayList<Element> elements
//    }


    protected getRestrictionDetails(XsdRestriction restriction, Collection<XsdSimpleType> simpleDataTypes, ConceptualDomain conceptualDomain, String simpleTypeName){

        def dataType
        String rule
        String base = restriction?.base
        def baseValueDomain
        rule = getRuleFromPattern(restriction.patterns)

        if(base && base.contains("xs:")){
            dataType = DataType.findByName(base)
            return [dataType, rule, ""]
        }else if(base){
            baseValueDomain = findValueDomain(base, conceptualDomain)
            if(!baseValueDomain) {
                XsdSimpleType simpleDataType = simpleDataTypes.find{it.name==base}
                if(simpleDataType) baseValueDomain = matchOrCreateValueDomain(simpleDataType, simpleDataTypes, conceptualDomain)
            }
            if(!baseValueDomain){ throw new Exception('imported Simple Type base does not exist in the schema or in the system, please validate you schema or import the schema it is dependant on')}
            dataType = baseValueDomain.dataType
            if(rule && baseValueDomain.rule){
                rule = addToRule(rule, baseValueDomain.rule)
            }else if(!rule && baseValueDomain.rule){
                rule = baseValueDomain.rule
            }
        }

        if(restriction.enumeration) dataType =  createOrMatchEnumeratedType(simpleTypeName, restriction.enumeration)

        return [dataType, rule, baseValueDomain]
    }


    protected getRuleFromPattern(ArrayList<XsdPattern> patterns){
        String rule = ""
        patterns.each{ XsdPattern pattern ->
            if(!rule=="") rule =  addToRule(rule , "x ==~ /" +  pattern.value + "/") else  rule = "x ==~ /" + pattern.value + "/"
        }
        return rule
    }

    protected addToRule(String rule1, String rule2){
        return rule1 + "||" + rule2
    }

    protected createOrMatchEnumeratedType(String name, String data) {
        def dataTypeReturn
        if (data.contains("\n") || data.contains("\r")) {
            String[] lines = data.split("\\r?\\n")
            if (lines.size() > 0 && lines[] != null) {
                Map enumerations = parseLines(lines)
                if (!enumerations.isEmpty()) {
                    String enumString = sortEnumAsString(enumerations)
                    dataTypeReturn = EnumeratedType.findWhere(enumAsString: enumString)
                    if (!dataTypeReturn) dataTypeReturn = new EnumeratedType(name: name, enumerations: enumerations).save()
                }
            }
        }
        dataTypeReturn
    }

    protected sortEnumAsString(Map enumerations){
        return enumerations.sort().collect { key, val ->
            "${this.quote(key)}:${this.quote(val)}"
        }.join('|')

    }

    protected Map parseLines(String[] lines){
        Map enumerations = [:]
        lines.each { enumeratedValues ->
            def EV = enumeratedValues.split(":")
            if (EV != null && EV.size() > 1 && EV[0] != null && EV[1] != null) {
                def key = EV[0]
                def value = EV[1]
                key = key.trim()
                if (value.isEmpty()) value = "_" else {
                    if (value.size() > 244) value = value[0..244]
                    value.trim()
                }
                enumerations.put(key, value)
            }
        }
        return enumerations
    }


    protected String quote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }

    protected String unquote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.reverseEach { original, pattern ->
            ret = ret.replace(pattern, original)
        }
        ret
    }

}

class Element{
    DataElement dataElement
    Map metadata
}
