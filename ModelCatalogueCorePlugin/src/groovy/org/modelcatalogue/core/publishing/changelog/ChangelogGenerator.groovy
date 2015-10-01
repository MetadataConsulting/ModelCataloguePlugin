package org.modelcatalogue.core.publishing.changelog

import com.craigburke.document.core.builder.DocumentBuilder
import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.Multimap
import grails.util.GrailsNameUtils
import groovy.util.logging.Log4j
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.audit.ChangeType
import org.modelcatalogue.core.audit.DefaultAuditor
import org.modelcatalogue.core.util.ClassificationFilter
import org.modelcatalogue.core.util.delayable.Delayable
import org.modelcatalogue.core.util.docx.ModelCatalogueWordDocumentBuilder

import java.text.SimpleDateFormat

@Log4j
class ChangelogGenerator {

    final AuditService auditService
    final ClassificationService classificationService

    ChangelogGenerator(AuditService auditService, ClassificationService classificationService) {
        this.auditService = auditService
        this.classificationService = classificationService
    }

    void generateChangelog(Classification classification, OutputStream outputStream) {
        DocumentBuilder builder = new ModelCatalogueWordDocumentBuilder(outputStream)

        def customTemplate = {
            'document' font: [family: 'Calibri'], margin: [left: 20, right: 10]
            'paragraph.title' font: [color: '#13D4CA', size: 26.pt], margin: [top: 200.pt]
            'paragraph.subtitle' font: [color: '#13D4CA', size: 18.pt]
            'paragraph.description' font: [color: '#13D4CA', size: 16.pt, italic: true], margin: [left: 30, right: 30]
            'heading1' font: [size: 20, bold: true]
            'heading2' font: [size: 18, bold: true]
            'heading3' font: [size: 16, bold: true]
            'heading4' font: [size: 16]
            'heading5' font: [size: 15]
            'heading6' font: [size: 14]
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
                paragraph "Changelog for ${classification.name}", style: 'title',  align: 'center'
                paragraph(style: 'subtitle', align: 'center') {
                    text "${classification.status}"
                    lineBreak()
                    text SimpleDateFormat.dateInstance.format(new Date())
                }
                if (classification.description) {
                    paragraph(style: 'classification.description', margin: [left: 50, right: 50]) {
                        text classification.description
                    }
                }
                pageBreak()

                heading1 "Classification Changes"

                printPropertiesChanges(delayable, classification, classification)

                heading1 'Models'

                for (Model model in getModelsForClassification(classification)) {
                    printPropertiesChanges(delayable, model, classification)
                    printModelStructuralChanges(delayable, model, classification)
                }


            }
        }

        log.debug "Classification $classification.name changelog exported to Word Document"


    }

    private void printPropertiesChanges(Delayable<DocumentBuilder> builder, CatalogueElement element, Classification classification) {
        builder.with {
            heading2 "$element.name ($element.combinedVersion, $element.status)", ref: "${element.getId()}"

            if (getChanges(element, classification, ChangeType.NEW_VERSION_CREATED)) {
                paragraph {
                    text "New version "
                    text element.versionNumber, font: [bold: true]
                    text " created"
                }
            } else if (getChanges(element, classification, ChangeType.NEW_ELEMENT_CREATED)) {
                paragraph "New ${GrailsNameUtils.getNaturalName(element.class.name)} created"
            } else if (getChanges(element, classification, ChangeType.ELEMENT_DEPRECATED)) {
                paragraph {
                    text "${GrailsNameUtils.getNaturalName(element.class.name)} has been "
                    text "deprecated", font: [bold: true]
                }
            }

            List<Change> changedProperties = getChanges(element, classification, ChangeType.PROPERTY_CHANGED, ChangeType.METADATA_CREATED, ChangeType.METADATA_DELETED, ChangeType.METADATA_UPDATED)

            if (changedProperties) {
                Map<String, List<String>> rows = new TreeMap<String, List<String>>().withDefault {['', '']}

                for(Change change in changedProperties) {
                    String propLabel = change.type == ChangeType.PROPERTY_CHANGED ? GrailsNameUtils.getPropertyName(change.property) : change.property
                    List<String> vals = rows[propLabel]
                    vals[0] = vals[0] ?: valueForPrint(change.oldValue)
                    vals[1] = valueForPrint(change.newValue)
                    rows[propLabel] = vals
                }

                paragraph font: [bold: true], "Changed Properties"

                printChangesTable builder, rows
            }


        }
    }

    private void printChangesTable(Delayable<DocumentBuilder> builder, Map<String, List<String>> rows) {
        builder.table {
            row {
                cell "Property"
                cell "Old Value"
                cell "New Value"
            }
            for (Map.Entry<String, List<String>> change in rows) {
                row {
                    cell change.key
                    cell change.value[0]
                    cell change.value[1]
                }
            }
        }
    }

    private void printModelStructuralChanges(Delayable<DocumentBuilder> builder, Model model, Classification classification) {
        List<Change> relationshipChanges = getChanges(model, classification, ChangeType.RELATIONSHIP_CREATED, ChangeType.RELATIONSHIP_DELETED, ChangeType.RELATIONSHIP_ARCHIVED, ChangeType.RELATIONSHIP_METADATA_CREATED, ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED)
        log.info "Found ${relationshipChanges.size()} relationship changes: $relationshipChanges"

        Multimap<String, Change> byDestination = LinkedHashMultimap.create()

        for (Change ch in relationshipChanges) {
            byDestination.put "${getRelationshipType(ch)}:${getDestinationId(ch)}".toString(), ch
        }

        handleRelationshipChanges(builder, model, byDestination, RelationshipType.hierarchyType, "Changed Child Models", "New child model", "Child model removed")
        handleRelationshipChanges(builder, model, byDestination, RelationshipType.containmentType, "Changed Data Elements", "New data element", "Data element removed")
        handleRelationshipChanges(builder, model, byDestination, RelationshipType.synonymType, "Changed Synonyms", "New synonym", "Synonym removed")
        handleRelationshipChanges(builder, model, byDestination, RelationshipType.relatedToType, "Changed Relations", "Newly related", "No longer related")
        handleRelationshipChanges(builder, model, byDestination, RelationshipType.baseType, "Changed Bases", "Newly based on", "No longer based on")
    }

    private void handleRelationshipChanges(Delayable<DocumentBuilder> builder, CatalogueElement owner, Multimap<String, Change> byDestination, RelationshipType type, String changesSummaryHeading, String newRelationshipNote, String removedRelationshipNote) {
        if (byDestination.keySet().any { it.startsWith(type.name) }) {
            builder.heading3 changesSummaryHeading
        }
        for (CatalogueElement destination in owner.getOutgoingRelationsByType(type)) {
            Set<Change> changes = byDestination.removeAll("${type.name}:${destination.getLatestVersionId() ?: destination.getId()}".toString())
            if (changes) {
                builder.heading4 "${destination.name} ($destination.combinedVersion, $destination.status)"
                if (changes.any { it.type == ChangeType.RELATIONSHIP_CREATED}) {
                    builder.paragraph newRelationshipNote
                }
                Set<Change> metadataChanges = changes.findAll { it.type in [ChangeType.RELATIONSHIP_METADATA_CREATED, ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED]}
                if (metadataChanges) {
                    builder.paragraph "Updated Relationship Metadata", font: [bold: true]

                    Map<String, List<String>> rows = new TreeMap<String, List<String>>().withDefault {['', '']}

                    for(Change change in metadataChanges) {
                        String propName = getRelationshipMetadataName(change)
                        List<String> vals = rows[propName]
                        vals[0] = (getOldRelationshipMetadataValue(change)?.toString() ?: '')
                        vals[1] = (getNewRelationshipMetadataValue(change)?.toString() ?: '')
                        rows[propName] = vals
                    }

                    printChangesTable builder, rows

                }
            }
        }

        Set<String> otherHierarchyChanges = byDestination.keySet().findAll { it.startsWith("${type.name}:") }

        for (String key in otherHierarchyChanges) {
            Set<Change> rest = byDestination.removeAll(key)
            Change deleteChange = new ArrayList<Change>(rest).reverse().find { it.type == ChangeType.RELATIONSHIP_DELETED}

            if (!deleteChange) {
                continue
            }

            def value = DefaultAuditor.readValue(deleteChange.oldValue)

            builder.heading4 "${value.destination.name} (${value.destination.latestVersionId}.${value.destination.versionNumber}, $value.destination.status)"
            builder.paragraph removedRelationshipNote
        }
    }

    private static String getRelationshipMetadataName(Change ch) {
        switch (ch.type) {
            case [ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED]:
                return DefaultAuditor.readValue(ch.oldValue).name
            case ChangeType.RELATIONSHIP_METADATA_CREATED:
                return DefaultAuditor.readValue(ch.newValue).name

            default:
                throw new IllegalArgumentException("Cannot get old relationship metadata value from $ch")
        }
    }

    private static String getOldRelationshipMetadataValue(Change ch) {
        switch (ch.type) {
            case [ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED]:
                return DefaultAuditor.readValue(ch.oldValue).extensionValue
            case ChangeType.RELATIONSHIP_METADATA_CREATED:
                return ''

            default:
                throw new IllegalArgumentException("Cannot get old relationship metadata value from $ch")
        }
    }

    private static String getNewRelationshipMetadataValue(Change ch) {
        switch (ch.type) {
            case [ChangeType.RELATIONSHIP_METADATA_CREATED, ChangeType.RELATIONSHIP_METADATA_UPDATED]:
                return DefaultAuditor.readValue(ch.newValue).extensionValue
            case ChangeType.RELATIONSHIP_METADATA_DELETED:
                return ''

            default:
                throw new IllegalArgumentException("Cannot get new relationship metadata value from $ch")
        }
    }

    private static Object getRelationship(Change ch) {
        switch (ch.type) {
            case [ChangeType.RELATIONSHIP_CREATED, ChangeType.RELATIONSHIP_ARCHIVED]:
                return DefaultAuditor.readValue(ch.newValue)
            case ChangeType.RELATIONSHIP_DELETED:
                return DefaultAuditor.readValue(ch.oldValue)
            case [ChangeType.RELATIONSHIP_METADATA_CREATED, ChangeType.RELATIONSHIP_METADATA_UPDATED]:
                return DefaultAuditor.readValue(ch.newValue).relationship
            case ChangeType.RELATIONSHIP_METADATA_DELETED:
                return DefaultAuditor.readValue(ch.oldValue).relationship

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

    private List<Change> getChanges(CatalogueElement element, Classification classification, ChangeType... types) {
        auditService.getChanges(element, sort: 'dateCreated', order: 'asc'){
            ne 'undone', true
            isNull 'parentId'
            gte 'dateCreated', classification.dateCreated
            if (types) {
                inList 'type', types.toList()
            }
        }.items
    }

    private Collection<Model> getModelsForClassification(Classification classification) {
        classificationService.classified(Model, ClassificationFilter.includes(classification)).list(sort: 'name')
    }

    private static String valueForPrint(String storedValue) {
        if (!storedValue) {
            return ''
        }
        def value = DefaultAuditor.readValue(storedValue)
        if (!value) {
            return ''
        }
        if (value instanceof CharSequence) {
            return value
        }
        if (value instanceof CatalogueElement) {
            return value.name
        }
        return value.toString()
    }


}
