package modelcataloguegenomicsplugin

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

//@TestFor(GelJsonService)
class GelJsonServiceSpec extends IntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()

        DefaultCatalogueBuilder catalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)

        catalogueBuilder.build {

            dataModel(name: 'testDataModel1') {
                dataClass(name: 'rare disease group 1') {
                    dataClass(name: 'rare disease subgroup 1.1') {
                        dataClass(name: 'rare disease disorder 1.1.1') {
                            dataClass(name: 'rare disease disorder 1.1.1 eligibility',lastUpdated: new Date())
                            dataClass(name: 'rare disease disorder 1.1.1 phenotypes',lastUpdated: new Date()) {
                                dataClass(name: 'test hpo terms 1')
                                dataClass(name: 'test hpo terms 2')
                                dataClass(name: 'test hpo terms 3')
                                dataClass(name: 'test hpo terms 4')
                            }
                            dataClass(name: 'rare disease disorder 1.1.1 clinical tests',lastUpdated: new Date()) {
                                dataClass(name: 'clinical test1')
                                dataClass(name: 'clinical test2')
                                dataClass(name: 'clinical test3')
                                dataClass(name: 'clinical test4')
                            }
                        }
                    }
                }
            }
        }

    }


    def "export model to json"(){

        when:
        DataClass model = DataClass.findByName('rare disease group 1')
        def json = new GelJsonService().printDiseaseOntology(model)

        then:
        println json
        json == expected

    }

    def today = new Date().format("yyyy-MM-dd")
    def expected = "{\"DiseaseGroups\": [\n" +
        "{ \n" +
        "   \"id\" : \"3\",\n" +
        "   \"name\" : \"rare disease subgroup 1.1\",   \"subGroups\" : [{      \n" +
        "       \"id\" : \"4\",\n" +
        "       \"name\" : \"rare disease disorder 1.1.1\",       \"specificDisorders\" : [           { \n" +
        "           \"id\" : \"5\",\n" +
        "            \"name\" : \"rare disease disorder 1.1.1 eligibility\",\n" +
        "                \"eligibilityQuestion\": {\n" +
        "                        \"date\":\"" + today + "\",\n" +
        "                        \"version\": \"1\"\n" +
        "                   },\n" +
        "           \"shallowPhenotypes\" : [],\n" +
        "           \"tests\" : []\n" +
        "           }\n" +
        ",           { \n" +
        "           \"id\" : \"6\",\n" +
        "            \"name\" : \"rare disease disorder 1.1.1 phenotypes\",\n" +
        "                \"eligibilityQuestion\": {\n" +
        "                        \"date\":\"" + today + "\",\n" +
        "                        \"version\": \"1\"\n" +
        "                   },\n" +
        "           \"shallowPhenotypes\" : [],\n" +
        "           \"tests\" : [\n" +
        "           ]\n" +
        "           }\n" +
        ",           { \n" +
        "           \"id\" : \"11\",\n" +
        "            \"name\" : \"rare disease disorder 1.1.1 clinical tests\",\n" +
        "                \"eligibilityQuestion\": {\n" +
        "                        \"date\":\""+ today + "\",\n" +
        "                        \"version\": \"1\"\n" +
        "                   },\n" +
        "           \"shallowPhenotypes\" : [],\n" +
        "           \"tests\" : [\n" +
        "           ]\n" +
        "           }\n" +
        "       ]\n" +
        "       }\n" +
        "]\n" +
        "        }]\n" +
        "}"

}
