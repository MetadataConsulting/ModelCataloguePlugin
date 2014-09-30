package org.modelcatalogue.core.dataarchitect

import groovy.xml.QName
import org.modelcatalogue.core.*
import org.modelcatalogue.core.dataarchitect.xsd.*


class XSDImportService {

    static transactional = false
    
    def relationshipService

    def getClassifications(XsdSchema schema, String classificationName, Collection<QName> namespaces, String description) {
        Collection<Classification> classifications = []

        //match the conceptual domain
        classifications.add(matchOrCreateClassification(classificationName, schema.targetNamespace, description))
        for (namespace in namespaces) {
            def classification = Classification.findByNamespace(namespace.namespaceURI)
            if (!classification) {
                classifications = []
                break
            } else if (!classifications.find {
                it.namespace == namespace.namespaceURI
            }) classifications.add(classification)
        }

        return classifications
    }

    def matchOrCreateClassification(String classificationName, String namespaceURI, String description) {
        Classification classification = Classification.findByNamespace(namespaceURI)
        if (!classification) classification = new Classification(name: classificationName, namespace: namespaceURI, description: description).save()
        return classification
    }

    def matchOrCreateConceptualDomain(String conceptualDomainName, String namespaceURI, String description) {
        ConceptualDomain conceptualDomain = ConceptualDomain.findByNamespace(namespaceURI)
        if (!conceptualDomain) conceptualDomain = new ConceptualDomain(name: conceptualDomainName, namespace: namespaceURI, description: description).save()
        return conceptualDomain
    }

    def getConceptualDomains(XsdSchema schema, String conceptualDomainName, Collection<QName> namespaces, String description) {
        Collection<ConceptualDomain> conceptualDomains = []

        //match the conceptual domain
        conceptualDomains.add(matchOrCreateConceptualDomain(conceptualDomainName, schema.targetNamespace, description))
        for (namespace in namespaces) {
            def conceptualDomain = ConceptualDomain.findByNamespace(namespace.namespaceURI)
            if (!conceptualDomain) {
                conceptualDomains = []
                break
            } else if (!conceptualDomains.find {
                it.namespace == namespace.namespaceURI
            }) conceptualDomains.add(conceptualDomain)
        }

        return conceptualDomains
    }

    def createAll(Collection<XsdSimpleType> simpleDataTypes, Collection<XsdComplexType> complexDataTypes, Collection<XsdElement> topLevelElements, String classificationName, String conceptualDomainName, XsdSchema schema, Collection<QName> namespaces, Boolean createModelsForElements = false) {

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

                new XSDImporter(
                        simpleDataTypes:            simpleDataTypes,
                        complexDataTypes:           complexDataTypes,
                        topLevelElements:           topLevelElements,
                        classifications:            classifications,
                        conceptualDomains:          conceptualDomains,
                        createModelsForElements:    createModelsForElements,

                        relationshipService:        relationshipService
                ).createAll()
            }

            return [classifications.first(), conceptualDomains.first()]

        } catch (e) {
            log.error "Exception during import", e
            throw e
        }
    }



}
