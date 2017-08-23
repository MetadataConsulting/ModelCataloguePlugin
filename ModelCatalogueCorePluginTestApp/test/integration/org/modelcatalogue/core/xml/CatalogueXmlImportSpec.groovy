package org.modelcatalogue.core.xml

import org.codehaus.groovy.grails.commons.GrailsApplication

import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import spock.lang.Ignore
import spock.lang.Shared

import java.nio.charset.StandardCharsets

class CatalogueXmlImportSpec extends AbstractIntegrationSpec {

    @Shared
    GrailsApplication grailsApplication

    DataModelService dataModelService
    ElementService elementService
    @Shared DefaultCatalogueBuilder defaultCatalogueBuilder
    @Shared CatalogueXmlLoader catalogueXmlLoader

    def setup() {
        initCatalogue()
        defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService, true)
        catalogueXmlLoader = new CatalogueXmlLoader(defaultCatalogueBuilder)

    }

    def "load bases"() {
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
    def "test update an initial model/data element with no metadata"() {
        when:
            catalogueXmlLoader.load(inputStreamFromString(xmlFromInstructions {
                dataModel(name:'DM3'){
                    dataElement(name:'DE3') {
                        ext 'EXT1', 'EXTVAL1' //test passed with this!
                    }
                }
            }
            ))
            catalogueXmlLoader.load(inputStreamFromString(xmlFromInstructions {
                dataModel(name:'DM3'){
                    dataElement(name:'DE3') {
                        ext 'EXT1', 'EXTVAL2'
                    }
                }
            }
            ))
        then:
            /**
             * After update, a new DM3 was created, with a new DE3 which has metadata, whereas the first had none at all.
             */
            List<DataModel> dataModelDM3s = DataModel.findAllByName('DM3')
            assert dataModelDM3s.size() == 2
            assert dataModelDM3s[0].status == ElementStatus.DEPRECATED

            DataModel dataModelDM3Current = dataModelDM3s[1]
            assert dataModelDM3Current.status == ElementStatus.DRAFT

            dataElementFromDataModel('DE3', dataModelDM3Current).ext.get('EXT1') == 'EXTVAL2'

    }
    static Closure testUpdateInitialModelsInstructions = {
        dataModel(name:'DM1'){
            dataElement(name:'DE1'){
                ext 'EXT1', 'EXTVAL1'
            }
        }
        dataModel(name:'DM2') {
            ext 'DM2EXT', 'EXT'
            dataElement(name:'DE2')
        }
    } as Closure

    static Closure testUpdateModelsUpdate1Instructions = {
        dataModel(name:'DM1')
        dataModel(name:'DM2') {
            dataElement(name:'DE1') {
                ext 'EXT1', 'EXTVAL1'
            }
        }
    } as Closure

    static Closure testUpdateModelsUpdate2Instructions = {
        dataModel(name:'DM1'){
            dataElement(name:'DE1'){
                ext 'EXT1', 'EXTVAL2'
                ext 'EXT2', 'EXTVAL3'
            }
        }

/*
        dataModel(name:'DM2') {
            dataElement(name:'DE1') {
                ext 'EXT1', 'EXTVAL2'
            }
            //dataElement(name: 'DE2')
        }*/
    } as Closure

    void afterTestUpdateInitialModels() {
        DataModel dataModel = DataModel.findByName('DM1')
        assert dataModel
        dataModel.dataElements.findAll {dataElement ->
            dataElement.name == 'DE1'
        }.size() == 1 // a DM1 data model has a DE1 data element
    }

    void afterTestUpdateModelsUpdate1() {
        List<DataModel> dataModelsDM1 = DataModel.findAllByName('DM1')
        /**
         *  after update, there is still only one 'DM1' data model and it still has
         *  'DE1' data element– update does not delete.
         */
        assert dataModelsDM1.size() == 1
        assert dataElementFromDataModel('DE1', dataModelsDM1[0])

        /**
         * There are now two DM2: the previous, now deprecated, and the current, a draft.
         * And draft 'DM2' data model has 'DE1' element, and still has 'DE2' element, and still has metadata.
         */
        List<DataModel> dataModelDM2s = DataModel.findAllByName('DM2')
        assert dataModelDM2s.size() == 2
        assert dataModelDM2s[0].status == ElementStatus.DEPRECATED

        DataModel dataModelDM2Current = dataModelDM2s[1]
        assert dataModelDM2Current.status == ElementStatus.DRAFT

        assert dataModelDM2Current.ext.get('DM2EXT') == 'EXT'

        DataElement dm2de1 = dataElementFromDataModel('DE1', dataModelDM2Current)
        assert dm2de1.ext.get('EXT1') == 'EXTVAL1'

        DataElement dm2de2 = dataElementFromDataModel('DE2', dataModelDM2Current)
        assert dm2de2
    }

    void afterTestUpdateModelsUpdate2() {
        List<DataModel> dataModelsDM1 = DataModel.findAllByName('DM1')
        /**
         *  after update, there is still only one 'DM1' data model, with
         *  'DE1' data element, but the extensions changed.
         */
        assert dataModelsDM1.size() == 1
        List<DataElement> dm1de1s =  dataModelsDM1[0].dataElements.findAll {dataElement ->
            dataElement.name == 'DE1'
        }
        assert dm1de1s.size() == 1
        assert dm1de1s[0].ext.get('EXT1') == 'EXTVAL2'

        /*List<DataModel> dataModelDM2s = DataModel.findAllByName('DM2')
        assert dataModelDM2s.size() == 2
        assert dataModelDM2s[1].dataElements.findAll {dataElement ->
            dataElement.name == 'DE1'
        }[0].ext.get('EXT1') == 'EXTVAL2'*/

    }

    DataElement dataElementFromDataModel(String dataElementName, DataModel dataModel) {
        return dataModel.dataElements.find {
            dataElement -> dataElement.name == dataElementName
        }
    }

    def "test updating a data model"() {
        when: "initial models loaded"
            catalogueXmlLoader.load(inputStreamFromString(xmlFromInstructions(testUpdateInitialModelsInstructions)))
        then:
            afterTestUpdateInitialModels()

        when: "models updated 1st time (trying to 'move' DE1 to DM2)"
            catalogueXmlLoader.load(inputStreamFromString(xmlFromInstructions(testUpdateModelsUpdate1Instructions)))
        then:
            afterTestUpdateModelsUpdate1()

        when: "models updated 2nd time (just changing DE1's metadata in DM1)"
            catalogueXmlLoader.load(inputStreamFromString(xmlFromInstructions(testUpdateModelsUpdate2Instructions)))
        then:
            afterTestUpdateModelsUpdate2()

    }
   //@Ignore // should be the same effect as above.
    def "test updating a data model using defaultCatalogueBuilder"() {
        when: "initial models loaded"
        defaultCatalogueBuilder.build(testUpdateInitialModelsInstructions)
        then:
        afterTestUpdateInitialModels()

        when: "models updated 1st time (trying to 'move' DE1 to DM2)"
        defaultCatalogueBuilder.build(testUpdateModelsUpdate1Instructions)
        then:
        afterTestUpdateModelsUpdate1()

        when: "models updated 2nd time (just changing DE1's metadata in DM1)"
        defaultCatalogueBuilder.build(testUpdateModelsUpdate2Instructions)
        then:
        afterTestUpdateModelsUpdate2()

    }

    // helpers
    InputStream inputStreamFromString(String s) {

        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8))
    }

    String xmlFromInstructions(Closure instructions) {
        StringWriter stringWriter = new StringWriter()
        XmlCatalogueBuilder xmlCatalogueBuilder = new XmlCatalogueBuilder(stringWriter, true)
        xmlCatalogueBuilder.build(instructions)
        return stringWriter.toString()
    }
}
