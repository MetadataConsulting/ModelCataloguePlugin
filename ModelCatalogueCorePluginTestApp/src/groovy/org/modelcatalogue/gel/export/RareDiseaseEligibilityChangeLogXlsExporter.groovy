package org.modelcatalogue.gel.export

import builders.dsl.spreadsheet.builder.api.SheetDefinition
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.PerformanceUtilService
import org.modelcatalogue.core.audit.AuditService

/**
 * Eligibility Criteria spreadsheet implementation of RD Change Log Excel Exporter
 */
@Log4j
class RareDiseaseEligibilityChangeLogXlsExporter extends RareDiseaseChangeLogXlsExporter {

    private static final String ELIGIBILITY_SHEET = 'Eligibility Criteria change log'
    private static final int CURRENT_DETAILS = 6
    private static final int NEW_DETAILS = 7

    RareDiseaseEligibilityChangeLogXlsExporter(AuditService auditService, DataClassService dataClassService, PerformanceUtilService performanceUtilService, Integer depth = 5, Boolean includeMetadata = false) {
        super(auditService, dataClassService, performanceUtilService, depth, includeMetadata)
    }

    @Override
    public void export(CatalogueElement dataClass, OutputStream out) {
        exportXls(dataClass, out, ELIGIBILITY_SHEET)
    }

    @Override
    void buildSheet(SheetDefinition sheet, List lines) {
        sheet.with {
            row(1) {
                cell {
                    value 'Change reference'
                    style 'h3'
                    height 75
                    width 20
                }
                cell {
                    value 'Level 2 Disease Group (ID)'
                    style 'h3'
                    width 50
                }
                cell {
                    value 'Level 3 Disease Subtype (ID)'
                    style 'h3'
                    width 60
                }
                cell {
                    value 'Level 4 Specific Disorder (ID)'
                    width 60
                    style 'h3'
                }
                cell {
                    value 'Affected Data Item'
                    width 35
                    style 'h3'
                }
                cell {
                    value 'Change Type'
                    width 25
                    style 'h3'
                }
                cell {
                    value 'Current version details'
                    width 30
                    style 'h3'
                }
                cell {
                    value 'New version details'
                    width 30
                    style 'h3-green'
                }
            }

            buildRows(it, lines)

        }
    }

    @Override
    List<String> searchExportSpecificTypes(CatalogueElement model, List lines, groupDescriptions, level) {
        levelIdStack.put(level,model.id)
        if (model.name.matches("(?i:.*Eligibility.*)")) {
            checkChangeLog(model, lines, groupDescriptions, level, DETAIL_CHANGE_TYPES)
            iterateChildren(model, lines, groupDescriptions, level, DETAIL_CHANGE_TYPES)
        }

        lines
    }


     void buildRow(SheetDefinition sheet, List<String> line) {
        sheet.row {
            line.eachWithIndex{ String cellValue, int i ->
                cell {
                    value cellValue
                    if (i!= CURRENT_DETAILS && i!=NEW_DETAILS) style 'property-value'
                    if (i == CURRENT_DETAILS) style 'property-value-wrap'
                    if (i == NEW_DETAILS) style 'property-value-green'
                }
            }
        }
    }
}









