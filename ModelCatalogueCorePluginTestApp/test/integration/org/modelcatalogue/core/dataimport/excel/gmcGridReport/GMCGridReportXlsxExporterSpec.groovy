package org.modelcatalogue.core.dataimport.excel.gmcGridReport

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportXlsxExporter
import org.modelcatalogue.core.export.inventory.DataModelToXlsxExporterSpec
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import org.modelcatalogue.spreadsheet.query.api.SpreadsheetCriteria
import org.modelcatalogue.spreadsheet.query.poi.PoiSpreadsheetQuery
import spock.lang.IgnoreIf

@IgnoreIf( { System.getProperty('spock.ignore.slow')| System.getenv('jenkins.ignore') })
class GMCGridReportXlsxExporterSpec extends AbstractIntegrationSpec {

    public static final String ROOT_DATA_MODEL_NAME = 'Grid Report Data Model'
    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService
    GrailsApplication grailsApplication
    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()


    DataModel dataModel
    CatalogueBuilder catalogueBuilder

    def setup() {initRelationshipTypes()
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

        CatalogueXmlLoader loader = new CatalogueXmlLoader(builder)
        loader.load(DataModelToXlsxExporterSpec.getResourceAsStream('TestDataModelV1.xml'))
        loader.load(DataModelToXlsxExporterSpec.getResourceAsStream('TestDataModelV2.xml'))
        dataModel = DataModel.findByNameAndSemanticVersion('TestDataModel', '2')

        catalogueBuilder.build {
            automatic dataType

            dataModel(name: 'ARIA') {

                ext 'http://www.modelcatalogue.org/metadata/#organization', 'UCL'

                dataElement(name: 'P.Test Element 1') {
                    ext  "Semantic Matching", "yes"
                    ext "Known Issue" , "no"
                    ext "Immediate Solution", "this"
                    ext "Immediate Solution Owner" , "Mr Orange"
                    ext "Long Term Solution" , "that"
                    ext  "Long Term Solution Owner" , "Mr Pink"
                    ext  "Data Item Unique Code" , "X34DD"
                    ext  "Related To" , "unknown"
                    ext  "Part Of Standard Data Set" , "no"
                    ext  "Data Completeness" , "60%"
                    ext "Estimated Quality" , "100%"
                    ext "Timely" , "no"
                    dataType(name: 'Same Name')
                }
                dataElement(name: 'P.Test Element 2') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "no"
                    ext "Known Issue" , "s"
                    ext "Immediate Solution", "this s"
                    ext "Immediate Solution Owner" , "Mr Orange 1"
                    ext "Long Term Solution" , "that"
                    ext  "Long Term Solution Owner" , "Mr Pink 2"
                    ext  "Data Item Unique Code" , "X454DD"
                    ext  "Related To" , "test"
                    ext  "Part Of Standard Data Set" , "yes"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                }
                dataElement(name: 'P.Test Element 3') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "no"
                    ext "Known Issue" , "s"
                    ext "Immediate Solution", "this s"
                    ext "Immediate Solution Owner" , "Mr Orange 1"
                    ext "Long Term Solution" , "that"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                }
                dataElement(name: 'P.Test Element 4') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "no"
                    ext "Known Issue" , "s"
                    ext "Immediate Solution", "this s"
                    ext "Immediate Solution Owner" , "Mr Orange 1"
                    ext "Long Term Solution" , "that"
                    ext  "Long Term Solution Owner" , "Mr Pink 2"
                    ext  "Data Item Unique Code" , "X454DD"
                    ext  "Related To" , "test"
                    ext  "Part Of Standard Data Set" , "yes"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                }
                dataElement(name: 'P.Test Element 5') {
                    dataType(name: 'Same Name')
                    ext 'System Column', 'BLAH asdfs'
                    ext  "Semantic Matching", "no"
                    ext "Known Issue" , "s"
                    ext "Immediate Solution", "this s"
                    ext "Immediate Solution Owner" , ""
                    ext "Long Term Solution" , ""
                    ext  "Long Term Solution Owner" , ""
                    ext  "Data Item Unique Code" , ""
                    ext  "Related To" , ""
                    ext  "Part Of Standard Data Set" , ""
                    ext  "Data Completeness" , ""
                    ext "Estimated Quality" , ""
                    ext "Timely" , ""
                }
            }


            dataModel(name: 'CDR') {
                ext 'http://www.modelcatalogue.org/metadata/#organization', 'UCL'
                dataElement(name: 'P.Test Element 6') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "yes"
                    ext "Known Issue" , "sdsafadfs"
                    ext "Immediate Solution", "this sdf"
                    ext "Immediate Solution Owner" , "Mr Blue 1"
                    ext "Long Term Solution" , "that"
                    ext  "Long Term Solution Owner" , "Mr Red 2"
                    ext  "Data Item Unique Code" , "X1254DD"
                    ext  "Related To" , "another one"
                    ext  "Part Of Standard Data Set" , "Unknown"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                }
                dataElement(name: 'P.Test Element 7') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "yes"
                    ext "Known Issue" , "sdsafadfs"
                    ext "Immediate Solution", "this sdf"
                    ext "Immediate Solution Owner" , "Mr Blue 1"
                    ext "Long Term Solution" , "that"
                    ext  "Long Term Solution Owner" , "Mr Red 2"
                    ext  "Data Item Unique Code" , "X1254DD"
                    ext  "Related To" , "another one"
                    ext  "Part Of Standard Data Set" , "Unknown"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                }
                dataElement(name: 'P.Test Element 8') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "yes"
                    ext "Known Issue" , "sdsafadfs"
                    ext "Immediate Solution", "this sdf"
                    ext "Immediate Solution Owner" , "Mr Blue 1"
                    ext "Long Term Solution" , "that"
                    ext  "Long Term Solution Owner" , "Mr Red 2"
                    ext  "Data Item Unique Code" , "X1254DD"
                    ext  "Related To" , "another one"
                    ext  "Part Of Standard Data Set" , "Unknown"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                }
                dataElement(name: 'P.Test Element 9') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "yes"
                    ext "Known Issue" , "sdsafadfs"
                    ext "Immediate Solution", "this sdf"
                    ext "Immediate Solution Owner" , "Mr Blue 1"
                    ext "Long Term Solution" , "that"
                    ext  "Long Term Solution Owner" , "Mr Red 2"
                    ext  "Data Item Unique Code" , "X1254DD"
                    ext  "Related To" , "another one"
                    ext  "Part Of Standard Data Set" , "Unknown"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                }
                dataElement(name: 'P.Test Element 10') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "yes"
                    ext "Known Issue" , "sdsafadfs"
                    ext "Immediate Solution", "this sdf"
                    ext "Immediate Solution Owner" , "Mr Blue 1"
                    ext "Long Term Solution" , "that"
                    ext  "Long Term Solution Owner" , "Mr Red 2"
                    ext  "Data Item Unique Code" , "X1254DD"
                    ext  "Related To" , "another one"
                    ext  "Part Of Standard Data Set" , "Unknown"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                }
            }


            dataModel(name: 'SNOMED') {
                ext 'http://www.modelcatalogue.org/metadata/#organization', 'UCL'
                dataElement(name: 'P.Test Element 11') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "yes"
                    ext "Known Issue" , "sdsafadfs"
                    ext "Immediate Solution", "this sdf"
                    ext "Immediate Solution Owner" , "Mr Blue 1"
                    ext "Long Term Solution" , "that"
                    ext  "Long Term Solution Owner" , "Mr Red 2"
                    ext  "Data Item Unique Code" , "X1254DD"
                    ext  "Related To" , "another one"
                    ext  "Part Of Standard Data Set" , "Unknown"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                    ext "Comments" , "lbah blah blah"
                }
                dataElement(name: 'P.Test Element 12') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "yes"
                    ext "Known Issue" , "sdsafadfs"
                    ext "Immediate Solution", "this sdf"
                    ext "Immediate Solution Owner" , "Mr Blue 1"
                    ext "Long Term Solution" , "that"
                    ext  "Long Term Solution Owner" , "Mr Red 2"
                    ext  "Data Item Unique Code" , "X1254DD"
                    ext  "Related To" , "another one"
                    ext  "Part Of Standard Data Set" , "Unknown"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                }
                dataElement(name: 'P.Test Element 13') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "yes"
                    ext "Known Issue" , "sdsafadfs"
                    ext "Immediate Solution", "this sdf"
                    ext "Immediate Solution Owner" , "Mr Blue 1"
                    ext  "Data Item Unique Code" , "X1254DD"
                    ext  "Related To" , "another one"
                    ext  "Part Of Standard Data Set" , "Unknown"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                }
                dataElement(name: 'P.Test Element 14') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "yes"
                    ext "Known Issue" , ""
                    ext "Immediate Solution", "this sdf"
                    ext "Immediate Solution Owner" , "Mr Blue 1"
                    ext "Long Term Solution" , "that"
                    ext  "Long Term Solution Owner" , "Mr Red 2"
                    ext  "Data Item Unique Code" , "X1254DD"
                    ext  "Related To" , "another one"
                    ext  "Part Of Standard Data Set" , "Unknown"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"
                }
                dataElement(name: 'P.Test Element 15') {
                    dataType(name: 'Same Name')
                    ext  "Semantic Matching", "yes"
                    ext "Known Issue" , "sdsafadfs"
                    ext "Immediate Solution", "this sdf"
                    ext "Immediate Solution Owner" , "Mr Blue 1"
                    ext  "Part Of Standard Data Set" , "Unknown"
                    ext  "Data Completeness" , "70%"
                    ext "Estimated Quality" , "90%"
                    ext "Timely" , "no"

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
    @IgnoreIf( { System.getenv('jenkins.ignore') })
    def "export model to excel"() {
        setup:
        def file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")

        when:
        GMCGridReportXlsxExporter.create(dataModel, dataClassService, grailsApplication, 5).export(file.newOutputStream())
        FileOpener.open(file)

        SpreadsheetCriteria query = PoiSpreadsheetQuery.FACTORY.forFile(file)

        then:
        noExceptionThrown()
    }
}
