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

    public static final String ROOT_DATA_CLASS_NAME = 'Registration GRDM'
    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService
    GrailsApplication grailsApplication

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    DataClass dataClass

    def setup() {
        initRelationshipTypes()
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

        builder.build {
            dataModel (name: "Grid Report Data Model") {
                dataClass(name: ROOT_DATA_CLASS_NAME) {
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
                                dataClass(name: 'Event Details') {
                                    dataElement(name: 'Event Date') {
                                        dataType(name: 'xs:date', dataModel: 'XMLSchema')
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        dataClass = DataClass.findByName(ROOT_DATA_CLASS_NAME)

    }

    def "export model to excel"() {
        setup:
        def file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")

        when:
        GridReportXlsxExporter.create(dataClass, dataClassService, grailsApplication, 5).export(file.newOutputStream())
        FileOpener.open(file)

        SpreadsheetCriteria query = PoiSpreadsheetQuery.FACTORY.forFile(file)

        then:
        noExceptionThrown()
    }
}
