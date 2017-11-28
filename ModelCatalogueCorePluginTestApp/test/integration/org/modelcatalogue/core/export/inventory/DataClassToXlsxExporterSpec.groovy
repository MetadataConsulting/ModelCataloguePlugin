package org.modelcatalogue.core.export.inventory

import static org.modelcatalogue.core.util.test.FileOpener.open
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*

class DataClassToXlsxExporterSpec extends AbstractIntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService
    GrailsApplication grailsApplication

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    def setup() {
        buildComplexModel(dataModelService, elementService)
    }

    def "export model to excel"() {
        when:
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")
        DataClass model = DataClass.findByName(COMPLEX_MODEL_ROOT_DATA_CLASS_NAME)

        CatalogueElementToXlsxExporter.forDataClass(model, dataClassService, grailsApplication).export(file.newOutputStream())

        open file

        then:
        noExceptionThrown()

    }

}
