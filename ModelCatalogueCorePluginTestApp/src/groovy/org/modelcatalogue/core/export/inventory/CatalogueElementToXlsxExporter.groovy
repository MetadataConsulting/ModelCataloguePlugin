package org.modelcatalogue.core.export.inventory

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

import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.CHANGE_NEW
import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.CHANGE_REMOVAL
import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.CHANGE_UPDATE
import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H1
import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H2

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
    final Long elementForDiffId
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

        this.elementForDiffId = element.findPreviousVersion()?.id

        this.printMetadata = grailsApplication.config.mc.export.xlsx.printMetadata ?: false
        this.catalogueElementDiffs = new CatalogueElementDiffs(grailsApplication)
    }

    protected static String getModelCatalogueIdToPrint(CatalogueElement element) {
        element.hasModelCatalogueId() && !element.modelCatalogueId.startsWith('http') ? element.modelCatalogueId : element.combinedVersion
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

        CatalogueElement element = CatalogueElement.get(elementId)
        CatalogueElement elementForDiff = elementForDiffId ? CatalogueElement.get(elementForDiffId) : null

        List<DataClass> dataClasses = Collections.emptyList()

        if (HibernateHelper.getEntityClass(element) == DataClass) {
            dataClasses = [element as DataClass]
        } else if (HibernateHelper.getEntityClass(element) == DataModel) {
            dataClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.includes(element as DataModel), ImmutableMap.of('status', 'active'), true).items
        }

        log.info "Exporting Data Class ${element.name} (${element.combinedVersion}) to inventory spreadsheet."

        SpreadsheetBuilder builder = new PoiSpreadsheetBuilder()
        builder.build(outputStream) { WorkbookDefinition workbook ->
            apply ModelCatalogueStyles

            sheet("Introduction") { SheetDefinition sheet ->
                buildIntroduction(sheet, element)
            }

            sheet(CONTENT) { }

            sheet('Changes') { }

            buildDataClassesDetails(dataClasses, elementForDiff, workbook, null)

            sheet(CONTENT) { SheetDefinition sheet ->
                buildOutline(sheet, dataClasses, elementForDiff)
            }

            log.info "Printing all changes summary."

            sheet('Changes') {
                row {
                    cell {
                        style H1
                        value 'Changes Summary'
                        colspan 6
                }   }
                row {
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
                log.info "Sorting changes"
                Iterable<Diff> allDiffs = Iterables.concat(computedDiffs.values().collect { it.values() }).sort { a, b ->
                    int byName = a.element.name <=> b.element.name
                    if (byName != 0) {
                        return byName
                    }
                    return a.changeDescription <=> b.changeDescription
                }
                log.info "Printing ${allDiffs.size()} changes"
                for (Diff diff in allDiffs) {
                    row {
                        cell {
                            value getModelCatalogueIdToPrint(diff.element)
                            styles withDiffStyles(diff, ModelCatalogueStyles.CENTER_LEFT)
                        }
                        cell {
                            value GrailsNameUtils.getNaturalName(HibernateHelper.getEntityClass(diff.element).simpleName)
                            styles withDiffStyles(diff, ModelCatalogueStyles.CENTER_LEFT)
                        }
                        cell {
                            value diff.element.name
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

        }

        log.info "Exported ${GrailsNameUtils.getNaturalName(HibernateHelper.getEntityClass(element).simpleName)} ${element.name} (${element.combinedVersion}) to inventory spreadsheet."

    }

    protected void buildDataClassesDetails(Iterable<DataClass> dataClasses, CatalogueElement elementForDiff, WorkbookDefinition workbook, SheetDefinition sheet, int level = 0, Set<Long> processed = new HashSet<Long>()) {
        if (level > depth) {
            log.info "${' ' * level}- skipping ${dataClasses*.name} as the level is already $level (max. depth is $depth)"
            return
        }
        for (DataClass dataClassForDetail in dataClasses) {
            buildSheets(dataClassForDetail, null, elementForDiff, ImmutableMultimap.of(), workbook, sheet, false, level, processed)
        }
    }

    protected void buildDataClassesDetailsWithRelationships(Iterable<Relationship> relationships, CatalogueElement elementForDiff, Multimap<String, Diff> parentDiffs, WorkbookDefinition workbook, SheetDefinition sheet, boolean terminal, int level = 0, Set<Long> processed = new HashSet<Long>()) {
        if (level > depth) {
            log.info "${' ' * level}- skipping ${relationships*.destination*.name} as the level is already $level (max. depth is $depth)"
            return
        }
        for (Relationship relationship in relationships) {
            buildSheets(relationship.destination as DataClass, relationship, nextElementToDiff(elementForDiff, relationship), parentDiffs, workbook, sheet, terminal, level, processed)
        }
    }

    private static CatalogueElement nextElementToDiff(CatalogueElement elementForDiff, Relationship processedRelationship) {
        if (elementForDiff && processedRelationship.source.dataModel != processedRelationship.destination.dataModel) {
            String key = Diff.keyForRelationship(processedRelationship)
            CatalogueElement other = findOther(processedRelationship.source, elementForDiff)
            if (other) {
                Relationship previous = other.outgoingRelationships.find { Diff.keyForRelationship(it) == key }
                return (previous ?: processedRelationship).destination.dataModel
            }
        }
        return elementForDiff
    }

    private void buildSheets(DataClass dataClassForDetail, Relationship relationship, CatalogueElement elementForDiff, Multimap<String, Diff> parentDiffs, WorkbookDefinition workbook, SheetDefinition sheet, boolean terminal, int level = 0, Set<Long> processed = new HashSet<Long>()) {

        if (dataClassForDetail.id in processed) {
            log.info "${' ' * level}- skipping ${dataClassForDetail.name} as it is already processed"
            return
        }

        processed << dataClassForDetail.id
        log.info "Exporting detail for Data Class ${dataClassForDetail.name} (${dataClassForDetail.combinedVersion})"

        ImmutableMultimap<String, Diff> diffs = collectDiffs(dataClassForDetail, elementForDiff)

        if (isSkipExport(dataClassForDetail)) {
            log.info "${' ' * level}- skipped as ${Metadata.SKIP_EXPORT} evaluates to true"
            if (!terminal) {
                buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, elementForDiff, diffs, workbook, sheet, false, level + 1, processed)
                buildDataClassesDetailsWithRelationships(findDeleted(dataClassForDetail, diffs, RelationshipType.hierarchyType), elementForDiff, diffs, workbook, sheet, true, level + 1, processed)
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
                buildDataClassDetail(s, dataClassForDetail, relationship, elementForDiff, diffs, parentDiffs)

                if (!terminal) {
                    buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, elementForDiff, diffs, workbook, s, false, level + 1, new HashSet<Long>([dataClassForDetail.id]))
                    buildDataClassesDetailsWithRelationships(findDeleted(dataClassForDetail, diffs, RelationshipType.hierarchyType), elementForDiff, diffs, workbook, s, true, level + 1, new HashSet<Long>([dataClassForDetail.id]))
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
            buildDataClassDetail(sheet, dataClassForDetail, relationship, elementForDiff, diffs, parentDiffs)
            if (!terminal) {
                buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, elementForDiff, diffs, workbook, sheet, false, level + 1, processed)
                buildDataClassesDetailsWithRelationships(findDeleted(dataClassForDetail, diffs, RelationshipType.hierarchyType), elementForDiff, diffs, workbook, sheet, true, level + 1, processed)
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
            buildDataClassDetail(s, dataClassForDetail, relationship, elementForDiff, diffs, parentDiffs)
            // force top level sheets for children
            if (!terminal) {
                buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, elementForDiff, diffs, workbook, null, false, level + 1, processed)
                buildDataClassesDetailsWithRelationships(findDeleted(dataClassForDetail, diffs, RelationshipType.hierarchyType), elementForDiff, diffs, workbook, null, true, level + 1, processed)
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
        dataClassForDetail.ext[Metadata.SKIP_EXPORT]
    }

    protected static String normalizeDataClassName(DataClass dataClassForDetail) {
        "${dataClassForDetail.name}".replace("'", '')
    }


    private void buildDataClassDetail(SheetDefinition sheet, DataClass dataClass, Relationship relationship, CatalogueElement elementForDiff, Multimap<String, Diff> diffs, Multimap<String, Diff> parentDiffs) {

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
                        link to url "${dataClass.defaultModelCatalogueId.split("/catalogue")[0] + "/load?" + dataClass.defaultModelCatalogueId}"
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

                buildContainedElements(it, dataClass, elementForDiff, diffs)
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

    private ImmutableMultimap<String, Diff> collectDiffs(CatalogueElement element, CatalogueElement elementForDiff) {
        if (!element || !elementForDiff) {
            return ImmutableMultimap.of()
        }

        String key = "$element.id=>$elementForDiff.id"

        if (computedDiffs.containsKey(key)) {
            return computedDiffs[key]
        }

        Multimap<String, Diff> diffs = ImmutableMultimap.of()
        CatalogueElement other = findOther(element, elementForDiff)

        if (elementForDiff) {
            diffs = catalogueElementDiffs.differentiate(element, other)
        }

        computedDiffs[key] = diffs

        diffs
    }

    private buildContainedElements(SheetDefinition sheet, DataClass dataClass, CatalogueElement elementForDiff, Multimap<String, Diff> dataClassDiffs) {
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
                buildDataElement(sheet, containsRelationship, elementForDiff, dataClassDiffs)
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
                buildDataElement(sheet, deleted, elementForDiff, dataClassDiffs)
            }
        }
    }

    private static List<Relationship> findDeleted(DataClass dataClass, Multimap<String, Diff> diffs, RelationshipType type) {
        diffs.values().findAll {
            it.relationshipChange && it.selfMissing && it.key.startsWith("rel:${dataClass.latestVersionId ?: dataClass.id}=[${type.name}]=")
        }.collect { it.otherValue as Relationship }
    }

    private void buildDataElement(SheetDefinition sheet, Relationship containsRelationship, CatalogueElement elementForDiff, Multimap<String, Diff> dataClassDiffs) {

        DataElement element = containsRelationship.destination as DataElement
        DataType dataType = element.dataType

        ImmutableMultimap<String, Diff> dataElementDiffs = collectDiffs(element, elementForDiff)
        ImmutableMultimap<String, Diff> dataTypeDiffs = collectDiffs(element.dataType, elementForDiff)
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
                ImmutableMultimap<String, Diff> dataTypeWithRuleDiffs = collectDiffs(type, elementForDiff)

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

    protected void buildOutline(SheetDefinition sheet, List<DataClass> dataClasses, CatalogueElement elementForDiff) {
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

            buildDataClassesOutline(it, dataClasses, elementForDiff)

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

    private void buildDataClassesOutline(SheetDefinition sheet, List<DataClass> dataClasses, CatalogueElement elementForDiff) {
        dataClasses.each { DataClass dataClass ->
            buildChildOutline(sheet, dataClass, dataClass, null, elementForDiff, 1, ImmutableMultimap.of())
        }
    }

    protected void buildChildOutline(SheetDefinition sheet, DataClass dataClass, DataClass sheetOwner, Relationship relationship, CatalogueElement elementForDiff, int level, Multimap<String, Diff> parentDiffs, boolean terminal = false) {

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
        }


        ImmutableMultimap<String, Diff> allDiffs = ImmutableMultimap.builder().putAll(parentDiffs).putAll(catalogueElementDiffs.differentiate(dataClass, findOther(dataClass, elementForDiff))).build()

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

        Multimap<String, Diff> diffs = collectDiffs(dataClass, elementForDiff)
        List<Relationship> children = findDeleted(dataClass, diffs, RelationshipType.hierarchyType)

        if (!terminal && children.size() + dataClass.countParentOf() > 0) {
            sheet.group {
                for (Relationship child in dataClass.parentOfRelationships) {
                    buildChildOutline(sheet, child.destination as DataClass, nextSheetOwner(child, relationship, dataClass, sheetOwner), child, nextElementToDiff(elementForDiff, child), level + 1, diffs)
                }
                for (Relationship child in children) {
                    buildChildOutline(sheet, child.destination as DataClass, nextSheetOwner(child, relationship, dataClass, sheetOwner), child, nextElementToDiff(elementForDiff, child), level + 1, diffs, true)
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
