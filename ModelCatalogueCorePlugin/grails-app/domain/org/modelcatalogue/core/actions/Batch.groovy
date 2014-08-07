package org.modelcatalogue.core.actions

/**
 * Group of related actions.
 */
class Batch {

    String name

    // time stamping
    Date dateCreated
    Date lastUpdated

    static hasMany = [actions: Action]

    static constraints = {
        name size: 1..255
    }

}
