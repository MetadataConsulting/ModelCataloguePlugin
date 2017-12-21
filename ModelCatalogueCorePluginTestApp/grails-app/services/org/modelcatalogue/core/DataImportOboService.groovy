package org.modelcatalogue.core

import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.integration.obo.OboLoader

class DataImportOboService extends AbstractDataImportService {

    @Override
    String getContentType() {
        'text/obo'
    }

    @Override
    String getExecuteBackgroundMessage() {
        'Imported from OBO'
    }

    @Override
    void loadInputStream(DefaultCatalogueBuilder defaultCatalogueBuilder, InputStream inputStream, String name) {
        OboLoader loader = new OboLoader(defaultCatalogueBuilder)
        loader.load(inputStream, name)
    }
}