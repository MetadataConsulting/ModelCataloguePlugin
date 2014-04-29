package org.modelcatalogue.core.dataarchitect

import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import spock.lang.Shared
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */

class ImporterSpec extends AbstractIntegrationSpec {

    @Shared
    ImportRow validImportRow, invalidImportRow, validImportRow2
    Importer importer


    def setup() {
        loadFixtures()
        validImportRow = new ImportRow()
        validImportRow2 = new ImportRow()
        invalidImportRow = new ImportRow()
        importer = new Importer()


        //row 1
        validImportRow.dataElementName = "testDataItem"
        validImportRow.dataElementCode = "MC_1987234625347_1"
        validImportRow.parentModelName = "testParentModelCode"
        validImportRow.parentModelCode = "MC_1423_1"
        validImportRow.containingModelName = "testModel"
        validImportRow.containingModelCode = "MC_123_1"
        validImportRow.dataType =   "String"
        validImportRow.dataElementDescription =  "test description"
        validImportRow.measurementUnitName =   "mph"
        validImportRow.conceptualDomainName = "formula one"
        validImportRow.conceptualDomainDescription = " the domain of formula one"


        //row 2 -same model as row 1 but different data element
        validImportRow2.dataElementName = "testDataItem2"
        validImportRow2.dataElementCode = "MC_1987234625347_1"
        validImportRow2.parentModelName = "testParentModelCode"
        validImportRow2.parentModelCode = "MC_1423_1"
        validImportRow2.containingModelName = "testModel"
        validImportRow2.containingModelCode = "MC_123_1"
        validImportRow2.dataType =   "String"
        validImportRow2.dataElementDescription =  "test description 2"
        validImportRow2.measurementUnitName =   "cm3"
        validImportRow2.conceptualDomainName = "formula one"
        validImportRow2.conceptualDomainDescription = " the domain of formula one"

        //row 3 -same as row 1 but with updates

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

    def cleanup() {
    }

    void "test ingest importing two different versions "() {

        Collection<ImportRow> rows = []
        rows.add(validImportRow)
        rows.add(validImportRow2)


        when:
        importer.importAll(rows)

        then:
        importer.importQueue.contains(validImportRow)

        when:
        importer.ingestImportQueue()
        def dataElement1 = DataElement.findByName("testDataItem")
        def valueDomain1 = dataElement1.instantiatedBy
        def dataElement2 = DataElement.findByName("testDataItem2")
        def parentModel = Model.findByModelCatalogueId("MC_1423_1")
        def archivedContainingModel = Model.findByModelCatalogueId("MC_123_1")
        def containingModel = Model.findByModelCatalogueId("MC_123_2")
        def measureMPH = MeasurementUnit.findByNameIlike("mph")
        def dataType = DataType.findByNameIlike("String")
        def conceptualDomain = ConceptualDomain.findByName("formula one")


        then:
        importer.importQueue.size() == 0
        dataElement1
        parentModel
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

    //void "test ingest importing two of the same versions "() {}


//    void "test ingest invalid row "() {
//
//        Collection<ImportRow> rows = []
//        rows.add(invalidImportRow)
//
//        when:
//        importer.importAll(rows)
//
//        then:
//        importer.pendingAction.contains(invalidImportRow)
//
//    }



}
