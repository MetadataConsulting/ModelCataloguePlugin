package org.modelcatalogue.core.export.inventory

import groovy.util.logging.Log4j
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.docx.ModelCatalogueWordDocumentBuilder
import java.text.SimpleDateFormat

@Log4j
class DataModelToDocxExporter {

    DataClassService dataClassService
    ElementService elementService
    DataModel rootModel

    static final Map<String, Object> DEPRECATED_ENUM_CELL_TEXT = [font: [color: '#999999', italic: true]]
    private static final Map<String, Object> ENUM_HEADER_CELL_TEXT = [font: [size: 12, bold: true]]
    private static final Map<String, Object> DOMAIN_NAME = [font: [color: '#29BDCA', size: 14, bold: true]]
    private static final Map<String, Object> DOMAIN_CLASSIFICATION_NAME = [font: [color: '#999999', size: 12, bold: true]]

    Closure customTemplate

    //If there is an image to display on the front page
    def imagePath
    int depth = 3

    private void initVars(rootModel, dataClassService, ElementService elementService) {
        this.dataClassService = dataClassService
        this.elementService = elementService
        this.rootModel = rootModel
    }

    DataModelToDocxExporter(DataModel rootModel, DataClassService dataClassService, ElementService elementService) {
        initVars(rootModel, dataClassService, elementService)
        customTemplate = {
            'document' font: [family: 'Calibri'], margin: [left: 20, right: 10]
            'paragraph.title' font: [color: '#13D4CA', size: 26.pt], margin: [top: 200.pt]
            'paragraph.subtitle' font: [color: '#13D4CA', size: 18.pt]
            'paragraph.description' font: [color: '#13D4CA', size: 16.pt, italic: true], margin: [left: 30, right: 30]
            'heading1' font: [size: 18, bold: true]
            'heading2' font: [size: 17, family: 'Calibri Light', bold: true, color: '#3275B3']
            'heading3' font: [size: 16, family: 'Calibri Light', bold: true, color: '#3275B3']
            'heading4' font: [size: 14, family: 'Candara', color: '#124e77']
            'heading5' font: [size: 12, family: 'Century Gothic', color: '#000000', italic: true]
            'heading6' font: [size: 11, family: 'Calibri Light', color: '#3275B3']
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

    DataModelToDocxExporter(DataModel rootModel, DataClassService dataClassService, ElementService elementService, Closure customTemplate, imagePath, Integer depth = 3) {
        initVars(rootModel, dataClassService, elementService)
        this.customTemplate = customTemplate
        if(imagePath) this.imagePath = imagePath
        this.depth = depth
    }

    void export(OutputStream outputStream) {
        log.info "Exporting Data Model ${rootModel.name} (${rootModel.combinedVersion}) to inventory document."

        def dataClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.includes(rootModel)).items
        def builder = new ModelCatalogueWordDocumentBuilder(outputStream)
        def docHelper = new DocxSpecificationDataHelper(builder, depth + 1, elementService)

        List<DataModel> allVersionsOfModel = getAllVersionsOfModel(rootModel, [rootModel])

        builder.create {
            document(template: customTemplate) {

                def thisOrganisation = rootModel.ext.get(Metadata.ORGANISATION)?:""
                def thisOwner = rootModel.ext.get(Metadata.OWNER)?:""
                def reviewers = rootModel.ext.get(Metadata.REVIEWERS)?:""
                def authors = rootModel.ext.get(Metadata.AUTHORS)?:""

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
                    text "Version ${rootModel?.semanticVersion} ${rootModel?.status}"
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
                            cell "${model?.semanticVersion}", style: 'cell'
                            cell model?.lastUpdated, style: 'cell'
                            cell "${model?.revisionNotes}", style:'cell'
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
                    paragraph(style: 'classification.description') {
                        text rootModel.description
                    }
                }

                log.debug "found ${dataClasses.size()} top level dataClasses"
                for (DataClass dClass in dataClasses) {
                    if(dClass.status!= ElementStatus.DEPRECATED) {
                        docHelper.printClass(dClass, true, 1)
                    }
                }

                if (docHelper.usedDataTypes) {
                    docHelper.printTypes()
                }

                if(docHelper.rules) {
                    pageBreak()
                    heading1 'Business Rules'
                    docHelper.printRules()
                }

            }
        }

        log.info "data dataModel ${rootModel.name} (${rootModel.combinedVersion}) exported to inventory document."
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


}
