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
class RareDiseasesDocExporter {

    private static final Map<String, Object> CELL_TEXT_FIRST = [font: [size: 11, family: 'Calibri', bold: true]]
    private static final Map<String, Object> CELL_TEXT_SECOND = [font: [size: 10, family: 'Calibri']]
    private static final Map<String, Object> CELL_TEXT_SECOND_BOLD = [font: [size: 10, family: 'Calibri', bold: true]]

    public static final String EMPTY_STRING = ""
    public static final String LEVEL_3_TITLE = "Level 3 Title"
    public static final String LEVEL_4_TITLE = "Level 4 Title"
    public static final String PROJECT_NAME = "100,000 Genomes Project"
    public static final String RARE_DISEASE_CONDITIONS = "Rare Disease Conditions"
    public static final String ELIGIBILITY_TITLE_TEXT = "Eligibility Criteria"
    public static final String ELIGIBILITY_SIDEBAR = "Eligibility Statement"
    public static final String PHENOTYPE_TITLE_TEXT = "Phenotypes and Clinical Tests"
    public static final String PHENOTYPE_SIDEBAR = "Phenotypes"
    public static final String CLINICAL_TESTS_SIDEBAR = "Clinical Tests"

    DataClass rootModel
    private final Set<Long> processedModels = new HashSet<Long>()
    private final Map<String, String> levelDescriptions = new HashMap<>()
    private Integer modelCount = 0

    // determines whether the Eligibility Criteria OR Phenotypes & Clinical Tests report is produced
    private boolean eligibilityMode = true;

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
        'heading4' font: [size: 11, family: 'Calibri Light', color: '#224E76']
        'heading5' font: [size: 12, family: 'Calibri Light', color: '#3275B3', italic: true]
        'heading6' font: [size: 11, family: 'Calibri Light', color: '#3275B3']
        'table.row.cell.headerCell' font: [color: '#FFFFFF', size: 12.pt, bold: true], background: '#1F497D'
        'table.row.cell' font: [size: 10.pt]
        'paragraph.headerImage' height: 1.366.inches, width: 2.646.inches
    }

    RareDiseasesDocExporter(DataClass rootModel, Closure customTemplate, String imagePath, boolean eligibilityMode) {
        this.rootModel = rootModel
        this.customTemplate = customTemplate
        if(imagePath) this.imagePath = imagePath
        this.eligibilityMode = eligibilityMode
    }

    static String documentName(boolean eligibilityMode) {
        if (eligibilityMode) {
            return RARE_DISEASE_CONDITIONS + " " + ELIGIBILITY_TITLE_TEXT
        } else {
            return RARE_DISEASE_CONDITIONS + " " + PHENOTYPE_TITLE_TEXT
        }
    }


    void export(OutputStream outputStream) {
        log.info "Exporting Data Model ${rootModel.name} (${rootModel.combinedVersion}) to inventory document."

        DocumentBuilder builder = new ModelCatalogueWordDocumentBuilder(outputStream)

        builder.create {
            document(template: customTemplate) {

                createDocumentFrontPages(builder)

                if (rootModel.name) {
//                    heading3 rootModel.name   // using document name as it seems clearer than model name but leave commented for the moment
                    heading2 documentName(eligibilityMode)
                }

                Integer level = 1
                levelDescriptions.put(level, rootModel.name)
                descendModels(builder, rootModel, level)

            }

        }

        log.info "data dataModel ${documentName(eligibilityMode)} (${rootModel.combinedVersion}) exported to inventory document."
    }


    private void createDocumentFrontPages(DocumentBuilder builder) {

        List<DataClass> allVersionsOfDataClass = getAllVersionsOfDataClass(rootModel, [rootModel])

        builder.with {

            DataModel rootModelModel = rootModel.dataModel
            def thisOwner = rootModelModel.ext.get(Metadata.OWNER) ?: EMPTY_STRING
            def reviewers = rootModelModel.ext.get(Metadata.REVIEWERS)
            def authors = rootModelModel.ext.get(Metadata.AUTHORS)

            byte[] imageData
            if (imagePath) imageData = new URL(imagePath).bytes

            if (imagePath) {
                paragraph(align: 'right') {
                    image(data: imageData, height: 1.366.inches, width: 2.646.inches)
                }
            }

            paragraph(style: 'title', align: 'center') {
                text documentName(eligibilityMode)
            }

            paragraph(style: 'subtitle', align: 'right', margin: [right: 120]) {
                text PROJECT_NAME
            }

            paragraph(style: 'document', margin: [top: 120]) {
                text "Version ${rootModel.versionNumber} ${rootModel.status}"
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

                allVersionsOfDataClass.each { DataClass model ->
                    row {
                        cell "${model.versionNumber}", style: 'cell'
                        cell model.lastUpdated, style: 'cell'
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

            if(eligibilityMode) {
                createEligibilityIntroductionPage(builder)
            }
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


    private void descendModels(DocumentBuilder builder, DataClass model, Integer level) {
        log.debug "descendModels level=$level count=" + this.modelCount

        String modelName = model.name + " (${model.getLatestVersionId() ?: model.getId()}.${model.getVersionNumber() ?: 1})"
        levelDescriptions.put(level, modelName)

        if (level > 5) return   //don't go too deep, nothing for us down there...
//        if (modelCount > 10) return   //don't use too many models for testing

        //don't re-examine previously seen models
        if (model.getId() in processedModels) {
            log.debug "found model id $model.id in processedModels $processedModels"
            return
        }

        if(level==2) {  //1st sub-level heading
            builder.with {
                heading3 levelDescriptions.get(level)
            }
        }
        if(level==3) { //2nd sub-level heading
            builder.with {
                heading4 levelDescriptions.get(level)
            }
        }
        if(level==4) { //3rd sub-level heading
            builder.with {
                heading5 levelDescriptions.get(level)
            }
        }

        log.debug "level $level model name $model.name"
        log.debug "model description $model.description"


        for (DataClass child in model.parentOf) {
            processedModels << model.getId()

            if (child.name.matches("(?i:.*Eligibility.*)") && eligibilityMode) {
                this.modelCount = this.modelCount + 1
                printModel(builder, child, level + 1, false)

            } else if (child.name.matches("(?i:.*Phenotype.*)") && !eligibilityMode) {
                this.modelCount = this.modelCount + 1
                printModel(builder, child, level + 1, true)

            } else if (child.name.matches("(?i:.*Clinical Test.*)") && !eligibilityMode) {
                this.modelCount = this.modelCount + 1
                printModel(builder, child, level + 1, false)

            } else {
                descendModels(builder, child, level + 1)
            }
        }
    }



    private void printModel(DocumentBuilder builder, DataClass model, Integer level, boolean phenotypeMode) {
        log.debug "printModel level=$level"
        String modelName = model.name + " (${model.getLatestVersionId() ?: model.getId()}.${model.getVersionNumber() ?: 1})"
        levelDescriptions.put(level, modelName)

        builder.with {

            //4th sub-level heading - level 5
            heading6 levelDescriptions.get(level)  //eligibility, phenotypes & tests

            table(columns: [1,6], margin: [left: 20.px, right: 40.px], padding: 3.px, border: [size: 2.px]) {

                row {
                    cell (background: '#BED6ED') {
                        text CELL_TEXT_FIRST, LEVEL_3_TITLE
                    }
                    cell {
                        text CELL_TEXT_SECOND, levelDescriptions.get(level - 2)
                    }
                }

                row {
                    cell (background: '#BED6ED') {
                        text CELL_TEXT_FIRST, LEVEL_4_TITLE
                    }
                    cell {
                        text CELL_TEXT_SECOND, levelDescriptions.get(level - 1)
                    }
                }

                if (eligibilityMode) {
                    createPlainContentRows(builder, model)
                } else {
                    createNestedTableRows(builder, model, phenotypeMode)
                }

            }

            pageBreak()
        }
    }

    // this is a text panel (cell) with multiple paragraphs for the Eligibility Criteria
    private void createPlainContentRows(DocumentBuilder builder, DataClass model) {
        builder.with {
            row {
                cell(background: '#BED6ED') {
                    text ELIGIBILITY_SIDEBAR
                }
                cell {

                    String description = model.description ?: EMPTY_STRING
                    if (description) {
                        text CELL_TEXT_SECOND, description + '\n'
                    }

                    model.parentOf.each { DataClass child ->
                        if (child.name) {
                            text CELL_TEXT_SECOND_BOLD, "\n$child.name\n"
                        }
                        if (child.description) {
                            text CELL_TEXT_SECOND, "$child.description\n\n"
                        }
                    }

                }
            }
        }
    }

    // this is a cell with an inner table containing the Phenotypes or Clinical Test names
    private void createNestedTableRows(DocumentBuilder builder, DataClass model, boolean phenotypeMode) {
        List<String> cellTexts = new ArrayList<>();
        String childName = '';

        model.parentOf.each { DataClass child ->
            if (child.name) {
                if (phenotypeMode) {
                    childName = child.name + " (" + (child.ext.get("OBO ID") ?: EMPTY_STRING) + ")"
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

        if(maxRows == 0) {
            println "found maxRows= $maxRows at $model.name"
            println modelCount
        }
        builder.with {
            row {
                cell(background: '#BED6ED') {
                    text phenotypeMode ? PHENOTYPE_SIDEBAR : CLINICAL_TESTS_SIDEBAR
                }
                cell {
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
