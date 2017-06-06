package org.modelcatalogue.core.export.inventory

import com.craigburke.document.core.builder.DocumentBuilder
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.enumeration.Enumeration
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.util.docx.ModelCatalogueWordDocumentBuilder
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller

import java.text.SimpleDateFormat

@Log4j
class DataClassToDocxExporter {

    private static final Map<String, Object> HEADER_CELL =  [background: '#F2F2F2']
    private static final Map<String, Object> HEADER_CELL_TEXT =  [font: [color: '#29BDCA', size: 12, bold: true, family: 'Times New Roman']]
    private static final Map<String, Object> ENUM_HEADER_CELL_TEXT =  [font: [size: 12, bold: true]]
    private static final Map<String, Object> CELL_TEXT =  [font: [size: 10, family: 'Calibri']]
    private static final Map<String, Object> CELL_TEXT_FIRST = [font: [size: 10, family: 'Calibri', bold: true]]
    private static final Map<String, Object> DOMAIN_NAME =  [font: [color: '#29BDCA', size: 14, bold: true]]
    private static final Map<String, Object> DOMAIN_CLASSIFICATION_NAME =  [font: [color: '#999999', size: 12, bold: true]]


    final DataClassService dataClassService
    final ElementService elementService
    final Long dataClassId
    final Integer depth
    final Set<Long> processedDataClasses = new HashSet<Long>()


    DataClassToDocxExporter(DataClass dataClass, DataClassService dataClassService, Integer depth = 3, ElementService elementService) {
        this.dataClassId = dataClass.getId()
        this.dataClassService = dataClassService
        this.elementService = elementService
        this.depth = depth
    }

    void export(OutputStream outputStream) {
        processedDataClasses.clear()

        DataClass rootDataClass = DataClass.get(dataClassId)

        log.info "Exporting data class $rootDataClass to Word Document"

        DocumentBuilder builder = new ModelCatalogueWordDocumentBuilder(outputStream)

        DocxSpecificationDataHelper helper = new DocxSpecificationDataHelper(builder, depth)

        def customTemplate = {
            'document' font: [family: 'Calibri'], margin: [left: 20, right: 10]
            'paragraph.title' font: [color: '#13D4CA', size: 26.pt], margin: [top: 200.pt]
            'paragraph.subtitle' font: [color: '#13D4CA', size: 18.pt]
            'paragraph.description' font: [color: '#13D4CA', size: 16.pt, italic: true], margin: [left: 30, right: 30]
            'heading1' font: [size: 20, bold: true]
            'heading2' font: [size: 18, bold: true]
            'heading3' font: [size: 16, bold: true]
            'heading4' font: [size: 16]
            'heading5' font: [size: 15]
            'heading6' font: [size: 14]
            'paragraph.heading1' font: [size: 20, bold: true]
            'paragraph.heading2' font: [size: 18, bold: true]
            'paragraph.heading3' font: [size: 16, bold: true]
            'paragraph.heading4' font: [size: 16]
            'paragraph.heading5' font: [size: 15]
            'paragraph.heading6' font: [size: 14]
            'cell.headerCell' font: [color: '#29BDCA', size: 12.pt, bold: true], background: '#F2F2F2'
            'cell' font: [size: 10.pt]

        }


        builder.create {
            document(template: customTemplate) {
                paragraph rootDataClass.name, style: 'title',  align: 'center'
                paragraph(style: 'subtitle', align: 'center') {
                    text "${rootDataClass.status}"
                    lineBreak()
                    text SimpleDateFormat.dateInstance.format(new Date())
                }
                if (rootDataClass.description) {
                    paragraph(style: 'classification.description', margin: [left: 50, right: 50]) {
                        text rootDataClass.description
                    }
                }
                pageBreak()

                for (DataClass dataClass in rootDataClass.parentOf) {
                    printDataClass(builder, helper, dataClass, 1)
                }

                if (helper.usedDataTypes) {
                    pageBreak()
                    heading1 'Data Types'

                    for (DataType dataType in helper.usedDataTypes.keySet()) {

                        log.debug "Exporting data type $dataType to Word Document"

                        Map<String, Object> attrs = [ref: "${dataType.id}", style: 'heading2']
                        attrs.putAll(DOMAIN_NAME)

                        paragraph attrs, dataType.name

                        if (dataType.dataModel) {
                            paragraph {
                                text DOMAIN_CLASSIFICATION_NAME, "(${dataType.dataModel.name})"
                            }
                        }

                        if (dataType.description) {
                            paragraph {
                                text dataType.description
                            }
                        }
                        if (hasExtraInformation(dataType)) {
                            table(columns: [1,4], border: [size: 0], font: [color: '#5C5C5C']) {
                                if (dataType.instanceOf(PrimitiveType) && dataType.measurementUnit) {
                                    row {
                                        cell 'Unit of Measure'
                                        cell {
                                            text dataType.measurementUnit.name
                                            if (dataType.measurementUnit.description) {
                                                text ' ('
                                                text dataType.measurementUnit.description
                                                ')'
                                            }
                                        }
                                    }
                                }

                                if (dataType.instanceOf(ReferenceType) && dataType.dataClass) {
                                    row {
                                        cell 'Data Class'
                                        cell {
                                            text dataType.dataClass.name
                                            if (dataType.dataClass.description) {
                                                text ' ('
                                                text dataType.dataClass.description
                                                ')'
                                            }
                                        }
                                    }
                                }

                                if (dataType.regexDef) {
                                    row {
                                        cell 'Regular Expression'
                                        cell dataType.regexDef
                                    }
                                } else if (dataType.rule) {
                                    row {
                                        cell 'Rule'
                                        cell dataType.rule
                                    }
                                }

                                for (DataType parent in elementService.getTypeHierarchy([:], dataType).items) {
                                    if (parent.regexDef) {
                                        row {
                                            cell "Regular Expression\n(${CatalogueElementMarshaller.getClassifiedName(parent)})"
                                            cell parent.regexDef
                                        }
                                    } else if (parent.rule) {
                                        row {
                                            cell "Rule\n(${CatalogueElementMarshaller.getClassifiedName(parent)})"
                                            cell parent.rule
                                        }
                                    } else {
                                        row {
                                            cell "Based On"
                                            cell CatalogueElementMarshaller.getClassifiedName(parent)
                                        }
                                    }
                                }

                            }

                            if (dataType?.instanceOf(EnumeratedType)) {
                                paragraph(font:  [color: '#999999', bold: true]){
                                    text 'Enumerations'
                                }

                                table (border: [size: 1, color: '#D2D2D2']) {
                                    row (background: '#F2F2F2'){
                                        cell ENUM_HEADER_CELL_TEXT, 'Code'
                                        cell ENUM_HEADER_CELL_TEXT, 'Description'
                                    }
                                    Enumerations enumerations = dataType.enumerationsObject
                                    for (Enumeration entry in enumerations) {
                                        if (entry.deprecated) {
                                            row(DataModelToDocxExporter.DEPRECATED_ENUM_CELL_TEXT) {
                                                cell entry.key
                                                cell entry.value
                                            }
                                        } else {
                                            row {
                                                cell entry.key
                                                cell entry.value
                                            }
                                        }

                                    }
                                }
                            }
                        }

                        paragraph style: 'heading4', margin: [bottom: 0], font: [size: 11, bold: true, color: '#999999'], "Usages"
                        for (DataClass backref in helper.usedDataTypes.get(dataType)) {
                            paragraph(margin: [top: 0, bottom: 0]) {
                                link url: "#${backref.id}", style: 'heading4', font: [size: 9, color: '#29BDCA'], backref.name
                            }

                        }

                    }
                }

            }
        }

        log.debug "Data Model $rootDataClass exported to Word Document"
    }

    private boolean hasExtraInformation(DataType dataType) {
        (dataType.instanceOf(PrimitiveType) && dataType.measurementUnit) || (dataType.instanceOf(ReferenceType) && dataType.dataClass) || dataType.rule || dataType.countIsBasedOn() > 0
    }


    private void printDataClass(DocumentBuilder builder, DocxSpecificationDataHelper helper, DataClass dataClass, int level) {
        if (level > depth) {
            return
        }

        log.debug "Exporting data class $dataClass to Word Document"

        builder.with {
            if (dataClass.getId() in processedDataClasses) {
                "heading${Math.min(level + 1, 6)}" dataClass.name
            } else {
                "heading${Math.min(level + 1, 6)}" dataClass.name, ref: "${dataClass.getId()}"
            }

            paragraph {
                if (dataClass.description) {
                    text dataClass.description
                } else {
                    text "${dataClass.name} data class does not have any description yet.", font: [italic: true]
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
                            text HEADER_CELL_TEXT, 'Same As'
                        }
                    }
                    for (Relationship dataElementRelationship in dataClass.containsRelationships) {
                        DataElement dataElement = dataElementRelationship.destination
                        if (dataElement.dataType) {
                            helper.usedDataTypes.put(dataElement.dataType, dataClass)
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
                                    link attrs, dataElement.dataType.name
                                    if (dataElement.dataType?.instanceOf(EnumeratedType)) {
                                        if (dataElement.dataType.enumerations.size() <= 10) {
                                            text '\n\n'
                                            text 'Enumerations', font: [italic: true]
                                            text '\n'
                                            Enumerations enumerations = dataElement.dataType.enumerationsObject
                                            for (Enumeration entry in enumerations) {
                                                if (entry.deprecated) {
                                                    text "${entry.key ?: ''}", font: [italic: true, bold: true, color: '#999999']
                                                    text ":", font: [italic: true, bold: true, color: '#999999']
                                                    text "${entry.value ?: ''}", font: [italic: true, color: '#999999']
                                                    text "\n"
                                                } else {
                                                    text "${entry.key ?: ''}", font: [bold: true]
                                                    text ":"
                                                    text "${entry.value ?: ''}"
                                                    text "\n"
                                                }

                                            }
                                        }

                                    }
                                }
                            }
                            cell {
                                for (CatalogueElement synonym in dataElement.isSynonymFor) {
                                    text CELL_TEXT, getSameAs(synonym)
                                    lineBreak()
                                }

                            }
                        }
                    }
                }
            }

            if (!(dataClass.getId() in processedDataClasses)) {
                if (dataClass.countParentOf()) {
                    for (DataClass child in dataClass.parentOf) {
                        printDataClass(builder, helper, child, level + 1)
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

}
