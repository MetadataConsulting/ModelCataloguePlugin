package uk.co.brc.modelcatalogue

import org.modelcatalogue.core.util.builder.CatalogueBuilder
import org.modelcatalogue.core.dataarchitect.DataImportService

class ImportService {

    static transactional = false

    def grailsApplication
    def initCatalogueService
    CatalogueBuilder catalogueBuilder

    private static final QUOTED_CHARS = [
            "\\": "&#92;",
            ":": "&#58;",
            "|": "&#124;",
            "%": "&#37;",
    ]

    def importData(boolean failOnError = false) {
        initCatalogueService.initCatalogue(failOnError)

        getNhicFiles().each { filename -> singleImport(filename) }
    }
    /**
     * Get the list of available files for import
     * @return the list of available files for import
     */
    def getNhicFiles() {
        return fileFunctions.keySet()
    }

    /**
     * Carry out an import for a single file in the NHIC dataset
     * @param filename The filename. Must exist in the collection returned by <code>getNhicFiles</code>
//     */
    def singleImport(String filename) {
        initCatalogueService.initDefaultRelationshipTypes()
        def applicationContext = grailsApplication.mainContext
        String basePath = applicationContext.getResource("/").getFile().toString()

        new File("${basePath}" + "/WEB-INF/bootstrap-data" + filename).toCsvReader([charset: 'UTF-8', skipLines: 1]).eachLine { tokens ->
            fileFunctions[filename](tokens);
        }
    }


    private fileFunctions = [
            '/CAN_CUH.csv':
                    { tokens ->
                        catalogueBuilder.build {
                            classification(name:"NHIC"){
                                globalSearchFor dataType

                                description("NHIC conceptual domain i.e. value domains used the NHIC project")
                                model(name:"NHIC Datasets"){
                                    model(name:"Ovarian Cancer"){
                                        model(name:"CUH") {
                                            model(name:"Round 1"){
                                                model(name:tokens[1]){
                                                    model(name:tokens[2]){
                                                        dataElement(name:tokens[3], description:tokens[4]){
                                                            valueDomain(name:tokens[3].replaceAll("\\s", "_")){
                                                                DataImportService.importDataTypes(catalogueBuilder, tokens[3], [tokens[5]])
                                                            }
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
                            }
                        }

                        println "importing: " + tokens[0] + "_Round1_CAN"
                    }
    ]

    private static String quote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }
}