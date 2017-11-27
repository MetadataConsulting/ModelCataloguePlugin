package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.admin
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class FastActionsActivitySpec extends AbstractModelCatalogueGebSpec{

    private static final String create="a#role_data-models_create-data-modelBtn"
    private static final String  fastActions="a#role_navigation-right_fast-action-menu-item-link>span:nth-child(1)"
    private static final String  activity ="div.modal-body>div:nth-child(2)>div>a:nth-child(2)"
    private static final String  user = "td.col-md-8"


    def "login to model catalogue "(){
        login admin

        expect:
        check create displayed
    }

    def " select fast actions and click on the activity "(){
        Thread.sleep(2000L)
        click fastActions
        click activity

        expect:
        check modalHeader contains "Recent Activity"

    }

    @Ignore // TODO: Discuss last seen feature
    def "verify the most recent active users"(){

       expect:
       Thread.sleep(3000L)
       check user displayed
        click modalPrimaryButton
    }
}
