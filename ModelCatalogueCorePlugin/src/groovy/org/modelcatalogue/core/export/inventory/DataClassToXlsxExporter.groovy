package org.modelcatalogue.core.export.inventory

import groovy.util.logging.Log4j
import org.modelcatalogue.builder.spreadsheet.api.Cell
import org.modelcatalogue.builder.spreadsheet.api.Sheet
import org.modelcatalogue.builder.spreadsheet.api.SpreadsheetBuilder
import org.modelcatalogue.builder.spreadsheet.api.Workbook
import org.modelcatalogue.builder.spreadsheet.poi.PoiSpreadsheetBuilder
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.api.ElementStatus

@Log4j
class DataClassToXlsxExporter {

    static final String EXT_MIN_OCCURS = "Min Occurs"
    static final String EXT_MAX_OCCURS = "Max Occurs"

    final DataClassService dataClassService
    final Long dataClassId
    final Integer exportDepth

    final Map<Long, DataClass> processedDataClasss = [:]

    DataClassToXlsxExporter(DataClass dataClass, DataClassService dataClassService, Integer exportDepth = 3) {
        this.dataClassId = dataClass.getId()
        this.dataClassService = dataClassService
        this.exportDepth = exportDepth
    }

    void export(OutputStream outputStream) {
        DataClass dataClass = DataClass.get(dataClassId)
        log.info "Exporting Data Class ${dataClass.name} (${dataClass.combinedVersion}) to inventory spreadsheet."

        SpreadsheetBuilder builder = new PoiSpreadsheetBuilder()
        builder.build(outputStream) { Workbook workbook ->
            apply ModelCatalogueStyles
            sheet('DataClasses') { Sheet sheet ->
                buildOutline(sheet, dataClass)
            }

            int dataClasssCount = processedDataClasss.size()
            int counter = 0

            for (DataClass dataClassForDetail in processedDataClasss.values()) {
                log.info "[${++counter}/${dataClasssCount}] Exporting detail for Data Class ${dataClassForDetail.name} (${dataClassForDetail.combinedVersion})"
                buildDataClassDetailSheet(workbook, processedDataClasss, dataClassForDetail)
            }
        }

        log.info "Exported Data Class ${dataClass.name} (${dataClass.combinedVersion}) to inventory spreadsheet."

    }

    public static void buildDataClassDetailSheet(Workbook workbook, Map<Long, DataClass> processedDataClasss, DataClass dataClass) {
        workbook.sheet("${dataClass.combinedVersion} ${dataClass.name}") {
            row {
                cell {
                    width 10
                }
                cell {
                    width 30
                }
                cell {
                    width 30
                }
                cell {
                    width 10
                }
                cell {
                    width 10
                }
                cell {
                    width 70
                }
                cell {
                    width 10
                }
                cell {
                    width 70
                }
                cell {
                    width 10
                }
                cell {
                    width 70
                }
            }
            row {
                cell {
                    value dataClass.name
                    name getReferenceName(dataClass)
                    style 'h1'
                    colspan 4
                }
            }
            row {
                if (dataClass.description) {
                    cell {
                        value dataClass.description

                        height 100

                        style 'description'
                        colspan 4
                    }
                }
            }

            row {
                cell {
                    value 'Data Model'
                    style 'property-title'
                    colspan 2
                }
                cell {
                    value dataClass.dataModel?.name
                    style 'property-value'
                    colspan 2
                }
            }
            for(DataClass parent in dataClass.childOf) {
                if (parent.status != ElementStatus.DEPRECATED) {
                    row {
                        cell {
                            value 'Parent'
                            style 'property-title'
                            colspan 2
                        }
                        cell {
                            value "${parent.name} (${parent.combinedVersion})"
                            style 'property-value'
                            if (parent.getId() in processedDataClasss.keySet()) {
                                link to name getReferenceName(parent)
                            }
                            colspan 2
                        }
                    }
                }
            }
            row {
                cell {
                    value 'ID'
                    style 'property-title'
                    colspan 2
                }
                cell {
                    value dataClass.combinedVersion
                    style 'property-value'
                    colspan 2
                }
            }
            row {
                cell {
                    value 'Status'
                    style 'property-title'
                    colspan 2
                }
                cell {
                    value dataClass.status
                    style 'property-value'
                    colspan 2
                }
            }

            row()

            row {
                cell {
                    value 'Last Updated'
                    style 'property-title'
                    colspan 2
                }
                cell {
                    value dataClass.lastUpdated
                    style 'date'
                    style 'property-value'
                    colspan 2
                }
            }

            row()


            if (dataClass.countContains()) {
                buildContainedElements(it, dataClass)
            }

            row()

            row {
                cell {
                    value '<< Back to all Data Classes'
                    link to name 'DataClasses'
                    style 'note'
                    colspan 4
                }
            }
        }
    }

    private static buildContainedElements(Sheet sheet, DataClass dataClass) {
        sheet.with {
            row {
                cell {
                    value 'All Contained Data Elements'
                    style 'h2'
                    colspan 4
                }
            }
            row {
                cell {
                    value 'DE ID'
                    style 'inner-table-header'
                }

                cell {
                    value 'Data Element Name'
                    style 'inner-table-header'
                    colspan 2
                }

                cell {
                    value 'Multiplicity'
                    style 'inner-table-header'
                }

                cell {
                    value 'DT ID'
                    style 'inner-table-header'
                }

                cell {
                    value 'Data Type Name'
                    style 'inner-table-header'
                }

                cell {
                    value 'MU ID'
                    style 'inner-table-header'
                }

                cell {
                    value 'Measurement Unit Name'
                    style 'inner-table-header'
                }

                cell {
                    value 'DC ID'
                    style 'inner-table-header'
                }

                cell {
                    value 'Referenced Data Class Name'
                    style 'inner-table-header'
                }
            }


            for (Relationship containsRelationship in dataClass.containsRelationships) {
                DataElement element = containsRelationship.destination as DataElement
                row {
                    style 'data-element-row'
                    cell {
                        value element.combinedVersion
                        style {
                            align bottom right
                        }
                    }
                    cell {
                        value element.name
                        colspan 2
                    }
                    cell {
                        value getMultiplicity(containsRelationship)
                        style {
                            align top right
                        }
                    }
                    if (element.dataType) {
                        cell {
                            value element.dataType.combinedVersion
                            style {
                                align bottom right
                            }
                        }
                        cell {
                            value element.dataType.name
                        }

                        if (element.dataType.instanceOf(PrimitiveType) && element.dataType.measurementUnit) {
                            cell('F') {
                                value element.dataType.measurementUnit.combinedVersion
                                style {
                                    align bottom right
                                }
                            }
                            cell {
                                value element.dataType.measurementUnit.name
                            }
                        } else {
                            2.times { cell() }
                        }

                        if (element.dataType.instanceOf(ReferenceType) && element.dataType.dataClass) {
                            cell('H') {
                                value element.dataType.dataClass.combinedVersion
                                style {
                                    align bottom right
                                }
                            }
                            cell {
                                value element.dataType.dataClass.name
                            }
                        } else {
                            2.times { cell() }
                        }
                    } else {
                        6.times { cell() }
                    }
                }
                row {
                    style 'data-element-description-row'


                    cell('B') {
                        value element.description
                        colspan 3
                    }

                    if (element.dataType) {
                        cell ('F') { Cell theCell ->
                            createDescriptionAndOrEnums(theCell, element.dataType)
                        }

                        if (element.dataType.instanceOf(PrimitiveType) && element.dataType.measurementUnit) {
                            cell ('H') {
                                value element.dataType.measurementUnit.description
                            }
                        }

                        if (element.dataType.instanceOf(ReferenceType) && element.dataType.dataClass) {
                            cell ('J') {
                                value element.dataType.dataClass.description
                            }
                        }

                    }

                }
                if (element.ext) {
                    for (Map.Entry<String, String> entry in element.ext) {
                        row {
                            cell('B') {
                                value entry.key
                                style 'metadata-key'
                            }
                            cell {
                                value entry.value
                                style 'metadata-value'
                                colspan 2
                            }
                        }
                    }
                }
            }
        }
    }

    private static String getMultiplicity(Relationship relationship) {
        String min = relationship.ext[EXT_MIN_OCCURS] ?: '0'
        String max = relationship.ext[EXT_MAX_OCCURS] ?: '*'

        if (max.toLowerCase() in ['unbounded', '' + Integer.MAX_VALUE]) {
            max = '*'
        }

        return "${min}..${max}"
    }

    static void createDescriptionAndOrEnums(Cell cell, DataType dataType) {
        if (dataType.description) {
            cell.text dataType.description
        }

        if (dataType.instanceOf(EnumeratedType)) {
            Map<String, String> enumerations = dataType.enumerations

            if (enumerations) {
                if (dataType.description) {
                    cell.text '\n\n'
                }

                cell.text 'Enumerations', {
                    size 12
                    bold
                }

                cell.text '\n'

                for (Map.Entry<String, String> entry in enumerations) {
                    cell.text entry.key, {
                        bold
                    }
                    cell.text ': '
                    cell.text  entry.value
                    cell.text '\n'
                }
            }
        }
    }

    private static String getReferenceName(DataClass dataClass) {
        "${dataClass.name} (${dataClass.combinedVersion})"
    }


    private void buildOutline(Sheet sheet, DataClass dataClass) {
        sheet.with {
            row(2) {
                cell {
                    value dataClass.name
                    style 'h1'
                    colspan 2
                    name 'DataClasses'
                }
            }
            row {
                if (dataClass.description) {
                    cell {
                        value dataClass.description

                        height 100

                        style 'description'
                        colspan 2

                    }
                }
            }

            row {
                cell {
                    value dataClass.dataModel?.name
                    style 'property-value'
                    style 'model-catalogue-id'
                    colspan 2
                }
            }

            row {
                cell {
                    value dataClass.combinedVersion
                    style 'model-catalogue-id'
                    colspan 2
                }
            }
            row {
                cell {
                    value dataClass.status
                    style 'status'
                    colspan 2
                }
            }

            row()

            row {
                cell {
                    value new Date()
                    style 'date'
                    colspan 2
                }
            }


            row()


            row {
                cell {
                    value 'All Inner Data Classes'
                    style 'h2'
                    colspan 2
                }
            }
            row {
                cell {
                    value 'ID'
                    width 10
                    style 'inner-table-header'
                }

                cell {
                    value 'Name'
                    width 70
                    style 'inner-table-header'
                }
            }


            buildChildOutline(it, dataClass, 1)

            row()

            row {
                cell {
                    value 'Click the data class cell to show the detail'
                    style 'note'
                    colspan 2
                }
            }

        }
    }

    private void buildChildOutline(Sheet sheet, DataClass dataClass, int level) {
        sheet.row {
            cell {
                value dataClass.combinedVersion
                style {
                    align bottom right
                }
                link to name getReferenceName(dataClass)
            }
            cell {
                value dataClass.name
                link to name getReferenceName(dataClass)
                style {
                    if (level) {
                        indent (level * 2)
                    }
                }
            }
        }

        if (level > exportDepth) {
            processedDataClasss.put(dataClass.getId(), dataClass)
            return
        }

        if (dataClass.getId() in processedDataClasss.keySet()) {
            return
        }

        processedDataClasss.put(dataClass.getId(), dataClass)

        if (dataClass.countParentOf()) {
            sheet.group {
                for (DataClass child in dataClass.parentOf) {
                    buildChildOutline(sheet, child, level + 1)
                }
            }
        }
    }
}
