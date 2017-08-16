package org.modelcatalogue.core.xml

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import spock.lang.Shared

class CatalogueXmlImportSpec extends AbstractIntegrationSpec {

    @Shared
    GrailsApplication grailsApplication

    def dataModelService
    def elementService
    @Shared DefaultCatalogueBuilder builder
    @Shared CatalogueXmlLoader catalogueXmlLoader

    def setup() {
        initCatalogue()
        builder = new DefaultCatalogueBuilder(dataModelService, elementService, true)
        catalogueXmlLoader = new CatalogueXmlLoader(builder)

    }

    def "load bases"(){
        final String DATA_MODEL_NAME = "based on test"
        final String GRAND_PARENT_NAME = "grand parent"
        final String PARENT_NAME = "parent"
        final String CHILD_NAME = "child"
        final String GRAND_CHILD_NAME = "grand child"



        when:
            InputStream nhic = getClass().getResourceAsStream('grand_child_without_id.catalogue.xml')
            catalogueXmlLoader.load(nhic)

            DataModel dataModel = DataModel.findByName(DATA_MODEL_NAME)
            DataType grandParent = DataType.findByName(GRAND_PARENT_NAME)
            DataType parent = DataType.findByName(PARENT_NAME)
            DataType child = DataType.findByName(CHILD_NAME)
            DataType grandChild = DataType.findByName(GRAND_CHILD_NAME)

        then:
            dataModel
            grandParent
            grandParent.dataModel == dataModel
            parent
            parent.dataModel == dataModel
            child
            child.dataModel == dataModel
            grandChild
            grandChild.dataModel == dataModel
    }

    def "test updating a data model"() {
        when: "initial models loaded"
            catalogueXmlLoader.load(getClass().getResourceAsStream('test_update_models_initial.catalogue.xml'))

        then:
            DataModel dataModel = DataModel.findByName('DM1')
            assert dataModel
            dataModel.dataElements.findAll {dataElement ->
                dataElement.name == 'DE1'
            }.size() == 1 // a DM1 data model has a DE1 data element
        when: "models updated 1st time"
            catalogueXmlLoader.load(getClass().getResourceAsStream('test_update_models_update1.catalogue.xml'))
        then:
            List<DataModel> dataModelsDM1U1 = DataModel.findAllByName('DM1')
            /**
             *  after update, there is still only one 'DM1' data model and it still has
             *  'DE1' data element– update does not delete.
             */
            dataModelsDM1U1.size() == 1
            dataModelsDM1U1[0].dataElements.findAll {dataElement ->
                dataElement.name == 'DE1'
            }.size() == 1

            /**
             * There are now two DM2: the previous, now deprecated, and the current, a draft.
             * And 'DM2' data model has 'DE1' element with extension 'EXT1' value 'EXTVAL2'
             */
            List<DataModel> dataModelDM2s = DataModel.findAllByName('DM2')
            dataModelDM2s.size() == 2
            DataModel dataModelDM2Current = dataModelDM2s[1]
            DataElement dm2de1 =
                dataModelDM2Current.dataElements.find {dataElement ->
                    dataElement.name == 'DE1'
                }
            dm2de1.ext.get('EXT1') == 'EXTVAL2'
        when: "models updated 2nd time"
            catalogueXmlLoader.load(getClass().getResourceAsStream('test_update_models_update2.catalogue.xml'))
        then:
            List<DataModel> dataModelsDM1U2 = DataModel.findAllByName('DM1')
            /**
             *  after update, there is still only one 'DM1' data model, with
             *  'DE1' data element, but the extensions changed.
             */
            dataModelsDM1U2.size() == 1
            List<DataElement> dm1de1s =  dataModelsDM1U2[0].dataElements.findAll {dataElement ->
                dataElement.name == 'DE1'
            }
            dm1de1s.size() == 1
            dm1de1s[0].ext.get('EXT1') == 'EXTVAL2'



    }

}
