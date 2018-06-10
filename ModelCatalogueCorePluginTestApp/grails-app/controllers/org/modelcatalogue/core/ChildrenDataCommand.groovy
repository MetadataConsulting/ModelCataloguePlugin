package org.modelcatalogue.core
import grails.validation.Validateable

@Validateable
class ChildrenDataCommand {
    int offset
    int max
    static constraints = {
        offset nullable: false
        max nullable: false
    }
}

