package org.modelcatalogue.core.export.inventory

import com.craigburke.document.core.builder.DocumentBuilder
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.ValidationRule

/**
 * Prints the tables for element data specification using the DocumentBuilder library
 */
@Log4j
class DocxSpecificationDataHelper {

    final Set<Long> processedDataClasses = new HashSet<Long>()
    final Set<ValidationRule> rules = new HashSet<>()

    private static final Map<String, Object> HEADER_CELL = [background: '#F2F2F2']
    private static final Map<String, Object> HEADER_CELL_TEXT = [font: [color: '#29BDCA', size: 12, bold: true, family: 'Times New Roman']]
    private static final Map<String, Object> CELL_TEXT = [font: [size: 10, family: 'Calibri']]
    private static final Map<String, Object> CELL_TEXT_FIRST = [font: [size: 10, family: 'Calibri', bold: true]]
    private static final def TITLE_COLUMN_CELL = [font: [bold: true]]

    private DocumentBuilder builder

    DocxSpecificationDataHelper(DocumentBuilder builder) {
        this.builder = builder
    }
    final Set<DataType> usedDataTypes = new TreeSet<DataType>([compare: { DataType a, DataType b ->
        a?.name <=> b?.name
    }] as Comparator<DataType>)

    def printModel(DataClass dataClass, int level) {
        if (level > 50) {
            // only go 3 levels deep
            return
        }

        log.debug "Exporting data class $dataClass to Word Document"

        builder.with {
            if (dataClass.getId() in processedDataClasses) {
                "heading${Math.min(level + 1, 6)}" dataClass.name, ref: "${dataClass.getId()}"
            } else {
                "heading${Math.min(level + 1, 6)}" dataClass.name
            }

            paragraph {
                if (dataClass.description) {
                    text dataClass.description
                } else {
                    text " ", font: [italic: true]
                }
            }

            if (!dataClass.countContains() && !dataClass.countParentOf()) {
                paragraph {
                    text "${dataClass.name} data class does not have any inner data classes or data elements yet.", font: [italic: true]
                }
            }

            if (dataClass.countContains()) {
                table(padding: 1, border: [size: 1, color: '#D2D2D2'], columns: [2, 3, 2, 2, 3]) {
                    row {
                        cell HEADER_CELL, {
                            text HEADER_CELL_TEXT, 'Name'
                        }
                        cell HEADER_CELL, {
                            text HEADER_CELL_TEXT, 'Description'
                        }
                        cell HEADER_CELL, {
                            text HEADER_CELL_TEXT, 'Multiplicity'
                        }
                        cell HEADER_CELL, {
                            text HEADER_CELL_TEXT, 'Data Type'
                        }
                        cell HEADER_CELL, {
                            text HEADER_CELL_TEXT, 'Related To'
                        }
                    }
                    for (Relationship dataElementRelationship in dataClass.containsRelationships) {
                        DataElement dataElement = dataElementRelationship.destination
                        if (dataElement.dataType) {
                            usedDataTypes << dataElement.dataType
                        }
                        row {
                            cell {
                                text CELL_TEXT_FIRST, "${dataElement.name} (${dataElement.getCombinedVersion()})"
                            }
                            cell {
                                text CELL_TEXT, dataElement.description ?: ''
                            }
                            cell {
                                text CELL_TEXT, getMultiplicity(dataElementRelationship)
                            }
                            cell {
                                if (dataElement.dataType) {
                                    Map<String, Object> attrs = [url: "#${dataElement.dataType.id}", font: [bold: true]]
                                    attrs.putAll(CELL_TEXT)
                                    text attrs, dataElement.dataType.name
                                    if (dataElement.dataType?.instanceOf(EnumeratedType)) {
                                        text '\n\n'
                                        text 'Enumerations', font: [italic: true]
                                        text '\n'
                                        if (dataElement.dataType.enumerations.size() <= 10) {
                                            for (entry in dataElement.dataType.enumerations) {
                                                text "${entry.key ?: ''}", font: [bold: true]
                                                text ":"
                                                text "${entry.value ?: ''}"
                                                text "\n"
                                            }
                                        }

                                    }
                                }
                            }
                            cell {
                                for (CatalogueElement relatedTo in dataElement.relatedTo) {
                                    text CELL_TEXT, getSameAs(relatedTo)
                                    lineBreak()
                                }

                            }
                        }
                    }
                }
            }

            if (dataClass.contextFor) {
                table(border: [size: 0.px]) {
                    dataClass.contextFor.each {
                        ValidationRule vr ->
                            row {
                                cell "Rule"
                                cell vr.rule
                            }
                            if (!(vr in rules)) rules << vr
                    }
                }
            }

            if (!(dataClass.getId() in processedDataClasses)) {
                if (dataClass.countParentOf()) {
                    for (DataClass child in dataClass.parentOf) {
                        printModel(child, level + 1)
                    }
                }
            }

            processedDataClasses << dataClass.getId()
        }
    }

    private static String getSameAs(CatalogueElement element) {
        if (!element.dataModel) {
            return "${element.name}"
        }
        "${element.name} (${element.ext['Data Item No'] ? "${element.ext['Data Item No']} from " : ''}${element.dataModel.name})"
    }

    private static String getMultiplicity(Relationship relationship) {
        "${relationship.ext['Min Occurs'] ?: 0}..${relationship.ext['Max Occurs'] ?: 'unbounded'}"
    }

    def printRules() {
        builder.with {
            rules.each { vr ->
                println "Printing rule: ${vr.name}"
                heading3 vr.name
                table(border: [size: 1, color: '#D2D2D2']) {
                    row {
                        cell "Component"
                        cell TITLE_COLUMN_CELL, vr.component
                    }
                    row {
                        cell "Rule Focus"
                        cell TITLE_COLUMN_CELL, vr.ruleFocus
                    }
                    row {
                        cell "Trigger"
                        cell TITLE_COLUMN_CELL, vr.trigger
                    }
                    row {
                        cell "Rule"
                        cell TITLE_COLUMN_CELL, vr.rule
                    }
                    row {
                        cell "Error Condition"
                        cell TITLE_COLUMN_CELL, vr.errorCondition
                    }
                    row {
                        cell "Issue Record"
                        cell TITLE_COLUMN_CELL, vr.issueRecord
                    }
                    row {
                        cell "Notification"
                        cell TITLE_COLUMN_CELL, vr.notification
                    }
                    row {
                        cell "Notification Target"
                        cell TITLE_COLUMN_CELL, vr.notificationTarget
                    }
                    row {
                        cell "Last Updated"
                        cell TITLE_COLUMN_CELL, vr.lastUpdated.format("yyyy-MM-dd")
                    }
                    row {
                        cell "Version Created"
                        cell TITLE_COLUMN_CELL, vr.versionCreated.format("yyyy-MM-dd")
                    }
                    row {
                        cell "Status"
                        cell TITLE_COLUMN_CELL, vr.status.toString()
                    }
                }
            }
        }
    }

}
