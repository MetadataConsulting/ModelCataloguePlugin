package org.modelcatalogue.core.publishing

import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors

@Log4j
class CloningChain extends PublishingChain {

    private final CloningContext context

    private CloningChain(CatalogueElement published, CloningContext context) {
        super(published)
        this.context = context
    }

    static CloningChain create(CatalogueElement published, CloningContext strategy) {
        return new CloningChain(published, strategy)
    }

    protected CatalogueElement doRun(Publisher<CatalogueElement> publisher) {
        CatalogueElement existing = context.findExisting(published)

        if (existing) {
            return existing
        }

        if (published.instanceOf(DataModel)) {
            context.addClone(published, context.destination)
        } else {
            startUpdating()
        }

        for (CatalogueElement element in required) {
            // required dependencies are not cloned but referenced directly
            context.addClone(element, element)
        }

        for (Collection<CatalogueElement> elements in queue) {
            for (CatalogueElement element in elements) {
                if (context.dataModel && context.dataModel != element.dataModel && context.dataModel != element) {
                    processed << element.id
                    continue
                }
                if (element.id in processed || isUpdatingInProgress(element) || isDeprecated(element)) {
                    continue
                }
                processed << element.id
                log.debug "Requesting clone creation of $element from $published"
                CatalogueElement clone = element.cloneElement(publisher, context.destination, context)
                if (clone.hasErrors()) {
                    String message = FriendlyErrors.printErrors("Draft version $clone has errors", clone.errors)
                    log.warn(message)
                    return rejectDraftDependency(clone, message)
                }
            }
        }
        return createClone()
    }

    private CatalogueElement createClone() {
        if (published.instanceOf(DataModel)) {
            // in case of data model just copy the relationships and return the destination
            context.delayRelationshipCopying(context.destination, published)
            return context.destination
        }

        if (published.archived) {
            published.errors.rejectValue('status', 'org.modelcatalogue.core.CatalogueElement.element.cannot.be.archived', 'Cannot create clone from deprecated element!')
            return published
        }

        Class<? extends CatalogueElement> type = HibernateProxyHelper.getClassWithoutInitializingProxy(published)

        GrailsDomainClass domainClass = Holders.applicationContext.getBean(GrailsApplication).getDomainClass(type.name) as GrailsDomainClass

        CatalogueElement clone = type.newInstance()

        clone.dataModel = context.destination

        for (prop in domainClass.persistentProperties) {
            if (!prop.association && published.hasProperty(prop.name) && prop.name != 'dataModel') {
                clone.setProperty(prop.name, published.getProperty(prop.name))
            }
        }

        clone.latestVersionId = null
        clone.versionNumber = 1
        clone.versionCreated = new Date()

        clone.status = ElementStatus.UPDATED
        clone.dateCreated = new Date()

        clone.beforeDraftPersisted()

        if (!clone.save(flush: true, deepValidate: false)) {
            return clone
        }

        context.addClone(published, clone)

        restoreStatus()

        clone.addToIsClonedFrom(published, skipUniqueChecking: true)

        context.delayRelationshipCopying(clone, published)

        published.afterDraftPersisted(clone)

        clone.status = ElementStatus.DRAFT
        clone.latestVersionId = clone.id

        clone.save(flush: true, deepValidate: false)
    }


    private CatalogueElement rejectDraftDependency(CatalogueElement element, String message) {
        restoreStatus()
        published.errors.reject('org.modelcatalogue.core.CatalogueElement.cannot.create.draft.dependency', "Cannot create draft of dependency ${element}, please, resolve the issue first. You'll see more details when you try to create draft manualy\n\n$message")
        published
    }

}
