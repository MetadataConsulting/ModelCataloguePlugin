package org.modelcatalogue.core.actions

/**
 * Group of related actions.
 */
class Batch {

    static searchable = true

    String name

    // time stamping
    Date dateCreated
    Date lastUpdated

    Boolean archived = Boolean.FALSE

    static hasMany = [actions: Action]

    static constraints = {
        name size: 1..255
    }

    static mapping = {
        sort dateCreated: 'desc'
    }

}
