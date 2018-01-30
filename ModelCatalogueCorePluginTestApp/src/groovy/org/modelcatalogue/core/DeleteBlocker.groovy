package org.modelcatalogue.core

import groovy.transform.CompileStatic

@CompileStatic
class DeleteBlocker {
    Object elementPreventsDeletion
    DeleteBlockerReason reason
}

