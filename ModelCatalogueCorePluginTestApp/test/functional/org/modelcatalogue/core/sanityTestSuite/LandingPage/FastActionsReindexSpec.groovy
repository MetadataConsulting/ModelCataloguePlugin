package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec

import static org.modelcatalogue.core.geb.Common.getAdmin
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton


class FastActionsReindexSpec extends AbstractModelCatalogueGebSpec{


    private static final String create="a#role_data-models_create-data-modelBtn"
    private static final String  fastActions="a#role_navigation-right_fast-action-menu-item-link>span:nth-child(1)"
    private static final String  reindex ="div.modal-body>div:nth-child(2)>div>a:nth-child(11)"


    def "login to model catalogue "(){
        login admin

        expect:
        check create displayed
    }

    def " select fast actions and click on the activity "(){

        click fastActions
        click reindex

        expect:
        check modalHeader contains "Do you want to reindex"

    }
    def "verify the most recent active users"(){

        expect:
        click modalPrimaryButton
    }
}
