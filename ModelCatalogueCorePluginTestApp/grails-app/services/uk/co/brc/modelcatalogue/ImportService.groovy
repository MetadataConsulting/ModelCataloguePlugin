package uk.co.brc.modelcatalogue

import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.integration.excel.ExcelLoader

class ImportService {

    static transactional = false

    def grailsApplication
    CatalogueBuilder catalogueBuilder

    def importData() {
        String basePath = grailsApplication.mainContext.getResource("/").getFile().toString()

        catalogueBuilder.build {
            skip draft
            dataModel(name:"NHIC"){
                new File("${basePath}" + "/WEB-INF/bootstrap-data/CAN_CUH.csv").toCsvReader([charset: 'UTF-8', skipLines: 1]).eachLine { tokens ->
                    globalSearchFor dataType

                    description("NHIC conceptual domain i.e. value domains used the NHIC project")
                    dataClass(name:"NHIC Datasets"){
                        dataClass(name:"Ovarian Cancer"){
                            dataClass(name:"CUH") {
                                dataClass(name:"Round 1"){
                                    dataClass(name:tokens[1]){
                                        dataClass(name:tokens[2]){
                                            dataElement(name:tokens[3], description:tokens[4]){

                                                def enumerations = tokens[5] ? ExcelLoader.parseEnumeration(tokens[5].split("\\r?\\n")) : [:]
                                                ExcelLoader.importDataTypes(catalogueBuilder, tokens[3], enumerations ? tokens[5] : null, null, null, null, null)

                                                ext "NHIC_Identifier:", tokens[0].take(2000)
                                                ext "Link_to_existing definition:", tokens[6].take(2000)
                                                ext "Notes_from_GD_JCIS", tokens[7].take(2000)
                                                ext "[Optional]_Local_Identifier", tokens[8].take(2000)
                                                ext "A", tokens[9].take(2000)
                                                ext "B", tokens[10].take(2000)
                                                ext "C", tokens[11].take(2000)
                                                ext "D", tokens[12].take(2000)
                                                ext "E", tokens[13].take(2000)
                                                ext "F", tokens[14].take(2000)
                                                ext "G", tokens[15].take(2000)
                                                ext "H", tokens[16].take(2000)
                                                ext "E2", tokens[17].take(2000)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    println "importing: " + tokens[0] + "_Round1_CAN"
                }

            }

        }
    }
}