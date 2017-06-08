package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import org.modelcatalogue.core.geb.Common.*

import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton

@Stepwise
class NewVersionOfDataModelSpec extends AbstractModelCatalogueGebSpec{

    private static final String dataModel="a#role_item_catalogue-element-menu-item-link>span:nth-child(1)"
    private static final String newVersion="a#create-new-version-menu-item-link>span:nth-child(3)"
    private static final String semanticVersion="input#semanticVersion"
    private static final String table="tr.warning>td:nth-child(1)"
    private static final String  createdVersion="tr.warning>td:nth-child(1)>a:nth-child(2)"
    private static final String  dataModelButton="a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String  deleteButton="a#delete-menu-item-link>span:nth-child(3)"
    private static String version=" 0.0.2"
    private static final String createVersion="a#role_modal_modal-create-new-versionBtn"

    def " login in to model catalogue and navigate to model "() {
        when:
        loginCurator()
        select 'test'
        selectInTree "Versions"

        then:
        check rightSideTitle contains 'test History'

    }

    def "navigate to data model on the top menu and select version"() {
        when:
        click dataModel
        click newVersion

        then:
        check modalHeader contains "New Version of Data Model"
    }

    def "create a new version"() {
        when:
        fill semanticVersion with version

        click createVersion
        Thread.sleep(3000L)
        click modalPrimaryButton

        then:
        check table contains '0.0.2'
    }
    def"delete the created version"(){

        when:
        Thread.sleep(2000L)
        click createdVersion

        and:
        click dataModelButton

        and:
        click deleteButton

        click modalPrimaryButton


        then:
        check table isGone()
    }
}
