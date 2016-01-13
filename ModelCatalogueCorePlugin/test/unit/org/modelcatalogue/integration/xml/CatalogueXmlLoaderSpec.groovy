package org.modelcatalogue.integration.xml

import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import spock.lang.Specification

class CatalogueXmlLoaderSpec extends Specification {

    StringWriter stringWriter
    XmlCatalogueBuilder builder
    CatalogueXmlLoader loader

    def setup() {
        XMLUnit.ignoreWhitespace = true
        XMLUnit.ignoreComments = true
        XMLUnit.ignoreAttributeOrder = true
        stringWriter = new StringWriter()
        builder = new XmlCatalogueBuilder(stringWriter)
        loader = new CatalogueXmlLoader(builder)
    }


    def "write simple measurement unit"() {
        expect:
        similar 'newton.catalogue.xml'

    }

    def "load legacy"() {
        expect:
        similar 'newton2.catalogue.xml'

    }

    def "write simple data type"() {
        expect:
        similar 'integer.catalogue.xml'
    }

    def "write enumerated type"() {
        expect:
        similar 'gender.catalogue.xml'
    }

    def "write simple value domain"() {
        expect:
        similar 'force.catalogue.xml'
    }

    def "write simple data element"() {
        expect:
        similar 'adhesion.catalogue.xml'
    }

    def "write simple model"() {
        expect:
        similar 'locomotive.catalogue.xml'
    }

    def "write simple classification"() {
        expect:
        similar 'transportation.catalogue.xml'
    }


    boolean similar(String sampleFile) {
        loader.load(getClass().getResourceAsStream(sampleFile))
        String xml = stringWriter.toString()
        String expected = getClass().getResourceAsStream(sampleFile.replace('catalogue.xml', 'reference.catalogue.xml')).text

        println "==ORIGINAL=="
        println getClass().getResourceAsStream(sampleFile).text

        println "==ACTUAL=="
        println xml

        println "==EXPECTED=="
        println expected



        Diff diff = new Diff(xml, expected)
        DetailedDiff detailedDiff = new DetailedDiff(diff)

        assert detailedDiff.similar(), detailedDiff.toString()
        return true
    }

    String build(@DelegatesTo(CatalogueBuilder) Closure cl) {
        builder.build cl
        stringWriter.toString()
    }

}
