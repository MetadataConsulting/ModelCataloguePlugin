package org.modelcatalogue.core.actions

import org.modelcatalogue.core.Extension

/**
 * Created by ladin on 05.08.14.
 */
class ActionParameter implements Extension {
    String name
    String extensionValue

    static belongsTo = [action: Action]

    static constraints = {
        name size: 1..255
        extensionValue maxSize: 1000, nullable: true
    }

    static mapping = {
        sort 'name'
    }
}
