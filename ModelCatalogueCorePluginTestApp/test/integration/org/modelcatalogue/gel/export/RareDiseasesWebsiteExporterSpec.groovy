package org.modelcatalogue.gel.export

import org.modelcatalogue.core.DataModel

class RareDiseasesWebsiteExporterSpec extends AbstractRareDiseasesExporterSpec {

    def "generate website"() {
        DataModel dataModel = buildTestModel(true)

        RareDiseasesWebsiteExporter exporter = new RareDiseasesWebsiteExporter(genomicsService, dataModel)



    }

}
