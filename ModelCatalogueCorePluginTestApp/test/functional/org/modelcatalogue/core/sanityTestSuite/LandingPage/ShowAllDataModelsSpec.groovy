package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.curator

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class ShowAllDataModelsSpec  extends AbstractModelCatalogueGebSpec {

    private static final String  create  = "a#role_data-models_create-data-modelBtn"
    private static final String  fastActions="a#role_navigation-right_fast-action-menu-item-link>span:nth-child(1)"
    private static final String  showAllModels="div.modal-body>div:nth-child(2)>div>a:nth-child(7)>h4"

    def "login to model catalogue"() {
        when:
        login curator

        then:
        check create displayed
    }

    def "navigate to the top menu and select fast action"() {
        when:
        click fastActions
        click  showAllModels

        then:
        check create displayed
    }
}
