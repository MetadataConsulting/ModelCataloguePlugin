package org.modelcatalogue.core.sanityTestSuite

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.gebUtils.Common.create
import static org.modelcatalogue.core.gebUtils.Common.description
import static org.modelcatalogue.core.gebUtils.Common.messages
import static org.modelcatalogue.core.gebUtils.Common.modalHeader
import static org.modelcatalogue.core.gebUtils.Common.modalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.modelCatalogueId
import static org.modelcatalogue.core.gebUtils.Common.nameLabel
import static org.modelcatalogue.core.gebUtils.Common.rightSideTitle
import static org.modelcatalogue.core.gebUtils.Common.save

@Stepwise
class CreateDataTypeAndSelectPrimitiveSpec extends AbstractModelCatalogueGebSpec {

    private static final String primitive = "input#pickPrimitiveType"
    private static final String measurementUnit = "input#measurementUnit"
    private static final String deleteButton= "a#delete-menu-item-link>span:nth-child(3)"
    private static final String  createMeasurement = "a.create-new-cep-item>span>span:nth-child(2"
    private static final String  primitiveTypeButton = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String dataTypeCreated = "tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a"
    private static final String firstRow = "tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)"

   def "login to Model Catalogue and select model"() {
    when:
    loginCurator()
    select 'Test 3'
    selectInTree 'Data Types'
    then:
    check rightSideTitle contains 'Active Data Types'
}

def "Navigate to data type page"() {
    when:
    click create
    then:
    check modalHeader contains 'Create Data Type'
}

def " fill the create data type form"() {
    when:
    fill nameLabel with "TESTING_DATA_TYPE"

    fill modelCatalogueId with "MET-4567"

    fill description with "my description of data type${System.currentTimeMillis()}"

    and: 'select primitive button'
    click primitive
    fill measurementUnit with 'boolean'
    remove messages
    and:
    click save
    then:
    check firstRow contains 'TESTING_DATA_TYPE'

}

def "delete the created data type"() {

    when:
    click dataTypeCreated

    and:
    click primitiveTypeButton
    and:
    click deleteButton
    and:
    click modalPrimaryButton

    then:
    check firstRow isGone()

  }
}
