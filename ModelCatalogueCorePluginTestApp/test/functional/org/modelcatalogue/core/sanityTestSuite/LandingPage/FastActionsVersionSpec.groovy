package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.gebUtils.Common.getAdmin
import static org.modelcatalogue.core.gebUtils.Common.getModalHeader
import static org.modelcatalogue.core.gebUtils.Common.getModalPrimaryButton

@Stepwise
class FastActionsVersionSpec extends AbstractModelCatalogueGebSpec{


    private static final String create="a#role_data-models_create-data-modelBtn"
    private static final String  fastActions="a#role_navigation-right_fast-action-menu-item-link>span:nth-child(1)"
    private static final String  catalogueVersion ="div.modal-body>div:nth-child(2)>div>a:nth-child(7)>h4"
    private static final String  version = "div.modal-body"


    def "login to model catalogue "(){
        login admin

        expect:
        check create displayed
    }

    def " select fast actions and click on the activity "(){

        click fastActions
        click catalogueVersion

        expect:
        check modalHeader contains "Model Catalogue Version"

    }
    def "verify the Model Catalogue Version"(){

        expect:
        check version contains "Version"
        click modalPrimaryButton
    }
}
