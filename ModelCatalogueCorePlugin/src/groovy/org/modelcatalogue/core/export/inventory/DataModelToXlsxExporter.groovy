package org.modelcatalogue.core.export.inventory

import groovy.util.logging.Log4j
import org.modelcatalogue.builder.spreadsheet.api.Sheet
import org.modelcatalogue.builder.spreadsheet.api.Workbook
import org.modelcatalogue.builder.spreadsheet.poi.PoiSpreadsheetBuilder
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType

@Log4j
class DataModelToXlsxExporter {

    DataClassService dataClassService
    DataModel dataModel
    Integer depth = 3

    Map<Long, DataClass> processedDataClasses = [:]

    void export(OutputStream outputStream) {
        log.info "Exporting Data Model ${dataModel.name} (${dataModel.combinedVersion}) to inventory spreadsheet."

        def dataClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.includes(dataModel)).items
        def builder = new PoiSpreadsheetBuilder()
        builder.build(outputStream) { Workbook workbook ->
            apply ModelCatalogueStyles
            sheet("DataModel") { Sheet sheet ->
                buildOutline(sheet, dataModel, dataClasses)
            }

            // build separate sheets for each data class covered by outline
            processedDataClasses.eachWithIndex { item, i ->
                log.info "[${i+1}/${processedDataClasses.size()}] exporting details for data class " +
                        "${item.value.name} (${item.value.combinedVersion})"
                DataClassToXlsxExporter.buildDataClassDetailSheet(workbook, processedDataClasses, item.value)
            }
        }

        log.info "data model ${dataModel.name} (${dataModel.combinedVersion}) exported to inventory spreadsheet."
    }

    private void buildOutline(Sheet sheet, DataModel dataModel, List<DataClass> dataClasses) {
        sheet.with {
            // data model
            row(2) {
                cell {
                    value dataModel.name
                    style "h1"
                    colspan 2
                    name "DataClasses"
                }
            }
            row {
                if (dataModel.description) {
                    cell {
                        value dataModel.description
                        style "description"
                        colspan 2
                        height 100
                    }
                }
            }
            row {
                cell {
                    value dataModel.combinedVersion
                    style "model-catalogue-id"
                    colspan 2
                }
            }
            row {
                cell {
                    value dataModel.status
                    style "status"
                    colspan 2
                }
            }
            row()
            row {
                cell {
                    value new Date()
                    style "date"
                    colspan 2
                }
            }
            row()

            // data classes
            row {
                cell {
                    value "All Inner Data Classes"
                    style "h2"
                    colspan 2
                }
            }

            row {
                cell {
                    value 'ID'
                    style 'inner-table-header'
                    width 10
                }
                cell {
                    value 'Name'
                    style 'inner-table-header'
                    width 70
                }
            }
            buildDataClassesOutline(it, dataClasses)

            // footer
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

    private void buildDataClassesOutline(Sheet sheet, List<DataClass> dataClasses, int level = 1) {
        dataClasses.each { DataClass dataClass ->
            sheet.row {
                cell {
                    value dataClass.combinedVersion
                    style {
                        align bottom right
                    }
                    link to name DataClassToXlsxExporter.getReferenceName(dataClass)
                }
                cell {
                    value dataClass.name
                    link to name DataClassToXlsxExporter.getReferenceName(dataClass)
                    style {
                        if (level) {
                            indent (level * 2)
                        }
                    }
                }
            }

            if (processedDataClasses.containsKey(dataClass.id)) {
                return
            }

            if (level > depth) {
                processedDataClasses.put(dataClass.id, dataClass)
                return
            }

            processedDataClasses.put(dataClass.id, dataClass)

            if (dataClass.countParentOf() > 0) {
                sheet.group {
                    buildDataClassesOutline(sheet, dataClass.parentOf, level + 1)
                }
            }
        }
    }
}
