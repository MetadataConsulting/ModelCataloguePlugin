package x.org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.Model

class UmljServiceISpec extends AbstractIntegrationSpec {

    def umljService, initCatalogueService

//    @Ignore
    def "test import"() {
        initCatalogueService.initCatalogue(true)
        def filenameXsd = "test/integration/resources/CLLDataModel0.1.umlj"
        Classification classification = new Classification(name: "GeL Cancer Core").save()

        when:
        InputStream inputStream = new FileInputStream(filenameXsd)
        umljService.importUmlDiagram(inputStream, "rare_diseases_combined", classification)

        def patient = Model.findByName("Patient")
        def patientData = patient.contains
        def de4 = patientData[4]

        then:
        patient
        de4

    }
}
