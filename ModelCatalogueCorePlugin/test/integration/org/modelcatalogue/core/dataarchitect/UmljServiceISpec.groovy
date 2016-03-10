package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataClass

class UmljServiceISpec extends AbstractIntegrationSpec {

    UmljService umljService
    CatalogueBuilder catalogueBuilder

    def "test import"() {
        initCatalogue()

        def filenameXsd = "test/integration/resources/CLLDataModel0.1.umlj"
        DataModel classification = new DataModel(name: "GeL Cancer Core").save()

        when:
        InputStream inputStream = new FileInputStream(filenameXsd)
        umljService.importUmlDiagram(catalogueBuilder, inputStream, "rare_diseases_combined", classification)

        def patient = DataClass.findByName("Patient")
        def patientData = patient.contains
        def de4 = patientData[4]

        then:
        patient
        de4

    }
}
