package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.CatalogueBuilder
import org.modelcatalogue.core.dataarchitect.DataImportService
import org.springframework.web.multipart.MultipartFile

@Transactional
class LoincImportService {

    static transactional = true
    def grailsApplication
    CatalogueBuilder catalogueBuilder

    private static final QUOTED_CHARS = [
            "\\": "&#92;",
            ":": "&#58;",
            "|": "&#124;",
            "%": "&#37;",
    ]

    def serviceMethod(InputStream loinc ) {

        loinc.toCsvReader([charset: 'UTF-8', skipLines: 1]).eachLine { tokens ->
            catalogueBuilder.build {
                classification(name: "LOINC") {
                    globalSearchFor dataType
                    description("LOINC conceptual domain")
                    model(name: "LOINC Datasets") {
                        model(name: tokens[5]) {
                            model(name: tokens[8]) {
                                dataElement(name: tokens[2], description: tokens[4]) {
                                    valueDomain(name: tokens[29].replaceAll("\\s", "_")) {
                                        //DataImportService.importDataTypes(catalogueBuilder, tokens[3], [tokens[5]])
                                    }
                                    ext "Loinc Ref:", tokens[1]
                                    ext "Source:", tokens[9]
                                    ext "Date Last Changed", tokens[10]
                                    ext "Property", tokens[3]
                                    ext "Scale Type", tokens[6]
                                    ext "Method Type", tokens[7]
                                    ext "Related Names", tokens[28]
                                    ext "Under Observation", tokens[30]
                                    ext "Example Units", tokens[34]
                                    ext "HL7 v2 Datatype", tokens[36]
                                    ext "HL7 v3 Datatype", tokens[37]
                                    ext "Example UCUM Units", tokens[40]
                                }
                            }

                        }
                    }
                }
            }

            println "importing : first token" + tokens[0] + ":STUFF"
        }
    }




    static Map<String,String> parseEnumeration(String[] lines){
        Map enumerations = new HashMap()

        lines.each { enumeratedValues ->

            def EV = enumeratedValues.split(":")

            if (EV != null && EV.size() > 1 && EV[0] != null && EV[1] != null) {
                def key = EV[0]
                def value = EV[1]

                if (value.size() > 244) {
                    value = value[0..244]
                }

                key = key.trim()
                value = value.trim()


                enumerations.put(key, value)
            }
        }
        return enumerations
    }

    /**
     *
     * @param name data element/item name
     * @param dataType - Column F - content of - either blank or an enumeration or a named datatype.
     * @return
     */
    private static importDataTypes(CatalogueBuilder catalogueBuilder, name, dataType) {

        //default data type to return is the string data type
        for (line in dataType) {
            String[] lines = line.split("\\r?\\n");
            if (!(lines.size() > 0 && lines != null)) {
                return catalogueBuilder.dataType(name: "String")
            }

            def enumerations = parseEnumeration(lines)

            if(!enumerations){
                return catalogueBuilder.dataType(name: name) ?: catalogueBuilder.dataType(name: "String")
            }
            String enumString = enumerations.sort() collect { key, val ->
                "${quote(key)}:${quote(val)}"
            }.join('|')

            def dataTypeReturn = EnumeratedType.findWhere(enumAsString: enumString)

            if (dataTypeReturn) {
                return catalogueBuilder.dataType(name: dataTypeReturn.name)
            }
            return catalogueBuilder.dataType(name: name.replaceAll("\\s", "_"), enumerations: enumerations)
        }
        catalogueBuilder.dataType(name: "String")
    }

    private static String quote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }


}
