package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.DataModelListPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore

import static org.modelcatalogue.core.geb.Common.getAdmin
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

//@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteA')  })
@Stepwise
class FastActionsVersionSpec extends AbstractModelCatalogueGebSpec{


    private static final String create="a#role_data-models_create-data-modelBtn"
    private static final String  fastActions="a#role_navigation-right_fast-action-menu-item-link>span:nth-child(1)"
    private static final String  catalogueVersion ="div.modal-body>div:nth-child(2)>div>a:nth-child(7)"
    private static final String  version = "div.modal-body"


    def "login to model catalogue "() {
        when:
        to LoginPage
        LoginPage loginPage = browser.page LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DataModelListPage

        and:
        check create displayed
    }

    def "select fast actions and click on the activity"() {

        click fastActions
        click catalogueVersion

        expect:
        check modalHeader contains "Model Catalogue Version"
    }

    def "verify the Model Catalogue Version"() {
        expect:
        check version contains "Version"
        click modalPrimaryButton
    }
}
