package org.modelcatalogue.core.dataarchitect

import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Importer)
class ImporterSpec extends Specification {

    @Shared
    ImportRow validImportRow, invalidImportRow
    Importer importer
    Collection<ImportRow> rows

    def setup() {

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

    }




}
