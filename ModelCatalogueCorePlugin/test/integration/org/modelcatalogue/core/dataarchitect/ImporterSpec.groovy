package org.modelcatalogue.core.dataarchitect

import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */

class ImporterSpec extends AbstractIntegrationSpec {

    @Shared
    Importer importer
    @Shared
    ImportRow validImportRow, validImportRow2, modelOnlyImportRow, invalidImportRow, modelOnlyImportRow2

    def setupSpec() {
        importer = new Importer()
        loadFixtures()
        validImportRow = new ImportRow()
        validImportRow2 = new ImportRow()
        modelOnlyImportRow = new ImportRow()
        modelOnlyImportRow2 = new ImportRow()
        invalidImportRow = new ImportRow()

        //model only import row
        modelOnlyImportRow.dataElementName = ""
        modelOnlyImportRow.dataElementCode = ""
        modelOnlyImportRow.parentModelName = "testParentModelCode"
        modelOnlyImportRow.parentModelCode = "MC_037e6962-3b6f-4ae4-a171-2570b64dfq10_1"
        modelOnlyImportRow.containingModelName = "testJustModel"
        modelOnlyImportRow.containingModelCode = "MC_037e6162-2b6f-4ae4-a171-2570b64daf10_1"
        modelOnlyImportRow.dataType = ""
        modelOnlyImportRow.dataElementDescription = ""
        modelOnlyImportRow.measurementUnitName = ""
        modelOnlyImportRow.conceptualDomainName = "formula one"
        modelOnlyImportRow.conceptualDomainDescription = " the domain of formula one"

        //model only import row
        modelOnlyImportRow2.dataElementName = ""
        modelOnlyImportRow2.dataElementCode = ""
        modelOnlyImportRow2.parentModelName = ""
        modelOnlyImportRow2.parentModelCode = ""
        modelOnlyImportRow2.containingModelName = "testParentModelCode"
        modelOnlyImportRow2.containingModelCode = "MC_037e6962-3b6f-4ae4-a171-2570b64dfq10_1"
        modelOnlyImportRow2.dataType = ""
        modelOnlyImportRow2.dataElementDescription = ""
        modelOnlyImportRow2.measurementUnitName = ""
        modelOnlyImportRow2.conceptualDomainName = "formula one"
        modelOnlyImportRow2.conceptualDomainDescription = " the domain of formula one"

        //row 1
        validImportRow.dataElementName = "testDataItem"
        validImportRow.dataElementCode = "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1"
        validImportRow.parentModelName = "testParentModelCode"
        validImportRow.parentModelCode = "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1"
        validImportRow.containingModelName = "testModel"
        validImportRow.containingModelCode = "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1"
        validImportRow.dataType = "String"
        validImportRow.dataElementDescription = "test description"
        validImportRow.measurementUnitName = "Degrees of Fahrenheit"
        validImportRow.conceptualDomainName = "formula one"
        validImportRow.conceptualDomainDescription = " the domain of formula one"

        //row 2 -same model as row 1 but different data element
        validImportRow2.dataElementName = "testDataItem2"
        validImportRow2.dataElementCode = "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1"
        validImportRow2.parentModelName = "testParentModelCode"
        validImportRow2.parentModelCode = "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1"
        validImportRow2.containingModelName = "testModel"
        validImportRow2.containingModelCode = "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1"
        validImportRow2.dataType = "String"
        validImportRow2.dataElementDescription = "test description 2"
        validImportRow2.measurementUnitName = "Degrees Celsius"
        validImportRow2.conceptualDomainName = "formula one"
        validImportRow2.conceptualDomainDescription = " the domain of formula one"
    }


    @Unroll
    def "validate valid Row where expected #number"() {

        ImportRow importRow = new ImportRow()

        when:
        importRow.dataElementName = dataElementName
        importRow.dataElementCode = dataElementCode
        importRow.parentModelName = parentModelName
        importRow.parentModelCode = parentModelCode
        importRow.containingModelName = containingModelName
        importRow.containingModelCode = containingModelCode
        importRow.dataType = dataType
        importRow.dataElementDescription = dataElementDescription
        importRow.measurementUnitName = measurementUnitName
        importRow.conceptualDomainName = conceptualDomainName
        importRow.conceptualDomainDescription = conceptualDomainDescription
        def row = importer.validateAndActionRow(importRow)

        then:
        row.rowActions.size() == size
        row.rowActions.collect { it.action == rowAction } == actions

        where:
        number | size | actions | rowAction                                                                                                                  | dataElementName | dataElementDescription | dataElementCode                             | parentModelName       | parentModelCode                             | containingModelName | containingModelCode                         | dataType | measurementUnitName | conceptualDomainName | conceptualDomainDescription
        1      | 0    | []      | null                                                                                                                       | "testDataItem"  | "blah blah blah"       | "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1" | "testParentModelCode" | "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1" | "testModel"         | "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1" | "String" | "mph"               | "formula one"        | " the domain of formula one"
        2      | 1    | [true]  | "Data element does not have model catalogue code. New data element will be created."                                       | "testDataItem"  | "blah blah blah"       | ""                                          | "testParentModelCode" | "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1" | "testModel"         | "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1" | "String" | "mph"               | "formula one"        | " the domain of formula one"
        3      | 1    | [true]  | "No data element in row. Only Model information imported"                                                                  | ""              | "blah blah blah"       | "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1" | "testParentModelName" | "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1" | "testModel"         | "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1" | "String" | "mph"               | "formula one"        | " the domain of formula one"
        4      | 1    | [true]  | "the model catalogue code for the data element is invalid, please action to import row"                                    | "testDataItem"  | "blah blah blah"       | "MC_037easdfsadf70b64dff10_1"               | "testParentModelCode" | "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1" | "testModel"         | "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1" | "String" | "mph"               | "formula one"        | " the domain of formula one"
        5      | 1    | [true]  | "please enter conceptual domain name to import row"                                                                        | "testDataItem"  | "blah blah blah"       | "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1" | "testParentModelCode" | "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1" | "testModel"         | "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1" | "String" | "mph"               | ""                   | " the domain of formula one"
        6      | 1    | [true]  | "please complete the containing model name to import row"                                                                  | "testDataItem"  | "blah blah blah"       | "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1" | "testParentModelCode" | "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1" | ""                  | "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1" | "String" | "mph"               | "formula one"        | " the domain of formula one"
        7      | 1    | [true]  | "Containing model does not have model catalogue code. New model will be created."                                          | "testDataItem"  | "blah blah blah"       | "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1" | "testParentModelCode" | "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1" | "testModel"         | ""                                          | "String" | "mph"               | "formula one"        | " the domain of formula one"
        8      | 1    | [true]  | "the model catalogue code for the containing model is invalid, please action to import row"                                | "testDataItem"  | "blah blah blah"       | "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1" | "testParentModelCode" | "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1" | "testModel"         | "MC_037e6162-5b6f-4ae4-a171-asd"            | "String" | "mph"               | "formula one"        | " the domain of formula one"
        9      | 1    | [true]  | "Parent model does not have model catalogue code. New model will be created."                                              | "testDataItem"  | "blah blah blah"       | "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1" | "testParentModelCode" | ""                                          | "testModel"         | "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1" | "String" | "mph"               | "formula one"        | " the domain of formula one"
        10     | 1    | [true]  | "the model catalogue code for the parent model is invalid, please action to import row"                                    | "testDataItem"  | "blah blah blah"       | "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1" | "testParentModelCode" | "MC_037e6162-3b6f-4ae4-asdf-das"            | "testModel"         | "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1" | "String" | "mph"               | "formula one"        | " the domain of formula one"
        11     | 1    | [true]  | "the row does not contain a data type therefore will not be associated with a value domain, is this the expected outcome?" | "testDataItem"  | "blah blah blah"       | "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1" | "testParentModelCode" | "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1" | "testModel"         | "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1" | ""       | "mph"               | "formula one"        | " the domain of formula one"
    }

    def "addRows to importer then action those rows"() {

        when:
        importer.addRow(validImportRow)
        importer.addRow(validImportRow2)
        importer.addRow(modelOnlyImportRow)

        then:
        importer.importQueue.contains(validImportRow)
        importer.importQueue.contains(validImportRow2)
        importer.pendingAction.contains(modelOnlyImportRow)

        when:
        importer.resolveImportRowPendingAction(modelOnlyImportRow, "dataElementName", ActionType.MODEL_ONLY_ROW)

        then:
        !importer.pendingAction.contains(modelOnlyImportRow)
        importer.importQueue.contains(modelOnlyImportRow)

        cleanup:
        importer.importQueue.remove(modelOnlyImportRow)
        importer.importQueue.remove(validImportRow2)
        importer.importQueue.remove(validImportRow)

    }

    def "add model only Rows to importer, action those rows and then ingest"() {

        when:
        importer.addRow(modelOnlyImportRow)
        importer.addRow(modelOnlyImportRow2)

        then:
        importer.pendingAction.contains(modelOnlyImportRow)
        importer.pendingAction.contains(modelOnlyImportRow2)

        when:
        importer.resolveImportRowPendingAction(modelOnlyImportRow, "dataElementName", ActionType.MODEL_ONLY_ROW)
        importer.resolveImportRowPendingAction(modelOnlyImportRow2, "dataElementName", ActionType.MODEL_ONLY_ROW)

        then:
        importer.importQueue.contains(modelOnlyImportRow)
        importer.importQueue.contains(modelOnlyImportRow2)

        when:
        importer.ingestImportQueue()
        Model parentModel = Model.findByModelCatalogueId("MC_037e6162-3b6f-4ae4-a171-2570b64dfq10_1")
        Model childModel = Model.findByModelCatalogueId("MC_037e6162-5b6f-4ae4-a171-2570b64daf10_1")

        then:
        parentModel
        childModel
        importer.models.contains(childModel)
        importer.models.contains(parentModel)
        importer.models.size()==2

    }

    def "test import new enumerated data type"(){
        when:
        def dataType = importer.importDataType('testEnum', "t:test|t1:testONe")
        def testDataType =  DataType.findByName("testEnum")

        then:
        dataType
        testDataType
        testDataType==dataType

        cleanup:
        dataType.delete()

    }

    def "test import existing data type"(){
        when:
        def dataType = importer.importDataType('string', 'string')

        then:
        dataType.name == "String"

    }

    def "test import existing data type with :"(){
        when:
        def dataType = importer.importDataType('asd', 'xs:string')

        then:
        dataType.name == "xs:string"

    }

    def "test import existing enumerated data type"(){
        when:
        def dataType = importer.importDataType('xxxxx', 'm:male|f:female|u:unknown|ns:not specified')

        then:
        dataType.name == "gender"
    }

    def "import invalid data type"(){
        when:
        def dataType = importer.importDataType('blah', 'blah')

        then:
        !dataType
    }


    def "test import invalid enumerated data type"(){
        when:
        def dataType = importer.importDataType('test', 'a:|asdasd|asdad:ads')
        then:
        !dataType
    }

    def "test import existing measurement unit"(){

        def params = [:]
        params.name = "Degrees Celsius"
        params.symbol = "°C"

        when:
        def mu = importer.importMeasurementUnit(params)

        then:
        mu.name == "Degrees Celsius"
        mu.symbol == "°C"
    }

    def "test import existing measurement unit just name"(){

        def params = [:]
        params.name = "Degrees Celsius"

        when:
        def mu = importer.importMeasurementUnit(params)

        then:
        mu.name == "Degrees Celsius"
        mu.symbol == "°C"
    }

    def "test import existing measurement unit just symbol"(){

        def params = [:]
        params.symbol = "°C"

        when:
        def mu = importer.importMeasurementUnit(params)

        then:
        mu.name == "Degrees Celsius"
        mu.symbol == "°C"
    }


    def "test import existing conceptualDomain"(){
        when:
        def cd = importer.importConceptualDomain("public libraries", "")

        then:
        cd.name == "public libraries"
        cd.description== "this is a container for the domain for public libraries"

    }

    def "test create conceptualDomain"(){
        when:
        def cd = importer.importConceptualDomain("test", "testConceptualDomain")

        then:
        cd
        cd.name == "test"
        cd.description== "testConceptualDomain"

        cleanup:
        cd.delete()

    }


    def "test import data element"(){

        def book = Model.findByName("book")
        def dataType = DataType.findByName("String")
        def cd = ConceptualDomain.findByName("public libraries")


        when:
        def de = importer.importDataElement([name: "testDataElement", description: "asdf asdffsda", modelCatalogueId: ""], ['1a':"as", '2a':"adsf"], book, [name: "values", description: "blabh albh",  dataType: dataType, measurementUnit: null], cd)

        then:
        de
        de.containedIn.contains(book)

    }

    def "test ingest importing two different versions"() {

        when:
        importer.addRow(validImportRow)
        importer.addRow(validImportRow2)

        then:
        importer.importQueue.contains(validImportRow)
        importer.importQueue.contains(validImportRow2)

        when:
        importer.ingestImportQueue()
        importer.actionPendingModels()
        def dataElement1 = DataElement.findByName("testDataItem")
        def valueDomain1 = dataElement1.instantiatedBy
        def dataElement2 = DataElement.findByName("testDataItem2")
        def parentModel = Model.findByModelCatalogueId("MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1")
        def archivedContainingModel = Model.findByModelCatalogueId("MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1")
        def containingModel = Model.findByModelCatalogueId("MC_037e6162-5b6f-4ae4-a171-2570b64dff10_2")
        def measureMPH = MeasurementUnit.findByNameIlike("Degrees Celsius")
        def dataType = DataType.findByNameIlike("String")
        def conceptualDomain = ConceptualDomain.findByName("formula one")

        then:
        importer.importQueue.size() == 0
        dataElement1
        parentModel
        archivedContainingModel
        containingModel
        measureMPH
        dataType
        conceptualDomain
        dataElement2
        parentModel.parentOf.contains(containingModel)
        containingModel.contains.contains(dataElement2)
        archivedContainingModel.contains.contains(dataElement1)
        containingModel.supersedes.contains(archivedContainingModel)
        dataElement2.supersedes.contains(dataElement1)
        dataElement2.instantiatedBy == valueDomain1
    }


}
