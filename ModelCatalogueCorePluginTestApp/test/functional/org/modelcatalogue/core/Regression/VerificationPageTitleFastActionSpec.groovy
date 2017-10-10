package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import spock.lang.Title
import static org.modelcatalogue.core.geb.Common.*


@Stepwise @Title("https://metadata.atlassian.net/browse/MET-1486")
class VerificationPageTitleFastActionSpec extends AbstractModelCatalogueGebSpec{


    private static final String  activeUsers ="div.form-group>table>tbody>tr:nth-child(1)>th"
    private static final String  cancelButton ="button.btn-warning"
    private static final String  textPresent ="form.ng-pristine>div:nth-child(3)>label"
    private static final String  headerPresent ="form.ng-pristine>div:nth-child(1)>label"



    def" Login to Model Catalogue"(){


        when:
        loginAdmin()

        then:
        check 'span.mc-name' contains'Model Catalogue'
    }

    def" Select Fast Action and page titles"(){


        when:
        fastAction 'Activity'

        then:
        check modalHeader is 'Recent Activity'

        and:
        check activeUsers is 'Most Recent Active Users'
        click modalPrimaryButton



        when:
        fastAction 'Import Catalogue XML'


        then:
        check modalHeader is 'Import Model Catalogue XML File'
        check '#asset' isDisplayed()
        click cancelButton



        when:
        fastAction 'Import Excel'

        then:
        check modalHeader is 'Import Excel File'
        check textPresent is 'Customize Columns Headers'
        click cancelButton


        when:
        fastAction 'Import Model Catalogue DSL File'

        then:
        check modalHeader is 'Import Model Catalogue DSL File'
        check '#asset' isDisplayed()
        click cancelButton


        when:
        fastAction 'Import OBO'

        then:
        check modalHeader is 'Import OBO File'
        check headerPresent is 'Ontology Name'
        click cancelButton


        when:
        fastAction 'Model Catalogue Version'

        then:
        check modalHeader is 'Model Catalogue Version'
        check 'div.modal-body' contains 'Version'
        click modalPrimaryButton

    }
}
