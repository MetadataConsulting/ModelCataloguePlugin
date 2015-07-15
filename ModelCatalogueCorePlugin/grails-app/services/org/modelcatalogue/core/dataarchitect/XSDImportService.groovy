package org.modelcatalogue.core.dataarchitect

import groovy.xml.QName
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.dataarchitect.xsd.*

class XSDImportService {

    static transactional = false
    
    def relationshipService
    def classificationService

    def getClassifications(XsdSchema schema, String classificationName, Collection<QName> namespaces, String description) {
        Collection<DataModel> classifications = []

        //match the conceptual domain
        classifications.add(matchOrCreateClassification(classificationName, schema.targetNamespace, description))
        for (namespace in namespaces) {
            def classification = DataModel.findByNamespace(namespace.namespaceURI)
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
        DataModel classification = DataModel.findByNamespace(namespaceURI)
        if (!classification) classification = new DataModel(name: classificationName, namespace: namespaceURI, description: description).save()
        return classification
    }

    Collection<DataModel> createAll(Collection<XsdSimpleType> simpleDataTypes, Collection<XsdComplexType> complexDataTypes, Collection<XsdElement> topLevelElements, String classificationName, String conceptualDomainName, XsdSchema schema, Collection<QName> namespaces, Boolean createModelsForElements = false) {

        try {
            Collection<DataModel> classifications = []
            def description = null
            if (schema.targetNamespace) {
                description = "Generated from Schema........ \r\n Info: \r\n targetNamespace: " + schema.targetNamespace + "\r\n"
                if (schema.attributeFormDefault) description += "attributeFormDefault: " + schema.attributeFormDefault + "\r\n"
                if (schema.blockDefault) description += "blockDefault: " + schema.blockDefault + "\r\n"
                if (schema.elementFormDefault) description += "attributeFormDefault: " + schema.elementFormDefault + "\r\n"
                if (schema.finalDefault) description += "attributeFormDefault: " + schema.finalDefault + "\r\n"
                if (schema.id) description += "attributeFormDefault: " + schema.id + "\r\n"
                if (schema.version) description += "attributeFormDefault: " + schema.version + "\r\n"
            }

            classifications = getClassifications(schema, classificationName, namespaces, description)

            //if the getConceptualDomains returns empty array i.e. some namespaces don't exist
            if (classifications.size() > 0) {

                new XSDImporter(
                        simpleDataTypes:            simpleDataTypes,
                        complexDataTypes:           complexDataTypes,
                        topLevelElements:           topLevelElements,
                        classifications:            classifications,
                        createModelsForElements:    createModelsForElements,

                        relationshipService: relationshipService,
                        classificationService: classificationService
                ).createAll()
            }

            classifications
        } catch (e) {
            log.error "Exception during import", e
            throw e
        }
    }



}
