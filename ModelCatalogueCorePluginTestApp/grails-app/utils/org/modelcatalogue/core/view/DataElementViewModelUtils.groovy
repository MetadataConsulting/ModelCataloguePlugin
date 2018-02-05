package org.modelcatalogue.core.view

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.IdName

@CompileStatic
class DataElementViewModelUtils {

    @CompileDynamic
    static List<DataElementViewModel> ofProjections(def dataElementList) {
        if ( !dataElementList ) {
            return [] as List<DataElementViewModel>
        }
        dataElementList.collect { def arr ->
            new DataElementViewModel(id: arr[0] as Long,
                    name: arr[1] as String,
                    lastUpdated: arr[2] as Date,
                    status: arr[3] as ElementStatus,
                    modelCatalogueId: arr[4] as String,
                    dataModel: new IdName(id: arr[5] as Long, name: arr[6] as String),
            )
        }
    }

}
