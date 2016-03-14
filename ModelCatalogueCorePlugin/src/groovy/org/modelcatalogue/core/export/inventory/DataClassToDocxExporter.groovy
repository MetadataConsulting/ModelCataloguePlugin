package org.modelcatalogue.core.export.inventory

import com.craigburke.document.core.builder.DocumentBuilder
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.docx.ModelCatalogueWordDocumentBuilder

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
    final Long dataClassId
    final Integer exportDepth
    final Set<DataType> usedDataTypes = new TreeSet<DataType>([compare: { DataType a, DataType b ->
        a?.name <=> b?.name
    }] as Comparator<DataType>)
    final Set<Long> processedModels = new HashSet<Long>()


    DataClassToDocxExporter(DataClass model, DataClassService dataClassService, Integer exportDepth = 3) {
        this.dataClassId = model.getId()
        this.dataClassService = dataClassService
        this.exportDepth = exportDepth
    }

    void export(OutputStream outputStream) {

        usedDataTypes.clear()
        processedModels.clear()

        DataClass rootModel = DataClass.get(dataClassId)

        log.info "Exporting data class $rootModel to Word Document"

        DocumentBuilder builder = new ModelCatalogueWordDocumentBuilder(outputStream)

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
                paragraph rootModel.name, style: 'title',  align: 'center'
                paragraph(style: 'subtitle', align: 'center') {
                    text "${rootModel.status}"
                    lineBreak()
                    text SimpleDateFormat.dateInstance.format(new Date())
                }
                if (rootModel.description) {
                    paragraph(style: 'classification.description', margin: [left: 50, right: 50]) {
                        text rootModel.description
                    }
                }
                pageBreak()

                for (DataClass model in rootModel.parentOf) {
                    printModel(builder, model, 1)
                }

                if (usedDataTypes) {
                    pageBreak()
                    heading1 'Data Types'

                    for (DataType dataType in usedDataTypes) {

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
                                    for (Map.Entry<String, String> entry in dataType.enumerations) {
                                        row {
                                            cell entry.key
                                            cell entry.value
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        log.debug "Data Model $rootModel exported to Word Document"
    }

    private boolean hasExtraInformation(DataType dataType) {
        (dataType.instanceOf(PrimitiveType) && dataType.measurementUnit) || (dataType.instanceOf(ReferenceType) && dataType.dataClass) || dataType.rule
    }


    private void printModel(DocumentBuilder builder, DataClass model, int level) {
        if (level > exportDepth) {
            return
        }

        log.debug "Exporting data class $model to Word Document"

        builder.with {
            if (model.getId() in processedModels) {
                "heading${Math.min(level + 1, 6)}" model.name, ref: "${model.getId()}"
            } else {
                "heading${Math.min(level + 1, 6)}" model.name
            }

            paragraph {
                if (model.description) {
                    text model.description
                } else {
                    text "${model.name} data class does not have any description yet.", font: [italic: true]
                }
            }

            if (!model.countContains() && !model.countParentOf()) {
                paragraph {
                    text "${model.name} data class does not have any inner data classes or data elements yet.", font: [italic: true]
                }
            }

            if (model.countContains()) {
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
                    for (Relationship dataElementRelationship in model.containsRelationships) {
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
                                for (CatalogueElement synonym in dataElement.isSynonymFor) {
                                    text CELL_TEXT, getSameAs(synonym)
                                    lineBreak()
                                }

                            }
                        }
                    }
                }
            }

            if (!(model.getId() in processedModels)) {
                if (model.countParentOf()) {
                    for (DataClass child in model.parentOf) {
                        printModel(builder, child, level + 1)
                    }
                }
            }

            processedModels << model.getId()
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
