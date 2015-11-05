package org.modelcatalogue.core.xml

import org.modelcatalogue.core.Asset

class AssetPrintHelper extends CatalogueElementPrintHelper<Asset> {

    @Override
    String getTopLevelName() {
        "asset"
    }
}
