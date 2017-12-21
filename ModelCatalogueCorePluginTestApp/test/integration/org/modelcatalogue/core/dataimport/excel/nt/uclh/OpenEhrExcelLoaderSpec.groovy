package org.modelcatalogue.core.dataimport.excel.nt.uclh

import grails.util.Holders
import org.apache.commons.lang3.tuple.Pair
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.modelcatalogue.core.dataimport.excel.ExcelLoaderSpec
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import spock.lang.Shared

/**
 * Created by adammilward on 31/08/2017.
 */
class OpenEhrExcelLoaderSpec extends AbstractIntegrationSpec {

    @Shared String resourcePath = (new File("test/integration/org/modelcatalogue/core/dataimport/excel/nt/uclh")).getAbsolutePath()
    @Shared ElementService elementService
    @Shared DataModelService dataModelService
    @Shared DataClassService dataClassService
    @Shared GrailsApplication grailsApplication

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()
    @Shared DefaultCatalogueBuilder defaultCatalogueBuilder

    @Shared CatalogueXmlLoader catalogueXmlLoader
    @Shared DataModel sourceDataModel
    @Shared OpenEhrExcelLoader excelLoader
    @Shared excelLoaderXmlResult


    @Shared List<String> testOpenEhrFiles = ['GEL_openEHR_Cross_references_test.xlsx']


    def setupSpec(){
        initRelationshipTypes()
        defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
        catalogueXmlLoader = new CatalogueXmlLoader(defaultCatalogueBuilder)

        catalogueXmlLoader.load(getClass().getResourceAsStream('TestDataModelV1.xml'))
        catalogueXmlLoader.load(getClass().getResourceAsStream('TestDataModelV2.xml'))
        sourceDataModel = DataModel.findByNameAndSemanticVersion('TestDataModel', '2')
        excelLoader = new OpenEhrExcelLoader()

        def modelCatalogueSearchService = Holders.applicationContext.getBean("elasticSearchService")
        modelCatalogueSearchService.reindex(true)
    }


    def "test expected output for OpenEHR #file"() {

        when:
        excelLoader.loadModel(WorkbookFactory.create(new FileInputStream(resourcePath + '/' + 'GEL_openEHR_Cross_references_test.xlsx')))

        DataModel dm = DataModel.findByName("Open EHR")
        DataElement de = DataElement.findByName("RD-Phenotype.Participant ID (12502.2)")

        then:
        dm
        de
        de.relatedTo[0].name == "LOCAL PATIENT IDENTIFIER*"


//        expect:
//        similar excelLoaderXmlResult(file, '_openEHR_Cross_references_test').left, getClass().getResourceAsStream('OpenEhrTest.xml').text
//        where:
//        file << testOpenEhrFiles

    }


}
