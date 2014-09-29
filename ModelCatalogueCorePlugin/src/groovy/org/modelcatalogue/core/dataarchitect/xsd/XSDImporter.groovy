package org.modelcatalogue.core.dataarchitect.xsd

import groovy.util.logging.Log4j
import groovy.xml.QName
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.RelationshipDirection

import static org.modelcatalogue.core.EnumeratedType.quote

@Log4j
class XSDImporter {

    Collection<XsdSimpleType> simpleDataTypes
    Collection<XsdComplexType> complexDataTypes
    Collection<XsdElement> topLevelElements
    Collection<Classification> classifications
    Collection<ConceptualDomain> conceptualDomains

    RelationshipService relationshipService

    XsdSchema schema
    Collection<QName> namespaces

    ArrayList<Model> circularModels = []
    ArrayList<Model> modelsCreated = []

    def createAll() {
        log.info("Processing ${schema.targetNamespace} STARTED")
        log.info("Processing simple types")
        createValueDomainsAndDataTypes()
        log.info("Processing complex types")
        createModelsAndElements()
        log.info("Processing ${schema.targetNamespace} FINISHED")
    }

    def createValueDomainsAndDataTypes() {

        simpleDataTypes.each { XsdSimpleType simpleDataType ->
            matchOrCreateValueDomain(simpleDataType)
        }

    }

    def createModelsAndElements(String containerModelName = "") {

        if (!containerModelName) containerModelName = classifications.first()?.name + " types"
        Classification typeClassification = Classification.findByNamespace(classifications.first()?.namespace + " types")
        if (!typeClassification) typeClassification = new Classification(name: classifications.first()?.name + " types", namespace: classifications.first()?.namespace + " types").save()
        classifications.add(typeClassification)

        Model containerModel = findModel(containerModelName)
        if (!containerModel) containerModel = new Model(name: containerModelName, description: "Container model for complex types. This is automatically generated. You can remove this container model and curate the data as you wish").save()
        complexDataTypes.each { XsdComplexType complexType ->
            matchOrCreateModel(complexType)
        }

        modelsCreated.each { Model model ->
            if (!model.childOf) {
                model.addToChildOf(containerModel)
                model.addToClassifications(typeClassification)
            }
        }

        circularModels.each { Model model ->
            def isBasedOn = model.isBasedOn.first()
            def isBasedOnContains = (!isBasedOn) ?: isBasedOn.contains
            def modelContains = model.contains
            isBasedOnContains.each { DataElement de ->
                def contained = modelContains.find { it.name == de.name }
                if (!contained) model.addToContains(de)
            }
            def isBasedOnChildren = (!isBasedOn) ?: isBasedOn.parentOf
            def modelChildren = model.parentOf
            isBasedOnChildren.each { Model md ->
                def contained = modelChildren.find { it.name == md.name }
                if (!contained) model.addToParentOf(md)
            }
        }

        containerModel = addClassifications(containerModel)

        topLevelElements.each { XsdElement element ->
            ArrayList<Element> elements = []
            elements = getElementsFromXsdElement(elements, element)
        }
    }

    protected addClassifications(PublishedElement element) {
        element.addToClassifications(classifications.first())
        return element
    }

    def matchOrCreateModel(XsdComplexType complexType) {
        log.info("Processing model for complex type ${complexType.name}: ${complexType.description}")
        ArrayList<Element> elements = []
        def baseModel = ""
        def model = findModel(complexType.name)
        if (!model) {

            model = new Model(name: complexType.name, description: complexType.description, status: PublishedElementStatus.UPDATED).save(flush: true, failOnError: true)
            model = addClassifications(model)
            modelsCreated.add(model)

            if (complexType?.restriction) (elements, baseModel) = getRestrictionDetails(complexType.restriction, complexType.name)

            if (complexType?.sequence) elements = addElements(elements, getElementsFromSequence(complexType.sequence))

            if (complexType?.complexContent) {
                def complexElements, complexBase
                (complexElements, complexBase) = getComplexContentDetails(complexType.complexContent)
                elements = addElements(elements, complexElements)
                if (complexBase) baseModel = complexBase
            }

            if (baseModel) {
                model.addToIsBasedOn(baseModel)
                elements = addElements(elements, getElementsFromModel(baseModel), true)
            }

            if (complexType.attributes) elements = addElements(elements, getElementsFromAttributes(complexType.attributes))

            elements.each { Element element ->
                if (element.dataElement) {
                    def relationship = model.addToContains(element.dataElement)
                    element.metadata.each { metadata ->
                        relationship.ext.put(metadata.key, metadata.value)
                    }
                } else if (element.model) {
                    def relationship = model.addToParentOf(element.model)
                    element.metadata.each { metadata ->
                        relationship.ext.put(metadata.key, metadata.value)
                    }
                }
            }

            model.status = PublishedElementStatus.DRAFT

        }
        return model
    }

    protected getElementsFromModel(Model model) {
        ArrayList<Element> elements = []
        ListWithTotal<Relationship> containedElements = relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, model, RelationshipType.containmentType)
        containedElements.items.each { Relationship relationship ->
            def element = new Element()
            element.dataElement = relationship.destination
            element.metadata = relationship.ext
            elements.add(element)
        }
        ListWithTotal<Relationship> childElements = relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, model, RelationshipType.hierarchyType)
        childElements.items.each { Relationship relationship ->
            def element = new Element()
            element.model = relationship.destination
            element.metadata = relationship.ext
            elements.add(element)
        }
        return elements
    }


    protected addUnions(ValueDomain valueDomain, XsdUnion union) {
        union.simpleTypes.each { simpleDataType ->
            def unionValueDomain = matchOrCreateValueDomain(simpleDataType)
            unionValueDomain.addToIsUnitedIn(valueDomain)
        }
        valueDomain
    }

    Model findModel(String name) {
        def models, model
        models = Model.findAllByName(name)
        models.each { Model md ->
            classifications.each { Classification classification ->
                if (md.classifications.contains(classification)) model = md
            }
        }
        model
    }

    protected ValueDomain findValueDomain(String name, DataType dataType = null, String fixed = null) {
        List<ValueDomain> valueDomains = ValueDomain.findAllByNameOrNameIlike(name, "$name (in %)")

        for (ValueDomain domain in valueDomains) {
            if (dataType && domain.dataType == dataType) {
                if (conceptualDomains.intersect(domain.conceptualDomains)) {
//                    if (!fixed) {
//                        return domain
//                    } else if (domain.rule.contains(fixedRule(fixed))) {
                        return domain
//                    }
                }
            } else if (!dataType) {
                if (conceptualDomains.intersect(domain.conceptualDomains)) {
//                    if (!fixed) {
//                        return domain
//                    } else if (domain.rule.contains(fixedRule(fixed))) {
                        return domain
//                    }
                }
            }

        }
        return null
    }

    protected getComplexContentDetails(XsdComplexContent complexContent) {

        ArrayList<Element> elements = []
        def baseModel = ""
        if (complexContent?.restriction) (elements, baseModel) = getRestrictionDetails(complexContent.restriction)
        if (complexContent?.extension) {
            def extElements, extBaseModel
            (extElements, extBaseModel) = getExtensionDetails(complexContent.extension)
            elements = addElements(elements, extElements)
            baseModel = extBaseModel
        }
        if (complexContent?.attributes) {
            elements = addElements(elements, getElementsFromAttributes(complexContent.attributes))
        }

        return [elements, baseModel]
    }


    protected getExtensionDetails(XsdExtension extension) {

        ArrayList<Element> elements = []
        def baseModel = extension?.base
        if (baseModel) baseModel = getBaseModel(baseModel) else baseModel = ""

        if (extension?.restriction) {
            def restrictionBase
            (elements, restrictionBase) = getRestrictionDetails(extension.restriction)
            if (restrictionBase) baseModel = restrictionBase
        }
        if (extension?.group) elements = addElements(elements, getElementsFromGroup(extension.group))
        if (extension?.choice) elements = addElements(elements, getElementsFromChoice(extension.choice))
        if (extension?.sequence) elements = addElements(elements, getElementsFromSequence(extension.sequence))
        if (extension?.attributes) {
            def els = getElementsFromAttributes(extension.attributes)
            elements = addElements(elements, els)
        }

        return [elements, baseModel]

    }

    protected getBaseModel(String base) {
        def baseModel
        def complexType = inXsdComplexTypes(base)
        if (complexType) baseModel = matchOrCreateModel(complexType) else baseModel = findModel(base)

        return baseModel
    }

    protected ArrayList<Element> addElements(ArrayList<Element> elements, ArrayList<Element> elementsToAdd, Boolean inherited = false) {
        elementsToAdd.each { Element element ->
            elements = addElement(elements, element, inherited)
        }
        return elements
    }

    protected ArrayList<Element> addElement(ArrayList<Element> elements, Element element, Boolean inherited = false) {
        Element overrideElement
        if (element?.dataElement) overrideElement = elements.find {
            it?.dataElement && it?.dataElement.name == element?.dataElement.name
        }
        if (element?.model) overrideElement = elements.find { it?.model && it?.model.name == element?.model.name }
        //remove overriden element (if overriden)
        if (!inherited || !overrideElement) {
            elements.add(element)
        }

        return elements
    }

    protected getRestrictionDetails(XsdRestriction restriction) {

        ArrayList<Element> elements = []
        String base = restriction?.base
        def baseModel = ""

        if (base) {
            baseModel = getBaseModel(base)
            elements = getElements(restriction)
            [elements, baseModel]

        } else {
            elements = getElements(restriction)
            [elements, baseModel]
        }

    }

    protected ArrayList<Element> getElements(XsdRestriction restriction) {
        ArrayList<Element> elements = []
        if (restriction?.attributes) elements = getElementsFromAttributes(restriction.attributes)
        if (restriction?.sequence) elements = addElements(elements, getElementsFromSequence(restriction.sequence))
        return elements
    }

    protected getElementsFromChoice(XsdChoice choice) {
        ArrayList<Element> elements = []

        choice.choiceElements.each { XsdChoice ch ->
            elements = addElements(elements, getElementsFromChoice(ch))
        }

        choice.sequenceElements.each { XsdSequence seq ->
            elements = addElements(elements, getElementsFromSequence(seq))
        }

        choice.groupElements.each { XsdGroup gr ->
            elements = addElements(elements, getElementsFromGroup(gr))
        }

        choice.elements.each { XsdElement el ->
            getElementsFromXsdElement(elements, el)
        }

        return elements
    }


    protected getElementsFromGroup(XsdGroup group) {
        ArrayList<Element> elements = []

        if (group?.choice) addElements(elements, getElementsFromChoice(group.choice))
        if (group?.sequence) addElements(elements, getElementsFromSequence(group.sequence))

        return elements
    }

    protected getElementsFromSequence(XsdSequence sequence) {
        ArrayList<Element> elements = []

        sequence.choiceElements.each { XsdChoice ch ->
            elements = addElements(elements, getElementsFromChoice(ch))
        }

        sequence.sequenceElements.each { XsdSequence seq ->
            elements = addElements(elements, getElementsFromSequence(seq))
        }

        sequence.groupElements.each { XsdGroup gr ->
            elements = addElements(elements, getElementsFromGroup(gr))
        }


        sequence.elements.each { XsdElement el ->
            elements = getElementsFromXsdElement(elements, el)
        }

        return elements
    }

    protected getElementsFromXsdElement(ArrayList<Element> elements, XsdElement el) {
        def complexType
        if (el.type) complexType = inXsdComplexTypes(el.type)

        if (complexType) {
            def metadata = [:]
            if (el?.minOccurs) metadata.put("Min Occurs", el.minOccurs)
            if (el?.maxOccurs) metadata.put("Max Occurs", el.maxOccurs)
            elements = addElement(elements, createElementModelFromXSDComplexElement(complexType, el, metadata))
        } else {
            complexType = findModel(el.type)
            if (complexType) {
                def metadata = [:]
                if (el?.minOccurs) metadata.put("Min Occurs", el.minOccurs)
                if (el?.maxOccurs) metadata.put("Max Occurs", el.maxOccurs)
                elements = addElement(elements, createElementModelFromXSDComplexElement(complexType, el, metadata))
            } else {
                elements = addElement(elements, createElementFromXSDElement(el))
            }
        }

        return elements
    }

    protected inXsdComplexTypes(String type) {
        XsdComplexType complexType = complexDataTypes.find { it.name == type }
        return complexType
    }

    protected getElementsFromAttributes(ArrayList<XsdAttribute> attributes) {
        ArrayList<Element> elements = []

        attributes.each { XsdAttribute attribute ->
            Element element = createElementFromAttribute(attribute)
            elements = addElement(elements, element)
        }

        return elements

    }

    protected Element createElementModelFromXSDComplexElement(XsdComplexType complexType, XsdElement el, Map metadata = [:]) {
        Model oldModel = matchOrCreateModel(complexType)
        Model newModel = new Model(name: el.name, description: el.description).save()
        modelsCreated.add(newModel)
        if (oldModel?.status == PublishedElementStatus.UPDATED) circularModels.add(newModel)
        newModel = addClassifications(newModel)
        newModel = copyRelations(newModel, oldModel)
        newModel.addToIsBasedOn(oldModel)
        def element = new Element()
        element.model = newModel
        element.metadata = metadata
        return element
    }

    protected Element createElementModelFromXSDComplexElement(Model oldModel, XsdElement el, Map metadata = [:]) {

        Model newModel = new Model(name: el.name, description: el.description).save()
        modelsCreated.add(newModel)
        newModel = addClassifications(newModel)
        newModel = copyRelations(newModel, oldModel)
        newModel.addToIsBasedOn(oldModel)
        def element = new Element()
        element.model = newModel
        element.metadata = metadata
        return element
    }


    protected Model copyRelations(Model newModel, Model oldModel) {

        for (Relationship r in oldModel.incomingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession' || r.relationshipType.name == 'base' || r.relationshipType.name == 'hierarchy') continue
            def newR = relationshipService.link(r.source, newModel, r.relationshipType)
            r.ext.each { key, value ->
                newR.ext.put(key, value)
            }
        }

        for (Relationship r in oldModel.outgoingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession' || r.relationshipType.name == 'base') continue
            def newR = relationshipService.link(newModel, r.destination, r.relationshipType)
            r.ext.each { key, value ->
                newR.ext.put(key, value)
            }
        }

        return newModel
    }


    protected createElementFromXSDComplexElement(XsdComplexType complexType, Map metadata = [:]) {
        def model = matchOrCreateModel(complexType)
        def element = new Element()
        element.model = model
        element.metadata = metadata
        return element
    }


    protected createElementFromXSDElement(XsdElement xsdElement) {
        Element element = new Element()
        ValueDomain valueDomain
        def description = (xsdElement.description) ?: xsdElement.section + "." + xsdElement.name
        DataElement dataElement = new DataElement(name: xsdElement.name, description: description)
        dataElement = addClassifications(dataElement)
        if (xsdElement?.type) valueDomain = findValueDomain(xsdElement.type)
        else if (xsdElement?.simpleType) valueDomain = matchOrCreateValueDomain(xsdElement.simpleType)
        dataElement.valueDomain = valueDomain
        dataElement.save()
        def metadata = ["Type": "xs:element"]
        if (xsdElement?.minOccurs) metadata.put("Min Occurs", xsdElement.minOccurs)
        if (xsdElement?.maxOccurs) metadata.put("Max Occurs", xsdElement.maxOccurs)
        element.metadata = metadata
        element.dataElement = dataElement
        return element
    }

    protected createElementFromAttribute(XsdAttribute attribute) {

        Element element = new Element()
        ValueDomain valueDomain
        def description = (attribute.description) ?: attribute.section + "." + attribute.name
        DataElement dataElement = new DataElement(name: attribute.name, description: description).save()
        dataElement = addClassifications(dataElement)
        if (attribute?.defaultValue) dataElement.ext.put("defaultValue", attribute.defaultValue)
        if (attribute?.fixed) dataElement.ext.put("fixed", attribute.fixed)
        if (attribute?.id) dataElement.ext.put("id", attribute.id)
        if (attribute?.form) dataElement.ext.put("defaultValue", attribute.form)
        if (attribute?.ref) dataElement.ext.put("defaultValue", attribute.ref)
        if (attribute?.type) valueDomain = findValueDomain(attribute.type)
        else if (attribute?.simpleType) valueDomain = matchOrCreateValueDomain(attribute.simpleType, attribute.fixed)
        dataElement.valueDomain = valueDomain
        dataElement.save()
        def metadata = ["Type": "xs:attribute"]
        if (attribute?.use) metadata.put("use", attribute.use)
        element.metadata = metadata
        element.dataElement = dataElement
        return element

    }

    protected ValueDomain matchOrCreateValueDomain(XsdSimpleType simpleDataType, String fixed = null) {
        log.info("Processing value domain for simple type ${simpleDataType.name}: ${simpleDataType.description}")
        def (dataType, rule, baseValueDomain) = getRestrictionDetails(simpleDataType.restriction, simpleDataType.name)
        def valueDomain = findValueDomain(simpleDataType.name, dataType, fixed)
        if (!valueDomain) {
            valueDomain = new ValueDomain(name: simpleDataType.name, description: simpleDataType.description, dataType: dataType, rule: fixed ? fixedRule(fixed) : rule).save(flush: true, failOnError: true)
            valueDomain.addToConceptualDomains(conceptualDomains.first())

            if (baseValueDomain) valueDomain.addToIsBasedOn(baseValueDomain)
            if (simpleDataType.union) valueDomain = addUnions(valueDomain, simpleDataType.union)

            //TODO: get metadata(is there any?)
        }
        return valueDomain
    }


    protected getRestrictionDetails(XsdRestriction restriction, String simpleTypeName) {

        DataType dataType = null
        String rule = getRuleFromRestrictions(restriction)
        String base = restriction?.base
        ValueDomain baseValueDomain = null

        if (base && base.contains("xs:")) {
            dataType = DataType.findByName(base)
            return [dataType, rule, ""]
        } else if (base) {
            baseValueDomain = findValueDomain(base)
            if (!baseValueDomain) {
                XsdSimpleType foundSimpleDataType = simpleDataTypes.find { it.name == base }
                if (foundSimpleDataType) baseValueDomain = matchOrCreateValueDomain(foundSimpleDataType)
            }
            if (!baseValueDomain) {
                throw new Exception('imported Simple Type base [ ' + base + ' ] does not exist in the schema or in the system, please validate you schema or import the schema it is dependant on')
            }
            dataType = baseValueDomain.dataType
            if (rule && baseValueDomain.rule) {
                rule = addToRule(baseValueDomain.rule, rule)
            } else if (!rule && baseValueDomain.rule) {
                rule = baseValueDomain.rule
            }
        }

        if (restriction?.enumeration) dataType = createOrMatchEnumeratedType(simpleTypeName, restriction.enumeration)

        return [dataType, rule, baseValueDomain]
    }


    protected static String getRuleFromRestrictions(XsdRestriction restriction) {
        if (!restriction) {
            return null
        }
        String rule = ""

        if (!(restriction.length in ["", null])) {
            rule = addToRule(rule, "length($restriction.length)")
        }

        if (!(restriction.minLength in ["", null])) {
            rule = addToRule(rule, "minLength($restriction.minLength)")
        }

        if (!(restriction.maxLength in ["", null])) {
            rule = addToRule(rule, "maxLength($restriction.maxLength)")
        }

        if (!(restriction.minInclusive in ["", null])) {
            rule = addToRule(rule, "minInclusive($restriction.minInclusive)")
        }

        if (!(restriction.minExclusive in ["", null])) {
            rule = addToRule(rule, "minExclusive($restriction.minExclusive)")
        }

        if (!(restriction.maxInclusive in ["", null])) {
            rule = addToRule(rule, "maxInclusive($restriction.maxInclusive)")
        }

        if (!(restriction.maxExclusive in ["", null])) {
            rule = addToRule(rule, "maxExclusive($restriction.maxExclusive)")
        }

        String composedPatterns = restriction?.patterns?.inject(rule) { String acc, XsdPattern pattern ->
            addToRule(acc, "x ==~ /" + pattern.value + "/")
        }
        if (rule || composedPatterns) {
            rule = addToRule(rule, composedPatterns)
        }
        rule ?: null
    }

    protected static String addToRule(String rule1, String rule2) {
        if (rule1 && !rule2) return rule1
        if (rule2 && !rule1) return rule2
        return "$rule1 && $rule2"
    }

    protected static EnumeratedType createOrMatchEnumeratedType(String name, String data) {
        if (data.contains("\n") || data.contains("\r")) {
            String[] lines = data.split("\\r?\\n")
            if (lines != null && lines.size() > 0) {
                Map enumerations = parseLines(lines)
                if (!enumerations.isEmpty()) {
                    EnumeratedType type = EnumeratedType.findByEnumAsString(sortEnumAsString(enumerations))
                    if (type) {
                        return type
                    }
                    return new EnumeratedType(name: name, enumerations: enumerations).save(failOnError: true)
                }
            }
        }
        return null
    }

    protected static sortEnumAsString(Map<String, String> enumerations) {
        return enumerations.sort().collect { String key, String val ->
            "${quote(key)}:${quote(val)}"
        }.join('|')

    }

    protected static Map<String, String> parseLines(String[] lines) {
        Map<String, String> enumerations = [:]
        lines.each { String enumeratedValues ->
            String[] EV = enumeratedValues.split(":")
            if (EV != null && EV.size() > 1 && EV[0] != null && EV[1] != null) {
                String key = EV[0]
                String value = EV[1]
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
//
//    protected static fixedRule(String fixed) {
//        "fixed('${fixed.replaceAll("'","\\\\'")}')"
//    }

}


class Element {
    DataElement dataElement
    Model model
    Map metadata

}
