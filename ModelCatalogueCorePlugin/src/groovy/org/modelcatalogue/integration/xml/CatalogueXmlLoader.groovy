package org.modelcatalogue.integration.xml

import groovy.util.logging.Log
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild
import org.modelcatalogue.builder.api.CatalogueBuilder

@Log
class CatalogueXmlLoader {

    private static final List<String> SUPPORTED_NAMESPACE_URLS = [
            'http://www.metadataregistry.org.uk/assets/schema/1.0/metadataregistry.xsd',
            'http://www.metadataregistry.org.uk/assets/schema/1.0.1/metadataregistry.xsd',
            'http://www.metadataregistry.org.uk/assets/schema/1.0.2/metadataregistry.xsd',
            'http://www.metadataregistry.org.uk/assets/schema/1.1/metadataregistry.xsd',
            'http://www.metadataregistry.org.uk/assets/schema/1.1.1/metadataregistry.xsd',
            'http://www.metadataregistry.org.uk/assets/schema/1.1.2/metadataregistry.xsd',
            'http://www.metadataregistry.org.uk/assets/schema/1.2/metadataregistry.xsd',
    ].asImmutable()

    private static final String CATALOGUE_NAMESPACE_PREFIX = "http://www.metadataregistry.org.uk/assets/schema/"

    private static final List<String> IGNORED_ATTRS = ['href']
    private static final List<String> CATALOGUE_ELEMENT_ELEMENTS = ['classification', 'model', 'dataModel', 'dataClass','dataElement', 'valueDomain', 'dataType', 'measurementUnit', 'unitOfMeasure']

    CatalogueBuilder builder

    CatalogueXmlLoader(CatalogueBuilder builder) {
        this.builder = builder
    }


    void load(InputStream xml) {
        assert xml, "no input stream provided"
        builder.build {
            // figure out how to make this validating
            XmlSlurper xs = new XmlSlurper(false, true)
            GPathResult loaded = xs.parse(xml)
            handleRelationshipTypes(loaded)
            handleChildren(loaded)
        }
    }

    private static Map<String, Object> parameters(NodeChild element) {
        if (element.attributes().containsKey('ref')) {
            return [id: element.attributes()['ref']]
        }
        Map<String, Object> ret = [:]
        element.attributes().each { Object key, Object value ->
            if (key.toString() in IGNORED_ATTRS) {
                return
            }
            if (key.toString() == 'classification') {
                ret['dataModel'] = value
                return
            }
            ret[key.toString()] = value
        }
        ret
    }

    private void handleRelationshipTypes(GPathResult parent) {
        parent.relationshipTypes.children().each { type ->
            String name = type.@name.text()
            builder.relationshipType(name: name, source: type.@source.text(), destination: type.@destination.text(), system: 'true' == type.@system.text(), bidirectional: 'true' == type.@bidirectional.text(),  versionSpecific: 'true' == type.@versionSpecific.text()){
                sourceToDestination(type.sourceToDestination[0].@label.text(), description: type.sourceToDestination[0].text())
                destinationToSource(type.destinationToSource[0].@label.text(), description: type.destinationToSource[0].text())
                rule(type.rule[0].text())
            }
        }
    }

    private void handleChildren(GPathResult parent) {
        parent.children().each {
            handleNode(it as NodeChild)
        }
    }

    private void handleNode(NodeChild element) {
        if (!(element.namespaceURI() in SUPPORTED_NAMESPACE_URLS)) {
            if (element.namespaceURI().startsWith(CATALOGUE_NAMESPACE_PREFIX)) {
                throw new IllegalArgumentException("Unsupported Catalogue XML namespace version: ${element.namespaceURI()}")
            }
            return
        }
        switch(element.name()) {
            case 'model': handleDataClass(element) ; break
            case 'dataClass': handleDataClass(element) ; break
            case 'dataElement': handleDataElement(element) ; break
            case 'classification': handleDataModel(element) ; break
            case 'dataModel': handleDataModel(element) ; break
            case 'measurementUnit': handleUnit(element) ; break
            case 'unitOfMeasure': handleUnit(element) ; break
            case 'dataType': handleDataType(element) ; break
            case 'valueDomain': handleValueDomain(element) ; break
            case 'description': handleDescription(element) ; break
            case 'regex': handleRegex(element) ; break
            case 'rule': handleRule(element) ; break
            case 'extensions': handleChildren(element) ; break
            case 'relationships': handleChildren(element) ; break
            case 'enumerations': break // handled by data type
            case 'relationshipTypes': break // handled already by handleRelationshipTypes(GPathResult)
            case 'metadata': handleMetadata(element) ; break
            case 'archived': handleArchived(element) ; break
            case 'inherited': handleInherited(element) ; break
            case 'extension': handleExtension(element) ; break
            case 'relatedTo': handleRelationship(element, true, 'relatedTo') ; break
            case 'synonym': handleRelationship(element, true, 'synonym') ; break
            case 'basedOn': handleRelationship(element, false, 'base') ; break
            case 'to': handleRelationship(element, true) ; break
            case 'from': handleRelationship(element, true) ; break
            case 'skipDraft': handleSkipDraft(element) ; break
            case 'copyRelationships': handleCopyRelationships(element) ; break
            default: log.warning "Cannot handle element with name ${element.name()}"
        }
    }

    private void handleSkipDraft(NodeChild element) {
        if (element.text() == 'true') {
            builder.skip(builder.draft)
        }
    }
    private void handleCopyRelationships(NodeChild element) {
        if (element.text() == 'true') {
            builder.copy(builder.relationships)
        }
    }

    private void handleExtension(NodeChild element) {
        builder.ext(element.@key.text() ?: '', element.text() ?: '')
    }

    private void handleArchived(NodeChild element) {
        if (element.parent()?.name() in CATALOGUE_ELEMENT_ELEMENTS) {
            builder.relationship {
                archived(element.text() == 'true')
            }
        }
    }
    private void handleInherited(NodeChild element) {
        if (element.parent()?.name() in CATALOGUE_ELEMENT_ELEMENTS) {
            builder.relationship {
                inherited(element.text() == 'true')
            }
        }
    }

    private void handleMetadata(NodeChild element) {
        if (element.parent()?.name() in CATALOGUE_ELEMENT_ELEMENTS) {
            builder.relationship {
                element.extension.each {
                    ext(it.@key.text() ?: '', it.text() ?: '')
                }
            }
        }
    }

    private void handleRelationship(NodeChild element, boolean outgoing, String relType = element.attributes()['relationshipType']) {
        if (element.attributes().containsKey('ref')) {
            builder.rel(relType)."${outgoing ? 'to' : 'from'}"(builder.ref(element.attributes()['ref'].toString())) {
                element.metadata.extension.each() {
                    ext(it.@key.text() ?: '', it.text() ?: '')
                }
                archived(element.archived.text() == 'true')
            }
            return
        }
        if (!element.attributes().containsKey('name')) {
            log.warning "Missing attribute name for element ${relType}[${element.attributes()}]"
            return
        }
        if (element.attributes().containsKey('dataModel') || element.attributes().containsKey('classification')) {
            builder.rel(relType)."${outgoing ? 'to' : 'from'}"((element.attributes()['dataModel'] ?: element.attributes()['classification']).toString(), element.attributes()['name'].toString()) {
                element.metadata.extension.each() {
                    ext(it.@key.text() ?: '', it.text() ?: '')
                }
                archived(element.archived.text() == 'true')
            }
            return
        }
        builder.rel(relType)."${outgoing ? 'to' : 'from'}"(element.attributes()['name'].toString()) {
            element.metadata.extension.each() {
                ext(it.@key.text() ?: '', it.text() ?: '')
            }
            archived(element.archived.text() == 'true')
        }
    }

    private void handleDescription(NodeChild element) {
        builder.description(element.text())
    }

    private void handleRegex(NodeChild element) {
        builder.regex(element.text())
    }

    private void handleRule(NodeChild element) {
        builder.rule(element.text())
    }

    private void handleDataModel(NodeChild element) {
        builder.dataModel(parameters(element)) {
            handleChildren(element)
        }
    }

    private void handleValueDomain(NodeChild element) {
        builder.valueDomain(parameters(element)) {
            handleChildren(element)
        }
    }

    private void handleDataElement(NodeChild element) {
        builder.dataElement(parameters(element)) {
            handleChildren(element)
        }
    }

    private void handleDataClass(NodeChild element) {
        builder.dataClass(parameters(element)) {
            handleChildren(element)
        }
    }

    private void handleDataType(NodeChild element) {
        Map<String, Object> parameters = parameters(element)

        if (element.enumerations) {
            Map<String, String> enumerations = [:]
            element.enumerations.children().each {
                enumerations[it.@value.text()] = it.text()
            }
            parameters.enumerations = enumerations
        }

        builder.dataType(parameters) {
            handleChildren(element)
        }
    }

    private void handleUnit(NodeChild element) {
        builder.measurementUnit(parameters(element)) {
            handleChildren(element)
        }
    }

}
