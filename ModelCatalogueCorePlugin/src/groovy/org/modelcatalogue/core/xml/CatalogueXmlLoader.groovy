package org.modelcatalogue.core.xml

import groovy.util.logging.Log4j
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.util.builder.CatalogueBuilder


@Log4j
class CatalogueXmlLoader {

    private static final List<String> IGNORED_ATTRS = ['href']

    CatalogueBuilder builder

    CatalogueXmlLoader(CatalogueBuilder builder) {
        this.builder = builder
    }


    Set<CatalogueElement> load(InputStream xml) {
        assert xml, "no input stream provided"
        builder.build {
            // figure out how to make this validating
            XmlSlurper xs = new XmlSlurper(false, true)
            handleChildren(xs.parse(xml))
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
            ret[key.toString()] = value
        }
        ret
    }

    private void handleChildren(GPathResult parent) {
        parent.children().each {
            handleNode(it as NodeChild)
        }
    }

    private void handleNode(NodeChild element) {
        if (element.namespaceURI() != CatalogueXmlPrinter.NAMESPACE_URL) {
            return
        }
        switch(element.name()) {
            case 'model': handleModel(element) ; break
            case 'dataElement': handleDataElement(element) ; break
            case 'classification': handleClassification(element) ; break
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
            case 'extension': handleExtension(element) ; break
            case 'relatedTo': handleRelationship(element, true, 'relatedTo') ; break
            case 'synonym': handleRelationship(element, true, 'synonym') ; break
            case 'basedOn': handleRelationship(element, false, 'base') ; break
            case 'to': handleRelationship(element, true) ; break
            case 'from': handleRelationship(element, true) ; break
            default: log.warn "Cannot handle element with name ${element.name()}"
        }
    }



    private void handleExtension(NodeChild element) {
        builder.ext(element.@key.text(), element.text())
    }

    private void handleRelationship(NodeChild element, boolean outgoing, String relType = element.attributes()['relationshipType']) {
        if (element.attributes().containsKey('ref')) {
            builder.rel(relType)."${outgoing ? 'to' : 'from'}"(builder.ref(element.attributes()['ref'].toString()))
            return
        }
        if (!element.attributes().containsKey('name')) {
            log.warn "Missing attribute name for element ${relType}[${element.attributes()}]"
            return
        }
        if (element.attributes().containsKey('classification')) {
            builder.rel(relType)."${outgoing ? 'to' : 'from'}"(element.attributes()['classification'].toString(), element.attributes()['name'].toString())
            return
        }
        builder.rel(relType)."${outgoing ? 'to' : 'from'}"(element.attributes()['name'].toString())
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

    private void handleClassification(NodeChild element) {
        builder.classification(parameters(element)) {
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

    private void handleModel(NodeChild element) {
        builder.model(parameters(element)) {
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
