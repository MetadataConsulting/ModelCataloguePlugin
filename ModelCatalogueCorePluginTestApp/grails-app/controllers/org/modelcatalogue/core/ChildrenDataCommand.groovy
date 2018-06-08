package org.modelcatalogue.core
import grails.validation.Validateable

@Validateable
class ChildrenDataCommand {
    Long offset
    Long max
    static constraints = {
        offset nullable: false
        max nullable: false
    }
}

