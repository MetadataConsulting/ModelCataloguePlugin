package org.modelcatalogue.core.dataarchitect.xsd

import groovy.util.logging.Log4j
import groovy.xml.QName
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.RelationshipDirection

import static org.modelcatalogue.core.EnumeratedType.quote

@Log4j
class XSDImporter {

    static final String XS_ATTRIBUTE = "xs:attribute"

    // -- options
    Boolean createModelsForElements
    // --

    Map<String,XsdSimpleType> simpleDataTypesByName = [:]

    Collection<XsdSimpleType> getSimpleDataTypes() {
        simpleDataTypesByName.values()
    }

    void setSimpleDataTypes(Collection<XsdSimpleType> types) {
        simpleDataTypesByName.clear()
        for (XsdSimpleType type in types) {
            simpleDataTypesByName.put(type.name, type)
        }
    }


    Map<String,XsdComplexType> complexDataTypesByName = [:]

    Collection<XsdComplexType> getComplexDataTypes() {
        complexDataTypesByName.values()
    }

    void setComplexDataTypes(Collection<XsdComplexType> types) {
        complexDataTypesByName.clear()
        for (XsdComplexType type in types) {
            complexDataTypesByName.put(type.name, type)
        }
    }

    Collection<XsdElement> topLevelElements
    Collection<Classification> classifications
    Collection<ConceptualDomain> conceptualDomains

    RelationshipService relationshipService

    XsdSchema schema
    Collection<QName> namespaces

    List<Model> circularModels = []
    List<Model> modelsCreated = []
    List<DataElement> elementsCreated = []

    Model publicTypesContainer
    Model rootElementsContainer

    def createAll() {
        log.info("Processing simple types")
        createValueDomainsAndDataTypes()
        log.info("Processing complex types")
        createModelsAndElements()

        log.info("Publishing elements as DRAFT")
        for (DataElement element in elementsCreated) {
            element.status = PublishedElementStatus.DRAFT
            element.save(failOnError: true)
        }

        log.info("Publishing models as DRAFT")
        for (Model model in modelsCreated) {
            model.status = PublishedElementStatus.DRAFT
            model.save(failOnError: true)
        }

        if (publicTypesContainer) {
            publicTypesContainer.status = PublishedElementStatus.DRAFT
            publicTypesContainer.save(failOnError: true)
        }

        if (rootElementsContainer) {
            rootElementsContainer.status = PublishedElementStatus.DRAFT
            rootElementsContainer.save(failOnError: true)
        }


        log.info("Processing FINISHED")
    }

    def createValueDomainsAndDataTypes() {

        simpleDataTypes.each { XsdSimpleType simpleDataType ->
            matchOrCreateValueDomain(simpleDataType)
        }

    }

    def createModelsAndElements(String containerModelName = "", String rootElementsModelName = "") {

        if (!containerModelName) containerModelName = classifications.first()?.name + " Complex Types"
        Classification typeClassification = Classification.findByNamespace(classifications.first()?.namespace + " Complex Types")
        if (!typeClassification) typeClassification = new Classification(name: classifications.first()?.name + " Complex Types", namespace: classifications.first()?.namespace + " Complex Types").save(failOnError: true)
        classifications.add(typeClassification)

        publicTypesContainer = findModel(containerModelName)
        if (!publicTypesContainer) publicTypesContainer = new Model(name: containerModelName, description: "Container model for complex types. This is automatically generated. You can remove this container model and curate the data as you wish", status: PublishedElementStatus.PENDING).save(failOnError: true)

        if (!createModelsForElements) {
            if (!rootElementsModelName) rootElementsModelName = classifications.first()?.name + " Root Elements"
            rootElementsContainer = findModel(rootElementsModelName)
            if (!rootElementsContainer) rootElementsContainer = new Model(name: rootElementsModelName, description: "Container model for root elements. This is automatically generated. You can remove this container model and curate the data as you wish", status: PublishedElementStatus.PENDING).save(failOnError: true)
        }


        complexDataTypes.each { XsdComplexType complexType ->
            matchOrCreateModel(complexType)
        }

        modelsCreated.each { Model model ->
            if (!model.childOf) {
                model.addToChildOf(publicTypesContainer)
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

        publicTypesContainer = addClassifications(publicTypesContainer)

        if (rootElementsContainer) {
            rootElementsContainer = addClassifications(rootElementsContainer)
        }


        topLevelElements.each { XsdElement element ->
            ArrayList<Element> elements = getElementsFromXsdElement([], element)
            if (rootElementsContainer) {
                for (Element e in elements) {
                    if (e.model) {
                        Relationship rel = relationshipService.link(rootElementsContainer, e.model, RelationshipType.hierarchyType)
                        if (!rel.hasErrors()) {
                            rel.ext.Name = element.name
                        }
                    }
                }
            }
        }
    }

    protected <E extends PublishedElement> E  addClassifications(E element) {
        element.addToClassifications(classifications.first())
        element
    }

    Model matchOrCreateModel(XsdComplexType complexType, String elementName = null) {
        String modelName = createModelsForElements && elementName ? elementName : complexType.name
        log.info("Processing model for complex type ${modelName}: ${complexType?.description}")
        ArrayList<Element> elements = []
        def baseModel = ""
        def model = findModel(modelName)
        if (!model) {

            model = new Model(name: modelName, description: complexType?.description, status: PublishedElementStatus.UPDATED).save(flush: true, failOnError: true)
            model = addClassifications(model)
            modelsCreated.add(model)

            if (complexType?.restriction) (elements, baseModel) = getRestrictionDetails(complexType.restriction)

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

            if (complexType?.attributes) elements = addElements(elements, getElementsFromAttributes(complexType.attributes))

            elements.each { Element element ->
                if (element.dataElement) {
                    def relationship = model.addToContains(element.dataElement)
                    element.metadata.each { metadata ->
                        relationship.ext.put(metadata.key, metadata.value?.toString())
                    }
                } else if (element.model) {
                    def relationship = model.addToParentOf(element.model)
                    element.metadata.each { metadata ->
                        relationship.ext.put(metadata.key, metadata.value?.toString())
                    }
                }
            }

            model.status = PublishedElementStatus.PENDING

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
            unionValueDomain.addToIsBasedOn(valueDomain)
        }
        valueDomain
    }



    List<Model> findModels(String name) {
        Model.executeQuery("""
            select m from Model m left join m.classifications c
            where m.name = :name and c in :classifications
            group by m
        """, [name: name, classifications: classifications])
    }

    Model findModel(String name) {
        List<Model> models = findModels(name)
        if (models) {
            return models[0]
        }
    }

    protected ValueDomain findValueDomain(String name, DataType dataType = null, String rule = null) {
        List<ValueDomain> valueDomains = ValueDomain.findAllByNameOrNameIlike(name, "$name (in %)")

        for (ValueDomain domain in valueDomains) {
            if (dataType && domain.dataType == dataType) {
                if (conceptualDomains.intersect(domain.conceptualDomains) && domain.rule == rule) {
                        return domain
                }
            } else if (!dataType) {
                if (conceptualDomains.intersect(domain.conceptualDomains) && domain.rule == rule) {
                        return domain
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

    protected List<Element> addElement(ArrayList<Element> elements, Element element, Boolean inherited = false) {
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
        if (el.type) {
            Model complexType = !createModelsForElements ? findModel(el.type) : findModels(el.type).find {
                it.ext.from != "xs:element"
            }

            if (!complexType) {
                XsdComplexType type = inXsdComplexTypes(el.type)
                if (type) {
                    // we
                    complexType = matchOrCreateModel(type)
                }
            }

            if (complexType) {

                if (createModelsForElements) {
                    List<Model> models = findModels(el.name) ?: []
                    Model model = models.find {
                        complexType in it.isBasedOn
                    }
                    if (!model) {
                        XsdComplexType type = inXsdComplexTypes(el.type)
                        if (type) {
                            model = matchOrCreateModel(type, el.name)
                            model.addToIsBasedOn complexType
                            model.ext.from = "xs:element"
                        }
                    }
                    if (model) {
                        return addElement(elements, createElementModelFromXSDComplexElement(model, el, [
                                Type        : "xs:element",
                                Name        : el.name,
                                "Min Occurs": el.minOccurs,
                                "Max Occurs": el.maxOccurs,

                        ]))
                    }
                }

                return addElement(elements, createElementModelFromXSDComplexElement(complexType, el, [
                        Type        : "xs:element",
                        Name        : el.name,
                        "Min Occurs": el.minOccurs,
                        "Max Occurs": el.maxOccurs,

                ]))
            }
        }
        if (el.complexType) {
            return addElement(elements, createElementModelFromXSDComplexElement(el.complexType, el, [
                    Type        : "xs:element",
                    Name        : el.name,
                    "Max Occurs": el.maxOccurs,
                    "Min Occurs": el.minOccurs
            ]))

        }
        return addElement(elements, createElementFromXSDElement(el))
    }

    protected XsdComplexType inXsdComplexTypes(String type) {
        complexDataTypesByName[type]
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
        new Element(model: matchOrCreateModel(complexType, el.name), metadata: metadata + [Name: el.name])
    }

    protected Element createElementModelFromXSDComplexElement(Model mode, XsdElement el, Map metadata) {
        new Element(model: mode, metadata: metadata + [Name: el.name])
    }


    protected Model copyRelations(Model newModel, Model oldModel) {

        for (Relationship r in oldModel.incomingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession' || r.relationshipType.name == 'base' || r.relationshipType.name == 'hierarchy') continue
            def newR = relationshipService.link(r.source, newModel, r.relationshipType)
            if (newR.hasErrors()) {
                log.error("ERROR copying relationships: $newR.errors")
            } else {
                r.ext.each { key, value ->
                    newR.ext.put(key, value)
                }
            }
        }

        for (Relationship r in oldModel.outgoingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession' || r.relationshipType.name == 'base') continue
            def newR = relationshipService.link(newModel, r.destination, r.relationshipType)
            if (newR.hasErrors()) {
                log.error("ERROR copying relationships: $newR.errors")
            } else {
                r.ext.each { key, value ->
                    newR.ext.put(key, value)
                }
            }
        }

        return newModel
    }


    protected createElementFromXSDElement(XsdElement xsdElement) {
        DataElement dataElement = matchOrCreateDataElement(xsdElement.name, matchOrCreateValueDomain(xsdElement), (xsdElement.description) ?: xsdElement.section + "." + xsdElement.name)


        new Element(dataElement: dataElement, metadata: [
                "Type":         "xs:element",
                 Name:           xsdElement.name,
                "Min Occurs":    xsdElement?.minOccurs,
                "Max Occurs":    xsdElement?.maxOccurs
        ])
    }

    protected matchOrCreateDataElement(String name, ValueDomain domain, String description = null) {
        DataElement dataElement = findDataElement(name, domain)

        if (!dataElement) {
            dataElement = new DataElement(name: name, description: description, valueDomain: domain, status: PublishedElementStatus.PENDING)
            dataElement = addClassifications(dataElement)
            elementsCreated << dataElement.save(failOnError: true)
        }

        dataElement
    }

    protected DataElement findDataElement(String name, ValueDomain domain) {
        if (!name) {
            return null
        }

        def elements

        if (domain) {
            elements = DataElement.executeQuery("""
                select de from DataElement de left join de.classifications c
                where de.name = :name and de.valueDomain = :domain and c in :classifications
                group by de
            """, [name: name, domain: domain, classifications: classifications])
        } else {
            elements = DataElement.executeQuery("""
                select de from DataElement de left join de.classifications c
                where de.name = :name and de.valueDomain is null and c in :classifications
                group by de
            """, [name: name, classifications: classifications])
        }

        if (elements) {
            return elements[0]
        }
    }

    protected ValueDomain matchOrCreateValueDomain(XsdElement xsdElement) {
        if (!xsdElement)            return null
        if (xsdElement.simpleType)  return matchOrCreateValueDomain(xsdElement.simpleType)
        if (xsdElement.type) {
            ValueDomain domain = findValueDomain(xsdElement.type)

            if (domain)             return domain

            XsdSimpleType simpleType = simpleDataTypesByName[xsdElement.type]

            if (simpleType)         return matchOrCreateValueDomain(simpleType)
        }
                                    return null
    }

    protected ValueDomain matchOrCreateValueDomain(XsdAttribute attribute) {
        if (!attribute)             return null
        if (attribute.simpleType)   return matchOrCreateValueDomain(attribute.simpleType)
        if (attribute.type) {
            ValueDomain domain = findValueDomain(attribute.type)

            if (domain)             return domain

            XsdSimpleType simpleType = simpleDataTypesByName[attribute.type]

            if (simpleType)         return matchOrCreateValueDomain(simpleType)
        }
    }

    protected createElementFromAttribute(XsdAttribute attribute) {
        if (!attribute) return null

        DataElement dataElement = matchOrCreateDataElement(attribute.name,  matchOrCreateValueDomain(attribute), attribute.description ?: attribute.section + "." + attribute.name)

        def metadata = [
                Type:               XS_ATTRIBUTE,
                Name:               attribute.name,
                Form:               attribute.form,
                ID:                 attribute.id,
                Fixed:              attribute.fixed,
                Default:            attribute.defaultValue,
                Ref:                attribute.ref,
        ]

        switch (attribute.use) {
            case 'required':
                metadata["Min Occurs"] = 1
                metadata["Max Occurs"] = 1
            break;
            case 'prohibited':
                metadata["Min Occurs"] = 0
                metadata["Max Occurs"] = 0
            break;
            case 'optional':
                metadata["Min Occurs"] = 0
                metadata["Max Occurs"] = 1
            break;
        }

        new Element(dataElement: dataElement, metadata: metadata)

    }

    protected ValueDomain matchOrCreateValueDomain(XsdSimpleType simpleDataType) {
        if (!simpleDataType) {
            return null
        }
        log.info("Processing value domain for simple type ${simpleDataType.name}: ${simpleDataType.description}")
        def (dataType, rule, baseValueDomain) = getRestrictionDetails(simpleDataType.restriction, simpleDataType.name)
        def valueDomain = findValueDomain(simpleDataType.name, dataType, rule)
        if (!valueDomain) {
            valueDomain = new ValueDomain(name: simpleDataType.name, description: simpleDataType.description, dataType: dataType, rule: rule).save(flush: true, failOnError: true)
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
                XsdSimpleType foundSimpleDataType = simpleDataTypesByName[base]
                if (foundSimpleDataType) baseValueDomain = matchOrCreateValueDomain(foundSimpleDataType)
            }
            if (!baseValueDomain) {
                throw new Exception('imported Simple Type base [ ' + base + ' ] does not exist in the schema or in the system, please validate you schema or import the schema it is dependant on')
            }
            dataType = baseValueDomain.dataType
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
        if (rule1 ==  rule2) return rule1

        Set<String> rules = new LinkedHashSet<String>()

        rules.addAll(rule1.split(/\s+&&\s+/))
        rules.add rule2

        return rules.join(' && ')
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
}


class Element {
    DataElement dataElement
    Model model
    Map metadata


    void setMetadata(Map metadata) {
        if (metadata) {
            this.metadata = metadata.findAll { it.value }
        } else {
            this.metadata = [:]
        }
    }

}
