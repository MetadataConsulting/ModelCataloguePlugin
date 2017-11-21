package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.gebUtils.CatalogueContent

import spock.lang.IgnoreIf

import spock.lang.Stepwise

import static org.modelcatalogue.core.gebUtils.Common.admin
import static org.modelcatalogue.core.gebUtils.Common.create
import static org.modelcatalogue.core.gebUtils.Common.description
import static org.modelcatalogue.core.gebUtils.Common.getInlineEdit
import static org.modelcatalogue.core.gebUtils.Common.getInlineEditSubmit
import static org.modelcatalogue.core.gebUtils.Common.getUp
import static org.modelcatalogue.core.gebUtils.Common.messages
import static org.modelcatalogue.core.gebUtils.Common.modalHeader
import static org.modelcatalogue.core.gebUtils.Common.modalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.modelCatalogueId
import static org.modelcatalogue.core.gebUtils.Common.nameLabel
import static org.modelcatalogue.core.gebUtils.Common.rightSideTitle
import static org.modelcatalogue.core.gebUtils.Common.save


@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class EditDataElementSpec extends AbstractModelCatalogueGebSpec {

    private static final String editButton="a#role_item-detail_inline-editBtn"
    private static final String dataElementButton="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String  deleteButton="a#delete-menu-item-link>span:nth-child(3)"
    private static final String  dataElement="tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a"
    static final String  metadataLink = 'form.ng-pristine>div:nth-child(7)>p>span>span:nth-child(1)'
    private static final String submit="span.fa-check"
    static final CatalogueContent metadataReviewed = CatalogueContent.create('*[name=metadata-reviewed]')
    static final CatalogueContent metadataApproved = CatalogueContent.create('*[name=metadata-approved]')

    def" login to model catalogue and select a draft model"(){

        login admin
        select 'Test 3'

        expect:
        check rightSideTitle  contains 'Test 3'

    }
    def" edit Basic  Metadata"(){

        when:
        click metadataLink

        then:
        check editButton displayed

        when:
        click inlineEdit
        fill 'metadata-authors' with 'Neville Chamberlain'
        fill 'metadata-reviewers' with 'Édouard Daladier'
        fill 'metadata-owner' with 'Jan Hus'
        fill metadataReviewed with '03/04/2017'
        fill metadataApproved with '03/05/2017'
        fill 'metadata-namespace' with 'Namespace'
        fill 'metadata-organization' with 'Organization'
        3.times { scroll up }
        click inlineEditSubmit

        then:
        check "input[name='name']" gone

        when:
        click editButton
        Thread.sleep(3000l)
        fill 'metadata-organization' with 'MetaData Consulting Limited'
        fill 'metadata-namespace' with 'xString'
        and:'save '
        click submit
        Thread.sleep(3000)

        then:
        check "input[name='name']" gone

    }
    def " create a data class and edit metadata "(){

        when:
        driver.navigate().back()
        Thread.sleep(3000L)
        select 'Test 3'
        selectInTree 'Data Elements'

        then:
        check rightSideTitle contains 'Active Data Elements'


        when:
        click create
        then:
        check modalHeader contains "Create Data Element"


        when:
        fill nameLabel with 'Edit Data Element'
        fill modelCatalogueId with 'MET-111'
        fill description with 'EDIT TESTING'

        and:
        click save

        then:
        check dataElement isDisplayed()

        when:
        Thread.sleep(3000L)
        click dataElement

        then:
        check rightSideTitle contains 'Edit Data Element'

        when:
        Thread.sleep(3000L)
        click editButton
        Thread.sleep(3000l)
        fill 'input#dataType' with  'integer'
        remove(messages)
        click submit

        then:
        noExceptionThrown()

    }

    def"delete the created Data Element"(){

        when:
        click dataElementButton
        click deleteButton

        then:
        check modalHeader is 'Do you really want to delete Data Element Edit Data Element?'

        when:
        click modalPrimaryButton

        then:
        check rightSideTitle contains 'Data Elements'

    }
}
