package org.modelcatalogue.core.actions

/**
 * Group of related actions.
 */
class Batch {

    String name
    String description

    // time stamping
    Date dateCreated
    Date lastUpdated

    Boolean archived = Boolean.FALSE

    static hasMany = [actions: Action]

    static constraints = {
        name size: 1..255
        description nullable: true, maxSize: 2000
    }

    static mapping = {
        sort 'name'
    }

}
