package org.modelcatalogue.core.export.inventory

import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.CHANGE_NEW
import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.CHANGE_REMOVAL
import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.CHANGE_UPDATE
import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H1
import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H2
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Iterables
import com.google.common.collect.Multimap
import grails.util.GrailsNameUtils
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.mime.MimeType
import org.modelcatalogue.spreadsheet.api.Sheet
import org.modelcatalogue.spreadsheet.builder.api.CellDefinition
import org.modelcatalogue.spreadsheet.builder.api.SheetDefinition
import org.modelcatalogue.spreadsheet.builder.api.SpreadsheetBuilder
import org.modelcatalogue.spreadsheet.builder.api.WorkbookDefinition
import org.modelcatalogue.spreadsheet.builder.poi.PoiSpreadsheetBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.CatalogueElementService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.diff.CatalogueElementDiffs
import org.modelcatalogue.core.diff.Diff
import org.modelcatalogue.core.enumeration.Enumeration
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

/**
 * For the Inventory Report.
 * Compares a Data Model or a Data Class with its previous version and produces an Excel spreadsheet detailing
 * the differences.
 * Uses CatalogueElementDiffs in the base case to compare two elements.
 */
@Log4j
class CatalogueElementToXlsxExporter {

    static final String CONTENT = 'Content'
    static final String DATA_CLASSES = 'DataClasses'
    public static final String DATA_TYPE_FIRST_COLUMN = 'F'
    public static final MimeType XLSX                  = new MimeType('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'xlsx')
    public static final MimeType EXCEL                 = new MimeType('application/vnd.ms-excel', 'xlsx')

    final DataClassService dataClassService
    final GrailsApplication grailsApplication
    final CatalogueElementDiffs catalogueElementDiffs
    final Long elementId
    final Long previousVersionElementForDiffId
    final Integer depth

    boolean printMetadata
    static inSubsection

    private Map<Long, DataClass> dataClassesProcessedInOutline = [:]
    private Set<String> namesPrinted = new HashSet<String>()
    private Set<Long> sheetsPrinted = new HashSet<Long>()

    static CatalogueElementToXlsxExporter forDataModel(DataModel element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new CatalogueElementToXlsxExporter(element, dataClassService, grailsApplication, depth)
    }

    static CatalogueElementToXlsxExporter forDataClass(DataClass element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new CatalogueElementToXlsxExporter(element, dataClassService, grailsApplication,  depth)
    }

    public CatalogueElementToXlsxExporter(CatalogueElement element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        this.elementId = element.getId()
        this.dataClassService = dataClassService
        this.grailsApplication = grailsApplication
        this.depth = depth
        this.previousVersionElementForDiffId = element.findPreviousVersion()?.id
        this.printMetadata = grailsApplication.config.mc.export.xlsx.printMetadata ?: false
        this.catalogueElementDiffs = new CatalogueElementDiffs(grailsApplication)
    }

    protected static String getModelCatalogueIdToPrint(CatalogueElement element) {
        element.hasModelCatalogueId() && !element.modelCatalogueId.startsWith('http') ? element.modelCatalogueId : (element.getLatestVersionId()) ?: element.getId()
    }

    protected static void buildIntroduction(SheetDefinition sheet, CatalogueElement dataModel) {
        sheet.with {
            row {
                cell {
                    value dataModel.name
                    colspan 4
                    style H1
                }
            }
            row {
                cell {
                    value "Version: $dataModel.dataModelSemanticVersion"
                    colspan 4
                    style H1
                }
            }
            row {
                cell {
                    style H1
                    colspan 4
                }
            }
            row {
                cell {
                    value "Introduction"
                    colspan 4
                    style H2
                }
            }
            row {
                cell {
                    value dataModel.description
                    height 100
                    colspan 4
                    style ModelCatalogueStyles.DESCRIPTION
                }
            }

            row()

            row {
                cell {
                    value "Document Version History"
                    colspan 4
                    style H2
                }
            }

            row()

            row {
                cell {
                    value 'Version'
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER, ModelCatalogueStyles.THIN_DARK_GREY_BORDER
                    width 12
                }
                cell {
                    value 'Date Issued'
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER, ModelCatalogueStyles.THIN_DARK_GREY_BORDER
                    width 12
                }
                cell {
                    value 'Brief Summary of Change'
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER, ModelCatalogueStyles.THIN_DARK_GREY_BORDER
                    width 40
                }
                cell {
                    value 'Owner\'s Name'
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER, ModelCatalogueStyles.THIN_DARK_GREY_BORDER
                    width 40
                }
            }

            for (DataModel version in CatalogueElementService.getAllVersions(dataModel.instanceOf(DataModel) ? dataModel : dataModel.dataModel).items) {
                row {
                    cell {
                        value version.semanticVersion
                        styles ModelCatalogueStyles.THIN_DARK_GREY_BORDER, ModelCatalogueStyles.CENTER_CENTER
                    }
                    cell {
                        value version.versionCreated
                        styles ModelCatalogueStyles.DATE_NORMAL, ModelCatalogueStyles.THIN_DARK_GREY_BORDER
                    }
                    cell {
                        value version.revisionNotes
                        styles ModelCatalogueStyles.DESCRIPTION, ModelCatalogueStyles.THIN_DARK_GREY_BORDER
                        height 40
                    }
                    cell {
                        value version.ext[Metadata.OWNER]
                        styles ModelCatalogueStyles.THIN_DARK_GREY_BORDER, ModelCatalogueStyles.CENTER_CENTER
                    }
                }
            }

            row()

            row {
                cell {
                    value "Date of Issue Reference"
                    style ModelCatalogueStyles.INNER_TABLE_HEADER
                    colspan 2
                }
                cell {
                    value new Date()
                    style ModelCatalogueStyles.DATE
                    colspan 2
                }
            }
        }
    }

    void export(OutputStream outputStream) {
        dataClassesProcessedInOutline = [:]
        namesPrinted = new HashSet<String>()
        sheetsPrinted = new HashSet<Long>()

        //set the element and the previous version of the element - so we can do a diff
        CatalogueElement element = CatalogueElement.get(elementId)
        CatalogueElement previousVersionElementForDiff = previousVersionElementForDiffId ? CatalogueElement.get(previousVersionElementForDiffId) : null

        // get the top level classes for the data model - if the export is for a data model
        // if the export is just for a class just use that class as the top level class
        List<DataClass> dataClasses = Collections.emptyList()
        if (HibernateHelper.getEntityClass(element) == DataClass) {
            dataClasses = [element as DataClass]
        } else if (HibernateHelper.getEntityClass(element) == DataModel) {
            dataClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.includes(element as DataModel), ImmutableMap.of('status', 'active'), true).items
        }

        log.info "Exporting Data Class ${element.name} (${element.combinedVersion}) to inventory spreadsheet."

        //create spreadsheet builder and build the spreadsheet
        SpreadsheetBuilder builder = new PoiSpreadsheetBuilder()
        builder.build(outputStream) { WorkbookDefinition workbook ->
            apply ModelCatalogueStyles

            //add sheets
            sheet("Introduction") { SheetDefinition sheet ->
                buildIntroduction(sheet, element)
            }
            sheet(CONTENT) { }
            sheet('Changes') { }

            //add all the sheets for classes based on the metadata
            buildDataClassesDetails(dataClasses, previousVersionElementForDiff, workbook, null)

            //fill content sheet
            sheet(CONTENT) { SheetDefinition sheet ->
                buildOutline(sheet, dataClasses, previousVersionElementForDiff)
            }

            log.info "Printing all changes summary."

            //fill changes sheet
            sheet('Changes') { SheetDefinition sheet ->
                row {
                    cell {
                        style H1
                        value 'Changes Summary'
                        colspan 6
                }
                }


                log.info "Sorting changes"
                Iterable<Diff> allDiffs = Iterables.concat(computedDiffs.values().collect { it.values() })

                //load diffs by class
                //key is the class id
                //if a data element diff is included - it's included with the class id

                Map diffsByClass = getDiffsMapByClass(allDiffs)

                diffsByClass.each{ key, val ->
                    printClassDetails(key, val, sheet)
                }



            }

        }

        log.info "Exported ${GrailsNameUtils.getNaturalName(HibernateHelper.getEntityClass(element).simpleName)} ${element.name} (${element.combinedVersion}) to inventory spreadsheet."

    }

    protected Map getDiffsMapByClass(allDiffs){
        Map diffsByClass = [:]

        allDiffs.each{ Diff diff ->
            if(diff!=null) {
                Set classDiffs = []
                classDiffs.add(diff)
                //if this is a class use it's id as the key
                // and add these diffs to those already recorded
                if (diff?.element && diff.element.instanceOf(DataClass)) {
                    //get the diffs that have already been collected
                    if(diffsByClass.get(diff.element.name)) {
                        classDiffs.addAll(diffsByClass.get(diff.element.name))
                    }
                    diffsByClass.put(diff.element.name, classDiffs)
                } else {

                    if (diff.parentClass) {
                        //get the diffs that have already been collected using the parent id
                        if(diffsByClass.get(diff.parentClass.name)) {
                            classDiffs.addAll(diffsByClass.get(diff.parentClass.name))
                        }
                        diffsByClass.put(diff.parentClass.name, classDiffs)
                    } else {
                        //they are top level deleted diffs - so look in the otherValue
                        if(diffsByClass.get(diff.otherValue.name)) {
                            classDiffs.addAll(diffsByClass.get(diff.otherValue.name))
                        }
                        diffsByClass.put(diff.otherValue.name, classDiffs)
                    }
                }
            }
        }

        diffsByClass
    }


    protected void printClassDetails(String key, Set val,  SheetDefinition sheet){

        sheet.with {
            row {
                cell {
                    style H1
                    value key
                    colspan 6
                }
            }
            row {
                cell {
                    value 'Parent ID'
                    width 10
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER
                }
                cell {
                    value 'Parent Name'
                    width 15
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER
                }
                cell {
                    value 'ID'
                    width 10
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER
                }
                cell {
                    value 'Type'
                    width 15
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER
                }
                cell {
                    value 'Name'
                    width 25
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER
                }
                cell {
                    value 'Description'
                    width 35
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER
                }
                cell {
                    value 'Old Value'
                    width 35
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER
                }
                cell {
                    value 'New Value'
                    width 35
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER
                }
            }
            val.each { Diff diff ->
                printChanges(diff, sheet)
            }
      }


    }

    protected void printChanges(Diff diff, SheetDefinition sheet){

        sheet.with {
            row {
                cell {
                    value "${(diff?.parentClass) ? getModelCatalogueIdToPrint(diff?.parentClass) : "Top level class, no parent"}"
                    styles withDiffStyles(diff, ModelCatalogueStyles.CENTER_LEFT)
                }
                cell {
                    value "${(diff?.parentClass) ? diff?.parentClass?.name : "Top level class, no parent"}"
                    styles withDiffStyles(diff, ModelCatalogueStyles.CENTER_LEFT)
                }
                cell {
                    value "${(diff?.element) ? getModelCatalogueIdToPrint(diff?.element) : getModelCatalogueIdToPrint(diff.otherValue)}"
                    styles withDiffStyles(diff, ModelCatalogueStyles.CENTER_LEFT)
                }
                cell {
                    value "${(diff?.element) ? GrailsNameUtils.getNaturalName(HibernateHelper.getEntityClass(diff.element).simpleName) : GrailsNameUtils.getNaturalName(HibernateHelper.getEntityClass(diff.otherValue).simpleName)}"
                    styles withDiffStyles(diff, ModelCatalogueStyles.CENTER_LEFT)
                }
                cell {
                    value "${(diff?.element) ? diff.element.name : diff.otherValue?.name}"
                    styles withDiffStyles(diff, ModelCatalogueStyles.DESCRIPTION, ModelCatalogueStyles.CENTER_LEFT)
                }
                cell {
                    value diff.changeDescription
                    styles withDiffStyles(diff, ModelCatalogueStyles.CENTER_LEFT)
                }
                cell {
                    value humanReadableValue(diff.otherValue)
                    styles withDiffStyles(diff, ModelCatalogueStyles.DESCRIPTION)
                }
                cell {
                    value humanReadableValue(diff.selfValue)
                    styles withDiffStyles(diff, ModelCatalogueStyles.DESCRIPTION)
                }
            }
        }
    }

    protected void buildDataClassesDetails(Iterable<DataClass> dataClasses, CatalogueElement previousVersionElementForDiff, WorkbookDefinition workbook, SheetDefinition sheet, int level = 0, Set<Long> processed = new HashSet<Long>()) {
        if (level > depth) {
            log.info "${' ' * level}- skipping ${dataClasses*.name} as the level is already $level (max. depth is $depth)"
            return
        }
        for (DataClass dataClassForDetail in dataClasses) {
            buildSheets(dataClassForDetail, null, previousVersionElementForDiff, ImmutableMultimap.of(), workbook, sheet, false, level, processed)
        }
    }

    protected void buildDataClassesDetailsWithRelationships(Iterable<Relationship> relationships, CatalogueElement previousVersionElementForDiff, Multimap<String, Diff> parentDiffs, WorkbookDefinition workbook, SheetDefinition sheet, boolean terminal, int level = 0, Set<Long> processed = new HashSet<Long>()) {
        if (level > depth) {
            log.info "${' ' * level}- skipping ${relationships*.destination*.name} as the level is already $level (max. depth is $depth)"
            return
        }
        for (Relationship relationship in relationships) {
            buildSheets(relationship.destination as DataClass, relationship, nextElementToDiff(previousVersionElementForDiff, relationship), parentDiffs, workbook, sheet, terminal, level, processed)
        }
    }

    private static CatalogueElement nextElementToDiff(CatalogueElement previousVersionElementForDiff, Relationship processedRelationship) {
        if (previousVersionElementForDiff && processedRelationship.source.dataModel != processedRelationship.destination.dataModel) {
            String key = Diff.keyForRelationship(processedRelationship)
            CatalogueElement other = findOther(processedRelationship.source, previousVersionElementForDiff)
            if (other) {
                Relationship previous = other.outgoingRelationships.find { Diff.keyForRelationship(it) == key }
                return (previous ?: processedRelationship).destination.dataModel
            }
        }
        return previousVersionElementForDiff
    }

    private void buildSheets(DataClass dataClassForDetail, Relationship relationship, CatalogueElement previousVersionElementForDiff, Multimap<String, Diff> parentDiffs, WorkbookDefinition workbook, SheetDefinition sheet, boolean terminal, int level = 0, Set<Long> processed = new HashSet<Long>()) {

        if (dataClassForDetail.id in processed) {
            log.info "${' ' * level}- skipping ${dataClassForDetail.name} as it is already processed"
            return
        }

        processed << dataClassForDetail.id
        log.info "Exporting detail for Data Class ${dataClassForDetail.name} (${dataClassForDetail.combinedVersion})"

        ImmutableMultimap<String, Diff> diffs = collectDiffs(dataClassForDetail, previousVersionElementForDiff, relationship?.source)

        if (isSkipExport(dataClassForDetail)) {
            log.info "${' ' * level}- skipped as ${Metadata.SKIP_EXPORT} evaluates to true"
            if (!terminal) {
                buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, previousVersionElementForDiff, diffs, workbook, sheet, false, level + 1, processed)
//                buildDataClassesDetailsWithRelationships(findDeleted(dataClassForDetail, diffs, RelationshipType.hierarchyType), previousVersionElementForDiff, diffs, workbook, sheet, true, level + 1, processed)
            }
            return
        }



        // always render on the new sheet
        if (isSubsection(dataClassForDetail, relationship)) {
            inSubsection = false
            if (dataClassForDetail.id in sheetsPrinted) {
                return
            }
            sheetsPrinted << dataClassForDetail.id
            workbook.sheet(getSafeSheetName(dataClassForDetail)) { SheetDefinition s ->
                buildBackToContentLink(s)
                log.info "${' ' * level}- printing ${dataClassForDetail.name} on new sheet"
                buildDataClassDetail(s, dataClassForDetail, relationship, previousVersionElementForDiff, diffs, parentDiffs)

                if (!terminal) {
                    buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, previousVersionElementForDiff, diffs, workbook, s, false, level + 1, new HashSet<Long>([dataClassForDetail.id]))
                    buildDataClassesDetailsWithRelationships(findDeleted(dataClassForDetail, diffs, RelationshipType.hierarchyType), previousVersionElementForDiff, diffs, workbook, s, true, level + 1, new HashSet<Long>([dataClassForDetail.id]))
                }

                row()
                buildBackToContentLink(s)
            }
            return
        }

        // inside subsection use existing sheet
        if (sheet) {
            log.info "${' ' * level}- printing ${dataClassForDetail.name} on existing sheet"
            inSubsection = true
            buildDataClassDetail(sheet, dataClassForDetail, relationship, previousVersionElementForDiff, diffs, parentDiffs)
            if (!terminal) {
                buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, previousVersionElementForDiff, diffs, workbook, sheet, false, level + 1, processed)
                buildDataClassesDetailsWithRelationships(findDeleted(dataClassForDetail, diffs, RelationshipType.hierarchyType), previousVersionElementForDiff, diffs, workbook, sheet, true, level + 1, processed)
            }
            return
        }

        if (dataClassForDetail.id in sheetsPrinted) {
            return
        }

        sheetsPrinted << dataClassForDetail.id

        // top level sheet
        workbook.sheet(getSafeSheetName(dataClassForDetail)) { SheetDefinition s ->
            buildBackToContentLink(s)
            log.info "${' ' * level}- printing ${dataClassForDetail.name} on new sheet"
            inSubsection = false
            buildDataClassDetail(s, dataClassForDetail, relationship, previousVersionElementForDiff, diffs, parentDiffs)
            // force top level sheets for children
            if (!terminal) {
                buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, previousVersionElementForDiff, diffs, workbook, null, false, level + 1, processed)
                buildDataClassesDetailsWithRelationships(findDeleted(dataClassForDetail, diffs, RelationshipType.hierarchyType), previousVersionElementForDiff, diffs, workbook, null, true, level + 1, processed)
            }

            row()
            buildBackToContentLink(s)
        }
    }

    static <T extends CatalogueElement> T findOther(T catalogueElement, CatalogueElement topLevelOtherElement) {
        // FIXME: won't work outside data model!!!
        if (!topLevelOtherElement) {
            return null
        }

        // returning null prevents unnecessary the comparison
        if (topLevelOtherElement == catalogueElement || catalogueElement.dataModel == topLevelOtherElement) {
            return null
        }

        DataModel otherDataModel

        if (HibernateHelper.getEntityClass(topLevelOtherElement) == DataModel) {
            otherDataModel = (DataModel) topLevelOtherElement
        } else {
            otherDataModel = topLevelOtherElement.dataModel
        }

        if (!otherDataModel) {
            return null
        }

        T result = (T) CatalogueElement.findByLatestVersionIdAndDataModel(catalogueElement.latestVersionId ?: catalogueElement.id, otherDataModel)

        if (result) {
            return result
        }

        return null
    }

    private static String getSafeSheetName(DataClass dataClassForDetail) {
        getSafeName("${getModelCatalogueIdToPrint(dataClassForDetail)} ${normalizeDataClassName(dataClassForDetail)}")
    }

    private static String getSafeName(String ref) {
        ref.replaceAll(/[^\p{Alnum}\\_]/, '_')
    }

    private static buildBackToContentLink(SheetDefinition sheet) {
        sheet.row {
            cell {
                value '<< Back to Content'
                link to name DATA_CLASSES
                style 'note'
                colspan 7
            }
        }
    }

    private static boolean isSubsection(DataClass dataClassForDetail, Relationship relationship = null) {
        relationship?.ext?.get(Metadata.SUBSECTION) ?: dataClassForDetail.ext[Metadata.SUBSECTION]
    }

    protected static boolean isSkipExport(DataClass dataClassForDetail) {
        dataClassForDetail.ext.get(Metadata.SKIP_EXPORT)=="true"
    }

    protected static String normalizeDataClassName(DataClass dataClassForDetail) {
        "${dataClassForDetail.name}".replace("'", '')
    }


    private void buildDataClassDetail(SheetDefinition sheet, DataClass dataClass, Relationship relationship, CatalogueElement previousVersionElementForDiff, Multimap<String, Diff> diffs, Multimap<String, Diff> parentDiffs) {

        sheet.with {
            row {
                cell {
                    width 10
                }
                cell {
                    width 10
                }
                cell {
                    width 30
                }
                cell {
                    width 30
                }
                cell {
                    width 10
                }
                cell {
                    width 25
                }
                cell {
                    width 75
                }
                cell {
                    width 100
                }
                cell {
                    width 100
                }
            }


            row {
                cell {
                    value "$dataClass.name"

                    String ref = getRef(sheet, dataClass)

                    if (!(ref in namesPrinted)) {
                        name getSafeName(ref)
                        namesPrinted << ref
                    }
                    styles withChangesHighlight('h1', ImmutableMultimap.builder().putAll(parentDiffs).putAll(diffs).build(), Diff.keyForRelationship(relationship), Diff.keyForSelf(relationship?.destination?.latestVersionId ?: relationship?.destination?.id))
                    colspan 7
                }
            }

            row {
                cell {
                    value 'Path'
                    style 'inner-table-header'
                    colspan 2
                }
                cell {
                    value "${(inSubsection)?(relationship?.source?.name)? "$relationship.source.name -> $dataClass.name" : "$dataClass.name": ""}"
                    style 'description'
                    colspan 3
                }
                cell {
                    value 'Multiplicity'
                    style 'inner-table-header'
                }
                cell {
                    value "${ (!inSubsection) ? "See Content Tab" : getMultiplicity(relationship) }"
                    style 'description'
                }

            }

// I don't think we need this but I'm going to leave it in whilst we test it with users
//            row {
//                cell {
//                    value 'Data Model'
//                    style 'property-title'
//                    colspan 2
//                }
//                cell {
//                    value dataClass.dataModel?.name
//                    style 'property-value'
//                    colspan 5
//                }
//            }

                row {
                    cell {
                        value 'Children'
                        style 'inner-table-header'
                        colspan 2
                        height 50
                    }
                    cell {
                        value "${(dataClass.parentOf.size > 0) ? (getIfChoiceText(dataClass)) ? "${getIfChoiceText(dataClass)} \r ${dataClass.parentOf.collect { it.name }.join(", \r ")} " : "${dataClass.parentOf.collect { it.name }.join(", \r ")} " : "None"}"
                        style 'description'
                        colspan 3
                    }

                    cell {
                        value 'Link'
                        style 'inner-table-header'
                    }

                    cell {
                        value "${dataClass.defaultModelCatalogueId.split("/catalogue")[0] + "/load?" + dataClass.defaultModelCatalogueId}"
                        style 'description'
                       // link to url "${dataClass.defaultModelCatalogueId.split("/catalogue")[0] + "/load?" + dataClass.defaultModelCatalogueId}"
                    }



                }


             if (dataClass?.description && dataClass?.description.matches(".*\\w.*") || getIfChoiceText(dataClass)) {
                 row {
                    cell {
                        value (getIfChoiceText(dataClass))? "$dataClass.description \r ${getIfChoiceText(dataClass)}" : dataClass.description
                        height 100
                        styles 'description'
                        colspan 7
                    }
                }
            }

            if (dataClass.countContains()) {

                buildContainedElements(it, dataClass, previousVersionElementForDiff, diffs)
            }
        }
    }

    private static String getRef(SheetDefinition sheet, DataClass dataClass) {
        if (sheet instanceof Sheet) {
            return "${sheet.name}_${dataClass.id}"
        }
        throw new IllegalArgumentException("Please, provide a readable sheet")
    }
    private static String getRef(DataClass sheetOwner, DataClass dataClass) {
        "${getSafeSheetName(sheetOwner)}_${dataClass.id}"
    }


    private Map<String,ImmutableMultimap<String, Diff>> computedDiffs = [:]

    private ImmutableMultimap<String, Diff> collectDiffs(CatalogueElement element, CatalogueElement previousVersionElementForDiff, DataClass parentClass){
        collectDiffs( element,  previousVersionElementForDiff,  parentClass, null)
    }

    private ImmutableMultimap<String, Diff> collectDiffs(CatalogueElement element, CatalogueElement previousVersionElementForDiff, DataClass parentClass, DataElement parentElement) {


        if (!element || !previousVersionElementForDiff) {
            return ImmutableMultimap.of()
        }

        String key = "$element.id=>$previousVersionElementForDiff.id"

        if (computedDiffs.containsKey(key)) {
            return computedDiffs[key]
        }

        Multimap<String, Diff> diffs = ImmutableMultimap.of()
        CatalogueElement other = findOther(element, previousVersionElementForDiff)

        if (previousVersionElementForDiff) {
            diffs = catalogueElementDiffs.differentiate(element, other, parentClass, parentElement)
        }

        computedDiffs[key] = diffs

        diffs
    }

    private buildContainedElements(SheetDefinition sheet, DataClass dataClass, CatalogueElement previousVersionElementForDiff, Multimap<String, Diff> dataClassDiffs) {
        sheet.with {
            row {
                cell {
                    value "${(dataClass.ext.get("http://xsd.modelcatalogue.org/section#type") == "choice")? "Choice of Data Elements" : "Contained Data Elements" }"
                    style 'h2'
                    colspan 7
                }
            }
            row {
                cell {
                    value 'DE ID'
                    style 'inner-table-header'
                }

                cell {
                    value 'Status'
                    style 'inner-table-header'
                }

                cell {
                    value 'Data Element'
                    style 'inner-table-header'
                    colspan 2
                }

                cell {
                    value 'Multiplicity'
                    style 'inner-table-header'
                }

                cell {
                    value 'Data Type'
                    style 'inner-table-header'
                    colspan 2
                }

                cell {
                    value 'Measurement Unit'
                    style 'inner-table-header'
                }

                cell {
                    value 'Referenced Data Class'
                    style 'inner-table-header'
                }
            }

            int i
            for (Relationship containsRelationship in dataClass.containsRelationships) {
                buildDataElement(sheet, containsRelationship, previousVersionElementForDiff, dataClassDiffs)
                if(i++ != dataClass.containsRelationships.size()-1) {
                    if (dataClass.ext.get("http://xsd.modelcatalogue.org/section#type") == "choice") {
                        row {
                            cell {
                                value 'OR'
                                style 'inner-table-header'
                                colspan 7
                                height 50
                            }
                        }
                    }
                }
            }
            for (Relationship deleted in findDeleted(dataClass, dataClassDiffs, RelationshipType.containmentType)) {
                buildDataElement(sheet, deleted, previousVersionElementForDiff, dataClassDiffs)
            }
        }
    }

    private static List<Relationship> findDeleted(DataClass dataClass, Multimap<String, Diff> diffs, RelationshipType type) {
        diffs.values().findAll {
            it.relationshipChange && it.selfMissing && it.key.startsWith("rel:${dataClass.latestVersionId ?: dataClass.id}=[${type.name}]=")
        }.collect { it.otherValue as Relationship }
    }

    private void buildDataElement(SheetDefinition sheet, Relationship containsRelationship, CatalogueElement previousVersionElementForDiff, Multimap<String, Diff> dataClassDiffs) {

        DataClass dataClass = containsRelationship.source as DataClass
        DataElement element = containsRelationship.destination as DataElement
        DataType dataType = element.dataType

        ImmutableMultimap<String, Diff> dataElementDiffs = collectDiffs(element, previousVersionElementForDiff, dataClass)
        ImmutableMultimap<String, Diff> dataTypeDiffs = collectDiffs(element.dataType, previousVersionElementForDiff, dataClass, element)
        Collection<Diff> missingEnums = dataTypeDiffs.values().findAll { it.enumerationChange && it.selfMissing }

        ListWithTotalAndType<DataType> typeHierarchy = dataType ? ElementService.getTypeHierarchy([:], dataType) : Lists.emptyListWithTotalAndType(DataType)

        if (!dataType && dataElementDiffs.get('dataType')) {
            Diff removedDataType = dataElementDiffs.get('dataType').find { it.selfMissing }
            if (removedDataType) {
                dataType = removedDataType.otherValue as DataType
            }
        }

        MeasurementUnit measurementUnit = dataType?.instanceOf(PrimitiveType) ? dataType.measurementUnit : null

        if (!measurementUnit && dataTypeDiffs.get('measurementUnit')) {
            Diff removedUnit = dataElementDiffs.get('measurementUnit').find { it.selfMissing }
            if (removedUnit) {
                measurementUnit = removedUnit.otherValue as MeasurementUnit
            }
        }

        DataClass referencedClass = dataType?.instanceOf(ReferenceType) ? dataType.dataClass : null

        if (!referencedClass && dataTypeDiffs.get('dataClass')) {
            Diff removedClass = dataElementDiffs.get('dataClass').find { it.selfMissing }
            if (removedClass) {
                referencedClass = removedClass.otherValue as DataClass
            }
        }

        sheet.row {
            cell {
                value getModelCatalogueIdToPrint(element)
                styles withChangesHighlight('data-element-bottom-right', dataClassDiffs, Diff.keyForRelationship(containsRelationship), Diff.keyForSelf(containsRelationship?.destination?.latestVersionId ?: containsRelationship?.destination?.id))
            }
            cell {
                value element.status
                styles withChangesHighlight('data-element-center-center', dataClassDiffs, Diff.keyForRelationship(containsRelationship), Diff.keyForSelf(containsRelationship?.destination?.latestVersionId ?: containsRelationship?.destination?.id))
            }
            cell {
                value element.name
                styles withChangesHighlight('data-element', dataClassDiffs, Diff.keyForRelationship(containsRelationship), Diff.keyForSelf(containsRelationship?.destination?.latestVersionId ?: containsRelationship?.destination?.id))
                colspan 2
            }
            cell {
                value getMultiplicity(containsRelationship)
                styles withChangesHighlight('data-element-top-right', dataClassDiffs, Diff.keyForRelationshipExtension(containsRelationship, Metadata.MIN_OCCURS), Diff.keyForRelationshipExtension(containsRelationship, Metadata.MAX_OCCURS), Diff.keyForRelationship(containsRelationship), Diff.keyForSelf(containsRelationship?.destination?.latestVersionId ?: containsRelationship?.destination?.id))
            }

            if (dataType) {
                cell {
                    value dataType.name
                    styles Iterables.concat(withChangesHighlight('data-element', dataElementDiffs, 'dataType'), withChangesHighlight(null, dataClassDiffs, Diff.keyForRelationship(containsRelationship), Diff.keyForSelf(containsRelationship?.destination?.latestVersionId ?: containsRelationship?.destination?.id)))
                    colspan 2
                    // FIXME: put the comment back as soon as following is resolved
                    // https://github.com/MetadataConsulting/spreadsheet-builder/issues/15
                    // comment dataType.dataModelSemanticVersion
                }

                if (measurementUnit) {
                    cell {
                        value measurementUnit.name
                        styles Iterables.concat(withChangesHighlight('data-element', dataTypeDiffs, 'measurementUnit'), withChangesHighlight(null, dataElementDiffs, 'dataType'), withChangesHighlight(null, dataClassDiffs, Diff.keyForRelationship(containsRelationship), Diff.keyForSelf(containsRelationship?.destination?.latestVersionId ?: containsRelationship?.destination?.id)))
                    }
                } else {
                    cell()
                }

                if (referencedClass) {
                    cell {
                        value referencedClass.name
                        styles Iterables.concat(withChangesHighlight('data-element', dataTypeDiffs, 'dataClass'), withChangesHighlight(null, dataElementDiffs, 'dataType'), withChangesHighlight(null, dataClassDiffs, Diff.keyForRelationship(containsRelationship), Diff.keyForSelf(containsRelationship?.destination?.latestVersionId ?: containsRelationship?.destination?.id)))
                    }
                } else {
                    cell()
                }
            } else {
                3.times { cell() }
            }
        }

        sheet.row {
            style 'data-element-description-row'

            cell('C') {
                value element.description
                colspan 3
                int desiredRowSpan = getRowSpanForDataTypeDetails(dataType, typeHierarchy, missingEnums)
                if (desiredRowSpan > 1) {
                    rowspan desiredRowSpan
                }
            }

            if (dataType) {
                cell(DATA_TYPE_FIRST_COLUMN) { CellDefinition theCell ->
                    if (dataType.description) {
                        text dataType.description
                    }
                    colspan 2
                }

                if (measurementUnit) {
                    cell('H') {
                        value measurementUnit.description
                    }
                }

                if (referencedClass) {
                    cell('I') {
                        value referencedClass.description
                    }
                }

            }

        }

        if (HibernateHelper.getEntityClass(dataType) == EnumeratedType && dataType.enumerations) {
            sheet.row {
                cell("F") {
                    text 'Enumerations', {
                        size 12
                        make bold
                    }
                    colspan 2
                }
            }
            Enumerations enumerations = dataType.enumerationsObject
            for (Enumeration entry in enumerations) {
                printEnumeration(sheet, entry, dataTypeDiffs)
            }

            for (Diff diff in missingEnums) {
                printEnumeration(sheet, diff.otherValue as Enumeration, dataTypeDiffs)
            }

        }

        if (typeHierarchy.items.any { it?.rule } || dataType?.rule) {
            sheet.row {
                cell("F") {
                    text 'Rules', {
                        size 12
                        make bold
                    }
                    colspan 2
                }
            }

            for (DataType type in [dataType] + typeHierarchy.items) {
                ImmutableMultimap<String, Diff> dataTypeWithRuleDiffs = collectDiffs(type, previousVersionElementForDiff, dataClass)

                if (type.rule || dataTypeWithRuleDiffs.containsKey('rule')) {
                    sheet.row {
                        cell(DATA_TYPE_FIRST_COLUMN) {
                            text type.name, {
                                make bold
                            }
                        }
                        cell { CellDefinition cell ->
                            text(type?.rule ?: (dataTypeWithRuleDiffs.get('rule') ? dataTypeWithRuleDiffs.get('rule').first().otherValue?.toString() : null))
                            styles withChangesHighlight(ModelCatalogueStyles.DESCRIPTION, dataTypeWithRuleDiffs, 'rule')
                        }
                    }
                }
            }
        }

        if (element.ext && printMetadata) {
            for (Map.Entry<String, String> entry in element.ext) {
                sheet.row {
                    cell('C') {
                        value entry.key
                        style 'metadata-key'
                    }
                    cell {
                        value entry.value
                        style 'metadata-value'
                        colspan 2
                    }
                }
            }
        }
    }

    private static printEnumeration(SheetDefinition sheet, Enumeration entry, ImmutableMultimap<String, Diff> dataTypeDiffs) {
        sheet.row {
            cell(DATA_TYPE_FIRST_COLUMN) { CellDefinition cell ->
                text entry.key, {
                    make bold
                    if (entry.deprecated) {
                        make italic
                        color lightGray
                    }
                }
                styles withChangesHighlight(null, dataTypeDiffs, Diff.keyForEnumeration(entry.id))
            }
            cell { CellDefinition cell ->
                text entry.value, {
                    if (entry.deprecated) {
                        make italic
                        color lightGray
                    }
                }
                styles withChangesHighlight(ModelCatalogueStyles.DESCRIPTION, dataTypeDiffs, Diff.keyForEnumeration(entry.id))
            }
        }
    }

    private static Iterable<String> withChangesHighlight(String style, Multimap<String, Diff> diffs, String... diffKeys) {
        ImmutableList.Builder<String> ret = ImmutableList.builder()

        if (style) {
            ret.add(style)
        }

        if (!diffKeys || !diffs) {
            return ret.build()
        }

        Collection<Diff> interestingDiffs = diffKeys.collect { diffs.get(it) }.flatten() as Collection<Diff>

        if (!interestingDiffs) {
            return ret.build()
        }

        if (interestingDiffs.any { it.selfMissing } ) {
            ret.add(CHANGE_REMOVAL)
        } else if (interestingDiffs.any { it.update } ) {
            ret.add(CHANGE_UPDATE)
        } else if (interestingDiffs.any { it.otherMissing } ) {
            ret.add(CHANGE_NEW)
        }

        ret.build()
    }

    private static Iterable<String> withDiffStyles(Diff diff, String... otherStyles) {
        ImmutableList.Builder<String> ret = ImmutableList.builder()

        ret.add(otherStyles)

        if (diff.isOtherMissing()) {
            ret.add CHANGE_NEW
        } else if (diff.isSelfMissing()){
            ret.add CHANGE_REMOVAL
        } else {
            ret.add CHANGE_UPDATE
        }

        return ret.build()
    }

    protected static int getRowSpanForDataTypeDetails(DataType dataType, ListWithTotalAndType<DataType> typeHierarchy, Collection<Diff> missingEnums) {
        if (!dataType) {
            return 1
        }
        int rowspan = 1

        if (HibernateHelper.getEntityClass(dataType) == EnumeratedType) {
            int enumerationSize = dataType.enumerations.size()
            if (enumerationSize > 0) {
                rowspan += 1 + enumerationSize
            }
        }

        int ruleCount = typeHierarchy.items.count { it?.rule }
        if (ruleCount > 0 || dataType?.rule) {
            rowspan += 1 + ruleCount
            if (dataType?.rule) {
                rowspan += 1
            }
        }

        if (missingEnums) {
            rowspan += missingEnums.size()
        }

        return rowspan
    }

    private static String getMultiplicity(Relationship relationship) {
        if (!relationship) {
            return ''
        }
        String min = relationship.ext[Metadata.MIN_OCCURS] ?: '0'
        String max = relationship.ext[Metadata.MAX_OCCURS] ?: '*'

        if (max.toLowerCase() in ['unbounded', '' + Integer.MAX_VALUE]) {
            max = '*'
        }

        return "${min}..${max}"
    }

    protected void buildOutline(SheetDefinition sheet, List<DataClass> dataClasses, CatalogueElement previousVersionElementForDiff) {
        log.info "Printing outline"
        sheet.with {
            row {
                cell {
                    value "Data Classes"
                    style  H2
                    colspan 3
                    name DATA_CLASSES
                }
            }

            row {
                cell {
                    value 'ID'
                    style 'inner-table-header'
                    width 10
                }
                cell {
                    value 'Name'
                    style 'inner-table-header'
                    width 60
                }
                cell {
                    value 'Multiplicty'
                    style 'inner-table-header'
                    width 10
                }
            }

            buildDataClassesOutline(it, dataClasses, previousVersionElementForDiff)

            // footer
            row()
            row {
                cell {
                    value 'Click the data class cell to show the detail'
                    style 'note'
                    colspan 3
                }
            }
        }
    }

    private void buildDataClassesOutline(SheetDefinition sheet, List<DataClass> dataClasses, CatalogueElement previousVersionElementForDiff) {


        //check if the previous data model had additional top level classes so they can be included in the outline
        def previousVersionDataClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.includes(previousVersionElementForDiff as DataModel), [:], true).items
        def previousVersionDataClassesChanged = []
        previousVersionDataClasses.each{ DataClass previousVersionDataClass->
            //if the data class was in the previous version of the data model and isn't in the new one, then delete it
            if(!dataClasses.find{it.latestVersionId==previousVersionDataClass.latestVersionId}) previousVersionDataClassesChanged.add(previousVersionDataClass)
        }


        //iterate through the changes for the current version classes
        dataClasses.each { DataClass dataClass ->
            ImmutableMultimap<String, Diff> topLevelDiffs = ImmutableMultimap.builder().putAll(catalogueElementDiffs.differentiateTopLevelClasses(dataClass, findOther(dataClass, previousVersionElementForDiff))).build()
            buildChildOutline(sheet, dataClass, dataClass, null, previousVersionElementForDiff, 1, topLevelDiffs)
        }

        //iterate through any changes from the previous version
        previousVersionDataClassesChanged.each { DataClass dataClass ->
            //note findOther and data class have been switched - this is because we are doing the comparison the other way round i.e. what was in the previous and isn't in this one
            ImmutableMultimap<String, Diff> topLevelDiffs = ImmutableMultimap.builder().putAll(catalogueElementDiffs.differentiateTopLevelClasses(null, dataClass)).build()

            //add the top level "deleted diffs" to the set of computed diffs i.e. the changes that appear in the changes sheet

            String key = "$dataClass.id=>$previousVersionElementForDiff.id"

            if (!computedDiffs.containsKey(key)) {
                computedDiffs[key] = topLevelDiffs
            }

            buildChildOutline(sheet, dataClass, dataClass, null, null, 1, topLevelDiffs)
        }
    }

    protected void buildChildOutline(SheetDefinition sheet, DataClass dataClass, DataClass sheetOwner, Relationship relationship, CatalogueElement previousVersionElementForDiff, int level, Multimap<String, Diff> parentDiffs, boolean terminal = false) {

        String[] relDiffKeys = new String[0]
        String[] multiplicityRelDiffKeys = new String[0]

        if (relationship) {
            relDiffKeys = [Diff.keyForRelationship(relationship), Diff.keyForSelf(relationship.destination.latestVersionId ?: relationship.destination.id)] as String[]
            multiplicityRelDiffKeys = [
                Diff.keyForRelationship(relationship),
                Diff.keyForSelf(relationship.destination.latestVersionId ?: relationship.destination.id),
                Diff.keyForRelationshipExtension(relationship, Metadata.MIN_OCCURS),
                Diff.keyForRelationshipExtension(relationship, Metadata.MAX_OCCURS),
            ] as String[]

            //else if this is a top level class - so we won't have a relationship but we do want to take the parent diffs
            //i.e. if it is a top level class that has been added - or a top level class that has been removed

        }else if(parentDiffs && level == 1) {
            relDiffKeys = parentDiffs.asMap().keySet().toArray(new String[parentDiffs.size()])
        }


        ImmutableMultimap<String, Diff> allDiffs = ImmutableMultimap.builder().putAll(parentDiffs).putAll(catalogueElementDiffs.differentiate(dataClass, findOther(dataClass, previousVersionElementForDiff))).build()

        sheet.row {
            String ref = getRef(sheetOwner, dataClass)

            cell {
                value getModelCatalogueIdToPrint(dataClass)
                styles withChangesHighlight(ModelCatalogueStyles.BOTTOM_RIGHT, allDiffs, relDiffKeys)
                if (ref in namesPrinted) {
                    link to name getSafeName(ref)
                }
            }
            cell {
                value dataClass.name
                if (ref in namesPrinted) {
                    link to name getSafeName(ref)
                }
                style {
                    indent (level * 2)
                }
                styles withChangesHighlight(null, allDiffs, relDiffKeys)
            }
            cell {
                value getMultiplicity(relationship)
                if (ref in namesPrinted) {
                    link to name getSafeName(ref)
                }
                styles withChangesHighlight(ModelCatalogueStyles.CENTER_RIGHT, allDiffs, multiplicityRelDiffKeys)
            }
        }

        if (level > depth) {
            dataClassesProcessedInOutline.put(dataClass.getId(), dataClass)
            return
        }

        // I don't believe this code is needed
        //it's quite nice to be able to see the subsections with a path

//        if (dataClass.getId() in dataClassesProcessedInOutline.keySet()) {
//            return
//        }

        dataClassesProcessedInOutline.put(dataClass.getId(), dataClass)

        Multimap<String, Diff> diffs = collectDiffs(dataClass, previousVersionElementForDiff, null)
        List<Relationship> children = findDeleted(dataClass, diffs, RelationshipType.hierarchyType)

        if (!terminal && children.size() + dataClass.countParentOf() > 0) {
            sheet.group {
                for (Relationship child in dataClass.parentOfRelationships) {
                    buildChildOutline(sheet, child.destination as DataClass, nextSheetOwner(child, relationship, dataClass, sheetOwner), child, nextElementToDiff(previousVersionElementForDiff, child), level + 1, diffs)
                }
                for (Relationship child in children) {
                    buildChildOutline(sheet, child.destination as DataClass, nextSheetOwner(child, relationship, dataClass, sheetOwner), child, nextElementToDiff(previousVersionElementForDiff, child), level + 1, diffs, true)
                }
            }
        }
    }

    private static DataClass nextSheetOwner(Relationship child, Relationship relationship, DataClass dataClass, DataClass sheetOwner) {

        return dataClass == sheetOwner ? child.destination as DataClass : sheetOwner
    }

    private static humanReadableValue(Object o) {
        if (!o) {
            return ' '
        }
        if (o instanceof CharSequence) {
            return o.toString()
        }

        if (o instanceof Enumeration) {
            return o.value
        }

        Class type = HibernateHelper.getEntityClass(o)
        if (CatalogueElement.isAssignableFrom(type)) {
            CatalogueElement element = o as CatalogueElement
            return element.name
        }
        if (Relationship.isAssignableFrom(type)) {
            Relationship rel = o as Relationship
            return rel.destination.name
        }

        return String.valueOf(o)
    }

    private static String getIfChoiceText(DataClass dataClass){
        if (dataClass.ext.get("http://xsd.modelcatalogue.org/section#type") == "choice") {
            // this is set to determine if the child classes are choices i.e. one or the other of the children
            return  "A choice of one of the following can be submitted together with each $dataClass.name report:"
        }
        return ""
    }
}
