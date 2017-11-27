package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.openqa.selenium.Keys
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.IgnoreRest
import spock.lang.Stepwise
import static org.modelcatalogue.core.geb.Common.*

@IgnoreIf({ !System.getProperty('geb.env') })
//@Ignore
@Stepwise
class CreateTagSpec extends AbstractModelCatalogueGebSpec {

    private static final String  table = 'td.col-md-4'
    private static final String  tagCreated = 'td.col-md-4>span>span>a'
    private static final String  deleteButton = 'a#delete-menu-item-link>span:nth-child(3)'
    private static final String   dataElementButton = 'a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'

    @IgnoreRest
    def "login to model catalogue and select element"() {

        when:
        loginCurator()
        select 'Test 3' open 'Data Elements' select 'No tags'
        then:
        check rightSideTitle is 'Active Data Elements'
    }

    def "Navigate to create a tag page"() {
        when:
        click create
        then:
        check modalHeader is "Create Data Element"
    }

    def " create a tag"() {
        when:
        fill nameLabel with("TESTING_TAG")
        fill modelCatalogueId with "M-234"
        fill description with "I am creating a tag ${System.currentTimeMillis()}"
        click save

        then:
        check table contains 'TESTING_TAG'
    }

    def"delete the created tag"(){

        when:
        click tagCreated

        and:
        click dataElementButton

        and:
        click deleteButton

        and:
        click modalPrimaryButton

        then:
        check table isGone()
    }
}
