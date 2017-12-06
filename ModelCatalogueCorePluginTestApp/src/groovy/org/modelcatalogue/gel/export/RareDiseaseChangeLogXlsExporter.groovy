package org.modelcatalogue.gel.export

import static java.lang.Boolean.FALSE
import static java.lang.Boolean.TRUE
import static java.util.Map.Entry
import static org.modelcatalogue.core.audit.ChangeType.*
import static RareDiseaseChangeLogXlsExporter.RareDiseaseChangeType.*
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import groovy.json.JsonSlurper
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import groovy.util.logging.Log4j
import org.apache.commons.lang.exception.ExceptionUtils
import org.modelcatalogue.spreadsheet.builder.api.SheetDefinition
import org.modelcatalogue.spreadsheet.builder.api.SpreadsheetBuilder
import org.modelcatalogue.spreadsheet.builder.api.WorkbookDefinition
import org.modelcatalogue.spreadsheet.builder.poi.PoiSpreadsheetBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.audit.ChangeType
import org.modelcatalogue.core.publishing.changelog.AbstractChangeLogGenerator
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.OrderedMap

/**
 * Created by rickrees on 18/04/2016.
 *
 */
@Log4j
abstract class RareDiseaseChangeLogXlsExporter extends AbstractChangeLogGenerator implements XlsExporter {

    public static final String EMPTY = ''
    public static final String PHENOTYPE = 'Phenotype'
    public static final String CLINICAL_TESTS = 'Clinical tests'
    public static final String GENERAL_RECURSIVE_CHANGELOG = 'Recurse Generic Model'
    public static final String GUIDANCE = 'Guidance'
    public static final int COLUMN_CONTEXT_THRESHOLD = 4
    public static final ArrayList<ChangeType> TOP_LEVEL_RELATIONSHIP_TYPES = [RELATIONSHIP_DELETED, RELATIONSHIP_CREATED]
    public static final ArrayList<ChangeType> DETAIL_CHANGE_TYPES = [RELATIONSHIP_DELETED, RELATIONSHIP_CREATED, RELATIONSHIP_METADATA_UPDATED, METADATA_UPDATED, METADATA_CREATED, METADATA_DELETED, RELATIONSHIP_METADATA_CREATED, RELATIONSHIP_METADATA_DELETED, PROPERTY_CHANGED]
    public static final int CLEAN_UP_GORM_PERIOD = 100      //cleanup every 100 changelog calls - seems to be best performing

    def suppressKeyDisplayList = ['parent of', 'child of', 'contains', 'name', 'description']
    def ignoreKeyListForDeletions = ['child of','contains', 'Class Type']
    def ignoreKeyListForCreations = ['child of', 'Class Type']
    def ignoreKeyList = ['parent of', 'child of','contains', 'Class Type']

    PerformanceUtilService performanceUtilService

    int callCount = 0
    int itemCount = 0
    Map<Long,Boolean> visitedModels                     // id and TRUE/FALSE whether changes found for this model or it's children
    Map<Integer,Long> levelIdStack                      // ids in the current stack
    ListMultimap<Long,List<String>> cachedChanges       // id and the cached changes for this model (string is ',' joined)
    Map<Integer, String> levelNameStack = new HashMap<>()
    private Date modelCreation


    public enum RareDiseaseChangeType {
        REMOVE_DATA_ITEM('Remove Data Item',null),
        REMOVE_DATA_VALUE('Remove Data Value',null),
        NEW_DATA_ITEM('New Data Item',null),
        NEW_DATA_VALUE('New Data Value',null),
        CHANGE_DESCRIPTION('Change Description', 'description'),
        CHANGE_DATA_VALUE('Change Data Value', null),
        NEW_METADATA('New Metadata', null),
        CHANGED_METADATA('Change Metadata', null),
        REMOVE_METADATA('Remove Metadata', null),
        NAME_CHANGE('Name Change','name'),
        TEXT_CHANGE('Text change','description')

        String renderedText
        String catalogChangeKey

        RareDiseaseChangeType(String renderedText, String catalogChangeKey) {
            this.renderedText = renderedText
            this.catalogChangeKey = catalogChangeKey
        }
    }


    RareDiseaseChangeLogXlsExporter(AuditService auditService, DataClassService dataClassService, PerformanceUtilService performanceUtilService, Integer depth = 5, Boolean includeMetadata = false) {
        super(auditService, dataClassService, depth, includeMetadata)
        this.performanceUtilService = performanceUtilService
    }

    @Override
    void generateChangelog(DataClass model, OutputStream outputStream) {}


    public void exportXls(CatalogueElement model, OutputStream out, String sheetName){
        def timeStart = new Date()
        List lines = buildContentRows(model)

        exportLinesAsXls sheetName, lines, out

        TimeDuration elapsed = TimeCategory.minus(new Date(), timeStart)
        log.info "stats: export took=$elapsed itemcount=$itemCount visitedModels (excludes previously visited) ${visitedModels.size()} cached models ${cachedChanges.size()}"
        log.info "Exported Rare Diseases as xls spreadsheet ${model.name} (${getDisplayVersion(model)})"
    }


    public List buildContentRows(CatalogueElement model) {
        int level = 1
        def lines = []
        def exclusions = []
        Map<String, String> groupDescriptions = new HashMap<>()
        visitedModels = new HashMap<>()
        levelIdStack = new HashMap<>()
        cachedChanges = ArrayListMultimap.create()
        modelCreation = model.dataModel.dateCreated

        log.info "Exporting Rare Diseases as xls ${model.name} (${getDisplayVersion(model)})"

        descendModels(model, lines, level, groupDescriptions, exclusions)

        lines
    }


    @Override
    def descendModels(CatalogueElement model, lines, level, Map groupDescriptions, exclusions) {

        switch (level) {
            case 1:
                String groupDescription = "$model.name (${getDisplayVersion(model)})"
                log.debug("level$level $groupDescription")
                groupDescriptions.put(2, EMPTY)     //pad for when no lower levels present
                groupDescriptions.put(3, EMPTY)
                groupDescriptions.put(4, EMPTY)
                checkChangeLog(model, lines, groupDescriptions, level, DETAIL_CHANGE_TYPES)
                break

            case 2:
                groupDescriptions.put(3, EMPTY)     //pad for when no lower levels present
            case 3:
                groupDescriptions.put(4, EMPTY)
            case 4:
                String groupDescription = "$model.name (${getDisplayVersion(model)})"
                log.info "$level $model $groupDescription --- $model.dataModel"
                groupDescriptions.put(level, groupDescription)
                checkChangeLog(model, lines, groupDescriptions, level, DETAIL_CHANGE_TYPES)
                break

            case 5:
                log.info "5 searching... $model --- $model.dataModel"

                lines = searchExportSpecificTypes(model, lines, groupDescriptions, level)
                return  //don't go deeper

            default:    //don't go deeper
                return
        }

        //don't recurse dataElements
        if (model instanceof DataElement) return

        model.contains.each { CatalogueElement child ->
            descendModels(child, lines, level + 1, groupDescriptions, exclusions)
        }
        model.parentOf?.each { CatalogueElement child ->
            descendModels(child, lines, level + 1, groupDescriptions, exclusions)
        }

    }

    // There is a significant performance improvement just knowing whether a model/children
    // have been seen before and do/don't have changes - if there are no changes we can skip.
    //
    // cases
    // 1. never visited before
    // 2. visited before and no changes here or below
    // 3. visited before and has changes or children have changes
    //
    List<String> iterateChildren(CatalogueElement model, List lines, String subtype = null, groupDescriptions, int level, List<ChangeType> typesToCheck) {

        levelIdStack.put(level,model.id)

        model.parentOf?.each { CatalogueElement child ->

            if (++itemCount % 500 == 0) {
                log.info "raw itemcount=$itemCount visitedModels ${visitedModels.size()}"
            }

            boolean visited = visitedModels.containsKey(child.id)
            boolean isVisitedWithChanges

            // case 1. not yet visited
            if(!visited) {
                log.debug("new model $child")
                checkChangesAndDescend(child, lines, subtype, groupDescriptions, level, typesToCheck)
                return
            }
            // else visited...with/without changes

            isVisitedWithChanges = visitedModels.get(child.id)

            //case 2.
            if(!isVisitedWithChanges ) {
                log.debug("visited no Changes $child.name")
                return
            }

            //case 3.
            log.debug("visited should have changes in cache $child.name")
            checkChangesAndDescend(child, lines, subtype, groupDescriptions, level, typesToCheck)
            return
        }
        lines
    }


    protected void checkChangesAndDescend(CatalogueElement child, List lines, String subtype, groupDescriptions, int level, List<ChangeType> typesToCheck) {
        checkChangeLog(child, lines, subtype, groupDescriptions, level, typesToCheck)

        if ((PHENOTYPE == subtype || CLINICAL_TESTS == subtype || GENERAL_RECURSIVE_CHANGELOG == subtype) && child.parentOf.size > 0) {   // can be nested
            iterateChildren(child, lines, subtype, groupDescriptions, level + 1, DETAIL_CHANGE_TYPES)
        }
    }


    // operates at level 6 & below
    //
    // Check whether we have cached changes otherwise get them from db
    //
    boolean checkChangeLog(CatalogueElement model, List lines, String subtype = null, groupDescriptions, int level, List<ChangeType> typesToCheck) {
        def rows
        List<String> changes = []
        int currLineCount = lines.size()
        levelIdStack.put(level, model.id)
        levelNameStack.put(level,"$model.name (${getDisplayVersion(model)})")

        // cache hit? use the cached changelog info
        if(cachedChanges.containsKey(model.id)) {
            log.debug("cache hit $model.name")
            generateLinesFromCachedChangeLogs(model, level, subtype, lines, groupDescriptions)
            saveChangedModelsTree(lines, currLineCount, model, level)
            return
        }

        List<Change> allChanged = getChangesAfterDate(model, modelCreation, typesToCheck.toArray(new ChangeType[0]))
        callCount++


        for (Change change : allChanged) {
            changes = []

            if (RELATIONSHIP_DELETED.equals(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    if (!ignoreKeyListForDeletions.contains(key)) {
                        changes = createSingleChangeRow(key, rowData, model, level, subtype, groupDescriptions, REMOVE_DATA_ITEM)
                        if (changes) lines << changes    //add row
                    }
                }
            }


            if (RELATIONSHIP_CREATED.equals(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    if (!ignoreKeyListForCreations.contains(key)) {
                        changes = createSingleChangeRow(key, rowData, model, level, subtype, groupDescriptions, NEW_DATA_ITEM)
                        if (changes) lines << changes    //add row
                    }
                }
            }


            if ([METADATA_UPDATED, RELATIONSHIP_METADATA_UPDATED].contains(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    if (!ignoreKeyList.contains(key)) {
                        changes = createSingleChangeRow(key, rowData, model, level, subtype, groupDescriptions, CHANGED_METADATA)
                        if (changes) lines << changes    //add row
                    }
                }
            }


            if (METADATA_CREATED.equals(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    if (!ignoreKeyList.contains(key)) {
                        changes = createSingleChangeRow(key, rowData, model, level, subtype, groupDescriptions, NEW_METADATA)
                        lines << changes    //add row
                    }
                }
            }

            if (RELATIONSHIP_METADATA_CREATED.equals(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    changes = createSingleChangeRow(key, rowData, model, level, subtype, groupDescriptions, NEW_METADATA)
                    if (changes) lines << changes    //add row
                }
            }

            if ([METADATA_DELETED,RELATIONSHIP_METADATA_DELETED].contains(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    changes = createSingleChangeRow(key, rowData, model, level, subtype, groupDescriptions, REMOVE_METADATA)
                    if (changes) lines << changes    //add row
                }
            }


            if (PROPERTY_CHANGED.equals(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->

                    def changeTypeToRender = CHANGE_DATA_VALUE
                    if (CHANGE_DESCRIPTION.catalogChangeKey.equalsIgnoreCase(key)) {
                        changeTypeToRender = CHANGE_DESCRIPTION
                    }
                    if (NAME_CHANGE.catalogChangeKey.equalsIgnoreCase(key)) {
                        changeTypeToRender = NAME_CHANGE
                    }
                    if (GUIDANCE.equals(subtype) && TEXT_CHANGE.catalogChangeKey.equalsIgnoreCase(key)) {
                        changeTypeToRender = TEXT_CHANGE
                    }
                    changes = createSingleChangeRow(key, rowData, model, level, subtype, groupDescriptions, changeTypeToRender)
                    if (changes) lines << changes    //add row
                }
            }
        }

        saveChangedModelsTree(lines, currLineCount, model, level)

        allChanged.isEmpty()
    }


    //
    // if this item has changed, mark all the ids in the tree above as having changes (breadcrumbs) so that it won't get
    // skipped if it subsequently appears below items in the tree without changes i.e. when they are checked for isVisitedWithChanges
    //
    protected void saveChangedModelsTree(List lines, int currLineCount, CatalogueElement model, int level) {
        if (callCount % CLEAN_UP_GORM_PERIOD == 0) {
            performanceUtilService.cleanUpGorm()
        }

        if (lines.size() > currLineCount) {
            visitedModels.put(model.id, TRUE)
            for (Entry<Integer, Long> entry : levelIdStack.entrySet()) {
                if (entry.key < level) {
                    visitedModels.put(entry.value, TRUE)
                }
            }
            log.debug "found changes for level$level model $model lines ${lines.size}"
        } else {
            visitedModels.put(model.id, FALSE)
        }
    }


    protected Map<String, List<String>> createRow(Change change) {
        Map<String, List<String>> rows = new HashMap<String, List<String>>().withDefault { ['', ''] }

            String propLabel = change.property
            if (change.type in [RELATIONSHIP_METADATA_CREATED,RELATIONSHIP_METADATA_UPDATED]) {
                if (mightBeJSON(change.newValue)) {
                    def jsonSlurper = new JsonSlurper()
                    def jsonMap = jsonSlurper.parseText(change.newValue)
                    propLabel = jsonMap.name
                    if(propLabel.contains('merge') || propLabel.contains('exclude')) {
                        propLabel = ''
                    }
                }
            }

            switch (change.type) {
                case RELATIONSHIP_METADATA_UPDATED:
                    propLabel = propLabel ?: ''
                    List<String> vals = rows[propLabel]
                    vals[0] = getOldRelationshipMetadataValue(change)?.toString() ?: change.oldValue
                    vals[1] = getNewRelationshipMetadataValue(change)?.toString() ?: change.newValue
                    rows[propLabel] = vals
                    break

                case RELATIONSHIP_METADATA_CREATED:
                    propLabel = propLabel ?: ''
                    List<String> vals = rows[propLabel]
                    vals[0] =  change.oldValue ?: ''
                    vals[1] = getNewRelationshipMetadataValue(change)?.toString() ?: change.newValue
                    rows[propLabel] = vals
                    break

                default:
                    propLabel = propLabel ?: ''
                    List<String> vals = rows[propLabel]
                    vals[0] = valueForPrint(change.property, change.oldValue)
                    vals[1] = valueForPrint(change.property, change.newValue)
                    rows[propLabel] = vals
            }
        rows
    }


    private List<String> createSingleChangeRow(String key, List<String> rowData, CatalogueElement model, int level, String subtype = null, groupDescriptions, RareDiseaseChangeType changeToRender) {
        List<String> changes = []

        try {
            def (String before, String after) = extractValues(rowData, changeToRender, key)

            if (!before && !after ) {       //don't print blank lines
                return []
            }

            if(model.ext.get(Metadata.CHANGE_REF)) {
                changes << model.ext.get(Metadata.CHANGE_REF)
            } else {
                changes << EMPTY
            }

            groupDescriptions.each { lvl, lvlName ->
                changes << lvlName
            }

            if(this instanceof RareDiseasePhenotypeChangeLogXlsExporter) {                   // Phenotypes/Clinical tests report format - extra cols
                changes << buildElementHierachyText(level, model)
                changes << (subtype ?: EMPTY)
            }

            changes << "$model.name (${getDisplayVersion(model)})"           // Affected Data Item

            changes << changeToRender.renderedText

            if(suppressKeyDisplayList.contains(key)) {
                changes << before
                changes << after
            } else {
                changes << (before ? "$key: $before" : '')
                changes << (after ? "$key: $after" : '')
            }

            if(this instanceof RareDiseasePhenotypeChangeLogXlsExporter) {            // cache detail changes
                cachedChanges.put(model.id, changes[5..-1].join(','))
            } else {
                cachedChanges.put(model.id, changes[4..-1].join(','))
            }

        } catch (Exception e) {
            log.error "Error reading unpacked changelog key=$key rowdata=$rowData \n  ${e.toString()}"
            return []
        }
        log.debug "\n$changes"
        changes
    }


    protected String buildElementHierachyText(int level, CatalogueElement model) {
        def hierarchyDescr = []

        levelNameStack.each { lvl, lvlName ->
            if (level > lvl && lvl > COLUMN_CONTEXT_THRESHOLD) {    //ignore columns that are already on the sheet
                hierarchyDescr << lvlName
            }
        }

        hierarchyDescr.join("->") ?: ''
    }

    // merge this item's hierarchy info with the (possibly multiple) cached changelogs
    private void generateLinesFromCachedChangeLogs(CatalogueElement model, int level, String subtype, List lines, groupDescriptions) {
        log.debug "use cached model =$model lines ${lines.size}"

        List<String> hierarchyChanges = []

        if(model.ext.get(Metadata.CHANGE_REF)) {
            hierarchyChanges << model.ext.get(Metadata.CHANGE_REF)
        } else {
            hierarchyChanges << EMPTY
        }

        groupDescriptions.each { lvl, lvlName ->
            hierarchyChanges << lvlName
        }

        if (groupDescriptions.size() < 3) {   //DataModelChangelogs hierarchy Cancer/RD can be shallower than RD conditions
            hierarchyChanges << "$model.name (${getDisplayVersion(model)})"
        }

        if(subtype) {
            hierarchyChanges << buildElementHierachyText(level, model)
        }


        for (String cachedChangeLog: cachedChanges.get(model.id)) {
            List<String> changes = []
            changes.addAll(hierarchyChanges)

            String[] cachedChangeValues = cachedChangeLog.split(',')
            changes.addAll(cachedChangeValues)
            lines << changes
        }
        log.debug("lines ${lines.size} after cache use")
    }


    private List extractValues(List<String> rowData, RareDiseaseChangeType changeToRender, String key) {
        String before = rowData.get(0)
        String after = rowData.get(1)

        if ([NEW_METADATA, REMOVE_METADATA, CHANGED_METADATA].contains(changeToRender)) {
            if (before.contains('orderedMap')) {
                before = extractDeepElement(before, key)
            }
            if (after.contains('orderedMap')) {
                after = extractDeepElement(after, key)
            }
        }

        if ([REMOVE_DATA_ITEM, NEW_DATA_ITEM, NEW_METADATA].contains(changeToRender)) {
            before = extractChangedElement(before)
            after = extractChangedElement(after)
        }
        [before, after]
    }

    // hideous jsonStr parsing follows, there must be a better way...
    public static String extractDeepElement(String elemName, String key) {
        String deepElementValue =''

        if (mightBeJSON(elemName)) {
            def jsonMap
            def jsonSlurper = new JsonSlurper()
            try {
                jsonMap = jsonSlurper.parseText(elemName)
            } catch (Exception e) {
                log.error "Error attempting json parse of $elemName"
                log.error ExceptionUtils.getStackTrace(e)
                return elemName
            }

            try {
                if (jsonMap && jsonMap.relationship) {

                    //try the quick json map
                    if (jsonMap.relationship instanceof Map && jsonMap.relationship.ext instanceof Map && jsonMap.relationship.ext.type == 'orderedMap') {

                        Map<String, String> orderedMap = OrderedMap.fromJsonMap(jsonMap.relationship.ext)
                        deepElementValue = "${orderedMap.get(key)}" // coerce to string even if null
                        return deepElementValue

                    } else {    // fallback to long winded string parse
                        def csvTokens = jsonMap.relationship.split(',')

                        boolean startRead = false
                        String accumulator = ''
                        int countLeftBrackets, countRightBrackets = 0

                        for (entry in csvTokens) {
                            if (entry.trim() == "elementType=${Relationship.class.name}") {
                                startRead = true
                                continue
                            }
                            if (startRead) {
                                String fragment = entry.trim()
                                accumulator += ",$fragment"

                                def leftBrackets = fragment =~ /\{/
                                def rightBrackets = fragment =~ /\}/
                                countLeftBrackets += leftBrackets.count
                                countRightBrackets += rightBrackets.count

                                if (countLeftBrackets != 0 && countLeftBrackets == countRightBrackets) {
                                    break
                                }
                            }
                        }

                        if (accumulator.indexOf('orderedMap') > -1 && accumulator.indexOf(key) > 0) {
                            int index = accumulator.indexOf("$key")
                            String keyFragment = accumulator.substring(index)
                            def keyValues = keyFragment.split(',')
                            for (int i = 0; i < keyValues.length; i++) {

                                if (keyValues[i] == key) {
                                    String value = keyValues[i + 1]
                                    deepElementValue = value.replace('value=', '').replace('}', '').replace(']', '')
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                log.error "Error attempting csv token parse of $elemName"
                log.error ExceptionUtils.getStackTrace(e)
                return elemName
            }
        }
        deepElementValue
    }


    private String extractChangedElement(String elemName) {
        if (mightBeJSON(elemName)) {
            def jsonMap
            def jsonSlurper = new JsonSlurper()
            try {
                jsonMap = jsonSlurper.parseText(elemName)
            } catch (Exception e) {
                log.error "Error attempting json parse of $elemName"
                log.error ExceptionUtils.getStackTrace(e)
                return elemName
            }


            try {
                if (jsonMap && jsonMap.destination) {

                    String destination = jsonMap.destination.replaceAll(/[{}]/, '')
                    Map<String, String> destinationMap = new HashMap<String, String>().withDefault { ['', ''] }
                    def csvTokens = destination.split(',')
                    csvTokens.each { csvToken ->
                        String[] tokens = csvToken.split('=')
                        if (tokens.length < 2) {
                            destinationMap.put(tokens[0], tokens[0])
                        } else {
                            destinationMap.put(tokens[0].trim(), tokens[1])
                        }
                    }
                    if (destinationMap.classifiedName && destinationMap.classifiedName instanceof String) { // classifiedName acts as a filter
                        String name = "${destinationMap.name ?: ''}"
                        String id = "${destinationMap.latestVersionId ?: destinationMap.id ?: ''}"

                        if(name && id) {
                            elemName = "$name (${id})"
                        } else {
                            elemName = name
                        }

                    } else {
                        elemName = ''
                    }
                }
            } catch (Exception e) {
                log.error "Error attempting csv token parse of $elemName"
                log.error ExceptionUtils.getStackTrace(e)
                return elemName
            }
        }
        elemName
    }


    //is it potentially useful json? unlikely if less than 15 chars
    public static boolean mightBeJSON(String jsonStr) {
        return jsonStr != null && (jsonStr.length() > 15) && (jsonStr.startsWith("[") && jsonStr.endsWith("]") || jsonStr.startsWith("{") && jsonStr.endsWith("}"));
    }


    def exportLinesAsXls(String sheetName, List lines, OutputStream out) {
        SpreadsheetBuilder builder = new PoiSpreadsheetBuilder()
        builder.build(out) { WorkbookDefinition workbook ->
            apply GelXlsStyles
            sheet(sheetName) { SheetDefinition sheet ->
                buildSheet(sheet, lines)
            }
        }

    }

    void buildRows(SheetDefinition sheet, List<List<String>> lines) {
        lines.eachWithIndex { line, int i ->
            log.debug("row $i=" + line)
            buildRow(sheet, line)
        }
    }

    private buildRow(SheetDefinition sheet, List<String> line) {
        sheet.row {
            line.eachWithIndex{ String cellValue, int i ->
                cell {
                    value cellValue
                    style 'property-value'
                    if (i==7) style 'property-value-wrap'
                    if (i==8) style 'property-value-green'
                }
            }
        }
    }



}









