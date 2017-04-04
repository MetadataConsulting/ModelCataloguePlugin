package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import static org.modelcatalogue.core.geb.Common.*

/**
 * Created by Berthe on 15/03/2017.
 */
class SearchCatalogueModelsSpec  extends AbstractModelCatalogueGebSpec{
    private static final String searchInput2 ="input#data-model-search-box"
    private static final String defaultButton = 'button#status-selector'
    private static final String all = "a#status-selector-all"
    private static final String draft = "a#status-selector-draft"
    private static final String finalized = "a#status-selector-finalized"

    void doSearch(){
        when:
            login viewer

        then:
            noExceptionThrown()

        when:
            // type in the search box
            fill searchInput2 with "cancel"
            // click on button next to search catalogue
            click defaultButton
            click draft

        then:
            check defaultButton is "Draft"

        when:
            // click on button next to search catalogue
            click defaultButton
            click finalized

        then:
            check defaultButton is "Finalized"

    }











}
