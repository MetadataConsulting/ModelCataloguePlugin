package org.modelcatalogue.core.xml

import groovy.xml.XmlUtil
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import spock.lang.Shared

class CatalogueXmlImportSpec extends AbstractIntegrationSpec {

    @Shared
    GrailsApplication grailsApplication

    CatalogueBuilder builder
    CatalogueXmlLoader loader
    def dataModelService
    def elementService


    def setup() {
        initCatalogue()

    }

    def "load bases"(){
        final String DATA_MODEL_NAME = "based on test"
        final String GRAND_PARENT_NAME = "grand parent"
        final String PARENT_NAME = "parent"
        final String CHILD_NAME = "child"
        final String GRAND_CHILD_NAME = "grand child"

        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService, true)
        loader = new CatalogueXmlLoader(builder)

        when:
            InputStream nhic = getClass().getResourceAsStream('grand_child.catalogue.xml')
            loader.load(nhic)

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

}
