package org.modelcatalogue.gel.export

import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import org.apache.commons.lang.exception.ExceptionUtils
import org.modelcatalogue.builder.spreadsheet.api.Sheet
import org.modelcatalogue.builder.spreadsheet.api.SpreadsheetBuilder
import org.modelcatalogue.builder.spreadsheet.api.Workbook
import org.modelcatalogue.builder.spreadsheet.poi.PoiSpreadsheetBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.audit.ChangeType
import org.modelcatalogue.core.publishing.changelog.AbstractChangeLogGenerator
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.OrderedMap

import static org.modelcatalogue.core.audit.ChangeType.*
import static org.modelcatalogue.gel.export.RareDiseaseChangeLogXlsExporter.RareDiseaseChangeType.*

/**
 * Created by rickrees on 18/04/2016.
 *
 */
@Log4j
abstract class RareDiseaseChangeLogXlsExporter extends AbstractChangeLogGenerator implements XlsExporter {

    public static final String PHENOTYPES_SHEET = 'HPO & Clinical tests change log'
    public static final String ELIGIBILITY_SHEET = 'Eligibility Criteria change log'

    static final String EMPTY_CHANGE_REF = ''
    public static final String PHENOTYPE = 'Phenotype'
    public static final String CLINICAL_TESTS = 'Clinical tests'
    public static final String GUIDANCE = 'Guidance'
    public static final ArrayList<ChangeType> TOP_LEVEL_RELATIONSHIP_TYPES = [RELATIONSHIP_DELETED, RELATIONSHIP_CREATED]
    public static final ArrayList<ChangeType> DETAIL_CHANGE_TYPES = [RELATIONSHIP_DELETED, RELATIONSHIP_CREATED, RELATIONSHIP_METADATA_UPDATED, METADATA_UPDATED, METADATA_CREATED, METADATA_DELETED, RELATIONSHIP_METADATA_CREATED, RELATIONSHIP_METADATA_DELETED, PROPERTY_CHANGED]
    def suppressKeyDisplayList = ['parent of', 'child of', 'contains', 'name', 'description']
    def ignoreKeyListForDeletions = ['child of','contains', 'Class Type']
    def ignoreKeyListForCreations = ['parent of', 'Class Type']
    def ignoreKeyList = ['parent of', 'child of','contains', 'Class Type']


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

    int modelCount = 0

    RareDiseaseChangeLogXlsExporter(AuditService auditService, DataClassService dataClassService, Integer depth = 5, Boolean includeMetadata = false) {
        super(auditService, dataClassService, depth, includeMetadata)
    }

    @Override
    void generateChangelog(DataClass model, OutputStream outputStream) {}


    public void exportXls(DataClass model, OutputStream out, String sheetName){
        List lines = buildContentRows(model)

        exportLinesAsXls sheetName, lines, out

        log.info "Exported Rare Diseases as xls spreadsheet ${model.name} (${model.combinedVersion})"
    }


    public List buildContentRows(DataClass model) {
        int level = 1
        def lines = []
        def exclusions = []
        Map<String, String> groupDescriptions = new HashMap<>()

        log.info "Exporting Rare Diseases as xls ${model.name} (${model.combinedVersion})"

        descendModels(model, lines, level, groupDescriptions, exclusions)

        lines
    }


    def descendModels(DataClass model, lines, level, Map groupDescriptions, exclusions) {

        switch (level) {
            case 1:     //ignore top Rare Disease level
                break

            case [2]:

                log.info "2 $model --- $model.dataModel"

                String groupDescription = "$model.name (${model.combinedVersion})"
                log.debug("level$level $groupDescription")
                groupDescriptions.put(level, groupDescription)
                break

            case [3]:

                log.info "3 $model --- $model.dataModel"

                String groupDescription = "$model.name (${model.combinedVersion})"
                log.debug("level$level $groupDescription")
                groupDescriptions.put(level, groupDescription)
                break

            case [4]:

                log.info "4 $model --- $model.dataModel"

                String groupDescription = "$model.name (${model.combinedVersion})"
                log.debug("level$level $groupDescription")
                groupDescriptions.put(level, groupDescription)
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

        model.contains.each { DataClass child ->
            descendModels(child, lines, level + 1, groupDescriptions, exclusions)
        }
        model.parentOf?.each { DataClass child ->
            descendModels(child, lines, level + 1, groupDescriptions, exclusions)
        }

    }

    // level 5 descending into level 6
    List<String> iterateChildren(CatalogueElement model, List lines, String subtype = null, groupDescriptions, level, List<ChangeType> typesToCheck) {
        if (modelCount % 100 == 0) {
            log.debug "modelCount=$modelCount"
        }

        model.parentOf?.each { CatalogueElement child ->

            log.debug("model $child.name")
            checkChangeLog(child, lines, subtype, groupDescriptions, level, typesToCheck)

            if( (PHENOTYPE == subtype || CLINICAL_TESTS ==  subtype) && child.parentOf.size > 0) {   // can be nested
                iterateChildren(child, lines, subtype, groupDescriptions, level+1, DETAIL_CHANGE_TYPES)
            }
        }
        lines
    }

    // operates at level 6 & below
    private void checkChangeLog(CatalogueElement model, List lines, String subtype = null, groupDescriptions, level, List<ChangeType> typesToCheck) {
        def rows
        List<String> changes = []
        int noLines = lines.size()

        List<Change> allChanged = getChanges(model, typesToCheck.toArray(new ChangeType[0]))    //get all changes in one go (perf!)


        for (Change change : allChanged) {
            changes = []

            if (RELATIONSHIP_DELETED.equals(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    if (!ignoreKeyListForDeletions.contains(key)) {
                        changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, REMOVE_DATA_ITEM)
                        if (changes) lines << changes    //add row
                    }
                }
            }


            if (RELATIONSHIP_CREATED.equals(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    if (!ignoreKeyListForCreations.contains(key)) {
                        changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, NEW_DATA_ITEM)
                        if (changes) lines << changes    //add row
                    }
                }
            }


            if ([METADATA_UPDATED, RELATIONSHIP_METADATA_UPDATED].contains(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    if (!ignoreKeyList.contains(key)) {
                        changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, CHANGED_METADATA)
                        if (changes) lines << changes    //add row
                    }
                }
            }


            if (METADATA_CREATED.equals(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    if (!ignoreKeyList.contains(key)) {
                        changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, NEW_METADATA)
                        lines << changes    //add row
                    }
                }
            }

            if (RELATIONSHIP_METADATA_CREATED.equals(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, NEW_METADATA)
                    if (changes) lines << changes    //add row
                }
            }

            if ([METADATA_DELETED,RELATIONSHIP_METADATA_DELETED].contains(change.type)) {
                rows = createRow(change)
                rows.each { key, rowData ->
                    changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, REMOVE_METADATA)
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
                    changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, changeTypeToRender)
                    if (changes) lines << changes    //add row
                }
            }
        }

        if(lines.size() > noLines) {
            log.debug "found changes for level$level model $model.name lines ${lines.size}"
        }
        modelCount++

        changes
    }

    protected Map<String, List<String>> createRow(Change change) {
        Map<String, List<String>> rows = new HashMap<String, List<String>>().withDefault { ['', ''] }

            String propLabel = change.property
            if (change.type in [RELATIONSHIP_METADATA_CREATED,RELATIONSHIP_METADATA_UPDATED]) {
                if (mightBeJSON(change.newValue)) {
                    def jsonSlurper = new JsonSlurper()
                    def jsonMap = jsonSlurper.parseText(change.newValue)
                    propLabel = jsonMap.name
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


    private List<String> createSingleChangeRow(String key, List<String> rowData, CatalogueElement model, String subtype = null, groupDescriptions, RareDiseaseChangeType changeToRender) {
        List<String> changes = []

        try {
            def (String before, String after) = extractValues(rowData, changeToRender, key)

            if (!before && !after ) {       //don't print blank lines
                return []
            }

            if(model.ext.get(Metadata.CHANGE_REF)) {
                changes << model.ext.get(Metadata.CHANGE_REF)
            } else {
                changes << EMPTY_CHANGE_REF
            }

            groupDescriptions.each { lvl, lvlName ->
                changes << lvlName
            }

            if(subtype) changes << subtype
            changes << model.name

            changes << changeToRender.renderedText

            if(suppressKeyDisplayList.contains(key)) {
                changes << before
                changes << after
            } else {
                changes << (before ? "$key: $before" : '')
                changes << (after ? "$key: $after" : '')
            }
        } catch (Exception e) {
            log.error "Error reading unpacked changelog key=$key rowdata=$rowData \n  ${e.toString()}"
            return []
        }
        log.debug "\n$changes"
        changes
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
                    if (destinationMap.classifiedName && destinationMap.classifiedName instanceof String) {
                        elemName = "${destinationMap.classifiedName ?: ''} (${destinationMap.semanticVersion ?: ''})"
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
        builder.build(out) { Workbook workbook ->
            apply GelXlsStyles
            sheet(sheetName) { Sheet sheet ->
                buildSheet(sheet, lines)
            }
        }

    }

    void buildRows(Sheet sheet, List<List<String>> lines) {
        lines.eachWithIndex { line, int i ->
            log.debug("row $i=" + line)
            buildRow(sheet, line)
        }
    }

    private buildRow(Sheet sheet, List<String> line) {
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









