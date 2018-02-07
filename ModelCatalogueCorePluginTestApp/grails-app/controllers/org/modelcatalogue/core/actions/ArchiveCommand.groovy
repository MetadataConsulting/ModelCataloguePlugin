package org.modelcatalogue.core.actions

import grails.validation.Validateable

@Validateable
class ArchiveCommand {
    List<Long> batchIds
    static constraints = {
        batchIds nullable: false, minSize: 1
    }
}
