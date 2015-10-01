package org.modelcatalogue.core.gel

import com.craigburke.document.core.builder.DocumentBuilder
import groovy.util.logging.Log4j
import org.hibernate.FetchMode
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.util.docx.ModelCatalogueWordDocumentBuilder

import java.text.SimpleDateFormat

@Log4j
class ClassificationToDocxExporter {

    private static final Map<String, Object> HEADER_CELL =  [background: '#F2F2F2']
    private static final Map<String, Object> HEADER_CELL_TEXT =  [font: [color: '#29BDCA', size: 12, bold: true, family: 'Times New Roman']]
    private static final Map<String, Object> ENUM_HEADER_CELL_TEXT =  [font: [size: 12, bold: true]]
    private static final Map<String, Object> CELL_TEXT =  [font: [size: 10, family: 'Calibri']]
    private static final Map<String, Object> CELL_TEXT_FIRST = [font: [size: 10, family: 'Calibri', bold: true]]
    private static final Map<String, Object> DOMAIN_NAME =  [font: [color: '#29BDCA', size: 14, bold: true]]
    private static final Map<String, Object> DOMAIN_CLASSIFICATION_NAME =  [font: [color: '#999999', size: 12, bold: true]]


    final Long classificationId
    final Set<ValueDomain> usedValueDomains = new TreeSet<ValueDomain>([compare: { ValueDomain a, ValueDomain b ->
        a?.name <=> b?.name
    }] as Comparator<ValueDomain>)
    final Set<Long> processedModels = new HashSet<Long>()


    ClassificationToDocxExporter(Classification classification) {
        classificationId = classification.getId()
    }

    void export(OutputStream outputStream) {

        usedValueDomains.clear()
        processedModels.clear()

        Classification classification = Classification.get(classificationId)

        log.info "Exporting classification $classification to Word Document"

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
                paragraph classification.name, style: 'title',  align: 'center'
                paragraph(style: 'subtitle', align: 'center') {
                    text "${classification.status}"
                    lineBreak()
                    text SimpleDateFormat.dateInstance.format(new Date())
                }
                if (classification.description) {
                    paragraph(style: 'classification.description', margin: [left: 50, right: 50]) {
                        text classification.description
                    }
                }
                pageBreak()

                for (Model model in getModelsForClassification(classification.id)) {
                    printModel(builder, model, 1)
                }

                if (usedValueDomains) {
                    pageBreak()
                    heading1 'Value Domains'

                    for (ValueDomain domain in usedValueDomains) {

                        log.debug "Exporting value domain $domain to Word Document"

                        Map<String, Object> attrs = [ref: "${domain.id}"]
                        attrs.putAll(DOMAIN_NAME)

                        heading2 attrs, domain.name

                        if (domain.classifications) {
                            paragraph {
                                text DOMAIN_CLASSIFICATION_NAME, "(${domain.classifications.first().name})"
                            }
                        }

                        if (domain.description) {
                            paragraph {
                                text domain.description
                            }
                        }
                        if (domain.unitOfMeasure || domain.dataType || domain.rule) {
                            table(columns: [1,4], border: [size: 0], font: [color: '#5C5C5C']) {
                                if (domain.dataType) {
                                    row {
                                        cell 'Data Type'
                                        cell {
                                            text domain.dataType.name
                                            if (domain.dataType.description) {
                                                text ' ('
                                                text domain.dataType.description
                                                ')'
                                            }
                                        }
                                    }
                                }

                                if (domain.unitOfMeasure) {
                                    row {
                                        cell 'Unit of Measure'
                                        cell {
                                            text domain.unitOfMeasure.name
                                            if (domain.unitOfMeasure.description) {
                                                text ' ('
                                                text domain.unitOfMeasure.description
                                                ')'
                                            }
                                        }
                                    }
                                }

                                if (domain.regexDef) {
                                    row {
                                        cell 'Regular Expression'
                                        cell domain.regexDef
                                    }
                                } else if (domain.rule) {
                                    row {
                                        cell 'Rule'
                                        cell domain.rule
                                    }
                                }

                            }

                            if (domain.dataType?.instanceOf(EnumeratedType)) {
                                paragraph(font:  [color: '#999999', bold: true]){
                                    text 'Enumerations'
                                }

                                table (border: [size: 1, color: '#D2D2D2']) {
                                    row (background: '#F2F2F2'){
                                        cell ENUM_HEADER_CELL_TEXT, 'Code'
                                        cell ENUM_HEADER_CELL_TEXT, 'Description'
                                    }
                                    for (Map.Entry<String, String> entry in domain.dataType.enumerations) {
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

        log.debug "Classification $classification exported to Word Document"
    }


    private void printModel(DocumentBuilder builder, Model model, int level) {
        if (level == 1 && model.childOf.any { CatalogueElement it -> it.classifications.any { it.id == classificationId } }) {
            return
        }

        log.debug "Exporting model $model to Word Document"

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
                    text "${model.name} model does not have any description yet.", font: [italic: true]
                }
            }

            if (!model.countContains() && !model.countParentOf()) {
                paragraph {
                    text "${model.name} model does not have any child models or data elements yet.", font: [italic: true]
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
                        if (dataElement.valueDomain) {
                            usedValueDomains << dataElement.valueDomain
                        }
                        row {
                            cell {
                                text CELL_TEXT_FIRST, "${dataElement.name} (${dataElement.getLatestVersionId() ?: dataElement.getId()}.${dataElement.getVersionNumber() ?: 1})"
                            }
                            cell {
                                text CELL_TEXT, dataElement.description ?: ''
                            }
                            cell {
                                text CELL_TEXT, getMultiplicity(dataElementRelationship)
                            }
                            cell {
                                if (dataElement.valueDomain) {
                                    Map<String, Object> attrs = [url: "#${dataElement.valueDomain.id}", font: [bold: true]]
                                    attrs.putAll(CELL_TEXT)
                                    text attrs, dataElement.valueDomain.name
                                    if (dataElement.valueDomain.dataType?.instanceOf(EnumeratedType)) {
                                        text '\n\n'
                                        text 'Enumerations', font: [italic: true]
                                        text '\n'
                                        if (dataElement.valueDomain.dataType.enumerations.size() <= 10) {
                                            for (entry in dataElement.valueDomain.dataType.enumerations) {
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

            if (!(classificationId in model.classifications*.getId())) {
                // do not continue down the tree if the model does not belong to the classification
                processedModels << model.getId()
                return
            }



            if (!(model.getId() in processedModels)) {
                if (model.countParentOf()) {
                    for (Model child in model.parentOf) {
                        printModel(builder, child, level + 1)
                    }
                }
            }

            processedModels << model.getId()
        }
    }

    private static String getSameAs(CatalogueElement element) {
        if (!element.classifications) {
            return "${element.name}"
        }
        "${element.name} (${element.ext['Data Item No'] ? "${element.ext['Data Item No']} from " : ''}${element.classifications.first().name})"
    }

    private static String getMultiplicity(Relationship relationship) {
        "${relationship.ext['Min Occurs'] ?: 0}..${relationship.ext['Max Occurs'] ?: 'unbounded'}"
    }

    private static Collection<Model>  getModelsForClassification(Long classificationId) {
        def results = Model.createCriteria().list {
            fetchMode "extensions", FetchMode.JOIN
            fetchMode "outgoingRelationships.extensions", FetchMode.JOIN
            fetchMode "outgoingRelationships.destination.classifications", FetchMode.JOIN
            incomingRelationships {
                and {
                    eq("relationshipType", RelationshipType.classificationType)
                    source { eq('id', classificationId) }
                }
            }
        }
        return results
    }

}
