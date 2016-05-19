package org.modelcatalogue.gel.export

import groovy.util.logging.Log4j
import org.hibernate.SessionFactory
import org.modelcatalogue.builder.spreadsheet.api.Sheet
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService

import static org.modelcatalogue.core.audit.ChangeType.PROPERTY_CHANGED

/**
 * Created by rickrees on 18/04/2016.
 * HPO and Clinical Tests spreadsheet implementation of RD Change Log Excel Exporter
 */
@Log4j
class RareDiseasePhenotypeChangeLogXlsExporter extends RareDiseaseChangeLogXlsExporter {

    private static final String PHENOTYPES_SHEET = 'HPO & Clinical tests change log'
    private static final int CURRENT_DETAILS = 7
    private static final int NEW_DETAILS = 8

    RareDiseasePhenotypeChangeLogXlsExporter(AuditService auditService, DataClassService dataClassService, SessionFactory sessionFactory, Integer depth = 5, Boolean includeMetadata = false) {
        super(auditService, dataClassService, sessionFactory, depth, includeMetadata)
    }

    @Override
    public void export(CatalogueElement dataClass, OutputStream out) {
        exportXls(dataClass, out, PHENOTYPES_SHEET)
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
                    value 'Phenotype /Clinical Tests/Guidance'
                    width 35
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
        String subtype
        levelIdStack.put(level,model.id)

        if (model.name.matches("(?i:.*Phenotype.*)")) {

            log.debug " --- $level $model $model.dataModel"

            subtype = PHENOTYPE
            checkChangeLog(model, lines, subtype, groupDescriptions, level, TOP_LEVEL_RELATIONSHIP_TYPES)
            iterateChildren(model, lines, subtype, groupDescriptions, level, DETAIL_CHANGE_TYPES)

        } else if (model.name.matches("(?i:.*Clinical Test.*)")) {

            subtype = CLINICAL_TESTS
            checkChangeLog(model, lines, subtype, groupDescriptions, level, TOP_LEVEL_RELATIONSHIP_TYPES)
            iterateChildren(model, lines, subtype, groupDescriptions, level, DETAIL_CHANGE_TYPES)

        } else if (model.name.matches("(?i:.*Guidance.*)")) {
            subtype = GUIDANCE
            checkChangeLog(model, lines, subtype, groupDescriptions, level, [PROPERTY_CHANGED])
        }

        lines
    }

    void buildRow(Sheet sheet, List<String> line) {
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









