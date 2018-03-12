package org.modelcatalogue.core.publishing

import com.google.common.collect.ImmutableList
import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.RelationshipDefinition
import org.modelcatalogue.core.RelationshipDefinitionBuilder

@Log4j
class CopyAssociationsAndRelationships {

    private final CatalogueElement draft
    private final CatalogueElement element
    private final PublishingContext context
    private final ImmutableList<RelationshipDirection> directions
    private final boolean versionSpecificOnly

    private RelationshipService relationshipService


    CopyAssociationsAndRelationships(CatalogueElement draft, CatalogueElement element, PublishingContext context, boolean versionSpecificOnly, RelationshipDirection... directions) {
        this.draft = draft
        this.element = element
        this.context = context
        this.directions = ImmutableList.copyOf(directions)
        this.versionSpecificOnly = versionSpecificOnly

        relationshipService = Holders.applicationContext.getBean(RelationshipService)
    }

    void afterDraftPersisted() {
        log.debug("Runnig after draft hooks from '${element}' to '${draft}'...")
        element.afterDraftPersisted(draft, context)
        log.debug("... after draft hooks ran from '${element}' to '${draft}'")
    }

    void copyRelationships(DataModel dataModel, Set<String> createdRelationshipHashes) {
        log.debug("Copying relationshps from '${element}' to '${draft}'...")
        if (!context.shouldCopyRelationshipsFor(draft)) {
            return
        }

        for (RelationshipDirection direction in directions) {
            copyRelationshipsInternal(dataModel, direction, createdRelationshipHashes)
        }

        Class type = context.getNewType(element) ?: HibernateHelper.getEntityClass(draft)

        GrailsDomainClass domainClass = Holders.applicationContext.getBean(GrailsApplication).getDomainClass(type.name) as GrailsDomainClass

        for (prop in domainClass.persistentProperties) {
            if (prop.association && (prop.manyToOne || prop.oneToOne) && element.hasProperty(prop.name) && prop.name != 'dataModel') {
                def value = element.getProperty(prop.name)
                if (value instanceof CatalogueElement) {
                    draft.setProperty(prop.name, context.resolve(value))
                }
            }
        }
        log.debug("... relationships copied from '${element}' to '${draft}'")
    }

    void copyRelationshipsInternal(DataModel dataModel, RelationshipDirection direction, Set<String> createdRelationshipHashes) {

        relationshipService.eachRelationshipPartitioned(direction, element) { Relationship r ->
            if (r.relationshipType.system) {
                return
            }

            if (versionSpecificOnly && !r.relationshipType.versionSpecific) {
                return
            }

            if (dataModel && r.relationshipType.versionSpecific && r.source.status != ElementStatus.DRAFT && dataModel != r.source.dataModel) {
                return
            }


            //don't copy inherited relationships - if they are inherited the based on relationships will be copied and they will get handled by that function

            if (r.inherited) {
                return
            }


            CatalogueElement otherSide
            String hash

            if (direction == RelationshipDirection.INCOMING) {
                otherSide = context.resolve(r.source)
                //if the relationships is archived and points to a deprecated item
                //get the latest version of the item (or the preferred draft)
                if (r.archived) {
                    otherSide = context.findExisting(otherSide)
                }
                hash = PublishingContext.hashForRelationship(otherSide, draft, r.relationshipType)
            } else {
                otherSide = context.resolve(r.destination)
                //if the relationships is archived and points to a deprecated item
                //get the latest version of the item (or the preferred draft)
                if (r.archived) {
                    otherSide = context.findExisting(otherSide)
                }

                hash = PublishingContext.hashForRelationship(draft, otherSide, r.relationshipType)
            }

            if (hash in createdRelationshipHashes) {
                return
            }

            RelationshipDefinitionBuilder definitionBuilder = direction == RelationshipDirection.INCOMING ? RelationshipDefinition.create(otherSide, draft, r.relationshipType) : RelationshipDefinition.create(draft, otherSide, r.relationshipType)

            definitionBuilder
                    .withArchived(r.archived)
                    .withInherited(r.inherited)
                    .withDataModel(r.dataModel)
                    .withIncomingIndex(r.incomingIndex)
                    .withOutgoingIndex(r.outgoingIndex)
                    .withMetadata(r.ext)
                    .withSkipUniqueChecking(true)

            if (draft.status == ElementStatus.DEPRECATED) {
                definitionBuilder.withIgnoreRules(true)
            }

            Relationship created = relationshipService.link definitionBuilder.definition

            if (created.hasErrors()) {
                throw new IllegalStateException(FriendlyErrors.printErrors("Migrated relationship ${created} contains errors", created.errors))
            }


            createdRelationshipHashes << hash
        }


    }

    static boolean isOverriding(Relationship created, Relationship old) {
        if (!(created.source.status == ElementStatus.DRAFT && created.destination.status == ElementStatus.DRAFT)) {
            return false
        }
        if (old.source.status == ElementStatus.DRAFT && old.destination.status != ElementStatus.DRAFT) {
            return true
        }
        if (old.source.status != ElementStatus.DRAFT && old.destination.status == ElementStatus.DRAFT) {
            return true
        }
        return false
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        CopyAssociationsAndRelationships that = (CopyAssociationsAndRelationships) o

        if (draft != that.draft) return false
        if (element != that.element) return false

        return true
    }


    int hashCode() {
        int result
        result = draft.hashCode()
        result = 31 * result + element.hashCode()
        return result
    }

    @Override
    String toString() {
        "from $element to $draft"
    }
}
