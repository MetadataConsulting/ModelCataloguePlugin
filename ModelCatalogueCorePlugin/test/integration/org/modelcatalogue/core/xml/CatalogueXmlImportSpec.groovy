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

    @Shared GrailsApplication grailsApplication

    CatalogueBuilder builder
    CatalogueXmlLoader loader
    def dataModelService
    def elementService



    def setup() {
        initCatalogue()

    }


   def "load xml - make change - load again"(){

       DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService, true)
       loader = new CatalogueXmlLoader(builder)

       when:
       InputStream nhic = getClass().getResourceAsStream('nhic.catalogue.xml')
       loader.load(nhic)

       then:
       DataModel.findByName("NHIC")


   }
}
