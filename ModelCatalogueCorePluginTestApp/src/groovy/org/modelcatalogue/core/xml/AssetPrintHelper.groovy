package org.modelcatalogue.core.xml

import org.modelcatalogue.core.Asset

/** Helper for printing Assets– simply overrides getTopLevelName */
@Singleton
class AssetPrintHelper extends CatalogueElementPrintHelper<Asset> {

    @Override
    String getTopLevelName() {
        "asset"
    }
}
