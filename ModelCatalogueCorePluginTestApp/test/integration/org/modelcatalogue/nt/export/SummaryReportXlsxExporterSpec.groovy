package org.modelcatalogue.nt.export

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.export.inventory.DataModelToXlsxExporterSpec
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import org.modelcatalogue.spreadsheet.query.api.SpreadsheetCriteria
import org.modelcatalogue.spreadsheet.query.poi.PoiSpreadsheetQuery

class SummaryReportXlsxExporterSpec extends AbstractIntegrationSpec {

    public static final String ROOT_DATA_MODEL_NAME = 'Grid Report Data Model'
    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService
    GrailsApplication grailsApplication

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    DataModel dataModel
    CatalogueBuilder catalogueBuilder

    def setup() {
        initRelationshipTypes()
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

        CatalogueXmlLoader loader = new CatalogueXmlLoader(builder)
        loader.load(DataModelToXlsxExporterSpec.getResourceAsStream('TestDataModelV1.xml'))
        loader.load(DataModelToXlsxExporterSpec.getResourceAsStream('TestDataModelV2.xml'))
        dataModel = DataModel.findByNameAndSemanticVersion('TestDataModel', '2')

        catalogueBuilder.build {
            automatic dataType

            dataModel(name: 'ARIA') {

                ext 'organisation', 'UCL'

                dataElement(name: 'P.Test Element 1') {
                    ext 'System Column', 'BLAH asdfs'
                    ext 'Known Issue', 'test issue'
                    ext 'Semantic Matching', 'yes'
                    ext 'Data Completeness', '80%'
                    ext 'Data Quality', '100%'
                    dataType(name: 'Same Name')
                }
                dataElement(name: 'P.Test Element 2') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH asdfs'
                    ext 'Known Issue', 'test issue'
                    ext 'Data Quality', '100%'
                }
                dataElement(name: 'P.Test Element 3') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH asdfs'
                    ext 'Semantic Matching', 'yes'
                    ext 'Data Completeness', '80%'
                    ext 'Data Quality', '100%'
                }
                dataElement(name: 'P.Test Element 4') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH asdfs'
                    ext 'Known Issue', 'test issue'
                    ext 'Semantic Matching', 'yes'
                    ext 'Data Completeness', '80%'
                    ext 'Data Quality', '100%'
                }
                dataElement(name: 'P.Test Element 5') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH asdfs'
                    ext 'Known Issue', 'test issue'
                    ext 'Semantic Matching', 'no'
                    ext 'Data Completeness', '80%'
                    ext 'Data Quality', '100%'
                }
            }


            dataModel(name: 'CDR') {
                ext 'organisation', 'UCL'
                dataElement(name: 'P.Test Element 6') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH 14s casdfs'
                    ext 'Known Issue', 'test issue'
                    ext 'Semantic Matching', 'yes'
                    ext 'Data Completeness', '80%'
                    ext 'Data Quality', '100%'
                }
                dataElement(name: 'P.Test Element 7') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH 52 asdfs'
                    ext 'Known Issue', 'test issue'
                    ext 'Semantic Matching', 'yes'
                    ext 'Data Completeness', '80%'
                    ext 'Data Quality', '100%'
                }
                dataElement(name: 'P.Test Element 8') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH asdfs'
                    ext 'Known Issue', 'test asdf issue'
                    ext 'Semantic Matching', 'yes'
                    ext 'Data Completeness', '80%'
                    ext 'Data Quality', '100%'
                }
                dataElement(name: 'P.Test Element 9') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH a2sdfs'
                    ext 'Known Issue', 'test  123issue'
                    ext 'Semantic Matching', 'yes'
                    ext 'Data Completeness', '80%'
                    ext 'Data Quality', '100%'
                }
                dataElement(name: 'P.Test Element 10') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH asdfs 1'
                    ext 'Known Issue', 'test issue asd'
                    ext 'Semantic Matching', 'no'
                    ext 'Data Completeness', '64%'
                    ext 'Data Quality', '90%'
                }
            }


            dataModel(name: 'SNOMED') {
                ext 'organisation', 'UCL'
                dataElement(name: 'P.Test Element 11') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH asdfs 1'
                    ext 'Semantic Matching', 'no'
                    ext 'Data Completeness', '64%'
                    ext 'Data Quality', '90%'
                }
                dataElement(name: 'P.Test Element 12') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH asdfs 1'
                    ext 'Known Issue', 'test issue asd'
                    ext 'Semantic Matching', 'no'
                    ext 'Data Completeness', '64%'
                    ext 'Data Quality', '90%'
                }
                dataElement(name: 'P.Test Element 13') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH asdfs 1'
                    ext 'Known Issue', 'test issue asd'
                    ext 'Semantic Matching', 'no'
                    ext 'Data Completeness', '74%'
                    ext 'Data Quality', '90%'
                }
                dataElement(name: 'P.Test Element 14') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH3 asdfs 1'
                    ext 'Known Issue', 'test issue asd'
                    ext 'Semantic Matching', 'no'
                    ext 'Data Completeness', '54%'
                    ext 'Data Quality', '92%'
                }
                dataElement(name: 'P.Test Element 15') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BL2AH asdfs 1'
                    ext 'Known Issue', 'test issue asd'
                    ext 'Semantic Matching', 'no'
                    ext 'Data Completeness', '23%'
                    ext 'Data Quality', '64%'

                }
            }

        }


        def dataElements = DataElement.findAllByDataModel(dataModel)
        dataElements.eachWithIndex{ de, index ->
            def deRel = DataElement.findByName("P.Test Element $index")
            if(deRel) de.addToRelatedTo(deRel)
        }


        def deMultiple = DataElement.findByName("P.Test Element 5")
        def deMultiple2 = DataElement.findByNameAndDataModel("LOCAL PATIENT IDENTIFIER*", dataModel)

        deMultiple.addToRelatedTo(deMultiple2)


    }

    def "export model to excel"() {
        setup:
        def file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")

        when:
        SummaryReportXlsxExporter.create(dataModel, dataClassService, grailsApplication, 5).export(file.newOutputStream())
        FileOpener.open(file)

        SpreadsheetCriteria query = PoiSpreadsheetQuery.FACTORY.forFile(file)

        then:
        noExceptionThrown()
    }
}
