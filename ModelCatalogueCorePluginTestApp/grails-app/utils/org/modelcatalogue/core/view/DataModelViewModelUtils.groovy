package org.modelcatalogue.core.view

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus

@CompileStatic
class DataModelViewModelUtils {

    static List<DataModelViewModel> of(List<DataModel> dataModelList) {
        if ( !dataModelList ) {
            return [] as List<DataModelViewModel>
        }
        dataModelList.collect { DataModel dataModel ->
            List<AssetViewModel> assetList = dataModel.assets.collect { Asset asset ->
                new AssetViewModel(name: asset.name, id: asset.id)
            }
            new DataModelViewModel(id: dataModel.id,
                    name: dataModel.name,
                    lastUpdated: dataModel.lastUpdated,
                    semanticVersion: dataModel.semanticVersion,
                    status: dataModel.status,
                    assetsList: assetList)
        }
    }
}