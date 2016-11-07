package org.modelcatalogue.core.publishing

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.builder.ProgressMonitor
import rx.Observer

import java.util.regex.Matcher

@CompileStatic
abstract class PublishingContext<C extends PublishingContext> {

    protected final Set<CopyAssociationsAndRelationships> pendingRelationshipsTasks = new LinkedHashSet<CopyAssociationsAndRelationships>()
    protected final Set<String> createdRelationshipHashes = new LinkedHashSet<String>()
    protected final Map<Long, Long> resolutions = [:]
    protected final Map<Long, Class<? extends CatalogueElement>> newTypes = [:]

    protected Observer<String> monitor = ProgressMonitor.NOOP

    protected DataModel dataModel

    PublishingContext(DataModel dataModel) {
        this.dataModel = dataModel
    }

    PublishingContext() {}

    Observer<String> getMonitor() {
        return monitor
    }

    static String hashForRelationship(CatalogueElement source, CatalogueElement destination, RelationshipType type) {
        "$source.id:$type.id:$destination.id"
    }

    @CompileDynamic
    static String nextPatchVersion(Object patchVersion) {
        String currentVersion = patchVersion?.toString()
        if (!currentVersion) {
            // null is considered to be 0.0.1
            return '0.0.2'
        }

        Matcher majorMinorPatch = currentVersion =~ /^(\d+)(\.(\d+))?(\.(\d+))?$/

        if (majorMinorPatch) {
            Integer major = majorMinorPatch[0][1] as Integer
            Integer minor = majorMinorPatch[0][3] as Integer ?: 0
            Integer patch = majorMinorPatch[0][5] as Integer ?: 0

            return "${major}.${minor}.${patch + 1}"
        }

        Matcher numberSuffix = currentVersion =~ /(.*?)(\d+)$/

        if (numberSuffix) {
            String base = numberSuffix[0][1]
            String suffix = numberSuffix[0][2]

            Integer suffixAsNumber = suffix as Integer

            return base + ("${suffixAsNumber + 1}".padLeft(suffix.size(), '0'))
        }

        return "${currentVersion}-01"
    }

    final void delayRelationshipCopying(CatalogueElement draft, CatalogueElement oldVersion) {
        pendingRelationshipsTasks << createCopyTask(draft, oldVersion)
    }

    protected CopyAssociationsAndRelationships createCopyTask(CatalogueElement draft, CatalogueElement oldVersion) {
        new CopyAssociationsAndRelationships(draft, oldVersion, this, false, RelationshipDirection.INCOMING, RelationshipDirection.OUTGOING)
    }

    void resolvePendingRelationships(Observer<String> monitor) {
        Integer total = pendingRelationshipsTasks.size()
        pendingRelationshipsTasks.eachWithIndex { CopyAssociationsAndRelationships it, int index ->
            monitor.onNext("Copying relationships [${(index + 1).toString().padLeft(5,'0')}/${total.toString().padLeft(5,'0')}]: $it".toString())
            it.afterDraftPersisted()
            it.copyRelationships(dataModel, createdRelationshipHashes)
        }
    }

    final C addResolution(CatalogueElement source, CatalogueElement draft) {
        resolutions[source.getId()] = draft.getId()
        return this as C
    }

    final CatalogueElement resolve(CatalogueElement original) {
        Long id = resolutions[original.getId()]
        if (id) {
            return CatalogueElement.get(id)
        }

        CatalogueElement existing = findExisting(original)

        if (existing) {
            return existing
        }

        return original
    }

    CatalogueElement findExisting(CatalogueElement original) {
        return null
    }

    DataModel getDataModel() {
        return dataModel
    }

    boolean shouldCopyRelationshipsFor(CatalogueElement catalogueElement) {
        return true
    }

    final Class<? extends CatalogueElement> getNewType(CatalogueElement element) {
        newTypes[element.getId()]
    }

    final C changeType(CatalogueElement element, Class<? extends CatalogueElement> newType) {
        newTypes[element.getId()] = newType
        this as C
    }

    final C withMonitor(Observer<String> monitor) {
        this.monitor = monitor
        this as C
    }

    final boolean isTypeChangeRequested() {
        return !newTypes.isEmpty()
    }

}
