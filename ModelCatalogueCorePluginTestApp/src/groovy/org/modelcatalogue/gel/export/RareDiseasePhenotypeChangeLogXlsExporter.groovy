package org.modelcatalogue.gel.export

import builders.dsl.spreadsheet.api.Configurer
import builders.dsl.spreadsheet.api.Keywords
import builders.dsl.spreadsheet.builder.api.CellDefinition
import builders.dsl.spreadsheet.builder.api.RowDefinition
import builders.dsl.spreadsheet.builder.api.SheetDefinition
import groovy.util.logging.Log4j
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService

/**
 * Created by rickrees on 18/04/2016.
 * HPO and Clinical Tests spreadsheet implementation of RD Change Log Excel Exporter
 */
@Log4j
class RareDiseasePhenotypeChangeLogXlsExporter extends RareDiseaseChangeLogXlsExporter {

    private static final String PHENOTYPES_SHEET = 'HPO & Clinical tests change log'
    private static final int CURRENT_DETAILS = 8
    private static final int NEW_DETAILS = 9

    RareDiseasePhenotypeChangeLogXlsExporter(AuditService auditService, DataClassService dataClassService, PerformanceUtilService performanceUtilService, Integer depth = 5, Boolean includeMetadata = false) {
        super(auditService, dataClassService, performanceUtilService, depth, includeMetadata)
    }

    @Override
    public void export(CatalogueElement dataClass, OutputStream out) {
        exportXls(dataClass, out, PHENOTYPES_SHEET)
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
                        cellDefinition.value 'Element hierarchy'
                        cellDefinition.width 20
                        cellDefinition.style 'h3'
                    }
                })
                rowDefinition.cell(new Configurer<CellDefinition>() {
                    @Override
                    void configure(CellDefinition cellDefinition) {
                        cellDefinition.value 'Phenotype /Clinical Tests/Guidance'
                        cellDefinition.width 35
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
        String subtype
        levelIdStack.put(level,model.id)

        if (model.name.matches("(?i:.*Phenotype.*)")) {

            log.debug " --- $level $model $model.dataModel"

            subtype = PHENOTYPE
            checkChangeLog(model, lines, subtype, groupDescriptions, level, DETAIL_CHANGE_TYPES)
            iterateChildren(model, lines, subtype, groupDescriptions, level, DETAIL_CHANGE_TYPES)

        } else if (model.name.matches("(?i:.*Clinical Test.*)")) {

            subtype = CLINICAL_TESTS
            checkChangeLog(model, lines, subtype, groupDescriptions, level, DETAIL_CHANGE_TYPES)
            iterateChildren(model, lines, subtype, groupDescriptions, level, DETAIL_CHANGE_TYPES)

        } else if (model.name.matches("(?i:.*Guidance.*)")) {
            subtype = GUIDANCE
            checkChangeLog(model, lines, subtype, groupDescriptions, level, DETAIL_CHANGE_TYPES)
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









