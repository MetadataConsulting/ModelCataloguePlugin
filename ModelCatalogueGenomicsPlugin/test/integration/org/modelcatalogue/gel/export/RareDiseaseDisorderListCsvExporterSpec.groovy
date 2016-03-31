package org.modelcatalogue.gel.export

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

/**
 * Created by rickrees on 24/03/2016.
 */
class RareDiseaseDisorderListCsvExporterSpec extends IntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    void setup() {
        initCatalogueService.initDefaultRelationshipTypes()

        DefaultCatalogueBuilder catalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)

        catalogueBuilder.build {

            dataModel(name: 'testDataModel1') {
                dataClass(name: 'rare diseases') {
                    dataClass(name: 'disease group 1,with comma') {     //with comma in names
                        dataClass(name: 'disease subgroup 1.1') {
                            dataClass(name: 'disease disorder A') {}
                        }
                        dataClass(name: 'disease subgroup 1.2,with comma') {
                            dataClass(name: 'disease disorder B,with comma') {}
                            dataClass(name: 'disease disorder C') {}
                        }
                    }
                    dataClass(name: '"disease group 2"') {             //with double quotes around names
                        dataClass(name: '"disease subgroup 2.1"') {
                            dataClass(name: '"disease disorder D"') {}
                            dataClass(name: 'disease disorder E') {}
                        }
                    }
                }
            }
        }

    }

    def "Export model to csv"() {
        OutputStream out = new ByteArrayOutputStream()
        when:
        DataClass model = DataClass.findByName('rare diseases')
        new RareDiseaseDisorderListCsvExporter(out).export(model)

        String csv = new String(out.toByteArray())

        then:
        noExceptionThrown()
        csv ==~ expectedCSV
    }



    private static String getExpectedCSV() {
        return '''id,diseaseGroup,diseaseSubgroup,diseaseName
[\\d]+,"disease group 1,with comma","disease subgroup 1.1","disease disorder A"
[\\d]+,"disease group 1,with comma","disease subgroup 1.2,with comma","disease disorder B,with comma"
[\\d]+,"disease group 1,with comma","disease subgroup 1.2,with comma","disease disorder C"
[\\d]+,"disease group 2","disease subgroup 2.1","disease disorder D"
[\\d]+,"disease group 2","disease subgroup 2.1","disease disorder E"'''
    }
}
