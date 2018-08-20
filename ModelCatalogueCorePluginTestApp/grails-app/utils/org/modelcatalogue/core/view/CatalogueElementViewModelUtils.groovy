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
            new CatalogueElementViewModel(id: arr.size() >= 1 ? arr[0] as Long : null,
                    domain: domain,
                    name: arr.size() >= 2 ? arr[1] as String : null,
                    lastUpdated: arr.size() >= 3 ? arr[2] as Date : null,
                    status: arr.size() >= 4 ? arr[3] as ElementStatus : null,
                    modelCatalogueId: arr.size() >= 5 ?arr[4] as String : null,
                    dataModel: arr.size() >= 7 ? new IdName(id: arr[5] as Long, name: arr[6] as String) : null,
            )
        }
    }

}
