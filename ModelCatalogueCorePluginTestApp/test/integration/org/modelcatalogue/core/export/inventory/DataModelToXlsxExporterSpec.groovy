package org.modelcatalogue.core.export.inventory

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import org.modelcatalogue.spreadsheet.query.api.Predicate
import org.modelcatalogue.spreadsheet.query.api.SpreadsheetCriteria
import org.modelcatalogue.spreadsheet.query.poi.PoiSpreadsheetQuery

class DataModelToXlsxExporterSpec extends AbstractIntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService
    GrailsApplication grailsApplication
    CatalogueBuilder catalogueBuilder

    DataModel dataModel

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    def setup() {
        initRelationshipTypes()
        CatalogueXmlLoader loader = new CatalogueXmlLoader(catalogueBuilder)
        loader.load(DataModelToXlsxExporterSpec.getResourceAsStream('TestDataModelV1.xml'))
        loader.load(DataModelToXlsxExporterSpec.getResourceAsStream('TestDataModelV2.xml'))
        dataModel = DataModel.findByNameAndSemanticVersion('TestDataModel', '2')
    }

    def "export model to excel"() {
        setup:
        def file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")

        when:
        CatalogueElementToXlsxExporter.forDataModel(dataModel, dataClassService, grailsApplication, Integer.MAX_VALUE).export(file.newOutputStream())
        FileOpener.open(file)

        SpreadsheetCriteria query = PoiSpreadsheetQuery.FACTORY.forFile(file)

        then:
        noExceptionThrown()
        query.exists {
            sheet('Introduction') {
                row {
                    cell {
                        value 'TestDataModel'
                        style {
                            font {
                                size 22
                            }
                        }
                    }
                }
            }
        }
        query.exists {
            sheet('Content') {
                row {
                    cell {
                        value '1..10'
                        style {
                            foreground ModelCatalogueStyles.CHANGE_NEW_COLOR
                        }
                    }
                }
            }
        }
        query.exists {
            sheet('Content') {
                row {
                    cell {
                        value 'IMAGING'
                        style {
                            foreground ModelCatalogueStyles.CHANGE_NEW_COLOR
                        }
                    }
                }
            }
        }
        query.exists {
            sheet('Content') {
                row {
                    cell {
                        value 'DEMOGRAPHICS'
                        style {
                            foreground ModelCatalogueStyles.CHANGE_REMOVAL_COLOR
                        }
                    }
                }
            }
        }
        query.exists {
            sheet(name(endsWith('Ovarian_Cancer'))) {
                row {
                    cell {
                        value 'ORGANISATION CODE (CODE OF PROVIDER)'
                        style {
                            foreground ModelCatalogueStyles.CHANGE_NEW_COLOR
        }   }   }   }   }
        query.exists {
            sheet(name(endsWith('Ovarian_Cancer'))) {
                row {
                    cell {
                        value 'PERSON BIRTH DATE'
                        style {
                            foreground ModelCatalogueStyles.CHANGE_REMOVAL_COLOR
        }   }   }   }   }
        query.exists {
            sheet(name(endsWith('Ovarian_Cancer'))) {
                row {
                    cell {
                        value 'IMAGING (231@2)'
                        style {
                            foreground ModelCatalogueStyles.CHANGE_NEW_COLOR
        }   }   }   }   }
        query.exists {
            sheet(name(endsWith('Ovarian_Cancer'))) {
                row {
                    cell {
                        value 'DEMOGRAPHICS (162@1)'
                        style {
                            foreground ModelCatalogueStyles.CHANGE_REMOVAL_COLOR
        }   }   }   }   }
        query.exists {
            sheet('Changes') {
                row {
                    cell {
                        value 'Changes Summary'
                        style {
                            font {
                                size 22
                            }
                        }
                        colspan { it > 1 }
                    }
                }
            }
        }
    }


    private static Predicate<String> endsWith(String suffix) {
        return { String s ->
            s.endsWith(suffix)
        } as Predicate<String>
    }
}
