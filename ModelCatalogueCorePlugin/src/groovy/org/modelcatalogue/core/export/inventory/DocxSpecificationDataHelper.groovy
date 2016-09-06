package org.modelcatalogue.core.export.inventory

import com.craigburke.document.core.builder.DocumentBuilder
import com.google.common.collect.SetMultimap
import com.google.common.collect.TreeMultimap
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

    final Set<ValidationRule> rules = new HashSet<>()

    private static final Map<String, Object> HEADER_CELL = [background: '#F2F2F2']
    private static final Map<String, Object> HEADER_CELL_TEXT = [font: [color: '#29BDCA', size: 12, bold: true, family: 'Times New Roman']]
    private static final Map<String, Object> CELL_TEXT = [font: [size: 10, family: 'Calibri']]
    private static final Map<String, Object> CELL_TEXT_FIRST = [font: [size: 10, family: 'Calibri', bold: true]]
    private static final def TITLE_COLUMN_CELL = [font: [bold: true]]

    private static final <T extends CatalogueElement> Comparator<T> compareByName(Class<T> type) {
        [compare: { T a, T b ->
            a?.name <=> b?.name
        }] as Comparator<T>
    }

    private DocumentBuilder builder
    int depth = 3

    DocxSpecificationDataHelper(DocumentBuilder builder, Integer depth) {
        this.builder = builder
        this.depth = depth
    }

    final SetMultimap<DataType, DataClass> usedDataTypes = TreeMultimap.create(compareByName(DataType), compareByName(DataClass))

    def printModel(DataClass dataClass, boolean recurse, int level, String multiplicity = "") {
        if ((recurse && level > depth) || level > 50 ) { //stop potential runaway?
            return
        }

        log.debug "Exporting data class $dataClass to Word Document level=$level"

        builder.with {

            if(level<5){
                pageBreak()
            }

            "heading${Math.min(level + 1, 6)}" dataClass.name + " [$multiplicity]", ref: dataClass.id

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
                            usedDataTypes.put dataElement.dataType, dataClass
                        }
                        row {
                            cell {
                                text CELL_TEXT_FIRST, "${dataElement.name} (${(dataElement.ext.get("http://www.modelcatalogue.org/metadata/genomics/#gel-id"))? dataElement.ext.get("http://www.modelcatalogue.org/metadata/genomics/#gel-id") + "@" + dataElement.getDataModelSemanticVersion() : dataElement.getCombinedVersion()}  )"
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
                                    link attrs, dataElement.dataType.name
                                    if (dataElement.dataType?.instanceOf(EnumeratedType)) {
                                        text '\n\n'
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

            if (recurse) {
                if (dataClass.countParentOf()) {
                    for (Relationship dataClassRelationship in dataClass.parentOfRelationships) {
                        DataClass child = dataClassRelationship.destination
                        printModel(child, true, level + 1, "${dataClassRelationship.ext.get("Min Occurs")?:"0"}..${dataClassRelationship.ext.get("Max Occurs")?:"*"}")
                    }
                }
            }

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
