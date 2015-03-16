package org.modelcatalogue.core.actions

import org.modelcatalogue.core.Extendible
import org.modelcatalogue.core.Extension
import org.modelcatalogue.core.util.ExtensionsWrapper
import org.modelcatalogue.core.util.FriendlyErrors

class Action implements Extendible<ActionParameter> {

    Class<? extends ActionRunner> type

    ActionState state = ActionState.PENDING

    /**
     * The text to be shown to the users.
     */
    String outcome

    /**
     * Result to be consumed by dependant actions.
     */
    String result

    // time stamping
    Date dateCreated
    Date lastUpdated

    static hasMany = [dependsOn: ActionDependency, dependencies: ActionDependency, extensions: ActionParameter]
    static mappedBy = [dependsOn: 'dependant', dependencies: 'provider']
    static belongsTo = [batch: Batch]
    static mapping = {
        // no need to optimistic locking, it usually just causes errors
        version false
    }

    static constraints = {
        outcome maxSize: 10000, nullable: true, bindable: false
        state bindable: false
        result maxSize: 1000, nullable: true, bindable: false
    }

    static transients = ['ext']

    final Map<String, String> ext = new ExtensionsWrapper(this)

    @Override
    Set<ActionParameter> listExtensions() {
        extensions
    }

    @Override
    ActionParameter addExtension(String name, String value) {
        if (getId() && isAttached()) {
            ActionParameter newOne = new ActionParameter(name: name, extensionValue: value, action: this)
            FriendlyErrors.failFriendlySaveWithoutFlush(newOne)
            addToExtensions(newOne)
            return newOne
        }
        throw new IllegalStateException("Cannot add extension before saving the element (id: ${getId()}, attached: ${isAttached()})")
    }

    @Override
    void removeExtension(ActionParameter extension) {
        removeFromExtensions(extension).save()
        extension.delete(flush: true)
    }

    @Override
    ActionParameter findExtensionByName(String name) {
        listExtensions()?.find { it.name == name }
    }

    @Override
    int countExtensions() {
        listExtensions()?.size() ?: 0
    }

    @Override
    ActionParameter updateExtension(ActionParameter old, String value) {
        if (old.extensionValue == value) {
            return
        }
        old.extensionValue = value
        FriendlyErrors.failFriendlySaveWithoutFlush(old)
    }
}
