package org.modelcatalogue.gel.export

import org.modelcatalogue.core.DataModel
import org.modelcatalogue.gel.GenomicsService


class RareDiseasesWebsiteExporter {

    final GenomicsService genomicsService
    final DataModel dataModel

    RareDiseasesWebsiteExporter(GenomicsService genomicsService, DataModel dataModel) {
        this.genomicsService = genomicsService
        this.dataModel = dataModel
    }
}









