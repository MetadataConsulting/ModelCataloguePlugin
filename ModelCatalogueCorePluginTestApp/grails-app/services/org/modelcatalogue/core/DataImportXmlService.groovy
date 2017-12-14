package org.modelcatalogue.core

import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.integration.xml.CatalogueXmlLoader

class DataImportXmlService extends  AbstractDataImportService {

    @Override
    String getContentType() {
        'application/xml'
    }

    @Override
    String getExecuteBackgroundMessage() {
        'Imported from XML'
    }

    @Override
    void loadInputStream(DefaultCatalogueBuilder defaultCatalogueBuilder, InputStream inputStream, String name) {
        CatalogueXmlLoader loader = new CatalogueXmlLoader(defaultCatalogueBuilder)
        loader.load(inputStream)
    }

}
