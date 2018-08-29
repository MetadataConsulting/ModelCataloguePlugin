package org.modelcatalogue.core.dataimport.excel.norththamesreport

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportXlsxExporter
import org.modelcatalogue.core.dataexport.excel.norththamesreport.NorthThamesMappingReportXlsxExporter
import org.modelcatalogue.core.export.inventory.DataModelToXlsxExporterSpec
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import spock.lang.IgnoreIf

@IgnoreIf( {
    System.getProperty('spock.ignore.slow') ||
    System.getenv('JENKINS_IGNORE') ||
    System.getProperty('IGNORE_OFFICE')
})
class NorthThamesMappingReportXlsxExporterSpec extends AbstractIntegrationSpec {

    public static final String ROOT_DATA_MODEL_NAME = 'North Thames Mapping Report Data Model'
    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService
    DataElementService dataElementService
    GrailsApplication grailsApplication
    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()


    DataModel dataModel
    CatalogueBuilder catalogueBuilder

    def setup() {

        initRelationshipTypes()


//        LOCAL SITE	LOCAL CODESET 1	LOCAL CODE 1	LOCAL CODE 1 NAME	LOCAL CODESET 2	LOCAL CODE 2	LOCAL CODE 2 DESCRIPTION	LOINC CODE	LOINC CODE DESCRIPTION	LOINC SPECIMEN TYPE	GEL CODE	GEL CODE DESCRIPTION	OPENEHR QUERY
//        RFH	LDPC	1844		XXX	1234		49765-1		Bld	34983@1.4.0	blah 	asdfs
//        RFH	LDPC	1410		XXX	532112		26464-8		Bld	35043@1.4.0	blahb	asdfadsf


        catalogueBuilder.build {
            automatic dataType

            dataModel(name: 'GEL LDPC Codes') {

                ext 'http://www.modelcatalogue.org/metadata/#organization', 'RFH'

                dataElement(name: 'localCodeset1.Test Element 1', description: 'blah blah') {
                    ext  "Index", "A18379"
                    dataType(name: 'Same Name')
                }
                dataElement(name: 'localCodeset1.Test Element 2', description: 'blah blah') {
                    ext  "Index", "A18379"
                    dataType(name: 'Same Name')
                }
            }

            dataModel(name: 'WinPath') {
                copy relationships
                ext 'http://www.modelcatalogue.org/metadata/#organization', 'RFH'

                dataElement(name: 'localCodeset2.Test Element 1', description: 'blah blah') {
                    ext  "WinPath TFC", "B36246233"
                    dataType(name: 'Same Name')
                    rel 'relatedTo' to 'GEL LDPC Codes','localCodeset1.Test Element 1'
                }
                dataElement(name: 'localCodeset2.Test Element 2', description: 'blah blah') {
                    ext  "WinPath TFC", "B1234"
                    dataType(name: 'Same Name')
                    rel 'relatedTo' to 'GEL LDPC Codes','localCodeset1.Test Element 2'
                }
            }

            dataModel(name: 'loinc') {
                copy relationships
                ext 'http://www.modelcatalogue.org/metadata/#organization', 'LOINC'

                dataElement(name: 'loinc.Test Element 1', description: 'blah blah') {
                    ext  "LOINC_NUM", "1234-1"
                    ext  "SYSTEM", "1234-1"
                    dataType(name: 'Urine')
                    rel 'relatedTo' to 'GEL LDPC Codes','localCodeset1.Test Element 1'
                }
                dataElement(name: 'loinc.Test Element 2', description: 'blah blah') {
                    ext  "LOINC_NUM", "98234-1"
                    ext  "SYSTEM", "Body fld"
                    dataType(name: 'Same Name')
                    rel 'relatedTo' to 'GEL LDPC Codes','localCodeset1.Test Element 2'
                }
            }


            dataModel(name: 'Rare Diseases') {
                copy relationships
                ext 'http://www.modelcatalogue.org/metadata/#organization', 'GEL'

                dataElement(name: 'genomics.Test Element 1', description: 'blah blah') {
                    ext  "code", "B36246233"
                    dataType(name: 'Same Name')
                    rel 'relatedTo' to 'GEL LDPC Codes','localCodeset1.Test Element 1'
                }
                dataElement(name: 'genomics.Test Element 2', description: 'blah blah') {
                    ext  "code", "B1234"
                    dataType(name: 'Same Name')
                    rel 'relatedTo' to 'GEL LDPC Codes','localCodeset1.Test Element 2'
                }
            }


            dataModel(name: 'Open EHR') {
                copy relationships
                ext 'http://www.modelcatalogue.org/metadata/#organization', 'Open EHR'

                dataElement(name: 'genomics.Test Element 1', description: 'blah blah') {
                    ext  "Archetype Path Query Statement", "select * from adsflkjdasafd"
                    dataType(name: 'Same Name')
                    rel 'relatedTo' to 'GEL LDPC Codes','localCodeset1.Test Element 1'
                }
                dataElement(name: 'genomics.Test Element 2', description: 'blah blah') {
                    ext  "Archetype Path Query Statement", "select * from adsflkjdasafd"
                    dataType(name: 'Same Name')
                    rel 'relatedTo' to 'GEL LDPC Codes','localCodeset1.Test Element 2'
                }
            }

            }

        }


    @IgnoreIf( { System.getenv('JENKINS_IGNORE') })
    def "export model to excel"() {
        setup:
        def file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")
        dataModel = DataModel.findByName("GEL LDPC Codes")

        when:
        NorthThamesMappingReportXlsxExporter.create(dataModel, dataClassService, dataElementService, grailsApplication, false).export(file.newOutputStream())
        FileOpener.open(file)

//        SpreadsheetCriteria query = PoiSpreadsheetCriteria.FACTORY.forFile(file)

        then:
        noExceptionThrown()
    }
}
