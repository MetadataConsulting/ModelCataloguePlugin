package org.modelcatalogue.core.dataarchitect
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataClass
import spock.lang.Ignore
import spock.lang.Requires

@Requires({ !System.getenv('TRAVIS') })
class UmljServiceISpec extends AbstractIntegrationSpec {

    def umljService
    def catalogueBuilder

    @Ignore
    def "test import"() {
        initCatalogue()
        def filenameXsd = "test/integration/resources/CLLDataModel0.1.umlj"
        DataModel classification = new DataModel(name: "GeL Cancer Core ${System.currentTimeMillis()}").save(failOnError: true)

        when:
        InputStream inputStream = new FileInputStream(filenameXsd)
        umljService.importUmlDiagram(catalogueBuilder, inputStream, "rare_diseases_combined", classification)

        def patient = DataClass.findByName("Patient")

        then:
        patient

        when:
        def patientData = patient.contains

        then:
        patientData
        patientData.size() >= 5

        when:
        def de4 = patientData[4]

        then:
        de4

    }
}
