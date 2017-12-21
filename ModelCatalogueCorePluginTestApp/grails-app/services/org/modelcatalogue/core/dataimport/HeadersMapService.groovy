package org.modelcatalogue.core.dataimport

class HeadersMapService {

    /**
     * This parses an XML file that contains a headersMap (maps logical to physical column names)
     * It also assigns the result to a class variable which is used as a default map if none is passed
     * @param xmlInput
     * @return the parsed XML file as a headers map, or null if XML file was not a headersMap
     */
    Map<String, String> parseXml(groovy.util.slurpersupport.GPathResult xml) {
        if (xml.name() == 'headersMap') {
            Map<String, String> hdrMap = [:]
            List<String> metadataKeys = []
            for (groovy.util.slurpersupport.Node n in xml.childNodes()) {
                if (n.name == 'metadata') {
                    metadataKeys += n.text()
                } else if (n.text()) {
                    hdrMap[n.name()] = n.text()
                }
            }
            hdrMap['metadata'] = metadataKeys
            return hdrMap
        } else {
            return null
        }
    }

    Map<String, String> headersMap(InputStream xmlInput) {
        parseXml(xmlInput)
    }

    /**
     * This parses an XML file that contains a headersMap (maps logical to physical column names)
     * It also assigns the result to a class variable which is used as a default map if none is passed
     * @param xmlInput
     * @return the parsed XML file as a headers map, or null if XML file was not a headersMap
     */
    Map<String, String> parseXml(InputStream xmlInput) {
        parseXml(new XmlSlurper().parse(xmlInput))
    }

    /**
     * This parses an XML file that contains a headersMap (maps logical to physical column names)
     * It also assigns the result to a class variable which is used as a default map if none is passed
     * @param xmlReader
     * @return the parsed XML file as a headers map, or null if XML file was not a headersMap
     */
    Map<String, String> parseXml(Reader xmlReader) {
        parseXml(new XmlSlurper().parse(xmlReader))
    }
}
