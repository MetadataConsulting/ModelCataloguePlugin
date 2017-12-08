package org.modelcatalogue.core.publishing.changelog

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass
import grails.util.GrailsNameUtils
import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.audit.ChangeType
import org.modelcatalogue.core.audit.LoggingAuditor
import org.modelcatalogue.core.comments.Comment
import org.modelcatalogue.core.comments.CommentsService
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.util.HibernateHelper

abstract class AbstractChangeLogGenerator {
    protected final AuditService auditService
    protected final DataClassService dataClassService
    protected final CommentsService commentsService
    protected final Integer depth
    protected final Boolean includeMetadata
    protected final Map<Long, List<Comment>> commentsCache = [:]

    AbstractChangeLogGenerator(AuditService auditService, DataClassService dataClassService, Integer depth = 3, Boolean includeMetadata = true) {
        this.auditService = auditService
        this.dataClassService = dataClassService
        try {
            commentsService = Holders.applicationContext.getBean(CommentsService)
        } catch (Exception ignored) {
            commentsService = null
            log.info "Comments are not enabled for this catalogue."
        }
        this.depth = depth
        this.includeMetadata = includeMetadata
    }

    abstract void generateChangelog(DataClass dataClass, OutputStream outputStream)

    protected static String getDisplayVersion(CatalogueElement model) {
        model.modelCatalogueId ?: model.latestVersionId ?: model.id
    }

    protected List<DataClass> collectDataClasses(DataClass dataClass) {
        dataClassService.getInnerClasses(dataClass, depth).items
    }

    protected List<Comment> getComments(CatalogueElement element) {
        List<Comment> comments = commentsCache[element.getId()]

        if (comments == null) {
            comments = commentsService.getComments(element)
            commentsCache[element.getId()] = comments
        }
        comments
    }

    protected boolean isForumEnabled() {
        commentsService?.forumEnabled
    }

    /**
     * Calls the closure with each changed property value
     * @param withNestedProperty
     */
    protected static void withChangedNestedProperties(CatalogueElement element, Closure withNestedProperty) {
        GrailsDomainClass grailsDomainClass = Holders.grailsApplication.getDomainClass(getEntityClass(element).name)

        grailsDomainClass.persistentProperties.findAll { it.name != 'dataModel' && (it.oneToOne || it.manyToOne) }.sort{ it.name }.each {
            def value = element.getProperty(it.name)
            if (value.respondsTo('instanceOf') && value.instanceOf(CatalogueElement)) {
                withNestedProperty(value)
            }
        }
    }

    /**
     * Collects the properties changes rows.
     * @param element
     * @return map where the keys are the property labels to be displayed and the list may contain the old value
     * as a first item if any and the new value as the second item if any
     */
    protected Map<String, List<String>> collectChangedPropertiesRows(CatalogueElement element) {
        Map<String, List<String>> rows = new TreeMap<String, List<String>>().withDefault { ['', ''] }
        List<Change> changedProperties
        if (includeMetadata) {
            changedProperties = getChanges(element, ChangeType.PROPERTY_CHANGED, ChangeType.METADATA_CREATED,
                ChangeType.METADATA_DELETED, ChangeType.METADATA_UPDATED)
        } else {
            changedProperties = getChanges(element, ChangeType.PROPERTY_CHANGED)
        }

        if (changedProperties) {
            for (Change change in changedProperties) {
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

        }
        rows
    }

    protected static String getRelationshipMetadataName(Change ch) {
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

    protected static String getOldRelationshipMetadataValue(Change ch) {
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

    protected static String getNewRelationshipMetadataValue(Change ch) {
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

    protected static String getRelationshipType(Change ch) {
        getRelationship(ch)?.type?.name
    }

    protected static Long getDestinationId(Change ch) {
        def rel = getRelationship(ch)
        if (rel.destination.latestVersionId) {
            return rel.destination.latestVersionId
        }
        return rel.destination.id
    }

    protected static Long getSourceId(Change ch) {
        def rel = getRelationship(ch)
        if (rel.source.latestVersionId) {
            return rel.source.latestVersionId
        }
        return rel.source.id
    }

    protected static String valueForPrint(String propertyName, String storedValue) {
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



    protected String getUpdateText(CatalogueElement element) {
        if (getChanges(element, ChangeType.NEW_VERSION_CREATED)) {
            return "New version ${element.versionNumber} of the ${GrailsNameUtils.getNaturalName(element.class.name)}"
        }

        if (getChanges(element, ChangeType.NEW_ELEMENT_CREATED)) {
            return "New ${GrailsNameUtils.getNaturalName(element.class.name)}"
        }

        if (getChanges(element, ChangeType.ELEMENT_DEPRECATED)) {
            return "${GrailsNameUtils.getNaturalName(element.class.name)} has been deprecated "
        }

        List<Change> changedProperties
        if (includeMetadata) {
            changedProperties = getChanges(element, ChangeType.PROPERTY_CHANGED, ChangeType.METADATA_CREATED,
                ChangeType.METADATA_DELETED, ChangeType.METADATA_UPDATED)
        } else {
            changedProperties = getChanges(element, ChangeType.PROPERTY_CHANGED)
        }

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

            return "${changedPropertiesLabels.join(', ')} ${changedPropertiesLabels.size() > 1 ? 'have' : 'has'} been changed"
        }

        GrailsDomainClass grailsDomainClass = Holders.grailsApplication.getDomainClass(HibernateHelper.getEntityClass(element).name)

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

    protected List<Change> getChanges(CatalogueElement element, ChangeType... types) {
        auditService.getChanges(element, sort: 'dateCreated', order: 'asc'){
            ne 'undone', true
            isNull 'parentId'
            if (types) {
                inList 'type', types.toList()
            }
        }.items
    }

    protected List<Change> getChangesAfterDate(CatalogueElement element, Date startDate, ChangeType... types) {
        auditService.getChanges(element, sort: 'dateCreated', order: 'asc'){
            ne 'undone', true
            isNull 'parentId'
            gt 'dateCreated', startDate

            if (types) {
                inList 'type', types.toList()
            }
        }.items
    }

    protected Map<String, RelationshipChangeItem> collectRelationshipChanges(RelationshipChangesCheckConfiguration configuration) {
        Map<String, RelationshipChangeItem> changeItemsByHeading = new TreeMap<String, RelationshipChangeItem>().withDefault {
            new RelationshipChangeItem()
        }
        for (CatalogueElement element in configuration.relations) {
            String heading = "$element.name (${getDisplayVersion(element)}, $element.status)"

            Set<Change> changes = configuration.getChanges(element)
            if (changes) {

                if (changes.any { it.type == ChangeType.RELATIONSHIP_CREATED }) {
                    changeItemsByHeading[heading].title = configuration.newRelationshipNote
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

                    changeItemsByHeading[heading].metadataChanges = rows

                }
            } else if (configuration.deep) {
                String update = getUpdateText(element)
                if (update) {
                    changeItemsByHeading[heading].title = "$update\n (See following)"
                }
            }
        }


        for (Set<Change> rest in configuration.otherChanges) {
            Change deleteChange = new ArrayList<Change>(rest).reverse().find {
                it.type == ChangeType.RELATIONSHIP_DELETED
            }

            if (!deleteChange) {
                continue
            }

            def value = LoggingAuditor.readValue(deleteChange.oldValue)

            String heading = value.destination.classifiedName

            if (configuration.incoming) {
                heading = value.source.classifiedName
            }

            changeItemsByHeading[heading].title = configuration.removedRelationshipNote
        }
        changeItemsByHeading
    }
}
