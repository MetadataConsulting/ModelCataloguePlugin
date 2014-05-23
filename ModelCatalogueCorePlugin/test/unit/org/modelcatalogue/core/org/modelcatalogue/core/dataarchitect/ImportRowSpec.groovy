package org.modelcatalogue.core.org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.dataarchitect.ActionType
import org.modelcatalogue.core.dataarchitect.ImportRow
import org.modelcatalogue.core.dataarchitect.RowAction
import spock.lang.Specification

/**
 * Created by adammilward on 23/05/2014.
 */
class ImportRowSpec extends Specification {

    def "test create new import row"(){

        when:
        ImportRow importRow = new ImportRow()
        importRow.dataElementName = "test"
        importRow.dataElementCode = "MC_65c2ed1c-ed7a-4d8d-b4aa-7991174a00ed_1"
        importRow.parentModelName = "testParentModel"
        importRow.parentModelCode = "MC_65c2ed1c-ed7a-4d8d-b4aa-7921174a00ed_1"
        importRow.containingModelName = "testContainingModel"
        importRow.containingModelCode = "MC_68c2ed1c-ed7a-4d8d-b4aa-7921174a00ed_1"
        importRow.dataType =  "String"
        importRow.dataElementDescription =   "de description"
        importRow.measurementUnitName =   "mph"
        importRow.conceptualDomainName = "conceptual domain"
        importRow.conceptualDomainDescription = "conceptual domain description"

        then:
        importRow

    }

    def "test create new import row with action"(){

        when:
        ImportRow importRow = new ImportRow()
        RowAction rowAction = new RowAction()
        rowAction.field = "dataElementName"
        rowAction.action = "This row contains a new data element. Would you like to create one?"
        rowAction.actionType = ActionType.CREATE_DATA_ELEMENT
        importRow.dataElementName = "test"
        importRow.dataElementCode = "MC_65c2ed1c-ed7a-4d8d-b4aa-7991174a00ed_1"
        importRow.parentModelName = "testParentModel"
        importRow.parentModelCode = "MC_65c2ed1c-ed7a-4d8d-b4aa-7921174a00ed_1"
        importRow.containingModelName = "testContainingModel"
        importRow.containingModelCode = "MC_68c2ed1c-ed7a-4d8d-b4aa-7921174a00ed_1"
        importRow.dataType =  "String"
        importRow.dataElementDescription =   "de description"
        importRow.measurementUnitName =   "mph"
        importRow.conceptualDomainName = "conceptual domain"
        importRow.conceptualDomainDescription = "conceptual domain description"
        importRow.rowActions.add(rowAction)

        then:
        importRow
        importRow.rowActions.contains(rowAction)

        when:
        importRow.resolveAction("dataElementName", ActionType.CREATE_DATA_ELEMENT)

        then:
        !importRow.rowActions.contains(rowAction)

    }

}

