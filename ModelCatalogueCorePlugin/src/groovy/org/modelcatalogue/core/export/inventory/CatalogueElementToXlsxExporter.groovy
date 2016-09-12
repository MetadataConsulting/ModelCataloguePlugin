package org.modelcatalogue.core.export.inventory

import com.google.common.collect.ImmutableMap
import grails.util.Holders
import groovy.util.logging.Log4j
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
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
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
    final Long elementId
    final Integer depth

    boolean printMetadata

    private Map<Long, DataClass> dataClassesProcessedInOutline = [:]
    private Set<Long> namesPrinted = new HashSet<Long>()

    static CatalogueElementToXlsxExporter forDataModel(DataModel element, DataClassService dataClassService, Integer depth = 3) {
        return new CatalogueElementToXlsxExporter(element, dataClassService, depth)
    }

    static CatalogueElementToXlsxExporter forDataClass(DataClass element, DataClassService dataClassService, Integer depth = 3) {
        return new CatalogueElementToXlsxExporter(element, dataClassService, depth)
    }

    private CatalogueElementToXlsxExporter(CatalogueElement element, DataClassService dataClassService, Integer depth = 3) {
        this.elementId = element.getId()
        this.dataClassService = dataClassService
        this.depth = depth

        printMetadata = Holders.config.mc.export.xlsx.printMetadata ?: false
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

        CatalogueElement element = CatalogueElement.get(elementId)

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

            buildDataClassesDetails(dataClasses, workbook, null)

            sheet(CONTENT) { Sheet sheet ->
                buildOutline(sheet, dataClasses)
            }

        }

        log.info "Exported Data Class ${element.name} (${element.combinedVersion}) to inventory spreadsheet."

    }

    protected void buildDataClassesDetails(Iterable<DataClass> dataClasses, Workbook workbook, Sheet sheet, int level = 0, Set<Long> processed = new HashSet<Long>()) {
        if (level > depth) {
            log.info "${' ' * level}- skipping ${dataClasses*.name} as the level is already $level (max. depth is $depth)"
            return
        }
        for (DataClass dataClassForDetail in dataClasses) {
            buildSheets(dataClassForDetail, null, workbook, sheet, level, processed)
        }
    }

    protected void buildDataClassesDetailsWithRelationships(Iterable<Relationship> relationships, Workbook workbook, Sheet sheet, int level = 0, Set<Long> processed = new HashSet<Long>()) {
        if (level > depth) {
            log.info "${' ' * level}- skipping ${relationships*.destination*.name} as the level is already $level (max. depth is $depth)"
            return
        }
        for (Relationship relationship in relationships) {
            buildSheets(relationship.destination as DataClass, relationship, workbook, sheet, level, processed)
        }
    }

    private void buildSheets(DataClass dataClassForDetail, Relationship relationship, Workbook workbook, Sheet sheet, int level = 0, Set<Long> processed = new HashSet<Long>()) {
        if (dataClassForDetail.id in processed) {
            log.info "${' ' * level}- skipping ${dataClassForDetail.name} as it is already processed"
            return
        }

        processed << dataClassForDetail.id
        log.info "Exporting detail for Data Class ${dataClassForDetail.name} (${dataClassForDetail.combinedVersion})"

        if (isSkipExport(dataClassForDetail)) {
            log.info "${' ' * level}- skipped as ${Metadata.SKIP_EXPORT} evaluates to true"
            buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, workbook, sheet, level + 1, processed)
            return
        }

        // always render subsections on the new sheet
        if (isSubsection(dataClassForDetail, relationship)) {
            workbook.sheet("${getModelCatalogueIdToPrint(dataClassForDetail)} ${normalizeDataClassName(dataClassForDetail)}") { Sheet s ->
                log.info "${' ' * level}- printing ${dataClassForDetail.name} on new sheet"
                buildDataClassDetail(s, dataClassForDetail)
                buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, workbook, s, level + 1, new HashSet<Long>([dataClassForDetail.id]))
            }
            return
        }

        // inside subsection use existing sheet
        if (sheet) {
            log.info "${' ' * level}- printing ${dataClassForDetail.name} on existing sheet"
            buildDataClassDetail(sheet, dataClassForDetail)
            buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, workbook, sheet, level + 1, processed)
            return
        }

        // top level sheet
        workbook.sheet("${getModelCatalogueIdToPrint(dataClassForDetail)} ${normalizeDataClassName(dataClassForDetail)}") { Sheet s ->
            log.info "${' ' * level}- printing ${dataClassForDetail.name} on new sheet"
            buildDataClassDetail(s, dataClassForDetail)
            // force top level sheets for children
            buildDataClassesDetailsWithRelationships(dataClassForDetail.parentOfRelationships, workbook, null, level + 1, processed)
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


    private void buildDataClassDetail(Sheet sheet, DataClass dataClass) {
        sheet.with {
            row {
                cell {
                    value '<< Back to Content'
                    link to name DATA_CLASSES
                    style 'note'
                    colspan 5
                }
            }
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
                        name getReferenceName(dataClass)
                        namesPrinted << dataClass.id
                    }
                    style 'h1'
                    colspan 5
                }
            }
            row {
                if (dataClass.description) {
                    cell {
                        value dataClass.description

                        height 100

                        style 'description'
                        colspan 5
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
            }
            row {
                cell {
                    value 'ID'
                    style 'property-title'
                    colspan 2
                }
                cell {
                    value getModelCatalogueIdToPrint(dataClass)
                    style 'property-value'
                    colspan 3
                }
            }
            row {
                cell {
                    value 'Status'
                    style 'property-title'
                    colspan 2
                }
                cell {
                    value dataClass.status
                    style 'property-value'
                    colspan 3
                }
            }

            row()

            row {
                cell {
                    value 'Last Updated'
                    style 'property-title'
                    colspan 2
                }
                cell {
                    value dataClass.lastUpdated
                    style 'date'
                    colspan 3
                }
            }

            row()


            if (dataClass.countContains()) {
                buildContainedElements(it, dataClass)
            }

            row()

            row {
                cell {
                    value '<< Back to Content'
                    link to name DATA_CLASSES
                    style 'note'
                    colspan 5
                }
            }
        }
    }

    private buildContainedElements(Sheet sheet, DataClass dataClass) {
        sheet.with {
            row {
                cell {
                    value 'All Contained Data Elements'
                    style 'h2'
                    colspan 5
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
                DataElement element = containsRelationship.destination as DataElement
                row {
                    cell {
                        value getModelCatalogueIdToPrint(element)
                        style 'data-element-bottom-right'
                    }
                    cell {
                        value element.status
                        style 'data-element-center-center'
                    }
                    cell {
                        value element.name
                        style 'data-element'
                        colspan 2
                    }
                    cell {
                        value getMultiplicity(containsRelationship)
                        style 'data-element-top-right'
                    }
                    if (element.dataType) {
                        cell {
                            value element.dataType.name
                            style 'data-element'
                            colspan 2
                        }

                        if (element.dataType.instanceOf(PrimitiveType) && element.dataType.measurementUnit) {
                            cell {
                                value element.dataType.measurementUnit.name
                                style 'data-element'
                            }
                        } else {
                            cell()
                        }

                        if (element.dataType.instanceOf(ReferenceType) && element.dataType.dataClass) {
                            cell {
                                value element.dataType.dataClass.name
                                style 'data-element'
                            }
                        } else {
                            cell()
                        }
                    } else {
                        3.times { cell() }
                    }
                }

                ListWithTotalAndType<DataType> typeHierarchy = element.dataType ? ElementService.getTypeHierarchy([:], element.dataType) : Lists.emptyListWithTotalAndType(DataType)

                row {
                    style 'data-element-description-row'

                    cell('C') {
                        value element.description
                        colspan 3
                        int desiredRowSpan = getRowSpanForDataTypeDetails(element.dataType, typeHierarchy)
                        if (desiredRowSpan > 1) {
                            rowspan desiredRowSpan
                        }
                    }

                    if (element.dataType) {
                        cell (DATA_TYPE_FIRST_COLUMN) { Cell theCell ->
                            if (element.dataType.description) {
                                text element.dataType.description
                            }
                            colspan 2
                        }

                        if (element.dataType.instanceOf(PrimitiveType) && element.dataType.measurementUnit) {
                            cell ('H') {
                                value element.dataType.measurementUnit.description
                            }
                        }

                        if (element.dataType.instanceOf(ReferenceType) && element.dataType.dataClass) {
                            cell ('I') {
                                value element.dataType.dataClass.description
                            }
                        }

                    }

                }

                if (HibernateHelper.getEntityClass(element.dataType) == EnumeratedType && element.dataType.enumerations) {
                    row {
                        cell("F") {
                            text 'Enumerations', {
                                size 12
                                bold
                            }
                            colspan 2
                        }
                    }
                    Enumerations enumerations = element.dataType.enumerationsObject
                    for (Enumeration entry in enumerations) {
                        row {
                            cell(DATA_TYPE_FIRST_COLUMN) { Cell cell ->
                                cell.text entry.key, {
                                    bold
                                    if (entry.deprecated) {
                                        italic
                                        color lightGray
                                    }
                                }
                            }
                            cell { Cell cell ->
                                text entry.value, {
                                    if (entry.deprecated) {
                                        italic
                                        color lightGray
                                    }
                                }
                                style ModelCatalogueStyles.DESCRIPTION
                            }
                        }
                    }
                }

                if (typeHierarchy.items.any { it.rule }) {
                    row {
                        cell("F") {
                            text 'Rules', {
                                size 12
                                bold
                            }
                            colspan 2
                        }
                    }
                    for (DataType type in typeHierarchy.items) {
                        row {
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
                        row {
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
        }
    }

    protected static int getRowSpanForDataTypeDetails(DataType dataType, ListWithTotalAndType<DataType> typeHierarchy) {
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

    protected static String getReferenceName(DataClass dataClass) {
        "${dataClass.name} (${getModelCatalogueIdToPrint(dataClass)})"
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
                    link to name getReferenceName(dataClass)
                }
            }
            cell {
                value dataClass.name
                if (!isSkipExport(dataClass) && dataClass.id in namesPrinted) {
                    link to name getReferenceName(dataClass)
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
                    link to name getReferenceName(dataClass)
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
