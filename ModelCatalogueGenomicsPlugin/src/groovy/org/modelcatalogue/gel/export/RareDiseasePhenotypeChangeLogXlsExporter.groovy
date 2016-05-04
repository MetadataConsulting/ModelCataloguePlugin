package org.modelcatalogue.gel.export

import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import org.modelcatalogue.builder.spreadsheet.api.Sheet
import org.modelcatalogue.builder.spreadsheet.api.SpreadsheetBuilder
import org.modelcatalogue.builder.spreadsheet.api.Workbook
import org.modelcatalogue.builder.spreadsheet.poi.PoiSpreadsheetBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.audit.ChangeType
import org.modelcatalogue.core.publishing.changelog.AbstractChangeLogGenerator

import static org.modelcatalogue.core.audit.ChangeType.*
import static org.modelcatalogue.gel.export.RareDiseasePhenotypeChangeLogXlsExporter.RareDiseaseChangeType.*

/**
 * Created by rickrees on 18/04/2016.
 *
 */
@Log4j
//@CompileStatic
class RareDiseasePhenotypeChangeLogXlsExporter extends AbstractChangeLogGenerator {

    public static final String PHENOTYPES_SHEET = 'HPO & Clinical tests change log'
    public static final String ELIGIBILITY_SHEET = 'Eligibility Criteria change log'

    static final String CHANGE_REF = ''
    public static final String PHENOTYPE = 'Phenotype'
    public static final String CLINICAL_TESTS = 'Clinical tests'
    public static final String GUIDANCE = 'Guidance'
    public static final ArrayList<ChangeType> TOP_LEVEL_RELATIONSHIP_TYPES = [RELATIONSHIP_DELETED, RELATIONSHIP_CREATED]
    public static final ArrayList<ChangeType> DETAIL_CHANGE_TYPES = [RELATIONSHIP_DELETED, RELATIONSHIP_CREATED, RELATIONSHIP_METADATA_UPDATED, METADATA_UPDATED, METADATA_CREATED, METADATA_DELETED, RELATIONSHIP_METADATA_CREATED, RELATIONSHIP_METADATA_DELETED, PROPERTY_CHANGED]
    def suppressKeyDisplayList = ['parent of', 'child of', 'contains', 'name', 'description']
    def ignoreKeyListForDeletions = ['child of','contains', 'Class Type']
    def ignoreKeyListForCreations = ['parent of', 'Class Type']
    def ignoreKeyList = ['parent of', 'child of','contains', 'Class Type']

    boolean isEligibilityReport

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


    RareDiseasePhenotypeChangeLogXlsExporter(AuditService auditService, DataClassService dataClassService, Integer depth = 5, Boolean includeMetadata = false) {
        super(auditService, dataClassService, depth, includeMetadata)
    }

    @Override
    void generateChangelog(DataClass model, OutputStream outputStream) {}

    public void exportPhenotypes(DataClass model, OutputStream out) {
        String sheetName = PHENOTYPES_SHEET
        export(model, out, sheetName)
    }

    public void exportEligibilityCriteria(DataClass model, OutputStream out) {
        isEligibilityReport = true
        String sheetName = ELIGIBILITY_SHEET
        export(model, out, sheetName)
    }

    private void export(DataClass model, OutputStream out, String sheetName){
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
                String groupDescription = "$model.name (${model.combinedVersion})"
            log.debug("$level $groupDescription")
                groupDescriptions.put(level, groupDescription)
                break

            case [3]:
                String groupDescription = "$model.name (${model.combinedVersion})"
                log.debug("$level $groupDescription")
                groupDescriptions.put(level, groupDescription)
                break

            case [4]:
                String groupDescription = "$model.name (${model.combinedVersion})"
                log.debug("$level $groupDescription")
                groupDescriptions.put(level, groupDescription)
                break


            case 5:
                log.debug "level 5 searching... $model.name"
                lines = generateLine(model, lines, groupDescriptions, level)
                log.debug("$level $model.name")
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

    // operates at level 5
    private List<String> generateLine(CatalogueElement model, List lines, groupDescriptions, level) {
        String subtype = null

        if(!isEligibilityReport) {
            if (model.name.matches("(?i:.*Phenotype.*)")) {

                subtype = PHENOTYPE
                checkChangeLog(model, lines, subtype, groupDescriptions, level, TOP_LEVEL_RELATIONSHIP_TYPES)
                iterateChildren(model, lines, subtype, groupDescriptions, level, DETAIL_CHANGE_TYPES)

            } else if (model.name.matches("(?i:.*Clinical Test.*)")) {

                subtype = CLINICAL_TESTS
                checkChangeLog(model, lines, subtype, groupDescriptions, level, TOP_LEVEL_RELATIONSHIP_TYPES)
                iterateChildren(model, lines, subtype, groupDescriptions, level, DETAIL_CHANGE_TYPES)

            } else if (model.name.matches("(?i:.*Guidance.*)")) {
                subtype = GUIDANCE
                checkChangeLog(model, lines, subtype, groupDescriptions, level, [PROPERTY_CHANGED])
            }
        } else {
            if (model.name.matches("(?i:.*Eligibility.*)")) {
                checkChangeLog(model, lines, subtype, groupDescriptions, level, TOP_LEVEL_RELATIONSHIP_TYPES)
                iterateChildren(model, lines, subtype, groupDescriptions, level, DETAIL_CHANGE_TYPES)
            }
        }
        lines
    }

    // level 5 descending into level 6
    private List<String> iterateChildren(CatalogueElement model, List lines, String subtype, groupDescriptions, level, List<ChangeType> typesToCheck) {
        model.parentOf?.each { CatalogueElement child ->
            log.debug("child $child.name")
            checkChangeLog(child, lines, subtype, groupDescriptions, level, typesToCheck)
        }
        lines
    }


    // operates at level 6
    private List<String> checkChangeLog(CatalogueElement model, List lines, String subtype, groupDescriptions, level, List<ChangeType> typesToCheck) {
        List<String> changes = []
        Map<String, List<String>> rows = new TreeMap<String, List<String>>().withDefault { ['', ''] }
        //determine changes for model
        log.debug "determine changes for $model.name"
        log.debug("${lines.size}")


        if (typesToCheck.contains(RELATIONSHIP_DELETED)) {
            changes = []
            List<Change> changed = getChanges(model, RELATIONSHIP_DELETED)
            rows = createRows(changed)
            rows.each { key, rowData ->
//                println "RELATIONSHIP_DELETED key=$key"
//                println "before=${rowData[0]}"
//                println "after${rowData[1]}"

                if(!ignoreKeyListForDeletions.contains(key)  ) {
                    changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, REMOVE_DATA_ITEM, RELATIONSHIP_DELETED)
                    if (changes) lines << changes    //add row
                }
//                println "resultant delete changes=$changes"
            }
        }


        if (typesToCheck.contains(RELATIONSHIP_CREATED)){
            changes = []
            List<Change> changed = getChanges(model, RELATIONSHIP_CREATED)
            rows = createRows(changed)
            rows.each { key, rowData ->
//                println "RELATIONSHIP_CREATED key=$key"
//                println "before=${rowData[0]}"
//                println "after${rowData[1]}"
                if(!ignoreKeyListForCreations.contains(key)  ) {
                    changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, NEW_DATA_ITEM, RELATIONSHIP_CREATED)
                    if (changes) lines << changes    //add row
                }
//                println "resultant changes=$changes"
            }
        }

        if (typesToCheck.contains(METADATA_UPDATED) || typesToCheck.contains(RELATIONSHIP_METADATA_UPDATED)){
            changes = []
            List<Change> changed = getChanges(model, METADATA_UPDATED)
            changed += getChanges(model, RELATIONSHIP_METADATA_UPDATED)

            rows = createRows(changed)
            rows.each { key, rowData ->
                if(!ignoreKeyList.contains(key)  ) {
                    changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, CHANGED_METADATA, null)
                    if (changes) lines << changes    //add row
                }
            }
        }

        if (typesToCheck.contains(METADATA_CREATED) ) {
            changes = []
            List<Change> changed = getChanges(model, METADATA_CREATED)
            rows = createRows(changed)
            rows.each { key, rowData ->
                if(!ignoreKeyList.contains(key)  ) {
                    changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, NEW_METADATA, null)
                    lines << changes    //add row
                }
            }
        }
        if ( typesToCheck.contains(RELATIONSHIP_METADATA_CREATED)) {
            changes = []
            List<Change> changed = getChanges(model, RELATIONSHIP_METADATA_CREATED)
            rows = createRows(changed)
            rows.each { key, rowData ->
                changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, NEW_METADATA, RELATIONSHIP_METADATA_CREATED)
                if (changes) lines << changes    //add row
            }
        }

        if (typesToCheck.contains(METADATA_DELETED) || typesToCheck.contains(RELATIONSHIP_METADATA_DELETED)) {
            changes = []
            List<Change> changed = getChanges(model, RELATIONSHIP_METADATA_DELETED)
            changed += getChanges(model, METADATA_DELETED)
            rows = createRows(changed)
            rows.each { key, rowData ->
                changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, REMOVE_METADATA, null)
                if (changes) lines << changes    //add row
            }
        }


        if (typesToCheck.contains(PROPERTY_CHANGED)){
            changes = []
            Map<String, List<String>> changedProperties = collectChangedPropertiesRows(model)

            changedProperties.each{ key, rowData ->

                def changeTypeToRender = CHANGE_DATA_VALUE
                if (CHANGE_DESCRIPTION.catalogChangeKey.equalsIgnoreCase(key)) {
                    changeTypeToRender = CHANGE_DESCRIPTION
                }
                if(NAME_CHANGE.catalogChangeKey.equalsIgnoreCase(key)) {
                    changeTypeToRender = NAME_CHANGE
                }
                if(GUIDANCE.equals(subtype) && TEXT_CHANGE.catalogChangeKey.equalsIgnoreCase(key) ) {
                    changeTypeToRender = TEXT_CHANGE
                }
                changes = createSingleChangeRow(key, rowData, model, subtype, groupDescriptions, changeTypeToRender, null)
                if (changes) lines << changes    //add row
            }
        }

        changes
    }

    protected Map<String, List<String>> createRows(List<Change> changes) {
        Map<String, List<String>> rows = new TreeMap<String, List<String>>().withDefault { ['', ''] }

        for (Change change in changes) {
            String propLabel = change.property
            if (change.type == ChangeType.RELATIONSHIP_METADATA_CREATED) {
                if (change.newValue != null && change.newValue.length() > 10) {
                    def jsonSlurper = new JsonSlurper()
                    def jsonMap = jsonSlurper.parseText(change.newValue)
                    propLabel = jsonMap.name
                }
            }
            propLabel = propLabel ?: ''
            List<String> vals = rows[propLabel]
            vals[0] = vals[0] ?: valueForPrint(change.property, change.oldValue)
            vals[1] = valueForPrint(change.property, change.newValue)
            rows[propLabel] = vals
        }
        rows
    }


    private List<String> createSingleChangeRow(String key, List<String> rowData, CatalogueElement model, String subtype, groupDescriptions, RareDiseaseChangeType changeToRender, ChangeType changeType) {
        List<String> changes = []

        try {
            changes << CHANGE_REF
            log.debug "Changed model =" + model

            groupDescriptions.each { lvl, lvlName ->
                changes << lvlName
            }

            if(subtype) changes << subtype
            changes << model.name

            String before = rowData.get(0)
            String after = rowData.get(1)

            if([NEW_METADATA, REMOVE_METADATA, CHANGED_METADATA].contains(changeToRender)) {
                if(before.contains('orderedMap')) {
                    before = extractDeepElement(before, key)
                }
                if(after.contains('orderedMap')) {
                    after = extractDeepElement(after, key)
                }
            }

            if ([REMOVE_DATA_ITEM, NEW_DATA_ITEM, NEW_METADATA].contains(changeToRender)) {
                before = extractChangedElement(before)
                after = extractChangedElement(after)
            }

            changes << changeToRender.renderedText

            if (!before && !after ) {       //don't print blank lines
                return []
            }

            if(suppressKeyDisplayList.contains(key)) {
                changes << before
                changes << after
            } else {
                changes << (before ? "$key: $before" : '')
                changes << (after ? "$key: $after" : '')
            }
        } catch (Exception e) {
            log.error "Error reading unpacked changelog key=$key rowdata=$rowData \n  ${e.toString()}"
            changes = []
        }
        changes
    }

    // hideous string parsing follows, there must be a better way...
    public static String extractDeepElement(String elemName, String key) {
        String deepElementValue =''

        if (elemName && elemName.length() > 15) {   //some length indicating it might be a json string
            def jsonMap
            def jsonSlurper = new JsonSlurper()
            try {
                jsonMap = jsonSlurper.parseText(elemName)
            } catch (Exception e) {
                log.error "Error attempting json parse of $elemName"
                return elemName
            }

            if (jsonMap && jsonMap.relationship) {

                def csvTokens = jsonMap.relationship.split(',')

                boolean startRead = false
                String accumulator = ''
                int countLeftBrackets, countRightBrackets = 0

                for ( entry in csvTokens ) {
                    if(entry.trim() == "elementType=${Relationship.class.name}" ) {
                        startRead = true
                        continue
                    }
                    if(startRead) {
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
                            String value = keyValues[i+1]
                            deepElementValue = value.replace('value=','').replace('}','').replace(']','')
                        }
                    }
                }

            }
        }
        deepElementValue
    }

    //probably needs more protection
    private String extractChangedElement(String elemName) {
        if (elemName && elemName.length() > 15) {   //some length indicating it might be a json string
            def jsonMap
            def jsonSlurper = new JsonSlurper()
            try {
                jsonMap = jsonSlurper.parseText(elemName)
            } catch (Exception e) {
                log.error "Error attempting json parse of $elemName"
                return elemName
            }
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
                if(destinationMap.classifiedName && destinationMap.classifiedName instanceof String) {
                    elemName = "${destinationMap.classifiedName ?: ''} (${destinationMap.semanticVersion ?: ''})"
                } else {
                    elemName = ''
                }
            }
        }
        elemName
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

    private void buildSheet(Sheet sheet, List lines) {
        sheet.with {
            row(1) {
                cell {
                    value 'Change reference'
                    style 'h3'
                    height 75
                    width 20
                }
                cell {
                    value 'Level 2 Disease Group (ID)'
                    style 'h3'
                    width 50
                }
                cell {
                    value 'Level 3 Disease Subtype (ID)'
                    style 'h3'
                    width 60
                }
                cell {
                    value 'Level 4 Specific Disorder (ID)'
                    width 60
                    style 'h3'
                }
                if (!isEligibilityReport)
                    cell {
                        value 'Phenotype /Clinical Tests/Guidance'
                        width 35
                        style 'h3'
                    }
                cell {
                    value 'Affected Data Item'
                    width 35
                    style 'h3'
                }
                cell {
                    value 'Change Type'
                    width 25
                    style 'h3'
                }
                cell {
                    value 'Current version details'
                    width 30
                    style 'h3'
                    style {wrap text}
                }
                cell {
                    value 'New version details'
                    width 30
                    style 'h3'
                    style {background('#c2efcf')}
                }
            }

            buildRows(it, lines)

        }
    }

    private void buildRows(Sheet sheet, List<List<String>> lines) {
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









