package org.modelcatalogue.core.publishing.changelog

import com.craigburke.document.core.builder.DocumentBuilder
import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.Multimap
import grails.util.GrailsNameUtils
import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.hibernate.proxy.HibernateProxyHelper
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

                delayable.whilePaused {
                    delayable.heading1 "Classification Changes"
                    printPropertiesChanges(delayable, classification, classification)
                }

                heading1 'Models'

                for (Model model in getModelsForClassification(classification)) {
                    log.info "Handling changes from Model $model.name"
                    delayable.whilePaused {
                        printPropertiesChanges(delayable, model, classification)
                        printModelStructuralChanges(delayable, model, classification)
                    }

                }
            }
        }

        log.info "Classification $classification.name changelog exported to Word Document"


    }

    private void printPropertiesChanges(Delayable<DocumentBuilder> builder, CatalogueElement element, Classification classification, int headingLevel = 2) {
        builder.with {
            "heading${Math.min(headingLevel, 5)}" "$element.name ($element.combinedVersion, $element.status)", ref: "${element.getId()}"

            if (getChanges(element, classification, ChangeType.NEW_VERSION_CREATED)) {
                requestRun()
                paragraph {
                    text "New version "
                    text element.versionNumber, font: [bold: true]
                    text " created"
                }
            } else if (getChanges(element, classification, ChangeType.NEW_ELEMENT_CREATED)) {
                requestRun()
                paragraph "New ${GrailsNameUtils.getNaturalName(element.class.name)} created"
            } else if (getChanges(element, classification, ChangeType.ELEMENT_DEPRECATED)) {
                requestRun()
                paragraph {
                    text "${GrailsNameUtils.getNaturalName(element.class.name)} has been "
                    text "deprecated", font: [bold: true]
                }
            }

            List<Change> changedProperties = getChanges(element, classification, ChangeType.PROPERTY_CHANGED, ChangeType.METADATA_CREATED, ChangeType.METADATA_DELETED, ChangeType.METADATA_UPDATED)

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

            GrailsDomainClass grailsDomainClass = Holders.grailsApplication.getDomainClass(HibernateProxyHelper.getClassWithoutInitializingProxy(element).name)

            grailsDomainClass.persistentProperties.findAll { it.oneToOne || it.manyToOne }.sort{ it.name }.each {
                def value = element.getProperty(it.name)
                if (value.respondsTo('instanceOf') && value.instanceOf(CatalogueElement)) {
                    builder.whilePaused {
                        printPropertiesChanges(builder, value as CatalogueElement, classification, headingLevel + 1)
                    }
                }
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

        Multimap<String, Change> byDestinationsAndSources = LinkedHashMultimap.create()

        for (Change ch in relationshipChanges) {
            byDestinationsAndSources.put "out:${getRelationshipType(ch)}:${getDestinationId(ch)}".toString(), ch
            byDestinationsAndSources.put "in:${getRelationshipType(ch)}:${getSourceId(ch)}".toString(), ch
        }

        handleRelationshipChanges(builder, byDestinationsAndSources, classification, RelationshipChangesCheckConfiguration.create(model, RelationshipType.hierarchyType).withChangesSummaryHeading("Changed Child Models").withNewRelationshipNote("New child model").withRemovedRelationshipNote("Child model removed"))
        handleRelationshipChanges(builder, byDestinationsAndSources, classification, RelationshipChangesCheckConfiguration.create(model, RelationshipType.containmentType).withChangesSummaryHeading("Changed Data Elements").withNewRelationshipNote("New data element").withRemovedRelationshipNote("Data element removed").withDeep(true))
        handleRelationshipChanges(builder, byDestinationsAndSources, classification, RelationshipChangesCheckConfiguration.create(model, RelationshipType.synonymType).withChangesSummaryHeading("Changed Synonyms").withNewRelationshipNote("New synonym").withRemovedRelationshipNote("Synonym removed"))
        handleRelationshipChanges(builder, byDestinationsAndSources, classification, RelationshipChangesCheckConfiguration.create(model, RelationshipType.relatedToType).withChangesSummaryHeading("Changed Relations").withNewRelationshipNote("Newly related").withRemovedRelationshipNote("No longer related"))
        handleRelationshipChanges(builder, byDestinationsAndSources, classification, RelationshipChangesCheckConfiguration.create(model, RelationshipType.baseType).withChangesSummaryHeading("Changed Bases").withNewRelationshipNote("Newly based on").withRemovedRelationshipNote("No longer based on").withIncoming(true))
    }

    private void handleRelationshipChanges(Delayable<DocumentBuilder> builder, Multimap<String, Change> byDestinationsAndSources, Classification classification, RelationshipChangesCheckConfiguration configuration) {
        builder.whilePaused {
            builder.heading3 configuration.changesSummaryHeading

            for (CatalogueElement element in (configuration.incoming ? configuration.element.getIncomingRelationsByType(configuration.type) : configuration.element.getOutgoingRelationsByType(configuration.type))) {
                builder.whilePaused {
                    if (configuration.deep) {
                        printPropertiesChanges(builder, element, classification, 4)
                    } else {
                        builder.heading4 "$element.name ($element.combinedVersion, $element.status)", ref: "${element.getId()}"
                    }
                    Set<Change> changes = byDestinationsAndSources.removeAll("${configuration.incoming ? 'in' : 'out'}:${configuration.type.name}:${element.getLatestVersionId() ?: element.getId()}".toString())
                    if (changes) {
                        builder.requestRun()

                        if (changes.any { it.type == ChangeType.RELATIONSHIP_CREATED }) {
                            builder.paragraph configuration.newRelationshipNote
                        }
                        Set<Change> metadataChanges = changes.findAll {
                            it.type in [ChangeType.RELATIONSHIP_METADATA_CREATED, ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED]
                        }
                        if (metadataChanges) {
                            builder.paragraph "Updated Relationship Metadata", font: [bold: true]

                            Map<String, List<String>> rows = new TreeMap<String, List<String>>().withDefault { ['', ''] }

                            for (Change change in metadataChanges) {
                                String propName = getRelationshipMetadataName(change) ?: ''
                                List<String> vals = rows[propName]
                                vals[0] = (getOldRelationshipMetadataValue(change)?.toString() ?: '')
                                vals[1] = (getNewRelationshipMetadataValue(change)?.toString() ?: '')
                                rows[propName] = vals
                            }

                            printChangesTable builder, rows

                        }
                    }
                }
            }
        }
        builder.whilePaused {
            Set<String> otherHierarchyChanges = byDestinationsAndSources.keySet().findAll { it.startsWith("${configuration.incoming ? 'in' : 'out'}:${configuration.type.name}:") }

            for (String key in otherHierarchyChanges) {
                Set<Change> rest = byDestinationsAndSources.removeAll(key)
                Change deleteChange = new ArrayList<Change>(rest).reverse().find { it.type == ChangeType.RELATIONSHIP_DELETED}

                if (!deleteChange) {
                    continue
                }

                builder.requestRun()

                def value = DefaultAuditor.readValue(deleteChange.oldValue)

                if (configuration.incoming) {
                    builder.heading4 "${value.destination.name} (${value.destination.latestVersionId}.${value.destination.versionNumber}, $value.destination.status)"
                } else {
                    builder.heading4 "${value.source.name} (${value.source.latestVersionId}.${value.source.versionNumber}, $value.source.status)"
                }
                builder.paragraph configuration.removedRelationshipNote
            }
        }
    }

    private static String getRelationshipMetadataName(Change ch) {
        switch (ch.type) {
            case [ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED]:
                def value = DefaultAuditor.readValue(ch.oldValue)
                return value instanceof CharSequence ? value : value?.name
            case ChangeType.RELATIONSHIP_METADATA_CREATED:
                def value = DefaultAuditor.readValue(ch.newValue)
                return value instanceof CharSequence ? value : value?.name

            default:
                throw new IllegalArgumentException("Cannot get old relationship metadata value from $ch")
        }
    }

    private static String getOldRelationshipMetadataValue(Change ch) {
        switch (ch.type) {
            case [ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED]:
                def value = org.modelcatalogue.core.audit.DefaultAuditor.readValue(ch.oldValue)
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
                def value = org.modelcatalogue.core.audit.DefaultAuditor.readValue(ch.newValue)
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
    private static Long getSourceId(Change ch) {
        def rel = getRelationship(ch)
        if (rel.source.latestVersionId) {
            return rel.source.latestVersionId
        }
        return rel.source.id
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

    private static String valueForPrint(String propertyName, String storedValue) {
        if (!storedValue) {
            return ''
        }
        def value = DefaultAuditor.readValue(storedValue)
        if (!value) {
            return ''
        }
        if (value instanceof CharSequence) {
            if (propertyName == 'enumAsString') {
                return EnumeratedType.stringToMap(value?.toString()).collect { "$it.key: $it.value" }.join('\n')
            }
        }
        if (value instanceof CatalogueElement) {
            return value.name
        }
        return value.toString()
    }


}
