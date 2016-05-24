package org.modelcatalogue.core.policy;

import org.modelcatalogue.core.CatalogueElement;
import org.modelcatalogue.core.DataModel;

public interface Convention {

    ConventionChecker getChecker();
    Class<? extends CatalogueElement> getTarget();
    String getProperty();
    String getConfiguration();
    String getMessage();

    void verify(DataModel model, CatalogueElement item);

}
