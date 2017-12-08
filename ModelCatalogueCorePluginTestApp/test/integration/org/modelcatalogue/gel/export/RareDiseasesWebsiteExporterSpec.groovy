package org.modelcatalogue.gel.export

import grails.gsp.PageRenderer
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.util.test.FileOpener
import spock.lang.IgnoreIf

@IgnoreIf( { System.getProperty('spock.ignore.slow') })
class RareDiseasesWebsiteExporterSpec extends AbstractRareDiseasesExporterSpec {

    @Rule TemporaryFolder folder = new TemporaryFolder()

    PageRenderer groovyPageRenderer

    def "generate website"() {
        DataModel dataModel = buildTestModel(true)
        File archive = folder.newFile('archive.zip')

        when:
        RareDiseasesWebsiteExporter exporter = new RareDiseasesWebsiteExporter(genomicsService, dataModel, groovyPageRenderer)
        archive.withOutputStream {
            exporter.export(it)
        }

        FileOpener.open(archive)

        then:
        noExceptionThrown()
    }

}
