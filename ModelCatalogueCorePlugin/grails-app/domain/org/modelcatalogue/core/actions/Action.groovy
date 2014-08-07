package org.modelcatalogue.core.actions

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Extendible
import org.modelcatalogue.core.Extension
import org.modelcatalogue.core.util.ExtensionsWrapper

class Action implements Extendible {

    Class<? extends ActionRunner> type

    ActionState state = ActionState.PENDING

    String outcome

    // time stamping
    Date dateCreated
    Date lastUpdated

    static hasMany = [dependsOn: ActionDependency, dependencies: ActionDependency, extensions: ActionParameter]
    static mappedBy = [dependsOn: 'dependant', dependencies: 'provider']
    static belongsTo = [batch: Batch]

    static constraints = {
        outcome maxSize: 10000, nullable: true, bindable: false
        state bindable: false
    }

    static mapping = {
        dependsOn lazy: false

    }

    static transients = ['ext']

    final Map<String, String> ext = new ExtensionsWrapper(this)

    @Override
    Set<Extension> listExtensions() {
        extensions
    }

    @Override
    Extension addExtension(String name, String value) {
        ActionParameter newOne = new ActionParameter(name: name, extensionValue: value, action: this)
        newOne.save(failOnError: true)
        addToExtensions(newOne)
        newOne
    }

    @Override
    void removeExtension(Extension extension) {
        if (extension instanceof ActionParameter) {
            removeFromExtensions(extension)
            extension.delete(flush: true)
        } else {
            throw new IllegalArgumentException("Only instances of ActionParameter are supported")
        }
    }

}
