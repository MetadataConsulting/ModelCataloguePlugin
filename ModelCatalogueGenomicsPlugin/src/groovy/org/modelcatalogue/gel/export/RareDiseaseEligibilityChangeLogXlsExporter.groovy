package org.modelcatalogue.gel.export

import groovy.util.logging.Log4j
import org.modelcatalogue.builder.spreadsheet.api.Sheet
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.audit.AuditService

/**
 * Eligibility Criteria spreadsheet implementation of RD Change Log Excel Exporter
 */
@Log4j
class RareDiseaseEligibilityChangeLogXlsExporter extends RareDiseaseChangeLogXlsExporter {

    RareDiseaseEligibilityChangeLogXlsExporter(AuditService auditService, DataClassService dataClassService, Integer depth = 5, Boolean includeMetadata = false) {
        super(auditService, dataClassService, depth, includeMetadata)
    }

    @Override
    public void export(DataClass dataClass, OutputStream out) {
        String sheetName = ELIGIBILITY_SHEET
        exportXls(dataClass, out, sheetName)
    }

    @Override
    void buildSheet(Sheet sheet, List lines) {
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
                    style {wrap text}
                }
                cell {
                    value 'New version details'
                    width 30
                    style 'h3'
                    style {background('#c2efcf')}
                }
            }

            buildRows(it, lines)

        }
    }

    @Override
    List<String> generateLine(CatalogueElement model, List lines, groupDescriptions, level) {

        if (model.name.matches("(?i:.*Eligibility.*)")) {
            checkChangeLog(model, lines, groupDescriptions, level, TOP_LEVEL_RELATIONSHIP_TYPES)
            iterateChildren(model, lines, groupDescriptions, level, DETAIL_CHANGE_TYPES)
        }

        lines
    }
}









