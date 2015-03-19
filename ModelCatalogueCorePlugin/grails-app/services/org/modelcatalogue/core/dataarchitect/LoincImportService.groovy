package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.util.builder.CatalogueBuilder

class LoincImportService {

    static transactional = false

    CatalogueBuilder catalogueBuilder

    def serviceMethod(InputStream loinc ) {

        loinc.toCsvReader([charset: 'UTF-8', skipLines: 1]).eachLine { tokens ->
            catalogueBuilder.build {
                classification(name: "LOINC") {
                    globalSearchFor dataType
                    description("LOINC conceptual domain")
                    model(name: "LOINC Datasets") {
                        model(name: tokens[4]) {
                            model(name: tokens[7]) {
                                dataElement(name: tokens[1], description: tokens[4]) {
                                    valueDomain(name: tokens[28].replaceAll("\\s", "_")) {
                                        //DataImportService.importDataTypes(catalogueBuilder, tokens[3], [tokens[5]])
                                    }
                                    ext "Loinc Ref:", tokens[0]
                                    ext "Source:", tokens[8]
                                    ext "Date Last Changed", tokens[9]
                                    ext "Property", tokens[2]
                                    ext "Scale Type", tokens[5]
                                    ext "Method Type", tokens[6]
                                    ext "Related Names", tokens[27]
                                    ext "Under Observation", tokens[29]
                                    ext "Example Units", tokens[33]
                                    ext "HL7 v2 Datatype", tokens[35]
                                    ext "HL7 v3 Datatype", tokens[36]
                                    ext "Example UCUM Units", tokens[39]
                                }
                            }

                        }
                    }
                }
            }

            println "importing : first token" + tokens[0] + ":STUFF"
        }
    }

}
