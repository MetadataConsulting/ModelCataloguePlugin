package org.modelcatalogue.gel.export

import com.craigburke.document.core.builder.DocumentBuilder
import groovy.util.logging.Log4j
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.docx.ModelCatalogueWordDocumentBuilder

import java.text.SimpleDateFormat

/**
 * This class generates either the Eligibility Criteria OR the Phenotypes & Clinical Tests report for Rare Diseases
 * according to the selected mode
 */
@Log4j
class RareDiseaseMaintenanceSplitDocsExporter {

    private static final Map<String, Object> CELL_TEXT_FIRST = [font: [size: 11, family: 'Calibri', bold: true]]
    private static final Map<String, Object> CELL_TEXT_SECOND = [font: [size: 10, family: 'Calibri']]
    private static final Map<String, Object> CELL_TEXT_SECOND_BOLD = [font: [size: 10, family: 'Calibri', bold: true]]
    private static final Map<String, Object> CELL_TEXT_THIRD = [font: [size: 8, family: 'Calibri Light', color: '#000000']]


    public static final String EMPTY_STRING = ""
    public static final String LEVEL_3_TITLE = "Level 3 Title"
    public static final String LEVEL_4_TITLE = "Level 4 Title"
    public static final String PROJECT_NAME = "100,000 Genomes Project"
    public static final String ELIGIBILITY_SIDEBAR = "Eligibility Statement"
    public static final String PHENOTYPE_SIDEBAR = "Phenotypes"
    public static final String CLINICAL_TESTS_SIDEBAR = "Clinical Tests"
    public static final String TABLE_ENTRIES_TEXT = "Entries ordered left to right in table"

    DataClass rootDataClass
    private final Set<Long> processedDataClasses = new HashSet<Long>()
    private final Map<String, String> levelNameDescriptions = new HashMap<>()
    private final Map<String, String> levelDataClassDescriptions = new HashMap<>()
    private Integer dataClassCount = 0
    Integer level

    //If there is an image to display on the front page
    def imagePath

    Closure customTemplate
    static Closure standardTemplate = {
        'document' font: [family: 'Calibri', size: 11], margin: [left: 30, right: 30]
        'paragraph.title' font: [color: '#1F497D', size: 24.pt, bold:true], margin: [top: 150.pt, bottom: 10.pt]
        'paragraph.subtitle' font: [family: 'Calibri Light', color: '#1F497D', size: 22.pt], margin: [top: 0.pt]
        'paragraph.description' font: [color: '#13D4CA', size: 16.pt, italic: true], margin: [left: 30, right: 30]
        'heading1' font: [size: 18, bold: true]
        'heading2' font: [size: 16, family: 'Calibri Light', bold: true, color: '#3275B3']
        'heading3' font: [size: 14, family: 'Calibri Light', bold: true, color: '#3275B3']
        'heading4' font: [size: 12, family: 'Candara', color: '#124e77']
        'heading5' font: [size: 12, family: 'Century Gothic', color: '#000000', italic: true]
        'heading6' font: [size: 11, family: 'Calibri Light', color: '#3275B3']
        'table.row.cell.headerCell' font: [color: '#FFFFFF', size: 12.pt, bold: true], background: '#1F497D'
        'table.row.cell' font: [size: 10.pt]
        'paragraph.headerImage' height: 1.366.inches, width: 2.646.inches
    }

    RareDiseaseMaintenanceSplitDocsExporter(DataClass rootDataClass, Closure customTemplate, String imagePath, Integer level) {
        this.rootDataClass = rootDataClass
        this.customTemplate = customTemplate
        this.level = level
        if(imagePath) this.imagePath = imagePath
    }


    void export(OutputStream outputStream) {
        log.info "Exporting Data Model ${rootDataClass.name} (${rootDataClass.dataModel.semanticVersion}) to inventory document."

        DocumentBuilder builder = new ModelCatalogueWordDocumentBuilder(outputStream)

        builder.create {
            document(template: customTemplate) {

                createDocumentFrontPages(builder)


                levelNameDescriptions.put(level, rootDataClass.name)
                levelDataClassDescriptions.put(level, rootDataClass.description)
                descendDataClasses(builder, rootDataClass, level)

            }

        }

        log.info "data dataModel ${rootDataClass.dataModel.name} (${rootDataClass.dataModel.semanticVersion}}) exported to inventory document."
    }


    private void createDocumentFrontPages(DocumentBuilder builder) {

        List<DataClass> allVersionsOfDataClass = getAllVersionsOfDataClass(rootDataClass, [rootDataClass])

        builder.with {

            DataModel rootDataClassModel = rootDataClass.dataModel
            def thisOwner = rootDataClassModel.ext.get(Metadata.OWNER) ?: EMPTY_STRING
            def reviewers = rootDataClassModel.ext.get(Metadata.REVIEWERS)
            def authors = rootDataClassModel.ext.get(Metadata.AUTHORS)

            byte[] imageData
            if (imagePath) imageData = new URL(imagePath).bytes

            if (imagePath) {
                paragraph(align: 'right') {
                    image(data: imageData, height: 1.366.inches, width: 2.646.inches)
                }
            }

//            paragraph(style: 'title', align: 'center') {
//                text documentName(eligibilityMode)
//            }

            paragraph(style: 'subtitle', align: 'right', margin: [right: 120]) {
                text PROJECT_NAME
            }

            paragraph(style: 'document', margin: [top: 120]) {
                text "Version ${rootDataClassModel.semanticVersion} ${rootDataClassModel.status}"
                lineBreak()
                text SimpleDateFormat.dateInstance.format(new Date())
            }

            pageBreak()

            heading1 'Document Management'
            paragraph(style: 'document', margin: [top: 20]) {
                text 'Document Owner: ', font: [bold: true]
                text thisOwner
                lineBreak()
                text 'Authors: ', font: [bold: true]
                if (authors) text authors
            }

            heading1 'Version Control'
            table {
                row {
                    cell 'Version', style: 'cell.headerCell'
                    cell 'Date', style: 'cell.headerCell'
                }

                allVersionsOfDataClass.each { DataClass dataClass ->
                    row {
                        cell "${dataClass.dataModel.semanticVersion}", style: 'cell'
                        cell dataClass.dataModel.ext.get("http://www.modelcatalogue.org/metadata/#released")? Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dataClass.dataModel.ext.get("http://www.modelcatalogue.org/metadata/#released")).format("dd/MM/yyyy"):'', style: 'cell'
                    }
                }
            }

            heading1 'Reviewers'
            table {
                row {
                    cell 'Name', style: 'cell.headerCell'
                    cell 'Responsibility', style: 'cell.headerCell'
                    cell 'Date', style: 'cell.headerCell'
                }
                if (reviewers) {
                    reviewers.split(',').each { String reviewer ->
                        row {
                            cell "${reviewer}", style: 'cell'
                            cell '', style: 'cell'
                            cell '', style: 'cell'
                        }
                    }
                }
            }

            heading1 'Approved by'
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

            pageBreak()

                createEligibilityIntroductionPage(builder)

        }
    }


    private void createEligibilityIntroductionPage(DocumentBuilder builder) {

        builder.with {
            heading2 introduction
            heading3 purpose
            paragraph(margin: [left: 10, right: 10]) {
                text aim
            }
            heading3 structureBackground
            paragraph(margin: [left: 10, right: 10]) {
                text introContent
            }
        }
    }


    ArrayList getAllVersionsOfDataClass(DataClass dataClass, List<DataClass> dataClasses) {
        def oldDataClass = dataClass.supersedes
        if (oldDataClass && !dataClasses.contains(oldDataClass.first())) {
            dataClasses.add(0, oldDataClass.first())
            getAllVersionsOfDataClass(oldDataClass.first(), dataClasses)
        } else {
            return dataClasses
        }
    }


    private void descendDataClasses(DocumentBuilder builder, DataClass dataClass, Integer level) {
        log.debug "descendDataClasses level=$level count=" + this.dataClassCount

        String dataClassName = dataClass.name + " (${dataClass.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id')?:"${dataClass.getLatestVersionId() ?: dataClass.getId() ?: '<id not assigned yet>'}"})"
        levelNameDescriptions.put(level, dataClassName)
        levelDataClassDescriptions.put(level, dataClass.description)

        if (level > 5) return   //don't go too deep, nothing for us down there...

        //don't re-examine previously seen dataClasses
        if (dataClass.getId() in processedDataClasses) {
            log.debug "found dataClass id $dataClass.id in processedDataClasses $processedDataClasses"
            return
        }

        if(level==2) {  //1st sub-level heading
            builder.with {
                heading3 levelNameDescriptions.get(level)
            }
        }
        if(level==3) { //2nd sub-level heading
            builder.with {
                heading4 levelNameDescriptions.get(level)
            }
        }
        if(level==4) { //3rd sub-level heading
            builder.with {
                heading5 levelNameDescriptions.get(level)
            }
        }

        log.debug "level $level dataClass name $dataClass.name"


                for (DataClass child in dataClass.parentOf) {

                    processedDataClasses << dataClass.getId()
                    dataClassName = child.name + " (${child.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id')?:child.getLatestVersionId()})"
                    levelNameDescriptions.put(level + 1, dataClassName)
                    if (level == 4) {

                                if (child.name.matches("(?i:.*Eligibility.*)")) {
                                    this.dataClassCount = this.dataClassCount + 1
                                    createPlainContentRows(builder, child)

                                } else if (child.name.matches("(?i:.*Phenotype.*)")) {
                                    this.dataClassCount = this.dataClassCount + 1
                                    createNestedTableRows(builder, child, true)

                                } else if (child.name.matches("(?i:.*Clinical Test.*)")) {
                                    this.dataClassCount = this.dataClassCount + 1
                                    createNestedTableRows(builder, child, false)
                                }
                    } else {
                        descendDataClasses(builder, child, level + 1)
                    }
        }

    }




    // this is a text panel (cell) with multiple paragraphs for the Eligibility Criteria
    private void createPlainContentRows(DocumentBuilder builder, DataClass dataClass) {
            builder.with {

                table(columns: [1, 6], margin: [left: 20.px, right: 40.px], padding: 3.px, border: [size: 2.px]) {
                    row {
                        cell(background: '#BED6ED') {
                            text ELIGIBILITY_SIDEBAR
                        }
                        cell {

                            String description = dataClass.description ?: EMPTY_STRING
                            if (description) {
                                text CELL_TEXT_SECOND, description + '\n'
                            }

                            dataClass.parentOf.each { DataClass child ->
                                if (child.name) {
                                    String childName = child.name
                                    text CELL_TEXT_SECOND_BOLD, "\n$childName\n"
                                }
                                if (child.description) {
                                    text CELL_TEXT_SECOND, "$child.description\n\n"
                                }
                            }

                        }
                    }
                }
            }
    }

    // this is a cell with an inner table containing the Phenotypes or Clinical Test names
    private void createNestedTableRows(DocumentBuilder builder, DataClass dataClass, boolean phenotypeMode) {
        List<String> cellTexts = new ArrayList<>();
        String childName = '';


        dataClass.parentOf.each { DataClass child ->
            if (child.name) {
                if (phenotypeMode) {
                    childName = child.name + " (" + (child.ext.get("OBO ID") ?: EMPTY_STRING) + ")"
                }else if(child.ext.get("http://www.modelcatalogue.org/metadata/genomics/#gel-test-id-versioned")){
                    childName = child.name + " (" + (child.ext.get("http://www.modelcatalogue.org/metadata/genomics/#gel-test-id-versioned") ?: EMPTY_STRING) + ")"
                } else {
                    childName = child.name + " (${child.getLatestVersionId() ?: child.getId()}.${child.getVersionNumber() ?: 1})"
                }
                cellTexts << childName
            }
        }



        if (cellTexts.size() == 0) {          // prevent doc failure & provide explicitly empty cell
            cellTexts << EMPTY_STRING
        }

        int totalCells = cellTexts.size()
        int maxRows = calculateRows(totalCells)
        int cellCounter = 0
        int maxCols = 3;

        builder.with {
            table(columns: [1,6], margin: [left: 20.px, right: 40.px], padding: 3.px, border: [size: 2.px]) {
                row {
                    cell(background: '#BED6ED') {
                        text phenotypeMode ? PHENOTYPE_SIDEBAR : CLINICAL_TESTS_SIDEBAR
                        text EMPTY_STRING + "\n\n"
                    }
                    cell {
                        if (!phenotypeMode) text CELL_TEXT_THIRD, (levelDataClassDescriptions.get(4)) ? levelDataClassDescriptions.get(4) + "\n\n" : EMPTY_STRING
                        text TABLE_ENTRIES_TEXT, font: [size: 8, family: 'Calibri Light', color: '#000000']
                        table(padding: 1, border: [size: 1, color: '#D2D2D2']) {
                            //inner table should be up to 3 cols wide
                            1.upto(maxRows, {
                                row {
                                    int colCount = 0;
                                    while (colCount < maxCols && cellCounter < totalCells) {
                                        cell {
                                            text CELL_TEXT_SECOND, cellTexts.get(cellCounter++)
                                            colCount++
                                        }
                                    }
                                }
                            })
                        }


                    }
                }
            }
            if(!phenotypeMode){
                pageBreak()
            }
        }



    }

    private int calculateRows(int totalCells) {
        int rows = totalCells / 3
        int rowRemainder = totalCells % 3 == 0 ? 0 : 1
        rows + rowRemainder
    }

    String introduction ="Introduction"
    String purpose ="Purpose of this document"
    String aim ="The aim of this document is to provide an up-to-date list of eligibility criteria for conditions approved for recruitment within the Genomics England Rare Diseases Programme."
    String structureBackground ="Structure and background to eligibility statements"

    String introContent = '''For each disease listed we provide an “eligibility statement” composed of the following key information:

1.\tInclusion criteria – the clinical features, characteristics or investigations that probands with a given disease must have in order to be eligible for recruitment.
2.\tExclusion criteria - the clinical features, characteristics or investigation findings that participants with a given disease must not have in order to be eligible for recruitment.
3.\tPrior genetic testing – this sets out both in general terms, and where appropriate more specifically, the genetic testing which participants with a given disease must have performed prior to recruitment.

Each eligibility statement has been informed by at least one clinician specialising in the field and incorporates comments provided during the consultation period with Genomic Medicine Centres. Therefore, we would like to take this opportunity to thank this community for providing their expertise and understanding of complex disorders so generously.

Given the rapid progress in the understanding of rare diseases worldwide, it is important that the eligibility statements continue to be reviewed and developed over time in light of new discoveries and changes in clinical practice. Therefore we will continue our engagement with the clinical community throughout the lifetime of the project.
'''

}
