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
    ImportRow validImportRow, invalidImportRow
    Importer importer
    Collection<ImportRow> rows

    def setup() {
        loadFixtures()
        validImportRow = new ImportRow()
        invalidImportRow = new ImportRow()
        importer = new Importer()
        rows = []

        validImportRow.dataElementName = "testDataItem"
        validImportRow.parentModelName = "testParentModelCode"
        validImportRow.parentModelCode = "MC_1423_1"
        validImportRow.containingModelName = "testModel"
        validImportRow.containingModelCode = "MC_123_1"
        validImportRow.dataType =   "text"
        validImportRow.dataElementDescription =  "test description"
        validImportRow.measurementUnitName =   "mph"
        validImportRow.conceptualDomainName = "formula one"
        validImportRow.conceptualDomainDescription = " the domain of formula one"

        invalidImportRow.dataElementName = "testDataItem"
        invalidImportRow.parentModelName = "testParentModelCode"
        invalidImportRow.parentModelCode = "asd"
        invalidImportRow.containingModelName = "testModel"
        invalidImportRow.containingModelCode = "asd"
        invalidImportRow.dataType =   "text"
        invalidImportRow.dataElementDescription =  "test description"
        invalidImportRow.measurementUnitName =   "mph"
        invalidImportRow.conceptualDomainName = "formula one"
        invalidImportRow.conceptualDomainDescription = " the domain of formula one"

        rows.add(validImportRow)
        rows.add(invalidImportRow)

    }

    def cleanup() {
    }

    void "test ingest valid and invalid rows row"() {

        when:
        importer.importAll(rows)

        then:
        importer.importQueue.contains(validImportRow)
        importer.pendingAction.contains(invalidImportRow)

        when:
        importer.ingestImportQueue()
        def dataElement = DataElement.findByName("testDataItem")
        def parentModel = Model.findByModelCatalogueId("MC_1423_1")
        def containingModel = Model.findByModelCatalogueId("MC_123_1")
        def measure = MeasurementUnit.findByNameIlike("mph")
        def dataType = DataType.findByNameIlike("text")
        def conceptualDomain = ConceptualDomain.findByName("formula one")


        then:

        dataElement
        parentModel
        containingModel
        measure
        dataType
        conceptualDomain
    }




}
