package org.modelcatalogue.core.export.inventory

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Iterables
import com.google.common.collect.Multimap
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.builder.spreadsheet.api.Cell
import org.modelcatalogue.builder.spreadsheet.api.Sheet
import org.modelcatalogue.builder.spreadsheet.api.SpreadsheetBuilder
import org.modelcatalogue.builder.spreadsheet.api.Workbook
import org.modelcatalogue.builder.spreadsheet.poi.PoiSpreadsheetBuilder
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
import org.modelcatalogue.core.diff.CatalogueElementDiffs
import org.modelcatalogue.core.diff.Diff
import org.modelcatalogue.core.enumeration.Enumeration
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H2

@Log4j
class CatalogueElementToXlsxExporter {

    static final String CONTENT = 'Content'
    static final String DATA_CLASSES = 'DataClasses'
    public static final String DATA_TYPE_FIRST_COLUMN = 'F'

    final DataClassService dataClassService
    final GrailsApplication grailsApplication
    final CatalogueElementDiffs catalogueElementDiffs
    final Long elementId
    final Long elementForDiffId
    final Integer depth

    boolean printMetadata

    private Map<Long, DataClass> dataClassesProcessedInOutline = [:]
    private Set<Long> namesPrinted = new HashSet<Long>()
    private Set<Long> sheetsPrinted = new HashSet<Long>()

    static CatalogueElementToXlsxExporter forDataModel(DataModel element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new CatalogueElementToXlsxExporter(element, dataClassService, grailsApplication, depth)
    }

    static CatalogueElementToXlsxExporter forDataClass(DataClass element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new CatalogueElementToXlsxExporter(element, dataClassService, grailsApplication,  depth)
    }

    private CatalogueElementToXlsxExporter(CatalogueElement element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
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

    protected static void buildIntroduction(Sheet sheet, CatalogueElement dataModel) {
        sheet.with {
            row {
                cell {
                    value dataModel.name
                    colspan 4
                    style ModelCatalogueStyles.H1
                }
            }
            row {
                cell {
                    value "Version: $dataModel.dataModelSemanticVersion"
                    colspan 4
                    style ModelCatalogueStyles.H1
                }
            }
            row {
                cell {
                    style ModelCatalogueStyles.H1
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
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER, ModelCatalogueStyles.THIN_DARK_GREY_BORDER, ModelCatalogueStyles.DIM_GRAY_FONT
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
                    styles ModelCatalogueStyles.INNER_TABLE_HEADER, ModelCatalogueStyles.THIN_DARK_GREY_BORDER, ModelCatalogueStyles.DIM_GRAY_FONT
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
        namesPrinted = new HashSet<Long>()
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
        builder.build(outputStream) { Workbook workbook ->
            apply ModelCatalogueStyles

            sheet("Introduction") { Sheet sheet ->
                buildIntroduction(sheet, element)
            }

            sheet(CONTENT) {}

            buildDataClassesDetails(dataClasses, elementForDiff, workbook, null)

            sheet(CONTENT) { Sheet sheet ->
                buildOutline(sheet, dataClasses)
            }

        }

        log.info "Exported Data Class ${element.name} (${element.combinedVersion}) to inventory spreadsheet."

    }

    protected void buildDataClassesDetails(Iterable<DataClass> dataClasses, CatalogueElement elementForDiff, Workbook workbook, Sheet sheet, int level = 0, Set<Long> processed = new HashSet<Long>()) {
        if (level > depth) {
            log.info "${' ' * level}- skipping ${dataClasses*.name} as the level is already $level (max. depth is $depth)"
            return
        }
        for (DataClass dataClassForDetail in dataClasses) {
            buildSheets(dataClassForDetail, null, elementForDiff, ImmutableMultimap.of(), workbook, sheet, level, processed)
        }
    }

    protected void buildDataClassesDetailsWithRelationships(Iterable<Relationship> relationships, CatalogueElement elementForDiff, Multimap<String, Diff> parentDiffs, Workbook workbook, Sheet sheet, int level = 0, Set<Long> processed = new HashSet<Long>()) {
        if (level > depth) {
            log.info "${' ' * level}- skipping ${relationships*.destination*.name} as the level is already $level (max. depth is $depth)"
            return
        }
        for (Relationship relationship in relationships) {
            CatalogueElement childElementForDiff = elementForDiff
            if (elementForDiff && relationship.source.dataModel != relationship.destination.dataModel) {
                String key = Diff.keyForRelationship(relationship)
                CatalogueElement other = findOther(relationship.source, elementForDiff)
                if (other) {
                    Relationship previous = other.outgoingRelationships.find { Diff.keyForRelationship(it) == key }
                    childElementForDiff = (previous ?: relationship).destination.dataModel
                }
            }
            buildSheets(relationship.destination as DataClass, relationship, childElementForDiff, parentDiffs, workbook, sheet, level, processed)
        }
    }

    private void buildSheets(DataClass dataClassForDetail, Relationship relationship, CatalogueElement elementForDiff, Multimap<String, Diff> parentDiffs, Workbook workbook, Sheet sheet, int level = 0, Set<Long> processed = new HashSet<Long>()) {
        if (dataClassForDetail.id in processed) {
            log.info "${' ' * level}- skipping ${dataClassForDetail.name} as it is already processed"
            return
        }

        processed << dataClassForDetail.id
        log.info "Exporting detail for Data Class ${dataClassForDetail.name} (${dataClassForDetail.combinedVersion})"

        if (isSkipExport(dataClassForDetail)) {
            log.info "${' ' * level}- skipped as ${Metadata.SKIP_EXPORT} evaluates to true"
            buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, elementForDiff, ImmutableMultimap.of(), workbook, sheet, level + 1, processed)
            return
        }

        ImmutableMultimap<String, Diff> diffs = collectDiffs(dataClassForDetail, elementForDiff)

        // always render subsections on the new sheet
        if (isSubsection(dataClassForDetail, relationship)) {
            if (dataClassForDetail.id in sheetsPrinted) {
                return
            }
            sheetsPrinted << dataClassForDetail.id
            workbook.sheet(getSafeSheetName(dataClassForDetail)) { Sheet s ->
                buildBackToContentLink(s)
                log.info "${' ' * level}- printing ${dataClassForDetail.name} on new sheet"
                buildDataClassDetail(s, dataClassForDetail, relationship, elementForDiff, diffs, parentDiffs)
                buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, elementForDiff, diffs, workbook, s, level + 1, new HashSet<Long>([dataClassForDetail.id]))

                row()
                buildBackToContentLink(s)
            }
            return
        }

        // inside subsection use existing sheet
        if (sheet) {
            log.info "${' ' * level}- printing ${dataClassForDetail.name} on existing sheet"
            buildDataClassDetail(sheet, dataClassForDetail, relationship, elementForDiff, diffs, parentDiffs)
            buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, elementForDiff, diffs, workbook, sheet, level + 1, processed)
            return
        }

        if (dataClassForDetail.id in sheetsPrinted) {
            return
        }

        sheetsPrinted << dataClassForDetail.id

        // top level sheet
        workbook.sheet(getSafeSheetName(dataClassForDetail)) { Sheet s ->
            buildBackToContentLink(s)
            log.info "${' ' * level}- printing ${dataClassForDetail.name} on new sheet"
            buildDataClassDetail(s, dataClassForDetail, relationship, elementForDiff, diffs, parentDiffs)
            // force top level sheets for children
            buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, elementForDiff, diffs, workbook, null, level + 1, processed)

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

        return (T) CatalogueElement.findByLatestVersionIdAndDataModel(catalogueElement.latestVersionId ?: catalogueElement.id, otherDataModel)
    }

    private static String getSafeSheetName(DataClass dataClassForDetail) {
        "${getModelCatalogueIdToPrint(dataClassForDetail)} ${normalizeDataClassName(dataClassForDetail)}".replaceAll(/[^\p{Alnum}\\_]/, '_')
    }

    private static buildBackToContentLink(Sheet sheet) {
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


    private void buildDataClassDetail(Sheet sheet, DataClass dataClass, Relationship relationship, CatalogueElement elementForDiff, Multimap<String, Diff> diffs, Multimap<String, Diff> parentDiffs) {

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
                    value dataClass.name
                    if (!(dataClass.id in namesPrinted)) {
                        name getSafeSheetName(dataClass)
                        namesPrinted << dataClass.id
                    }
                    styles withChangesHighlight('h1', parentDiffs, Diff.keyForRelationship(relationship))
                    colspan 7
                }
            }
            row {
                 if (dataClass.description) {
                    cell {
                        value dataClass.description
                        height 100

                        styles 'description'
                        colspan 7
                    }
                 }
            }

            row {
                cell {
                    value 'Data Model'
                    style 'property-title'
                    colspan 2
                }
                cell {
                    value dataClass.dataModel?.name
                    style 'property-value'
                    colspan 3
                }
                cell {
                    value 'ID'
                    style 'property-title'
                }
                cell {
                    value getModelCatalogueIdToPrint(dataClass)
                    style 'property-value'
                }
            }

            if (dataClass.countContains()) {
                buildContainedElements(it, dataClass, elementForDiff, diffs)
            }
        }
    }

    private ImmutableMultimap<String, Diff> collectDiffs(CatalogueElement element, CatalogueElement elementForDiff) {
        if (!element || !elementForDiff) {
            return ImmutableMultimap.of()
        }

        Multimap<String, Diff> diffs = ImmutableMultimap.of()
        CatalogueElement other = findOther(element, elementForDiff)

        if (other) {
            diffs = catalogueElementDiffs.differentiate(element, other)
        }

        diffs
    }

    private buildContainedElements(Sheet sheet, DataClass dataClass, CatalogueElement elementForDiff, Multimap<String, Diff> dataClassDiffs) {
        sheet.with {
            row {
                cell {
                    value 'All Contained Data Elements'
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


            for (Relationship containsRelationship in dataClass.containsRelationships) {
                buildDataElement(sheet, containsRelationship, elementForDiff, dataClassDiffs)
            }
            for (Relationship deleted in dataClassDiffs.values().findAll { it.relationshipChange && it.selfMissing && it.key.startsWith("rel:${dataClass.latestVersionId ?: dataClass.id}=[containment]=") }.collect { it.otherValue as Relationship }) {
                buildDataElement(sheet, deleted, elementForDiff, dataClassDiffs)
            }
        }
    }

    private void buildDataElement(Sheet sheet, Relationship containsRelationship, CatalogueElement elementForDiff, Multimap<String, Diff> dataClassDiffs) {

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
                styles withChangesHighlight('data-element-bottom-right', dataClassDiffs, Diff.keyForRelationship(containsRelationship))
            }
            cell {
                value element.status
                styles withChangesHighlight('data-element-center-center', dataClassDiffs, Diff.keyForRelationship(containsRelationship))
            }
            cell {
                value element.name
                styles withChangesHighlight('data-element', dataClassDiffs, Diff.keyForRelationship(containsRelationship))
                colspan 2
            }
            cell {
                value getMultiplicity(containsRelationship)
                styles withChangesHighlight('data-element-top-right', dataClassDiffs, Diff.keyForRelationshipExtension(containsRelationship, Metadata.MIN_OCCURS), Diff.keyForRelationshipExtension(containsRelationship, Metadata.MAX_OCCURS), Diff.keyForRelationship(containsRelationship))
            }

            if (dataType) {
                cell {
                    value dataType.name
                    styles Iterables.concat(withChangesHighlight('data-element', dataElementDiffs, 'dataType'), withChangesHighlight(null, dataClassDiffs, Diff.keyForRelationship(containsRelationship)))
                    colspan 2
                }

                if (measurementUnit) {
                    cell {
                        value measurementUnit.name
                        styles Iterables.concat(withChangesHighlight('data-element', dataTypeDiffs, 'measurementUnit'), withChangesHighlight(null, dataElementDiffs, 'dataType'), withChangesHighlight(null, dataClassDiffs, Diff.keyForRelationship(containsRelationship)))
                    }
                } else {
                    cell()
                }

                if (referencedClass) {
                    cell {
                        value referencedClass.name
                        styles Iterables.concat(withChangesHighlight('data-element', dataTypeDiffs, 'dataClass'), withChangesHighlight(null, dataElementDiffs, 'dataType'), withChangesHighlight(null, dataClassDiffs, Diff.keyForRelationship(containsRelationship)))
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
                cell(DATA_TYPE_FIRST_COLUMN) { Cell theCell ->
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
                        bold
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

        if (typeHierarchy.items.any { it.rule }) {
            sheet.row {
                cell("F") {
                    text 'Rules', {
                        size 12
                        bold
                    }
                    colspan 2
                }
            }
            for (DataType type in typeHierarchy.items) {
                sheet.row {
                    cell(DATA_TYPE_FIRST_COLUMN) {
                        text type.name, {
                            bold
                        }
                    }
                    cell { Cell cell ->
                        text type.rule
                        style ModelCatalogueStyles.DESCRIPTION
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

    private static printEnumeration(Sheet sheet, Enumeration entry, ImmutableMultimap<String, Diff> dataTypeDiffs) {
        sheet.row {
            cell(DATA_TYPE_FIRST_COLUMN) { Cell cell ->
                text entry.key, {
                    bold
                    if (entry.deprecated) {
                        italic
                        color lightGray
                    }
                }
                styles withChangesHighlight(null, dataTypeDiffs, Diff.keyForEnumeration(entry.id))
            }
            cell { Cell cell ->
                text entry.value, {
                    if (entry.deprecated) {
                        italic
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

        Collection<Diff> interestingDiffs = diffKeys.collect { diffs.get(it) }.flatten() as Collection<Diff>

        if (!interestingDiffs) {
            return ret.build()
        }

        if (interestingDiffs.any { it.otherMissing } ) {
            ret.add(ModelCatalogueStyles.CHANGE_NEW)
        } else if (interestingDiffs.any { it.selfMissing } ) {
            ret.add(ModelCatalogueStyles.CHANGE_REMOVAL)
        } else if (interestingDiffs.any { it.update } ) {
            ret.add(ModelCatalogueStyles.CHANGE_UPDATE)
        }

        ret.build()
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

        int ruleCount = typeHierarchy.items.count { it.rule }
        if (ruleCount > 0) {
            rowspan += 1 + ruleCount
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

    protected void buildOutline(Sheet sheet, List<DataClass> dataClasses) {
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

            buildDataClassesOutline(it, dataClasses)

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

    private void buildDataClassesOutline(Sheet sheet, List<DataClass> dataClasses) {
        dataClasses.each { DataClass dataClass ->
            buildChildOutline(sheet, dataClass, null, 1)
        }
    }

    protected void buildChildOutline(Sheet sheet, DataClass dataClass, Relationship relationship, int level) {
        sheet.row {
            cell {
                value getModelCatalogueIdToPrint(dataClass)
                style {
                    align bottom right
                }
                if (!isSkipExport(dataClass) && dataClass.id in namesPrinted) {
                    link to name getSafeSheetName(dataClass)
                }
            }
            cell {
                value dataClass.name
                if (!isSkipExport(dataClass) && dataClass.id in namesPrinted) {
                    link to name getSafeSheetName(dataClass)
                }
                style {
                    if (level) {
                        indent (level * 2)
                    }
                }
            }
            cell {
                value getMultiplicity(relationship)
                if (!isSkipExport(dataClass) && dataClass.id in namesPrinted) {
                    link to name getSafeSheetName(dataClass)
                }
                style {
                    align center right
                }
            }
        }

        if (level > depth) {
            dataClassesProcessedInOutline.put(dataClass.getId(), dataClass)
            return
        }

        if (dataClass.getId() in dataClassesProcessedInOutline.keySet()) {
            return
        }

        dataClassesProcessedInOutline.put(dataClass.getId(), dataClass)

        if (dataClass.countParentOf()) {
            sheet.group {
                for (Relationship child in dataClass.parentOfRelationships) {
                    if (!isSubsection(dataClass, relationship)) {
                        buildChildOutline(sheet, child.destination as DataClass, child, level + 1)
                    }
                }
            }
        }
    }
}
