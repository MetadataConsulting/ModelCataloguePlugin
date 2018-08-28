package org.modelcatalogue.gel.export

import builders.dsl.spreadsheet.api.Configurer
import builders.dsl.spreadsheet.builder.api.CellDefinition
import builders.dsl.spreadsheet.builder.api.RowDefinition
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
        sheet.row(1, new Configurer<RowDefinition>() {
            @Override
            void configure(RowDefinition rowDefinition) {
                rowDefinition.cell(new Configurer<CellDefinition>() {
                    @Override
                    void configure(CellDefinition cellDefinition) {
                        cellDefinition.value 'Change reference'
                        cellDefinition.style 'h3'
                        cellDefinition.height 75
                        cellDefinition.width 20
                    }
                })
                rowDefinition.cell(new Configurer<CellDefinition>() {
                    @Override
                    void configure(CellDefinition cellDefinition) {
                        cellDefinition.value 'Level 2 Disease Group (ID)'
                        cellDefinition.style 'h3'
                        cellDefinition.width 50
                    }
                })
                rowDefinition.cell(new Configurer<CellDefinition>() {
                    @Override
                    void configure(CellDefinition cellDefinition) {
                        cellDefinition.value 'Level 3 Disease Subtype (ID)'
                        cellDefinition.style 'h3'
                        cellDefinition.width 60
                    }
                })
                rowDefinition.cell(new Configurer<CellDefinition>() {
                    @Override
                    void configure(CellDefinition cellDefinition) {
                        cellDefinition.value 'Level 4 Specific Disorder (ID)'
                        cellDefinition.width 60
                        cellDefinition.style 'h3'
                    }
                })
                rowDefinition.cell(new Configurer<CellDefinition>() {
                    @Override
                    void configure(CellDefinition cellDefinition) {
                        cellDefinition.value 'Affected Data Item'
                        cellDefinition.width 35
                        cellDefinition.style 'h3'
                    }
                })
                rowDefinition.cell(new Configurer<CellDefinition>() {
                    @Override
                    void configure(CellDefinition cellDefinition) {
                        cellDefinition.value 'Change Type'
                        cellDefinition.width 25
                        cellDefinition.style 'h3'
                    }
                })
                rowDefinition.cell(new Configurer<CellDefinition>() {
                    @Override
                    void configure(CellDefinition cellDefinition) {
                        cellDefinition.value 'Current version details'
                        cellDefinition.width 30
                        cellDefinition.style 'h3'
                    }
                })
                rowDefinition.cell(new Configurer<CellDefinition>() {
                    @Override
                    void configure(CellDefinition cellDefinition) {
                        cellDefinition.value 'New version details'
                        cellDefinition.width 30
                        cellDefinition.style 'h3-green'
                    }
                })
            }
        })
        buildRows(sheet, lines)
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









