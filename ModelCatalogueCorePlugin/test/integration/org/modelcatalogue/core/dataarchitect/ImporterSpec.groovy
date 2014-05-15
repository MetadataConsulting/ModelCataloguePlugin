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

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */

class ImporterSpec extends AbstractIntegrationSpec {

    @Shared
    ImportRow validImportRow, invalidImportRow, validImportRow2, modelOnlyImportRow
    @Shared
    Importer importer

//    def void "placeholder test"(){}

    def setupSpec(){
        importer = new Importer()
        loadFixtures()
        validImportRow = new ImportRow()
        validImportRow2 = new ImportRow()
        modelOnlyImportRow = new ImportRow()
        invalidImportRow = new ImportRow()


        modelOnlyImportRow.dataElementName = ""
        modelOnlyImportRow.dataElementCode = ""
        modelOnlyImportRow.parentModelName = "testParentModelCode"
        modelOnlyImportRow.parentModelCode = "MC_037e6162-3b6f-4ae4-a171-2570b64dfq10_1"
        modelOnlyImportRow.containingModelName = "testJustModel"
        modelOnlyImportRow.containingModelCode = "MC_037e6162-5b6f-4ae4-a171-2570b64daf10_1"
        modelOnlyImportRow.dataType =   ""
        modelOnlyImportRow.dataElementDescription =  ""
        modelOnlyImportRow.measurementUnitName =   ""
        modelOnlyImportRow.conceptualDomainName = "formula one"
        modelOnlyImportRow.conceptualDomainDescription = " the domain of formula one"

//        //row 1
        validImportRow.dataElementName = "testDataItem"
        validImportRow.dataElementCode = "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1"
        validImportRow.parentModelName = "testParentModelCode"
        validImportRow.parentModelCode = "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1"
        validImportRow.containingModelName = "testModel"
        validImportRow.containingModelCode = "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1"
        validImportRow.dataType =   "String"
        validImportRow.dataElementDescription =  "test description"
        validImportRow.measurementUnitName =   "mph"
        validImportRow.conceptualDomainName = "formula one"
        validImportRow.conceptualDomainDescription = " the domain of formula one"
//
//
//        //row 2 -same model as row 1 but different data element
        validImportRow2.dataElementName = "testDataItem2"
        validImportRow2.dataElementCode = "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1"
        validImportRow2.parentModelName = "testParentModelCode"
        validImportRow2.parentModelCode = "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1"
        validImportRow2.containingModelName = "testModel"
        validImportRow2.containingModelCode = "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1"
        validImportRow2.dataType =   "String"
        validImportRow2.dataElementDescription =  "test description 2"
        validImportRow2.measurementUnitName =   "cm3"
        validImportRow2.conceptualDomainName = "formula one"
        validImportRow2.conceptualDomainDescription = " the domain of formula one"
//
//        //row 3 -same as row 1 but with updates
//
        invalidImportRow.dataElementName = "testDataItem"
        invalidImportRow.parentModelName = "testParentModelCode"
        invalidImportRow.parentModelCode = "asd"
        invalidImportRow.containingModelName = "testModel"
        invalidImportRow.containingModelCode = "asd"
        invalidImportRow.dataType =   "String"
        invalidImportRow.dataElementDescription =  "test description"
        invalidImportRow.measurementUnitName =   "mph"
        invalidImportRow.conceptualDomainName = "formula one"
        invalidImportRow.conceptualDomainDescription = " the domain of formula one"
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


//
    def "test import existing enumerated data type"(){
        when:
        def dataType = importer.importDataType('xxxxx', 'm:male|f:female|u:unknown|ns:not specified')

        then:
        dataType.name == "gender"
    }
//
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


    //name: "public libraries"


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

    def "test importModels"(){

        def book = Model.findByName("book")
        def chapter1 = Model.findByName("chapter1")
        def chapter2 = Model.findByName("chapter2")
        def cd = ConceptualDomain.findByName("public libraries")

        setup:
        book.addToParentOf(chapter1)
        chapter1.addToParentOf(chapter2)

        expect:
        chapter1.parentOf.contains(chapter2)
        book.parentOf.contains(chapter1)

        when:
        def model = importer.importModels(chapter2.modelCatalogueId, chapter2.name, "", "testModel", cd)

        then:
        model
        model.childOf.contains(chapter2)

        cleanup:
        book.removeFromParentOf(chapter1)
        chapter1.removeFromParentOf(chapter2)
        chapter2.removeFromParentOf(model)
        model.delete()

    }

    def "test importModels 2"(){

        def book = Model.findByName("book")
        def chapter1 = Model.findByName("chapter1")
        def chapter2 = Model.findByName("chapter2")
        def cd = ConceptualDomain.findByName("public libraries")

        setup:
        book.addToParentOf(chapter1)
        chapter1.addToParentOf(chapter2)

        expect:
        chapter1.parentOf.contains(chapter2)
        book.parentOf.contains(chapter1)

        when:
        def model = importer.importModels("", "", "", "testModels", cd)

        then:
        model
        !model.childOf.contains(chapter2)

        cleanup:
        book.removeFromParentOf(chapter1)
        chapter1.removeFromParentOf(chapter2)
        model.delete()

    }

    //importDataElement([name: row.dataElementName, description: row.dataElementDescription, modelCatalogueId: row.dataElementCode], row.metadata, model, [name: row.dataElementName.replaceAll("\\s", "_"), description: row.dataType.toString().take(2000), dataType: dataType, measurementUnit: measurementUnit], conceptualDomain)

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


//
//    def "test import data element"(){
//
//        setup:
//        DataElement de = DataElement.findByName("DE_author")
//        Model book = Model.findByName("book")
//        Model chapter1 = Model.findByName("chapter1")
//        book.addToParentOf(chapter1)
//        chapter1.addToContains(de)
//
//        expect:
//        chapter1.contains.contain(de)
//        book.parentOf.contains(chapter1)
//
//        when:
//
//
//
//        then:
//
//        cleanup:
//
//    }


    def "add parent models"(){

        when:
        importer.addParentModels(["test", "testchild1", "testchild2"], ConceptualDomain.findByName("formula one"))

        then:
        importer.parentModels.size()>0

    }


//modelOnlyImportRow

    def "test row with model only"(){

        when:
        importer.ingestRow(modelOnlyImportRow)
        def parentModel = Model.findByModelCatalogueId("MC_037e6162-3b6f-4ae4-a171-2570b64dfq10_1")
        def containingModel = Model.findByModelCatalogueId("MC_037e6162-5b6f-4ae4-a171-2570b64daf10_1")
        def conceptualDomain = ConceptualDomain.findByName("formula one")

        then:
        parentModel
        containingModel
        conceptualDomain

    }


//    def cleanup() {
//    }
//
//    void "test ingest importing two different versions "() {
//
//        Collection<ImportRow> rows = []
//        rows.add(validImportRow)
//        rows.add(validImportRow2)
//
//
//        when:
//        importer.addAll(rows)
//
//        then:
//        importer.importQueue.contains(validImportRow)
//
//        when:
//        importer.ingestImportQueue()
//        def dataElement1 = DataElement.findByName("testDataItem")
//        def valueDomain1 = dataElement1.instantiatedBy
//        def dataElement2 = DataElement.findByName("testDataItem2")
//        def parentModel = Model.findByModelCatalogueId("MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1")
//        def archivedContainingModel = Model.findByModelCatalogueId("MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1")
//        def containingModel = Model.findByModelCatalogueId("MC_037e6162-5b6f-4ae4-a171-2570b64dff10_2")
//        def measureMPH = MeasurementUnit.findByNameIlike("mph")
//        def dataType = DataType.findByNameIlike("String")
//        def conceptualDomain = ConceptualDomain.findByName("formula one")
//
//
//        then:
//        importer.importQueue.size() == 0
//        dataElement1
//        parentModel
//        containingModel
//        measureMPH
//        dataType
//        conceptualDomain
//        dataElement2
//
//        parentModel.parentOf.contains(containingModel)
//        containingModel.contains.contains(dataElement2)
//        archivedContainingModel.contains.contains(dataElement1)
//        containingModel.supersedes.contains(archivedContainingModel)
//        dataElement2.supersedes.contains(dataElement1)
//        dataElement2.instantiatedBy == valueDomain1
//
//    }

    //void "test ingest importing two of the same versions "() {}


//    void "test ingest invalid row "() {
//
//        Collection<ImportRow> rows = []
//        rows.add(invalidImportRow)
//
//        when:
//        importer.addAll(rows)
//
//        then:
//        importer.pendingAction.contains(invalidImportRow)
//
//    }



}
