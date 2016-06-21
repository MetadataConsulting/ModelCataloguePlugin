package org.modelcatalogue.core.export.inventory

import groovy.util.logging.Log4j
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.docx.ModelCatalogueWordDocumentBuilder

import java.text.SimpleDateFormat

@Log4j
class DataModelToDocxExporter {

    DataClassService dataClassService
    DataModel rootModel

    private static final Map<String, Object> ENUM_HEADER_CELL_TEXT = [font: [size: 12, bold: true]]
    private static final Map<String, Object> DOMAIN_NAME = [font: [color: '#29BDCA', size: 14, bold: true]]
    private static final Map<String, Object> DOMAIN_CLASSIFICATION_NAME = [font: [color: '#999999', size: 12, bold: true]]

    Closure customTemplate

    //If there is an image to display on the front page
    def imagePath

    void initVars(rootModel, dataClassService) {
        this.dataClassService = dataClassService
        this.rootModel = rootModel
    }

    DataModelToDocxExporter(DataModel rootModel, DataClassService dataClassService) {
        initVars(rootModel, dataClassService)
        customTemplate = {
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

    }

    DataModelToDocxExporter(DataModel rootModel, DataClassService dataClassService, Closure customTemplate, imagePath) {
        initVars(rootModel, dataClassService)
        this.customTemplate = customTemplate
        if(imagePath) this.imagePath = imagePath
    }

    ArrayList getAllVersionsOfModel(DataModel model, List<DataModel> models) {
        def oldModel = model.supersedes
        if (oldModel && !models.contains(oldModel.first())) {
            models.add(0, oldModel.first())
            getAllVersionsOfModel(oldModel.first(), models)
        } else {
            return models
        }
    }

    void export(OutputStream outputStream) {
        log.info "Exporting Data Model ${rootModel.name} (${rootModel.combinedVersion}) to inventory document."

        def dataClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.includes(rootModel)).items
        def builder = new ModelCatalogueWordDocumentBuilder(outputStream)
        def docHelper = new DocxSpecificationDataHelper(builder)

        List<DataModel> allVersionsOfModel = getAllVersionsOfModel(rootModel, [rootModel])



        builder.create {
            document(template: customTemplate) {

                def thisOrganisation = rootModel.ext.get(Metadata.ORGANISATION)
                def thisOwner = rootModel.ext.get(Metadata.OWNER)
                def reviewers = rootModel.ext.get(Metadata.REVIEWERS)
                def authors = rootModel.ext.get(Metadata.AUTHORS)

                byte[] imageData
                if(imagePath) imageData = new URL(imagePath).bytes

                if (imagePath){
                    paragraph(align: 'right'){
                        image(data: imageData, height: 1.366.inches, width: 2.646.inches)
                    }
                }

                if (thisOrganisation) {
                    paragraph(style: 'title', align: 'center') {
                        text thisOrganisation
                    }
                }

                paragraph (style: 'subtitle', align: 'center'){
                    text "${rootModel.name}"
                }

                paragraph(style: 'document', margin: [top: 120]) {
                    text "Version ${rootModel.semanticVersion} ${rootModel.status}"
                    lineBreak()
                    text SimpleDateFormat.dateInstance.format(new Date())
                }

                pageBreak()

                heading1 'Document Management'
                paragraph(style: 'document', margin: [top: 20]) {
                    text 'Document Owner: ', font:[bold:true]
                    text thisOwner
                    lineBreak()
                    text 'Authors: ', font:[bold:true]
                    if(authors) text authors
                }

                heading2 'Version Control'
                table {
                    row {
                        cell 'Version', style: 'cell.headerCell'
                        cell 'Date', style: 'cell.headerCell'
                        cell 'Summary of Changes', style: 'cell.headerCell'
                    }
                    allVersionsOfModel.each { DataModel model ->
                        row {
                            cell "${model.semanticVersion}", style: 'cell'
                            cell model.lastUpdated, style: 'cell'
                            cell "${model.revisionNotes}", style:'cell'
                        }
                    }
                }

                heading2 'Reviewers'
                table {
                    row {
                        cell 'Name', style: 'cell.headerCell'
                        cell 'Responsibility', style: 'cell.headerCell'
                        cell 'Date', style: 'cell.headerCell'
                    }
                    if(reviewers) {
                        reviewers.split(',').each { String reviewer ->
                            row {
                                cell "${reviewer}", style: 'cell'
                                cell '', style: 'cell'
                                cell '', style: 'cell'
                            }
                        }
                    }
                }

                heading2 'Approved by'
                table {
                    row {
                        cell 'Name', style: 'cell.headerCell'
                        cell 'Responsibility', style: 'cell.headerCell'
                        cell 'Date', style: 'cell.headerCell'
                    }
                    row {
                        cell thisOwner, style: 'cell'
                        cell '', style: 'cell'
                        cell '', style: 'cell'
                    }
                }

                if (rootModel.description) {
                    paragraph(style: 'classification.description', margin: [left: 50, right: 50]) {
                        text rootModel.description
                    }
                }

                for (DataClass dClass in dataClasses) {
                    docHelper.printModel(dClass, 1)
                }

                if (docHelper.usedDataTypes) {
                    pageBreak()
                    heading1 'Data Types'

                    for (DataType dataType in docHelper.usedDataTypes) {

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
                            table(columns: [1, 4], border: [size: 0], font: [color: '#5C5C5C']) {
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
                                        cell dataType.name + ' (' + dataType.latestVersionId + ')'
                                    }
                                }

                            }

                            if (dataType?.instanceOf(EnumeratedType)) {
                                paragraph(font: [color: '#999999', bold: true]) {
                                    text 'Enumerations'
                                }

                                table(border: [size: 1, color: '#D2D2D2']) {
                                    row(background: '#F2F2F2') {
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

                if(docHelper.rules) {
                    pageBreak()
                    heading2 'Validation Rules'
                    docHelper.printRules()
                }

            }
        }

        log.info "data dataModel ${rootModel.name} (${rootModel.combinedVersion}) exported to inventory document."
    }

    private boolean hasExtraInformation(DataType dataType) {
        (dataType.instanceOf(PrimitiveType) && dataType.measurementUnit) || dataType.instanceOf(EnumeratedType) || (dataType.instanceOf(ReferenceType) && dataType.dataClass) || dataType.rule
    }

}
