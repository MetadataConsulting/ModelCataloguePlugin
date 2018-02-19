package org.modelcatalogue.core.regression.datamodel.admin

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.curator
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteA')  })
@Stepwise
class ShowAllDataModelsSpec  extends AbstractModelCatalogueGebSpec {

    private static final String  create  = "a#role_data-models_create-data-modelBtn"
    private static final String  fastActions="a#role_navigation-right_fast-action-menu-item-link>span:nth-child(1)"
    private static final String  showAllModels="div.modal-body>div:nth-child(2)>div>a:nth-child(7)>h4"

    def "login to model catalogue"() {
        when:
        login Common.curator

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
