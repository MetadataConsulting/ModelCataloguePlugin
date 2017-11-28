package org.modelcatalogue.core.export.inventory

import com.craigburke.document.core.builder.DocumentBuilder
import groovy.util.logging.Log4j
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.ElementService
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


    DataClassService dataClassService
    ElementService elementService
    DataClass rootclass
    Closure customTemplate
    int depth = 3


    private void initVars(dataClass, dataClassService, ElementService elementService) {
        this.rootclass = dataClass
        this.dataClassService = dataClassService
        this.elementService = elementService
    }

    DataClassToDocxExporter(DataClass dataClass, DataClassService dataClassService, Integer depth = 3, ElementService elementService) {
        initVars(dataClass, dataClassService, elementService)
        this.customTemplate = {
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
        this.depth = depth
    }

    void export(OutputStream outputStream) {

        DataClass rootDataClass = rootclass

        log.info "Exporting Data Model ${rootDataClass.name} (${rootDataClass.combinedVersion}) to inventory document."

        DocumentBuilder builder = new ModelCatalogueWordDocumentBuilder(outputStream)
        DocxSpecificationDataHelper helper = new DocxSpecificationDataHelper(builder, depth + 1, elementService)


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

                helper.printClass(rootDataClass, true, 1)

                if (helper.usedDataTypes) {
                    helper.printTypes()
                }

                if(helper.rules) {
                    pageBreak()
                    heading1 'Business Rules'
                    helper.printRules()
                }
            }
        }

        log.info "data dataModel ${rootDataClass.name} (${rootDataClass.combinedVersion}) exported to inventory document."
    }



}
