package org.modelcatalogue.gel.export

import groovy.util.logging.Log4j
import org.modelcatalogue.builder.spreadsheet.api.AutoKeyword
import org.modelcatalogue.builder.spreadsheet.api.Sheet
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.util.DataModelFilter

/**
 * General purpose Change Log Excel Exporter
 * Runs from the DataModel level and traverses the DataClasses
 */
@Log4j
class DataModelChangeLogXlsExporter extends RareDiseaseChangeLogXlsExporter {

    private static final String DATA_SPEC_SHEET = 'Data Specification Change Log'

    private static final int DATA_CATEGORY = 2
    private static final int SECTION = 3
    private static final int DATA_ITEM_NAME = 4
    private static final int CURRENT_DETAILS = 5
    private static final int NEW_DETAILS = 6

    def headers = [
        1: 'Change reference',
        2: 'Data Category',
        3: 'Section (model cat ref)',
        4: 'Data Item Name',
        5: 'Change Type',
        6: 'Current version details',
        7: 'New version details'
    ]

    DataModelChangeLogXlsExporter(AuditService auditService, DataClassService dataClassService, PerformanceUtilService performanceUtilService, Integer depth = 5, Boolean includeMetadata = false) {
        super(auditService, dataClassService, performanceUtilService, depth, includeMetadata)
    }

    @Override
    public void export(CatalogueElement dataModel, OutputStream out) {

        List<DataClass> dataClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.includes((DataModel) dataModel)).items

        if (dataClasses.size() > 0) {
            exportXls(dataClasses?.get(0), out, DATA_SPEC_SHEET) // this report is dataClass centric
        } else {
            exportLinesAsXls DATA_SPEC_SHEET, [], out
        }
    }

    @Override
    void buildSheet(Sheet sheet, List lines) {
        sheet.with {
            row(1) {
                cell {
                    value headers.get(1)
                    style 'h3' //                    height 75
                    width AutoKeyword.AUTO
                }
                cell {
                    value headers.get(2)
                    style 'h3'
                    width AutoKeyword.AUTO
                }
                cell {
                    value headers.get(3)
                    width AutoKeyword.AUTO
                    style 'h3'
                }
                cell {
                    value headers.get(4)
                    width AutoKeyword.AUTO
                    style 'h3'
                }
                cell {
                    value headers.get(5)
                    width AutoKeyword.AUTO
                    style 'h3'
                }
                cell {
                    value headers.get(6)
                    width 30
                    style 'h3-wrap-thick'
                }
                cell {
                    value headers.get(7)
                    width 30
                    style 'h3-green'
                }
            }

            buildRows(it, lines)

        }
    }

    @Override
    void buildRows(Sheet sheet, List<List<String>> lines) {
        lines.eachWithIndex { line, int i ->
            log.debug("row $i=" + line)
            buildRow(sheet, line)
        }
    }

    private buildRow(Sheet sheet, List<String> line) {
        sheet.row {
            line.eachWithIndex { String cellValue, int i ->
                cell {
                    value cellValue
                    if (i!= CURRENT_DETAILS && i!=NEW_DETAILS) style 'property-value'
                    if (i == CURRENT_DETAILS) style 'property-value-wrap'
                    if (i == NEW_DETAILS) style 'property-value-green'
                }
            }
        }
    }

    @Override
    List<String> searchExportSpecificTypes(CatalogueElement model, List lines, groupDescriptions, level) {
        levelIdStack.put(level,model.id)
        checkChangeLog(model, lines, groupDescriptions, level, DETAIL_CHANGE_TYPES)
        iterateChildren(model, lines, groupDescriptions, level, DETAIL_CHANGE_TYPES)

        lines
    }

    @Override
    def descendModels(CatalogueElement model, lines, Object level, Map groupDescriptions, Object exclusions) {
        switch (level) {
            case 1:
                String groupDescription = "$model.name (${model.combinedVersion})"
                log.debug("level$level $groupDescription")
                groupDescriptions.put(DATA_CATEGORY, EMPTY)     //pad for when no lower levels present
                groupDescriptions.put(SECTION, EMPTY)
                checkChangeLog(model, lines, groupDescriptions, level, DETAIL_CHANGE_TYPES)
                break

            case DATA_CATEGORY:
                String groupDescription = "$model.name (${model.combinedVersion})"
                log.debug("level$level $groupDescription")
                groupDescriptions.put(level, groupDescription)
                groupDescriptions.put(SECTION, EMPTY)           //pad for when no lower levels present
                checkChangeLog(model, lines, groupDescriptions, level, DETAIL_CHANGE_TYPES)
                break

            case SECTION:
                String groupDescription = "$model.name (${model.combinedVersion})"
                log.debug("level$level $groupDescription")
                groupDescriptions.put(level, groupDescription)
                checkChangeLog(model, lines, groupDescriptions, level, DETAIL_CHANGE_TYPES)
                break

            case DATA_ITEM_NAME:
                log.debug "level 4 searching... $model.name"
                if (model instanceof DataClass) {
                    lines = searchExportSpecificTypes(model, lines, groupDescriptions, level)
                } else {
                    checkChangeLog(model, lines, groupDescriptions, level, DETAIL_CHANGE_TYPES)
                }
                return  //don't go deeper

            default:    //don't go deeper
                return
        }

        if (!(model.instanceOf(DataClass))) {
            return
        }

        model.contains.each { CatalogueElement child ->
            descendModels(child, lines, level + 1, groupDescriptions, exclusions)
        }
        model.parentOf?.each { CatalogueElement child ->
            descendModels(child, lines, level + 1, groupDescriptions, exclusions)
        }
    }

}









