package org.modelcatalogue.core.export.inventory

import groovy.util.logging.Log4j
import org.modelcatalogue.builder.spreadsheet.api.Cell
import org.modelcatalogue.builder.spreadsheet.api.Sheet
import org.modelcatalogue.builder.spreadsheet.api.SpreadsheetBuilder
import org.modelcatalogue.builder.spreadsheet.api.Workbook
import org.modelcatalogue.builder.spreadsheet.poi.PoiSpreadsheetBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.enumeration.Enumeration
import org.modelcatalogue.core.enumeration.Enumerations

@Log4j
class DataClassToXlsxExporter {

    static final String EXT_MIN_OCCURS = "Min Occurs"
    static final String EXT_MAX_OCCURS = "Max Occurs"
    static final String CONTENT = 'Content'
    static final String DATA_CLASSES = 'DataClasses'

    final DataClassService dataClassService
    final Long dataClassId
    final Integer depth

    final Map<Long, DataClass> processedDataClasss = [:]

    DataClassToXlsxExporter(DataClass dataClass, DataClassService dataClassService, Integer depth = 3) {
        this.dataClassId = dataClass.getId()
        this.dataClassService = dataClassService
        this.depth = depth
    }

    protected static String getModelCatalogueIdToPrint(CatalogueElement element) {
        element.hasModelCatalogueId() && !element.modelCatalogueId.startsWith('http') ? element.modelCatalogueId : element.combinedVersion
    }

    void export(OutputStream outputStream) {
        DataClass dataClass = DataClass.get(dataClassId)
        log.info "Exporting Data Class ${dataClass.name} (${dataClass.combinedVersion}) to inventory spreadsheet."

        SpreadsheetBuilder builder = new PoiSpreadsheetBuilder()
        builder.build(outputStream) { Workbook workbook ->
            apply ModelCatalogueStyles
            sheet(CONTENT) { Sheet sheet ->
                buildOutline(sheet, dataClass)
            }

            int dataClasssCount = processedDataClasss.size()
            int counter = 0

            for (DataClass dataClassForDetail in processedDataClasss.values()) {
                dataClassForDetail.setName("${dataClassForDetail.name}".replace("'",''))
                log.info "[${++counter}/${dataClasssCount}] Exporting detail for Data Class ${dataClassForDetail.name} (${dataClassForDetail.combinedVersion})"
                buildDataClassDetailSheet(workbook, processedDataClasss, dataClassForDetail)
            }
        }

        log.info "Exported Data Class ${dataClass.name} (${dataClass.combinedVersion}) to inventory spreadsheet."

    }

    public static void buildDataClassDetailSheet(Workbook workbook, Map<Long, DataClass> processedDataClasss, DataClass dataClass) {
        workbook.sheet("${getModelCatalogueIdToPrint(dataClass)} ${dataClass.name}") {
            row {
                cell {
                    width 10
                }
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
                    width 10
                }
                cell {
                    width 70
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
                    colspan 5
                }
            }
            row {
                if (dataClass.description) {
                    cell {
                        value dataClass.description

                        height 100

                        style 'description'
                        colspan 5
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
                    colspan 3
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
                            value "${parent.name} (${getModelCatalogueIdToPrint(parent)})"
                            style 'property-value'
                            if (parent.getId() in processedDataClasss.keySet()) {
                                link to name getReferenceName(parent)
                            }
                            colspan 3
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
                    value getModelCatalogueIdToPrint(dataClass)
                    style 'property-value'
                    colspan 3
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
                    colspan 3
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
                    colspan 3
                }
            }

            row()


            if (dataClass.countContains()) {
                buildContainedElements(it, dataClass)
            }

            row()

            row {
                cell {
                    value '<< Back to Content'
                    link to name DATA_CLASSES
                    style 'note'
                    colspan 5
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
                    colspan 5
                }
            }
            row {
                cell {
                    value 'DE ID'
                    style 'inner-table-header'
                }

                cell {
                    value 'Status'
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
                    value 'Status'
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
                    value 'Status'
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
                    value 'Status'
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
                    cell {
                        value getModelCatalogueIdToPrint(element)
                        style 'data-element-bottom-right'
                    }
                    cell {
                        value element.status
                        style 'data-element-center-center'
                    }
                    cell {
                        value element.name
                        style 'data-element'
                        colspan 2
                    }
                    cell {
                        value getMultiplicity(containsRelationship)
                        style 'data-element-top-right'
                    }
                    if (element.dataType) {
                        cell {
                            value getModelCatalogueIdToPrint(element.dataType)
                            style 'data-element-bottom-right'
                        }
                        cell {
                            value element.dataType.status
                            style 'data-element-center-center'
                        }
                        cell {
                            value element.dataType.name
                            style 'data-element'
                        }

                        if (element.dataType.instanceOf(PrimitiveType) && element.dataType.measurementUnit) {
                            cell('I') {
                                value getModelCatalogueIdToPrint(element.dataType.measurementUnit)
                                style 'data-element-bottom-right'
                            }
                            cell {
                                value element.dataType.measurementUnit.status
                                style 'data-element-center-center'
                            }
                            cell {
                                value element.dataType.measurementUnit.name
                                style 'data-element'
                            }
                        } else {
                            3.times { cell() }
                        }

                        if (element.dataType.instanceOf(ReferenceType) && element.dataType.dataClass) {
                            cell('L') {
                                value getModelCatalogueIdToPrint(element.dataType.dataClass)
                                style 'data-element-bottom-right'
                            }
                            cell {
                                value element.dataType.dataClass.status
                                style 'data-element-center-center'
                            }
                            cell {
                                value element.dataType.dataClass.name
                                style 'data-element'
                            }
                        } else {
                            3.times { cell() }
                        }
                    } else {
                        9.times { cell() }
                    }
                }
                row {
                    style 'data-element-description-row'


                    cell('C') {
                        value element.description
                        colspan 3
                    }

                    if (element.dataType) {
                        cell ('H') { Cell theCell ->
                            createDescriptionAndOrEnums(theCell, element.dataType)
                        }

                        if (element.dataType.instanceOf(PrimitiveType) && element.dataType.measurementUnit) {
                            cell ('K') {
                                value element.dataType.measurementUnit.description
                            }
                        }

                        if (element.dataType.instanceOf(ReferenceType) && element.dataType.dataClass) {
                            cell ('N') {
                                value element.dataType.dataClass.description
                            }
                        }

                    }

                }
                if (element.ext) {
                    for (Map.Entry<String, String> entry in element.ext) {
                        row {
                            cell('C') {
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
            Enumerations enumerations = dataType.enumerationsObject

            if (enumerations) {
                if (dataType.description) {
                    cell.text '\n\n'
                }

                cell.text 'Enumerations', {
                    size 12
                    bold
                }

                cell.text '\n'

                for (Enumeration entry in enumerations) {
                    cell.text entry.key, {
                        bold
                        if (entry.deprecated) {
                            italic
                            color lightGray
                        }
                    }
                    cell.text ': ', {
                        bold
                        if (entry.deprecated) {
                            italic
                            color lightGray
                        }
                    }
                    cell.text  entry.value, {
                        if (entry.deprecated) {
                            italic
                            color lightGray
                        }
                    }
                    cell.text '\n'
                }
            }
        }
    }

    private static String getReferenceName(DataClass dataClass) {
        "${dataClass.name} (${getModelCatalogueIdToPrint(dataClass)})"
    }


    private void buildOutline(Sheet sheet, DataClass dataClass) {
        sheet.with {
            row(2) {
                cell {
                    value dataClass.name
                    style 'h1'
                    colspan 2
                    name DATA_CLASSES
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
                    style 'model-catalogue-id'
                    colspan 2
                }
            }

            row {
                cell {
                    value getModelCatalogueIdToPrint(dataClass)
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
                    colspan 3
                }
            }
            row {
                cell {
                    value 'ID'
                    width 10
                    style 'inner-table-header'
                }

                cell {
                    value 'Status'
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
                value getModelCatalogueIdToPrint(dataClass)
                style {
                    align bottom right
                }
                link to name getReferenceName(dataClass)
            }
            cell {
                value dataClass.status
                style {
                    align center center
                }
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

        if (level > depth) {
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
