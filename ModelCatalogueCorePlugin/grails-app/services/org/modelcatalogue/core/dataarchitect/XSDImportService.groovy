package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import groovy.xml.QName
import org.hibernate.annotations.FetchMode
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElement
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.dataarchitect.xsd.XsdAttribute
import org.modelcatalogue.core.dataarchitect.xsd.XsdChoice
import org.modelcatalogue.core.dataarchitect.xsd.XsdComplexContent
import org.modelcatalogue.core.dataarchitect.xsd.XsdComplexType
import org.modelcatalogue.core.dataarchitect.xsd.XsdElement
import org.modelcatalogue.core.dataarchitect.xsd.XsdExtension
import org.modelcatalogue.core.dataarchitect.xsd.XsdGroup
import org.modelcatalogue.core.dataarchitect.xsd.XsdPattern
import org.modelcatalogue.core.dataarchitect.xsd.XsdRestriction
import org.modelcatalogue.core.dataarchitect.xsd.XsdSchema
import org.modelcatalogue.core.dataarchitect.xsd.XsdSequence
import org.modelcatalogue.core.dataarchitect.xsd.XsdSimpleType
import org.modelcatalogue.core.dataarchitect.xsd.XsdUnion
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.RelationshipDirection

import javax.persistence.criteria.JoinType

@Transactional
class XSDImportService {

    def relationshipService, modelService
    ArrayList<Model> circularModels = []
    ArrayList<Model> modelsCreated = []
    private static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]


    def getClassifications(XsdSchema schema, String classificationName, Collection<QName> namespaces, String description){
        Collection<Classification> classifications = []

        //match the conceptual domain
        classifications.add(matchOrCreateClassification(classificationName, schema.targetNamespace, description))
        for (namespace in namespaces) {
            def classification = Classification.findByNamespace(namespace.namespaceURI)
            if (!classification){
                classifications = []
                break
            }else if(!classifications.find{it.namespace == namespace.namespaceURI}) classifications.add(classification)
        }

        return classifications
    }

    def matchOrCreateClassification(String classificationName, String namespaceURI, String description){
        Classification classification = Classification.findByNamespace(namespaceURI)
        if(!classification) classification = new Classification(name: classificationName, namespace: namespaceURI, description: description).save()
        return classification
    }

    def matchOrCreateConceptualDomain(String conceptualDomainName, String namespaceURI, String description){
        ConceptualDomain conceptualDomain = ConceptualDomain.findByNamespace(namespaceURI)
        if(!conceptualDomain) conceptualDomain = new ConceptualDomain(name: conceptualDomainName, namespace: namespaceURI, description: description).save()
        return conceptualDomain
    }

    def getConceptualDomains(XsdSchema schema, String conceptualDomainName, Collection<QName> namespaces, String description){
        Collection<ConceptualDomain> conceptualDomains = []

        //match the conceptual domain
        conceptualDomains.add(matchOrCreateConceptualDomain(conceptualDomainName, schema.targetNamespace, description))
        for (namespace in namespaces) {
            def conceptualDomain = ConceptualDomain.findByNamespace(namespace.namespaceURI)
            if (!conceptualDomain){
                conceptualDomains = []
                break
            }else if(!conceptualDomains.find{it.namespace == namespace.namespaceURI}) conceptualDomains.add(conceptualDomain)
        }

        return conceptualDomains
    }

    def createAll(Collection<XsdSimpleType> simpleDataTypes, Collection<XsdComplexType> complexDataTypes, Collection<XsdElement> topLevelElements, String classificationName, String conceptualDomainName, XsdSchema schema, Collection<QName> namespaces){

        try {
            Collection<Classification> classifications = []
            Collection<ConceptualDomain> conceptualDomains = []
            def description
            if (schema.targetNamespace) {
                description = "Generated from Schema........ \r\n Info: \r\n targetNamespace: " + schema.targetNamespace + "\r\n"
                if (schema.attributeFormDefault) description += "attributeFormDefault: " + schema.attributeFormDefault + "\r\n"
                if (schema.blockDefault) description += "blockDefault: " + schema.blockDefault + "\r\n"
                if (schema.elementFormDefault) description += "attributeFormDefault: " + schema.elementFormDefault + "\r\n"
                if (schema.finalDefault) description += "attributeFormDefault: " + schema.finalDefault + "\r\n"
                if (schema.id) description += "attributeFormDefault: " + schema.id + "\r\n"
                if (schema.version) description += "attributeFormDefault: " + schema.version + "\r\n"
            }

            conceptualDomains = getConceptualDomains(schema, conceptualDomainName, namespaces, description)
            classifications = getClassifications(schema, classificationName, namespaces, description)

            //if the getConceptualDomains returns empty array i.e. some namespaces don't exist
            if (conceptualDomains.size() > 0 && classifications.size() > 0) {

                //make sure the new classifications from the new namespace are related to one another
                classifications.first().addToRelatedTo(conceptualDomains.first())

                createValueDomainsAndDataTypes(simpleDataTypes, conceptualDomains)
                createModelsAndElements(complexDataTypes, classifications, conceptualDomains, topLevelElements)
            }

            return [classifications.first(), conceptualDomains.first()]

        }catch(e){
            log.error "Exception during import", e
            throw e
        }
    }

    def createValueDomainsAndDataTypes(Collection<XsdSimpleType> simpleDataTypes, Collection<ConceptualDomain> conceptualDomains){

        simpleDataTypes.each{ XsdSimpleType simpleDataType ->
            matchOrCreateValueDomain(simpleDataType, simpleDataTypes, conceptualDomains)
        }

    }

    def createModelsAndElements(Collection<XsdComplexType> complexDataTypes, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdElement> topLevelElements, String containerModelName = ""){

        if(!containerModelName) containerModelName = classifications.first()?.name + " types"
        Classification typeClassification = Classification.findByNamespace(classifications.first()?.namespace + " types")
        if(!typeClassification) typeClassification = new Classification(name: classifications.first()?.name + " types", namespace: classifications.first()?.namespace + " types" ).save()
        classifications.add(typeClassification)

        Model containerModel = findModel(containerModelName, classifications)
        if(!containerModel) containerModel = new Model(name:  containerModelName , description: "Container model for complex types. This is automatically generated. You can remove this container model and curate the data as you wish").save()
        complexDataTypes.each{ XsdComplexType complexType ->
            Model model = matchOrCreateModel(complexType, classifications, conceptualDomains, complexDataTypes)
        }

        modelsCreated.each{ Model model->
            if(!model.childOf) {
                model.addToChildOf(containerModel)
                model.addToClassifications(typeClassification)
            }
        }

        circularModels.each{ Model model ->
            def isBasedOn = model.isBasedOn.first()
            def isBasedOnContains = (!isBasedOn)?:isBasedOn.contains
            def modelContains = model.contains
            isBasedOnContains.each{ DataElement de->
                def contained = modelContains.find{it.name==de.name}
                if(!contained) model.addToContains(de)
            }
            def isBasedOnChildren = (!isBasedOn)?:isBasedOn.parentOf
            def modelChildren = model.parentOf
            isBasedOnChildren.each{ Model md->
                def contained = modelChildren.find{it.name==md.name}
                if(!contained) model.addToParentOf(md)
            }
        }

        containerModel = addClassifications(containerModel, classifications)

        topLevelElements.each{ XsdElement element ->
            ArrayList<Element> elements = []
            elements = getElementsFromXsdElement(elements, element, classifications, conceptualDomains, complexDataTypes)
        }
    }

    protected addClassifications(PublishedElement element, Collection<Classification> classifications){
        element.addToClassifications(classifications.first())
        return element
    }

    def matchOrCreateModel(XsdComplexType complexType, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes){
        ArrayList<Element> elements = []
        def baseModel = ""
        def model = findModel(complexType.name, classifications)
        if(!model){

            model = new Model(name: complexType.name, description: complexType.description, status: PublishedElementStatus.UPDATED).save(flush:true, failOnError: true)
            model = addClassifications(model, classifications)
            modelsCreated.add(model)

            if(complexType?.restriction) (elements, baseModel) = getRestrictionDetails(complexType.restriction, classifications, complexType.name, conceptualDomains, complexDataTypes)

            if(complexType?.sequence) elements = addElements(elements, getElementsFromSequence(complexType.sequence, classifications, conceptualDomains, complexDataTypes))

            if(complexType?.complexContent){
                def complexElements, complexBase
                (complexElements, complexBase) = getComplexContentDetails(complexType.complexContent, classifications, conceptualDomains, complexDataTypes)
                elements = addElements(elements, complexElements)
                if(complexBase) baseModel = complexBase
            }

            if(baseModel) {
                model.addToIsBasedOn(baseModel)
                elements = addElements(elements, getElementsFromModel(baseModel), true)
            }

            if(complexType.attributes) elements = addElements(elements, getElementsFromAttributes(complexType.attributes, conceptualDomains, classifications))

            elements.each{ Element element ->
                if(element.dataElement) {
                    def relationship = model.addToContains(element.dataElement)
                    element.metadata.each { metadata ->
                        relationship.ext.put(metadata.key, metadata.value)
                    }
                }else if(element.model){
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

    protected getElementsFromModel(Model model){
        ArrayList<Element> elements = []
        ListWithTotal<Relationship> containedElements = relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, model, RelationshipType.containmentType)
        containedElements.items.each{ Relationship relationship ->
            def element = new Element()
            element.dataElement  = relationship.destination
            element.metadata = relationship.ext
            elements.add(element)
        }
        ListWithTotal<Relationship> childElements = relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, model, RelationshipType.hierarchyType)
        childElements.items.each{ Relationship relationship ->
            def element = new Element()
            element.model  = relationship.destination
            element.metadata = relationship.ext
            elements.add(element)
        }
        return elements
    }


    protected matchOrCreateValueDomain(XsdSimpleType simpleDataType, Collection<XsdSimpleType> simpleDataTypes, Collection<ConceptualDomain> conceptualDomains){
        def valueDomain = findValueDomain(simpleDataType.name, conceptualDomains)
        if(!valueDomain) {
            def (dataType, rule, baseValueDomain) = getRestrictionDetails(simpleDataType.restriction, simpleDataTypes, conceptualDomains, simpleDataType.name)
            valueDomain = new ValueDomain(name: simpleDataType.name, description: simpleDataType.description, dataType: dataType, rule: rule).save(flush: true, failOnError: true)
            valueDomain.addToConceptualDomains(conceptualDomains.first())

            if (baseValueDomain) valueDomain.addToIsBasedOn(baseValueDomain)
            if (simpleDataType.union) valueDomain = addUnions(valueDomain, simpleDataType.union, simpleDataTypes, conceptualDomains)

            //TODO: get metadata(is there any?)
        }
        return valueDomain
    }

    protected addUnions(ValueDomain valueDomain, XsdUnion union, Collection<XsdSimpleType> simpleDataTypes, Collection<ConceptualDomain> conceptualDomains){
        union.simpleTypes.each{simpleDataType ->
            def unionValueDomain = matchOrCreateValueDomain(simpleDataType, simpleDataTypes, conceptualDomains)
            unionValueDomain.addToIsUnitedIn(valueDomain)
        }
        valueDomain
    }

    def findModel(String name, Collection<Classification> classifications){
        def models, model
        models = Model.findAllByName(name)
        models.each{ Model md ->
            classifications.each { Classification classification ->
                if (md?.classifications.contains(classification)) model = md
            }
        }
        return model
    }

    protected findValueDomain(String name, Collection<ConceptualDomain> conceptualDomains){
        def valueDomains, valueDomain
        valueDomains = ValueDomain.findAllByName(name)
        if(valueDomains.size()==0) valueDomains = ValueDomain.findAllByNameIlike(name)
        //TODO: change to for loop
        valueDomains.find{ ValueDomain vd->
            conceptualDomains.each{ ConceptualDomain conceptualDomain ->
                if(vd?.conceptualDomains.contains(conceptualDomain)) valueDomain = vd
            }
        }

        return valueDomain
    }

    protected findDataElement(String name, String description, ValueDomain valueDomain, Collection<Classification> classifications){
        def dataElements, dataElement
        dataElements = DataElement.findAllByNameAndDescriptionAndValueDomain(name, description, valueDomain)
        dataElements.each{ DataElement de->
            classifications.each { Classification classification ->
                if (dataElements.classifcation.contains(classification)) dataElement = de
            }
        }
        return dataElement
    }

    protected getComplexContentDetails(XsdComplexContent complexContent, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes){

        ArrayList<Element> elements = []
        def baseModel = ""
        if(complexContent?.restriction) (elements, baseModel) = getRestrictionDetails(complexContent.restriction, classifications, conceptualDomains, complexDataTypes)
        if(complexContent?.extension){
            def extElements, extBaseModel
            (extElements, extBaseModel) = getExtensionDetails(complexContent.extension, classifications, conceptualDomains, complexDataTypes)
            elements = addElements(elements, extElements)
            baseModel = extBaseModel
        }
        if(complexContent?.attributes) {
            elements = addElements(elements, getElementsFromAttributes(complexContent.attributes, conceptualDomains, classifications))
        }

        return [elements, baseModel]
    }


    protected getExtensionDetails(XsdExtension extension, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes){

        ArrayList<Element> elements = []
        def baseModel = extension?.base
        if(baseModel) baseModel = getBaseModel(baseModel, classifications, conceptualDomains, complexDataTypes) else baseModel = ""

        if(extension?.restriction){
            def restrictionBase
           (elements, restrictionBase) = getRestrictionDetails(extension.restriction, classifications, conceptualDomains, complexDataTypes)
            if(restrictionBase) baseModel = restrictionBase
        }
        if(extension?.group) elements = addElements(elements, getElementsFromGroup(extension.group, classifications, conceptualDomains, complexDataTypes))
        if(extension?.choice) elements = addElements(elements, getElementsFromChoice(extension.choice, classifications, conceptualDomains, complexDataTypes))
        if(extension?.sequence) elements = addElements(elements, getElementsFromSequence(extension.sequence, classifications, conceptualDomains, complexDataTypes))
        if(extension?.attributes) {
            def els = getElementsFromAttributes(extension.attributes, conceptualDomains, classifications)
            elements = addElements(elements, els)
        }

        return [elements, baseModel]

    }

    protected getBaseModel(String base, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes){
        def baseModel
        def complexType = inXsdComplexTypes(base, complexDataTypes)
        if(complexType) baseModel = matchOrCreateModel(complexType, classifications, conceptualDomains, complexDataTypes) else baseModel = findModel(base, classifications)

        return baseModel
    }

    protected ArrayList<Element> addElements(ArrayList<Element> elements, ArrayList<Element> elementsToAdd, Boolean inherited = false){
        elementsToAdd.each{ Element element->
            elements = addElement(elements, element, inherited)
        }
        return elements
    }

    protected ArrayList<Element> addElement(ArrayList<Element> elements, Element element, Boolean inherited = false){
        Element overrideElement
        if(element?.dataElement) overrideElement = elements.find{ it?.dataElement &&  it?.dataElement.name == element?.dataElement.name}
        if(element?.model) overrideElement = elements.find{ it?.model &&  it?.model.name == element?.model.name}
        //remove overriden element (if overriden)
        if(!inherited || ! overrideElement){
            elements.add(element)
        }

        return elements
    }

    protected getRestrictionDetails(XsdRestriction restriction, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes){

        ArrayList<Element> elements = []
        String base = restriction?.base
        def baseModel = ""

        if(base){
            baseModel = getBaseModel(base, classifications, conceptualDomains, complexDataTypes)
            elements = getElements(restriction, classifications, conceptualDomains, complexDataTypes)
            [elements, baseModel]

        }else{
            elements = getElements(restriction, classifications, conceptualDomains, complexDataTypes)
            [elements, baseModel]
        }

    }

    protected ArrayList<Element> getElements(XsdRestriction restriction, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes){
        ArrayList<Element> elements = []
        if(restriction?.attributes) elements = getElementsFromAttributes(restriction.attributes, conceptualDomains, classifications)
        if(restriction?.sequence) elements = addElements(elements, getElementsFromSequence(restriction.sequence, classifications, conceptualDomains, complexDataTypes))
        return elements
    }

    protected getElementsFromChoice(XsdChoice choice, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes){
        ArrayList<Element> elements = []

        choice.choiceElements.each{ XsdChoice ch ->
            elements = addElements(elements, getElementsFromChoice(ch, classifications, conceptualDomains, complexDataTypes))
        }

        choice.sequenceElements.each { XsdSequence seq ->
           elements = addElements(elements, getElementsFromSequence(seq, classifications, conceptualDomains, complexDataTypes))
        }

        choice.groupElements.each { XsdGroup gr ->
            elements = addElements(elements, getElementsFromGroup(gr, classifications, conceptualDomains, complexDataTypes))
        }

        choice.elements.each{ XsdElement el ->
            getElementsFromXsdElement(elements, el, classifications, conceptualDomains, complexDataTypes)
        }

        return elements
    }


    protected getElementsFromGroup(XsdGroup group, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes){
        ArrayList<Element> elements = []

        if(group?.choice) addElements(elements, getElementsFromChoice(group.choice, classifications, conceptualDomains, complexDataTypes))
        if(group?.sequence) addElements(elements, getElementsFromSequence(group.sequence, classifications, conceptualDomains, complexDataTypes))

        return elements
    }

    protected getElementsFromSequence(XsdSequence sequence, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes){
        ArrayList<Element> elements = []

        sequence.choiceElements.each{ XsdChoice ch ->
            elements = addElements(elements, getElementsFromChoice(ch, classifications, conceptualDomains, complexDataTypes))
        }

        sequence.sequenceElements.each { XsdSequence seq ->
            elements = addElements(elements, getElementsFromSequence(seq, classifications, conceptualDomains, complexDataTypes))
        }

        sequence.groupElements.each { XsdGroup gr ->
            elements = addElements(elements, getElementsFromGroup(gr, classifications, conceptualDomains, complexDataTypes))
        }


        sequence.elements.each{ XsdElement el ->
            elements = getElementsFromXsdElement(elements, el, classifications, conceptualDomains, complexDataTypes)
        }

        return elements
    }

    protected getElementsFromXsdElement(ArrayList<Element> elements, XsdElement el, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes){
        def complexType
        if(el.type) complexType = inXsdComplexTypes(el.type, complexDataTypes)

        if(complexType){
            def metadata = [:]
            if(el?.minOccurs) metadata.put("Min Occurs", el.minOccurs)
            if(el?.maxOccurs) metadata.put("Max Occurs", el.maxOccurs)
            elements = addElement(elements, createElementModelFromXSDComplexElement(complexType, el, classifications, conceptualDomains, complexDataTypes, metadata))
        } else {
            complexType = findModel(el.type, classifications)
            if(complexType){
                def metadata = [:]
                if(el?.minOccurs) metadata.put("Min Occurs", el.minOccurs)
                if(el?.maxOccurs) metadata.put("Max Occurs", el.maxOccurs)
                elements = addElement(elements, createElementModelFromXSDComplexElement(complexType, el, classifications, conceptualDomains, complexDataTypes, metadata))
            }else {
                elements = addElement(elements, createElementFromXSDElement(el, classifications, conceptualDomains))
            }
        }

        return elements
    }

    protected inXsdComplexTypes(String type, Collection<XsdComplexType> complexDataTypes){
        XsdComplexType complexType = complexDataTypes.find{it.name==type}
        return complexType
    }

    protected getElementsFromAttributes(ArrayList <XsdAttribute> attributes, Collection<ConceptualDomain> conceptualDomains, Collection<Classification> classifications){
        ArrayList<Element> elements = []

        attributes.each{ XsdAttribute attribute ->
            Element element = createElementFromAttribute(attribute, conceptualDomains, classifications)
            elements = addElement(elements, element)
        }

        return elements

    }

    protected Element createElementModelFromXSDComplexElement(XsdComplexType complexType, XsdElement el, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes, Map metadata = [:]){
        Model oldModel = matchOrCreateModel(complexType, classifications, conceptualDomains, complexDataTypes)
        Model newModel = new Model(name: el.name, description: el.description).save()
        modelsCreated.add(newModel)
        if(oldModel?.status == PublishedElementStatus.UPDATED) circularModels.add(newModel)
        newModel = addClassifications(newModel, classifications)
        newModel = copyRelations(newModel, oldModel)
        newModel.addToIsBasedOn(oldModel)
        def element = new Element()
        element.model = newModel
        element.metadata = metadata
        return element
    }

    protected Element createElementModelFromXSDComplexElement(Model oldModel, XsdElement el, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes, Map metadata = [:]){

        def newModel = new Model(name: el.name, description: el.description).save()
        modelsCreated.add(newModel)
        newModel = addClassifications(newModel, classifications)
        newModel = copyRelations(newModel, oldModel)
        newModel.addToIsBasedOn(oldModel)
        def element = new Element()
        element.model = newModel
        element.metadata = metadata
        return element
    }


    protected Model copyRelations(Model newModel, Model oldModel){

        for (Relationship r in oldModel.incomingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession' || r.relationshipType.name == 'base'  || r.relationshipType.name == 'hierarchy' ) continue
            def newR = relationshipService.link(r.source, newModel, r.relationshipType)
            r.ext.each{ key, value ->
                newR.ext.put(key, value)
            }
        }

        for (Relationship r in oldModel.outgoingRelationships) {
            if (r.archived || r.relationshipType.name == 'supersession' || r.relationshipType.name == 'base' ) continue
            def newR =  relationshipService.link(newModel, r.destination, r.relationshipType)
            r.ext.each{ key, value ->
                newR.ext.put(key, value)
            }
        }

        return newModel
    }


    protected createElementFromXSDComplexElement(XsdComplexType complexType, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains, Collection<XsdComplexType> complexDataTypes, Map metadata = [:]){
        def model = matchOrCreateModel(complexType, classifications, conceptualDomains, complexDataTypes)
        def element = new Element()
        element.model = model
        element.metadata = metadata
        return element
    }



    protected createElementFromXSDElement(XsdElement xsdElement, Collection<Classification> classifications, Collection<ConceptualDomain> conceptualDomains){
        Element element = new Element()
        ValueDomain valueDomain
        def description = (xsdElement.description)?: xsdElement.section + "." + xsdElement.name
        DataElement dataElement = new DataElement(name: xsdElement.name, description: description)
        dataElement = addClassifications(dataElement, classifications)
        if(xsdElement?.type)  valueDomain = findValueDomain(xsdElement.type, conceptualDomains)
        else if(xsdElement?.simpleType) valueDomain = findValueDomain(xsdElement.name, conceptualDomains)
        dataElement.valueDomain = valueDomain
        dataElement.save()
        def metadata = ["Type":"xs:element"]
        if(xsdElement?.minOccurs) metadata.put("Min Occurs", xsdElement.minOccurs)
        if(xsdElement?.maxOccurs) metadata.put("Max Occurs", xsdElement.maxOccurs)
        element.metadata = metadata
        element.dataElement = dataElement
        return element
    }

    protected createElementFromAttribute(XsdAttribute attribute, Collection<ConceptualDomain> conceptualDomains, Collection<Classification> classifications){

        Element element = new Element()
        ValueDomain valueDomain
        def description = (attribute.description)?: attribute.section + "." + attribute.name
        DataElement dataElement = new DataElement(name: attribute.name, description: description).save()
        dataElement = addClassifications(dataElement, classifications)
        if(attribute?.defaultValue) dataElement.ext.put("defaultValue",attribute.defaultValue)
        if(attribute?.fixed) dataElement.ext.put("fixed",attribute.fixed)
        if(attribute?.id) dataElement.ext.put("id",attribute.id)
        if(attribute?.form) dataElement.ext.put("defaultValue",attribute.form)
        if(attribute?.ref) dataElement.ext.put("defaultValue",attribute.ref)
        if(attribute?.type)  valueDomain = findValueDomain(attribute.type, conceptualDomains)
        else if(attribute?.simpleType) valueDomain = findValueDomain(attribute.name, conceptualDomains)
        dataElement.valueDomain = valueDomain
        dataElement.save()
        def metadata = ["Type":"xs:attribute"]
        if(attribute?.use) metadata.put("use", attribute.use)
        element.metadata = metadata
        element.dataElement = dataElement
        return element

    }


    protected getRestrictionDetails(XsdRestriction restriction, Collection<XsdSimpleType> simpleDataTypes, Collection<ConceptualDomain> conceptualDomains, String simpleTypeName){

        def dataType
        String rule
        String base = restriction?.base
        def baseValueDomain
        if(restriction?.patterns) rule = getRuleFromPattern(restriction.patterns)

        if(base && base.contains("xs:")){
            dataType = DataType.findByName(base)
            return [dataType, rule, ""]
        }else if(base){
            baseValueDomain = findValueDomain(base, conceptualDomains)
            if(!baseValueDomain) {
                XsdSimpleType simpleDataType = simpleDataTypes.find{it.name==base}
                if(simpleDataType) baseValueDomain = matchOrCreateValueDomain(simpleDataType, simpleDataTypes, conceptualDomains)
            }
            if(!baseValueDomain){ throw new Exception('imported Simple Type base [ '+ base +' ] does not exist in the schema or in the system, please validate you schema or import the schema it is dependant on')}
            dataType = baseValueDomain.dataType
            if(rule && baseValueDomain.rule){
                rule = addToRule(rule, baseValueDomain.rule)
            }else if(!rule && baseValueDomain.rule){
                rule = baseValueDomain.rule
            }
        }

        if(restriction?.enumeration) dataType =  createOrMatchEnumeratedType(simpleTypeName, restriction.enumeration)

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
    Model model
    Map metadata

}
