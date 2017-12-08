package org.modelcatalogue.core.publishing

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass
import grails.util.Holders
import groovy.transform.CompileDynamic
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors
import rx.Observer

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

    protected CatalogueElement doRun(Publisher<CatalogueElement> publisher, Observer<String> monitor) {
        CatalogueElement existing = context.resolve(published)

        if (existing && existing != published) {
            return existing
        }

        if (published.instanceOf(DataModel)) {
            context.addResolution(published, context.destination)
        } else {
            startUpdating()
        }

        for (CatalogueElement element in required) {
            // required dependencies are not cloned but referenced directly
            context.addResolution(element, element)
        }

        for (Collection<CatalogueElement> elements in queue) {
            for (CatalogueElement element in elements) {
                if (context.destination && context.destination != element.dataModel && context.destination != element) {
                    if (element.instanceOf(DataModel)) {
                        context.addImport(element as DataModel)
                    } else {
                        context.addImport(element.dataModel)
                    }
                    processed << element.id
                    continue
                }
                if (element.id in processed || isUpdatingInProgress(element) || isDeprecated(element)) {
                    continue
                }
                processed << element.id
                log.debug "Requesting clone creation of $element from $published"
                CatalogueElement clone = element.cloneElement(publisher, context)
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

        Class<? extends CatalogueElement> type = getEntityClass(published)

        GrailsDomainClass domainClass = getGrailsDomainClass(type)

        if (!domainClass) {
            throw new IllegalStateException("Cannot find domain class for ${type}")
        }

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

        clone.beforeDraftPersisted(context)

        if (!clone.save(flush: true, deepValidate: false)) {
            return clone
        }

        context.addResolution(published, clone)

        restoreStatus()

        clone.createLinkFrom(published, RelationshipType.originType, skipUniqueChecking: true as Object)

        context.delayRelationshipCopying(clone, published)

        published.afterDraftPersisted(clone, context)

        clone.status = ElementStatus.DRAFT
        clone.latestVersionId = clone.id

        clone.save(flush: true, deepValidate: false)
    }

    @CompileDynamic
    private static GrailsDomainClass getGrailsDomainClass(Class<CatalogueElement> type) {
        Holders.applicationContext.getBean(GrailsApplication).getDomainClass(type.name) as GrailsDomainClass
    }


    private CatalogueElement rejectDraftDependency(CatalogueElement element, String message) {
        restoreStatus()
        published.errors.reject('org.modelcatalogue.core.CatalogueElement.cannot.create.draft.dependency', "Cannot create draft of dependency ${element}, please, resolve the issue first. You'll see more details when you try to create draft manualy\n\n$message")
        published
    }

}
