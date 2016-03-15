package org.modelcatalogue.core.publishing.changelog

import com.craigburke.document.core.builder.DocumentBuilder
import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.Multimap
import grails.util.GrailsNameUtils
import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.audit.ChangeType
import org.modelcatalogue.core.audit.LoggingAuditor
import org.modelcatalogue.core.comments.Comment
import org.modelcatalogue.core.comments.CommentsService
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.util.delayable.Delayable
import org.modelcatalogue.core.util.docx.ModelCatalogueWordDocumentBuilder

import java.text.SimpleDateFormat

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass

@Log4j
class ChangelogGenerator {

    private final AuditService auditService
    private final DataClassService dataClassService
    private final CommentsService commentsService
    private final Integer exportDepth

    private final Map<Long, List<Comment>> commentsCache = [:]

    ChangelogGenerator(AuditService auditService, DataClassService dataClassService, Integer exportDepth = 3) {
        this.auditService = auditService
        this.dataClassService = dataClassService
        try {
            commentsService = Holders.applicationContext.getBean(CommentsService)
        } catch (Exception ignored) {
            commentsService = null
            log.info "Comments are not enabled for this catalogue."
        }
        this.exportDepth = exportDepth
    }

    void generateChangelog(DataClass dataClass, OutputStream outputStream) {
        log.info "Generating changelog for data class $dataClass.name ($dataClass.combinedVersion)"
        DocumentBuilder builder = new ModelCatalogueWordDocumentBuilder(outputStream)

        def customTemplate = {
            'document' font: [family: 'Calibri'], margin: [left: 30, right: 30]
            'paragraph.title' font: [color: '#13D4CA', size: 26.pt], margin: [top: 200.pt]
            'paragraph.subtitle' font: [color: '#13D4CA', size: 18.pt]
            'paragraph.description' font: [color: '#13D4CA', size: 16.pt, italic: true], margin: [left: 30, right: 30]
            'heading1' font: [size: 20, bold: true]
            'heading2' font: [size: 18, bold: true]
            'heading3' font: [size: 16, bold: true, italic: true]
            'heading4' font: [size: 14, italic: true]
            'heading5' font: [size: 13]
            'heading6' font: [size: 12, bold: true]
            'paragraph.heading1' font: [size: 20, bold: true]
            'paragraph.heading2' font: [size: 18, bold: true]
            'paragraph.heading3' font: [size: 16, bold: true]
            'paragraph.heading4' font: [size: 16]
            'paragraph.heading5' font: [size: 15]
            'paragraph.heading6' font: [size: 14]
            'cell.headerCell' font: [color: '#29BDCA', size: 12.pt, bold: true], background: '#F2F2F2'
            'cell' font: [size: 10.pt]

        }

        Delayable<DocumentBuilder> delayable = new Delayable<>(builder)

        builder.create {
            document(template: customTemplate) {
                paragraph "Changelog for ${dataClass.name}", style: 'title',  align: 'center'
                paragraph(style: 'subtitle', align: 'center') {
                    text "${dataClass.combinedVersion}"
                    lineBreak()
                    text "${dataClass.status}"
                    lineBreak()
                    text SimpleDateFormat.dateInstance.format(new Date())
                }
                if (dataClass.description) {
                    paragraph(style: 'description', margin: [left: 50, right: 50]) {
                        text dataClass.description
                    }
                }
                pageBreak()

                delayable.whilePaused {
                    delayable.heading1 "Root Data Class Changes"
                    printPropertiesChanges(delayable, dataClass)
                }

                heading1 'Data Classes'

                Collection<DataClass> classes = dataClassService.getInnerClasses(dataClass, exportDepth).items
                int counter = 1
                int size = classes.size()
                for (DataClass child in classes) {
                    log.info "[${counter++}/${size}] Processing changes from Data Class $child.name"
                    delayable.whilePaused {
                        printPropertiesChanges(delayable, child)
                        printClassStructuralChanges(delayable, child)
                    }

                }
            }
        }

        log.info "Data Class $dataClass.name changelog exported to Word Document"


    }

    private String getUpdateText(CatalogueElement element) {
        if (getChanges(element, ChangeType.NEW_VERSION_CREATED)) {
            return "New version ${element.versionNumber} of the ${GrailsNameUtils.getNaturalName(element.class.name)}"
        }

        if (getChanges(element, ChangeType.NEW_ELEMENT_CREATED)) {
            return "New ${GrailsNameUtils.getNaturalName(element.class.name)}"
        }

        if (getChanges(element, ChangeType.ELEMENT_DEPRECATED)) {
            return "${GrailsNameUtils.getNaturalName(element.class.name)} has been deprecated "
        }

        List<Change> changedProperties = getChanges(element, ChangeType.PROPERTY_CHANGED, ChangeType.METADATA_CREATED, ChangeType.METADATA_DELETED, ChangeType.METADATA_UPDATED)

        if (changedProperties) {
            Set<String> changedPropertiesLabels = new TreeSet<String>()

            for (Change change in changedProperties) {
                String propLabel = change.property
                if (change.type == ChangeType.PROPERTY_CHANGED) {
                    if (change.property == 'enumAsString') {
                        propLabel = 'Enumerations'
                    } else {
                        propLabel = GrailsNameUtils.getNaturalName(change.property)
                    }
                }
                changedPropertiesLabels << propLabel
            }

            return "${changedProperties.join(', ')} ${changedProperties.size() > 1 ? 'have' : 'has'} been changed"
        }

        GrailsDomainClass grailsDomainClass = Holders.grailsApplication.getDomainClass(getEntityClass(element).name)

        grailsDomainClass.persistentProperties.findAll { it.oneToOne || it.manyToOne }.sort { it.name }.each {
            def value = element.getProperty(it.name)
            if (value.respondsTo('instanceOf') && value.instanceOf(CatalogueElement)) {
                String change = getUpdateText(value as CatalogueElement)
                if (change) {
                    return "${GrailsNameUtils.getNaturalName(it.name)} > ${change}"
                }
            }
        }
        return null
    }

    private void printPropertiesChanges(Delayable<DocumentBuilder> builder, CatalogueElement element, int headingLevel = 2) {
        builder.with {
            "heading${Math.min(headingLevel, 5)}" "$element.name ($element.combinedVersion, $element.status)", ref: "${element.getId()}"

            if (getChanges(element, ChangeType.NEW_VERSION_CREATED)) {
                requestRun()
                paragraph {
                    text "This is a new version "
                    text element.versionNumber, font: [bold: true]
                    text " of the ${GrailsNameUtils.getNaturalName(element.class.name)}."
                }
            } else if (getChanges(element, ChangeType.NEW_ELEMENT_CREATED)) {
                requestRun()
                paragraph "This is a new ${GrailsNameUtils.getNaturalName(element.class.name)}"
            } else if (getChanges(element, ChangeType.ELEMENT_DEPRECATED)) {
                requestRun()
                paragraph {
                    text "This ${GrailsNameUtils.getNaturalName(element.class.name)} has been "
                    text " deprecated", font: [bold: true]
                }
            }


            if (commentsService?.forumEnabled) {
                builder.whilePaused {
                    builder.heading3 'Comments'

                    List<Comment> comments = commentsCache[element.getId()]

                    if (comments == null) {
                        comments = commentsService.getComments(element)
                        commentsCache[element.getId()] = comments
                    }

                    if (comments) {
                        // first comment is always a description and link
                        for (Comment comment in comments.tail()) {
                            builder.requestRun()
                            builder.with {
                                paragraph "${comment.username} (${SimpleDateFormat.dateTimeInstance.format(comment.created)})" , font: [bold: true]
                                paragraph comment.text
                            }
                        }
                    }
                }
            }


            List<Change> changedProperties = getChanges(element, ChangeType.PROPERTY_CHANGED, ChangeType.METADATA_CREATED, ChangeType.METADATA_DELETED, ChangeType.METADATA_UPDATED)

            if (changedProperties) {
                requestRun()
                Map<String, List<String>> rows = new TreeMap<String, List<String>>().withDefault {['', '']}

                for(Change change in changedProperties) {
                    String propLabel = change.property
                    if (change.type == ChangeType.PROPERTY_CHANGED) {
                        if (change.property == 'enumAsString') {
                            propLabel = 'Enumerations'
                        } else {
                            propLabel = GrailsNameUtils.getNaturalName(change.property)
                        }
                    }
                    propLabel = propLabel ?: ''
                    List<String> vals = rows[propLabel]
                    vals[0] = vals[0] ?: valueForPrint(change.property, change.oldValue)
                    vals[1] = valueForPrint(change.property, change.newValue)
                    rows[propLabel] = vals
                }

                paragraph font: [bold: true], "Changed Properties"

                printChangesTable builder, rows
            }

            GrailsDomainClass grailsDomainClass = Holders.grailsApplication.getDomainClass(getEntityClass(element).name)

            grailsDomainClass.persistentProperties.findAll { it.oneToOne || it.manyToOne }.sort{ it.name }.each {
                def value = element.getProperty(it.name)
                if (value.respondsTo('instanceOf') && value.instanceOf(CatalogueElement)) {
                    builder.whilePaused {
                        printPropertiesChanges(builder, value as CatalogueElement, headingLevel + 1)
                    }
                }
            }


        }
    }

    private void printChangesTable(Delayable<DocumentBuilder> builder, Map<String, List<String>> rows) {
        builder.table(border: [size: 1, color: '#D2D2D2'], columns: [1,2,2], font: [size: 10]) {
            row(background: '#F2F2F2') {
                cell "Property", style: 'headerCell'
                cell "Old Value", style: 'headerCell'
                cell "New Value", style: 'headerCell'
            }

            for (Map.Entry<String, List<String>> change in rows) {
                String background = "#FFFFFF"

                if (change.value[0] && !change.value[1]) {
                    background = "#F2DEDE"
                } else if (!change.value[0] && change.value[1]) {
                    background = '#DFF0D8'
                }
                row (background: background){
                    cell change.key, font: [bold: true]
                    cell change.value[0]
                    cell change.value[1]
                }
            }
        }
    }

    private void printClassStructuralChanges(Delayable<DocumentBuilder> builder, DataClass dataClass) {
        List<Change> relationshipChanges = getChanges(dataClass, ChangeType.RELATIONSHIP_CREATED, ChangeType.RELATIONSHIP_DELETED, ChangeType.RELATIONSHIP_ARCHIVED, ChangeType.RELATIONSHIP_METADATA_CREATED, ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED)

        Multimap<String, Change> byDestinationsAndSources = LinkedHashMultimap.create()

        for (Change ch in relationshipChanges) {
            byDestinationsAndSources.put "out:${getRelationshipType(ch)}:${getDestinationId(ch)}".toString(), ch
            byDestinationsAndSources.put "in:${getRelationshipType(ch)}:${getSourceId(ch)}".toString(), ch
        }

        handleRelationshipChanges(builder, byDestinationsAndSources, RelationshipChangesCheckConfiguration.create(dataClass, RelationshipType.hierarchyType).withChangesSummaryHeading("Changed Inner Data Classes").withNewRelationshipNote("New inner data class").withRemovedRelationshipNote("Inner data class removed"))
        handleRelationshipChanges(builder, byDestinationsAndSources, RelationshipChangesCheckConfiguration.create(dataClass, RelationshipType.containmentType).withChangesSummaryHeading("Changed Data Elements").withNewRelationshipNote("New data element").withRemovedRelationshipNote("Data element removed").withDeep(true))
        handleRelationshipChanges(builder, byDestinationsAndSources, RelationshipChangesCheckConfiguration.create(dataClass, RelationshipType.synonymType).withChangesSummaryHeading("Changed Synonyms").withNewRelationshipNote("New synonym").withRemovedRelationshipNote("Synonym removed"))
        handleRelationshipChanges(builder, byDestinationsAndSources, RelationshipChangesCheckConfiguration.create(dataClass, RelationshipType.relatedToType).withChangesSummaryHeading("Changed Relations").withNewRelationshipNote("Newly related").withRemovedRelationshipNote("No longer related"))
        handleRelationshipChanges(builder, byDestinationsAndSources, RelationshipChangesCheckConfiguration.create(dataClass, RelationshipType.baseType).withChangesSummaryHeading("Changed Bases").withNewRelationshipNote("Newly based on").withRemovedRelationshipNote("No longer based on").withIncoming(true))
    }

    private void handleRelationshipChanges(Delayable<DocumentBuilder> builder, Multimap<String, Change> byDestinationsAndSources, RelationshipChangesCheckConfiguration configuration) {
        builder.whilePaused {
            builder.heading3 configuration.changesSummaryHeading

            Map<String, String> titleRows = new TreeMap<String, String>()
            Map<String, Map<String, List<String>>> metadataRows = new TreeMap<String, Map<String, List<String>>>()

            builder.whilePaused {
                for (CatalogueElement element in (configuration.incoming ? configuration.element.getIncomingRelationsByType(configuration.type) : configuration.element.getOutgoingRelationsByType(configuration.type))) {
                    String heading = "$element.name ($element.combinedVersion, $element.status)"

                    Set<Change> changes = byDestinationsAndSources.removeAll("${configuration.incoming ? 'in' : 'out'}:${configuration.type.name}:${element.getLatestVersionId() ?: element.getId()}".toString())
                    if (changes) {
                        builder.requestRun()

                        if (changes.any { it.type == ChangeType.RELATIONSHIP_CREATED }) {
                            titleRows[heading] = configuration.newRelationshipNote
                        }
                        Set<Change> metadataChanges = changes.findAll {
                            it.type in [ChangeType.RELATIONSHIP_METADATA_CREATED, ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED]
                        }
                        if (metadataChanges) {

                            Map<String, List<String>> rows = new TreeMap<String, List<String>>().withDefault {
                                ['', '']
                            }

                            for (Change change in metadataChanges) {
                                String propName = getRelationshipMetadataName(change) ?: ''
                                List<String> vals = rows[propName]
                                vals[0] = (getOldRelationshipMetadataValue(change)?.toString() ?: '')
                                vals[1] = (getNewRelationshipMetadataValue(change)?.toString() ?: '')
                                rows[propName] = vals
                            }

                            metadataRows[heading] = rows

                        }
                    } else if (configuration.deep) {
                        String update = getUpdateText(element)
                        if (update) {
                            titleRows[heading] = "$update\n (See following)"
                        }
                    }
                }


                Set<String> otherHierarchyChanges = byDestinationsAndSources.keySet().findAll { it.startsWith("${configuration.incoming ? 'in' : 'out'}:${configuration.type.name}:") }

                for (String key in otherHierarchyChanges) {
                    Set<Change> rest = byDestinationsAndSources.removeAll(key)
                    Change deleteChange = new ArrayList<Change>(rest).reverse().find { it.type == ChangeType.RELATIONSHIP_DELETED}

                    if (!deleteChange) {
                        continue
                    }

                    builder.requestRun()

                    def value = LoggingAuditor.readValue(deleteChange.oldValue)

                    String heading = "${value.destination.name} (${value.destination.getCombinedVersion()}, $value.destination.status)"

                    if (configuration.incoming) {
                        heading = "${value.source.name} (${value.source.getCombinedVersion()}, $value.source.status)"
                    }

                    titleRows[heading] = configuration.removedRelationshipNote
                }

                builder.table(border: [size: 1, color: '#D2D2D2'], columns: [1] * 10, font: [size: 10]) {
                    for (Map.Entry<String, String> entry in titleRows) {
                        Map<String, List<String>> metadataChanges = metadataRows[entry.key]
                        if (entry.value || metadataChanges) {
                            String background = "#FFFFFF"

                            if (entry.value == configuration.newRelationshipNote) {
                                background = "#DFF0D8"
                            } else if (entry.value == configuration.removedRelationshipNote) {
                                background = '#F2DEDE'
                            }
                            row(background: background) {
                                cell entry.key, colspan: 5, font: [bold: true, size: 12]
                                cell(entry.value ?: 'Metadata Updated', colspan: 5)
                            }
                        }
                        if (metadataChanges) {
                            row(background: '#F2F2F2') {
                                cell 'Updated Metadata', colspan: 2,style: 'headerCell', font: [size: 10]
                                cell 'Old Value', colspan: 4, style: 'headerCell'
                                cell 'New Value', colspan: 4, style: 'headerCell'
                            }
                            for (Map.Entry<String, List<String>> metadataEntry in metadataChanges) {
                                String background = "#FFFFFF"

                                if (metadataEntry.value[0] && !metadataEntry.value[1]) {
                                    background = "#F2DEDE"
                                } else if (!metadataEntry.value[0] && metadataEntry.value[1]) {
                                    background = '#DFF0D8'
                                }
                                row(background: background) {
                                    cell metadataEntry.key, colspan: 2
                                    cell metadataEntry.value[0], colspan: 4
                                    cell metadataEntry.value[1], colspan: 4
                                }
                            }
                        }
                    }
                }
            }


            if (configuration.deep) {
                for (CatalogueElement element in (configuration.incoming ? configuration.element.getIncomingRelationsByType(configuration.type) : configuration.element.getOutgoingRelationsByType(configuration.type))) {
                    builder.whilePaused {
                        printPropertiesChanges(builder, element, 4)
                    }
                }
            }
        }
    }

    private static String getRelationshipMetadataName(Change ch) {
        switch (ch.type) {
            case [ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED]:
                def value = LoggingAuditor.readValue(ch.oldValue)
                return value instanceof CharSequence ? value : value?.name
            case ChangeType.RELATIONSHIP_METADATA_CREATED:
                def value = LoggingAuditor.readValue(ch.newValue)
                return value instanceof CharSequence ? value : value?.name

            default:
                throw new IllegalArgumentException("Cannot get old relationship metadata value from $ch")
        }
    }

    private static String getOldRelationshipMetadataValue(Change ch) {
        switch (ch.type) {
            case [ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED]:
                def value = LoggingAuditor.readValue(ch.oldValue)
                return value instanceof CharSequence ? value : value?.extensionValue
            case ChangeType.RELATIONSHIP_METADATA_CREATED:
                return ''

            default:
                throw new IllegalArgumentException("Cannot get old relationship metadata value from $ch")
        }
    }

    private static String getNewRelationshipMetadataValue(Change ch) {
        switch (ch.type) {
            case [ChangeType.RELATIONSHIP_METADATA_CREATED, ChangeType.RELATIONSHIP_METADATA_UPDATED]:
                def value = LoggingAuditor.readValue(ch.newValue)
                return value instanceof CharSequence ? value : value?.extensionValue
            case ChangeType.RELATIONSHIP_METADATA_DELETED:
                return ''

            default:
                throw new IllegalArgumentException("Cannot get new relationship metadata value from $ch")
        }
    }

    private static Object getRelationship(Change ch) {
        switch (ch.type) {
            case [ChangeType.RELATIONSHIP_CREATED, ChangeType.RELATIONSHIP_ARCHIVED]:
                return LoggingAuditor.readValue(ch.newValue)
            case ChangeType.RELATIONSHIP_DELETED:
                return LoggingAuditor.readValue(ch.oldValue)
            case [ChangeType.RELATIONSHIP_METADATA_CREATED, ChangeType.RELATIONSHIP_METADATA_UPDATED]:
                return LoggingAuditor.readValue(ch.newValue).relationship
            case ChangeType.RELATIONSHIP_METADATA_DELETED:
                return LoggingAuditor.readValue(ch.oldValue).relationship

            default:
                throw new IllegalArgumentException("Cannot get relationship type from $ch")
        }
    }

    private static String getRelationshipType(Change ch) {
        getRelationship(ch)?.type?.name
    }

    private static Long getDestinationId(Change ch) {
        def rel = getRelationship(ch)
        if (rel.destination.latestVersionId) {
            return rel.destination.latestVersionId
        }
        return rel.destination.id
    }
    private static Long getSourceId(Change ch) {
        def rel = getRelationship(ch)
        if (rel.source.latestVersionId) {
            return rel.source.latestVersionId
        }
        return rel.source.id
    }

    private List<Change> getChanges(CatalogueElement element, ChangeType... types) {
        auditService.getChanges(element, sort: 'dateCreated', order: 'asc'){
            ne 'undone', true
            isNull 'parentId'
            if (types) {
                inList 'type', types.toList()
            }
        }.items
    }

    private static String valueForPrint(String propertyName, String storedValue) {
        if (!storedValue) {
            return ''
        }
        def value = LoggingAuditor.readValue(storedValue)
        if (!value) {
            return ''
        }
        if (value instanceof CharSequence) {
            if (propertyName == 'enumAsString') {
                return Enumerations.from(value).collect { "$it.key: $it.value" }.join('\n')
            }
        }
        if (value instanceof CatalogueElement) {
            return value.name
        }
        return value.toString()
    }


}
