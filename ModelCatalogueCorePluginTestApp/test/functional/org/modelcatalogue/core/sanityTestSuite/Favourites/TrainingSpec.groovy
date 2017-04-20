package org.modelcatalogue.core.sanityTestSuite.Favourites

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.sanityTestSuite.Login.LoginAsViewerSpec

class TrainingSpec extends AbstractModelCatalogueGebSpec {

    def "login to model and select a tab"() {

        when:
        loginAdmin()
        menuItem("role_navigation-right_fast-action-menu-item-link").click()



        then:
       noExceptionThrown()

    }
}
