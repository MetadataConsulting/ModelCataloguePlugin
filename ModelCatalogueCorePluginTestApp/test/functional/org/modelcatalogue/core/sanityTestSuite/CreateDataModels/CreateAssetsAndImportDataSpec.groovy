package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import grails.plugin.springsecurity.SecurityTagLib
import org.modelcatalogue.core.AssetWizardSpec
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueContent
import org.modelcatalogue.core.geb.Common.*
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save

@Stepwise
class CreateAssetsAndImportDataSpec extends AbstractModelCatalogueGebSpec{
    private static final String asset ="input#asset"
    private static final String circle = "span.fa-plus-circle"

    def"login and navigate to the model"(){
        when:
        loginCurator()
        select 'Test 6'
        selectInTree 'Assets'

        then:
        check rightSideTitle contains 'Active Assets'
    }
    def"navigate to create asset page"(){
        when:
        click create
        then:
        check modalHeader contains 'Create Asset'
    }
    def"create a new asset"(){
        when:
        fill nameLabel with " Sample excel${System.currentTimeMillis()}"
        fill asset with file ('example.xml')
        fill description with 'This is my asset'
        click save
        then:
        noExceptionThrown()
    }
    def"login and navigate to model"(){
        when:
        loginCurator()
        select 'Test 6'
        selectInTree 'Imported Data Models'
        then:
       check  rightSideTitle contains 'Imports'
    }

    def" import model "(){
        when:
        addDataModelImport 'Clinical Tags', 'Java'
        then:
        noExceptionThrown()
    }
    String file(String name) {
        new File(AssetWizardSpec.getResource(name).toURI()).absolutePath
    }

}
