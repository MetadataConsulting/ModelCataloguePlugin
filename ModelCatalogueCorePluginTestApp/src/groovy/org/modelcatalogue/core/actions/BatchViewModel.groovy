package org.modelcatalogue.core.actions

import groovy.transform.CompileStatic

@CompileStatic
class BatchViewModel {
    Long id
    String name
    Date lastUpdated
    Map<ActionState, Integer> actionStateCount
}