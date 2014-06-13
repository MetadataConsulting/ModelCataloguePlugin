package org.modelcatalogue.core.dataarchitect

/**
 * Created by adammilward on 17/04/2014.
 */
class RowAction {
    String field
    String action
    ActionType actionType

    static constraints = {
        field nullable: false, maxSize: 255
        action nullable: false, maxSize: 255
        actionType nullable: false
    }

}
