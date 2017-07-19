package org.modelcatalogue.gel.export

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.spreadsheet.query.api.SpreadsheetCriteria
import org.modelcatalogue.spreadsheet.query.poi.PoiSpreadsheetQuery

class GridReportXlsxExporterSpec extends AbstractIntegrationSpec {

    public static final String ROOT_DATA_MODEL_NAME = 'Grid Report Data Model'
    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService
    GrailsApplication grailsApplication

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    DataModel dataModel

    def setup() {
        initRelationshipTypes()
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

        builder.build {
            dataModel (name: ROOT_DATA_MODEL_NAME) {
                dataClass(name: "Registration GRDM") {
                    dataClass(name: 'Essential Data') {
                        dataClass(name: 'Registration and Consent') {
                            dataClass(name: 'Patient Identifiers') {
                                dataElement(name: 'Date of Birth') {
                                    dataType(name: 'xs:date', dataModel: 'XMLSchema')
                                    relationship {
                                        ext Metadata.MIN_OCCURS, "1"
                                        ext Metadata.MAX_OCCURS, "1"
                                    }
                                }
                                dataElement(name: 'Fornames')
                                dataElement(name: 'Surname')
                                relationship {
                                    ext Metadata.MIN_OCCURS, "1"
                                    ext Metadata.MAX_OCCURS, "1"
                                }
                            }
                            dataClass(name: 'Registration') {
                                dataClass(name: 'Event Details 2') {

                                    dataElement(name: 'Event Date 1232') {
                                        dataType(name: 'xs:date', dataModel: 'XMLSchema')
                                    }
                                }
                            }
                        }
                    }
                    dataClass(name: 'Patient Identifiers 2') {
                        dataElement(name: 'Date of Birth') {
                            dataType(name: 'xs:date', dataModel: 'XMLSchema')
                            relationship {
                                ext Metadata.MIN_OCCURS, "1"
                                ext Metadata.MAX_OCCURS, "1"
                            }
                        }
                        dataElement(name: 'Fornames 12')
                        dataElement(name: 'Surname3 ')
                        relationship {
                            ext Metadata.MIN_OCCURS, "1"
                            ext Metadata.MAX_OCCURS, "1"
                        }
                    }
                }
            }
        }

        dataModel = DataModel.findByName(ROOT_DATA_MODEL_NAME)

    }

    def "export model to excel"() {
        setup:
        def file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")

        when:
        GridReportXlsxExporter.create(dataModel, dataClassService, grailsApplication, 5).export(file.newOutputStream())
        FileOpener.open(file)

        SpreadsheetCriteria query = PoiSpreadsheetQuery.FACTORY.forFile(file)

        then:
        noExceptionThrown()
    }
}
