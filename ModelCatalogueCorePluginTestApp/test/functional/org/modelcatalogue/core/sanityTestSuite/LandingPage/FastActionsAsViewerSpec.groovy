package org.modelcatalogue.core.sanityTestSuite.LandingPage

import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class FastActionsAsViewerSpec extends AbstractModelCatalogueGebSpec {

    private static final String myModels = 'li#my-models>a'
    private static final String fastActions = 'span.fa-flash'
    private static final String catalogueVersion = 'div.modal-body>div:nth-child(2)>div>a:nth-child(2)'
    private static final String version = "div.modal-body"
    private static final String allDataModels = 'div.modal-body>div:nth-child(2)>div>a:nth-child(3)'

    def "login to model catalogue"() {
        loginViewer()

        expect:
        Thread.sleep(2000l)
        check  myModels contains'My Models'
    }

    def "navigate to the top menu and select fast actions"() {

        when:
        click fastActions

        and:'select version'
        click catalogueVersion

        then:
        check modalHeader contains "Model Catalogue Version"
    }

    def "verify the Model Catalogue Version"() {

        expect:
        check version contains "Version"
        click modalPrimaryButton
    }

    def "select show all models"() {

        when:
        click fastActions

        and:'select show all data models'
        click allDataModels

        then:
        check myModels displayed
    }

}
