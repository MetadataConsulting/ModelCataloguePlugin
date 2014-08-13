package org.modelcatalogue.core.actions

import org.modelcatalogue.core.CatalogueElement

/**
 * Created by ladin on 04.08.14.
 */
class ActionDependency {

    /**
     * The action which depends on performing the action specified as provider.
     */
    Action dependant

    /**
     * The action which is needed to be performed before the action specified as dependant.
     */
    Action provider

    static belongsTo = [dependant: Action, provider: Action]

    static constraints = {
        dependant validator: { Action val, ActionDependency obj ->
            if (obj.provider == val) {
                return false
            }
            if (obj.provider && obj.provider.batch?.id != val.batch?.id) {
                return "Actions must be from same batch"
            }
            return true
        }
    }
}
