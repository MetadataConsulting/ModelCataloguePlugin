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
import spock.lang.IgnoreIf

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
        dataModel.save(flush:true, failOnError:true)
        DataClass toDelete = DataClass.findByNameAndDataModel("NHIC Datasets Test for Delete", dataModel)
        toDelete.deleteRelationships()
        toDelete.delete(flush:true, failOnError:true)

        /*
        *  Differences between test data model v1 and test model v2
        *
        * CLASSES
        *
        * NHIC Datasets Test for Delete Class should be deleted i.e. only appear no the front page in red
        *
        * NHIC Datasets Class
        * v2 - Additional metadata - skip export true
        *
        * Ovarian Cancer Class
        * v2 - Additional metadata - subsection true
        *
        * CUH Class
        * v2 - Additional metadata - skip export  true
        *
        * Round 1 Class
        * v2 - Additional metadata - skip export  true
        *
        * Main Class
        * v2 - Additional metadata - skip export  true
        * v2 - Added Imaging
        * v2 - Moved Demographics to top level - SHOULD BE HIGHLIGHTED
        *
        * Patient Identity Details Class
        * v2 - Added Min Max Constraints (could be displayed as a main class change as well)
        * *  NHS Number Data Element
        * * * * v2 - changed data type to Number from String
        * *  nhsNumberStatusIndicatorCode Data Element
        * * * * v2 - data type changed
        * * * * * nhsNumberStatusIndicatorCode Type
        * * * * * deprecated  enumerations 6, modified enumeration 8, removed enumeration 01,07, added enumeration 4,5,10
        * *  Person Birth Data Data Element removed
        * *  ORGANISATION CODE (CODE OF PROVIDER) Data Element added
        *
        * Referrals Class
        * * sourceOfReferralForOutPatients Data Element
        * v2 - multiplicity added - min occurs 2, max occurs 5
        * v2 - metadata removed - E, F, G, H, E2
        * v2 type changed
        * * * * sourceOfReferralForOutPatients Type
        * * * * 07, 92 - deleted, 06, 10 deprecated, 99 new, 05 changed
        *
        *
        * */

    }

    @IgnoreIf( { System.getProperty('spock.ignore.slow') })
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
                        value 'IMAGING'
                        style {
                            foreground ModelCatalogueStyles.CHANGE_NEW_COLOR
        }   }   }   }   }

        //note the demographics sheet should probably be blue as it's been moved - need to look at the requirements
        query.exists {
            sheet(name(endsWith('DEMOGRAPHICS'))) {
                row {
                    cell {
                        value 'PERSON FAMILY NAME'
                        /*style {
                            foreground ModelCatalogueStyles.DATA_ELEMENT
        }*/   }   }   }   }
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
