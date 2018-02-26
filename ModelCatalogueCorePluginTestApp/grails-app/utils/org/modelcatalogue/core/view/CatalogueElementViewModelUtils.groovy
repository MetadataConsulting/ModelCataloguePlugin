package org.modelcatalogue.core.view

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.IdName
import org.modelcatalogue.core.util.MetadataDomain

@CompileStatic
class CatalogueElementViewModelUtils {

    @CompileDynamic
    static List<CatalogueElementViewModel> ofProjections(MetadataDomain domain, def dataElementList) {
        if ( !dataElementList ) {
            return [] as List<CatalogueElementViewModel>
        }
        dataElementList.collect { def arr ->
            new CatalogueElementViewModel(id: arr[0] as Long,
                    domain: domain,
                    name: arr[1] as String,
                    lastUpdated: arr[2] as Date,
                    status: arr[3] as ElementStatus,
                    modelCatalogueId: arr[4] as String,
                    dataModel: new IdName(id: arr[5] as Long, name: arr[6] as String),
            )
        }
    }

}
