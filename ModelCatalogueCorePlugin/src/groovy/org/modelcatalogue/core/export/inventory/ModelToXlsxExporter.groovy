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
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ModelService

@Log4j
class ModelToXlsxExporter {

    final ModelService modelService
    final Long modelId
    final Map<Long, Model> processedModels = [:]


    ModelToXlsxExporter(Model model, ModelService modelService) {
        this.modelId = model.getId()
        this.modelService = modelService
    }

    void export(OutputStream outputStream) {
        Model model = Model.get(modelId)
        log.info "Exporting Model ${model.name} (${model.combinedVersion}) to inventory spreadsheet."

        SpreadsheetBuilder builder = new PoiSpreadsheetBuilder()
        builder.build(outputStream) { Workbook workbook ->
            apply ModelCatalogueStyles
            sheet('Models') { Sheet sheet ->
                buildOutline(sheet, model)
            }

            int modelsCount = processedModels.size()
            int counter = 0

            for (Model modelForDetail in processedModels.values()) {
                log.info "[${++counter}/${modelsCount}] Exporting detail for Model ${modelForDetail.name} (${modelForDetail.combinedVersion})"
                buildModelDetailSheet(workbook, processedModels, modelForDetail)
            }
        }

        log.info "Exported Model ${model.name} (${model.combinedVersion}) to inventory spreadsheet."
        
    }

    void buildModelDetailSheet(Workbook workbook, Map<Long, Model> processedModels, Model model) {
        workbook.sheet("${model.combinedVersion} ${model.name}") {
            row {
                cell {
                    width 10
                }
                cell {
                    width 30
                }
                cell {
                    width 40
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
                    value model.name
                    name getReferenceName(model)
                    style 'h1'
                    colspan 3
                }
            }
            row {
                if (model.description) {
                    cell {
                        value model.description

                        height 100

                        style 'description'
                        colspan 3
                    }
                }
            }

            row {
                cell {
                    value 'Classification'
                    style 'property-title'
                    colspan 2
                }
                cell {
                    value model.classifications.collect { it.name }.unique().sort().join(', ')
                    style 'property-value'
                }
            }
            for(Model parent in model.childOf) {
                row {
                    cell {
                        value 'Parent'
                        style 'property-title'
                        colspan 2
                    }
                    cell {
                        value "${parent.name} (${parent.combinedVersion})"
                        style 'property-value'
                        if (parent.getId() in processedModels.keySet()) {
                            link to name getReferenceName(parent)
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
                    value model.combinedVersion
                    style 'property-value'
                }
            }
            row {
                cell {
                    value 'Status'
                    style 'property-title'
                    colspan 2
                }
                cell {
                    value model.status
                    style 'property-value'
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
                    value model.lastUpdated
                    style 'date'
                    style 'property-value'
                }
            }

            row()


            if (model.countContains()) {
                buildContainedElements(it, model)
            }

            row()

            row {
                cell {
                    value '<< Back to all models'
                    link to name 'Models'
                    style 'note'
                    colspan 3
                }
            }
        }
    }

    private static buildContainedElements(Sheet sheet, Model model) {
        sheet.with {
            row {
                cell {
                    value 'All Contained Data Elements'
                    style 'h2'
                    colspan 3
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
                    value 'VD ID'
                    style 'inner-table-header'
                }

                cell {
                    value 'Value Domain Name'
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
            }


            for (DataElement element in model.contains) {
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
                    if (element.valueDomain) {
                        cell {
                            value element.valueDomain.combinedVersion
                            style {
                                align bottom right
                            }
                        }
                        cell {
                            value element.valueDomain.name
                        }
                        if (element.valueDomain.dataType) {
                            cell {
                                value element.valueDomain.dataType.combinedVersion
                                style {
                                    align bottom right
                                }
                            }
                            cell {
                                value element.valueDomain.dataType.name
                            }
                        } else {
                            2.times { cell() }
                        }
                        if (element.valueDomain.unitOfMeasure) {
                            cell('H') {
                                value element.valueDomain.unitOfMeasure.combinedVersion
                                style {
                                    align bottom right
                                }
                            }
                            cell {
                                value element.valueDomain.unitOfMeasure.name
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
                        colspan 2
                    }

                    if (element.valueDomain) {
                        cell ('E') {
                            value element.valueDomain.description
                        }

                        if (element.valueDomain.dataType) {
                            cell ('G') { Cell cell ->
                                createDescriptionAndOrEnums(cell, element.valueDomain.dataType)
                            }
                        }
                        if (element.valueDomain.unitOfMeasure) {
                            cell ('I') {
                                value element.valueDomain.unitOfMeasure.description
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
                            }
                        }
                    }
                }
            }
        }
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

    private static String getReferenceName(Model model) {
        "${model.name} (${model.combinedVersion})"
    }


    private void buildOutline(Sheet sheet, Model model) {
        sheet.with {
            row(2) {
                cell {
                    value model.name
                    style 'h1'
                    colspan 2
                    name 'Models'
                }
            }
            row {
                if (model.description) {
                    cell {
                        value model.description

                        height 100

                        style 'description'
                        colspan 2

                    }
                }
            }

            row {
                cell {
                    value model.classifications.collect { it.name }.unique().sort().join(', ')
                    style 'property-value'
                    style 'model-catalogue-id'
                    colspan 2
                }
            }

            row {
                cell {
                    value model.combinedVersion
                    style 'model-catalogue-id'
                    colspan 2
                }
            }
            row {
                cell {
                    value model.status
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
                    value 'All Contained Models'
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


            buildChildOutline(it, model, 1)

            row()

            row {
                cell {
                    value 'Click the model cell to show the detail'
                    style 'note'
                    colspan 2
                }
            }

        }
    }

    private void buildChildOutline(Sheet sheet, Model model, int level) {
        sheet.row {
            cell {
                value model.combinedVersion
                style {
                    align bottom right
                }
                link to name getReferenceName(model)
            }
            cell {
                value model.name
                link to name getReferenceName(model)
                style {
                    if (level) {
                        indent (level * 2)
                    }
                }
            }
        }

        if (level > 3) {
            processedModels.put(model.getId(), model)
            return
        }

        if (model.getId() in processedModels.keySet()) {
            return
        }

        processedModels.put(model.getId(), model)

        if (model.countParentOf()) {
            sheet.group {
                for (Model child in model.parentOf) {
                    buildChildOutline(sheet, child, level + 1)
                }
            }
        }
    }
}
