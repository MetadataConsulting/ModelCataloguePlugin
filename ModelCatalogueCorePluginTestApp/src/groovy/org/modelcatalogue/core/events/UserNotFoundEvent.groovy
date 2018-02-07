package org.modelcatalogue.core.events

import groovy.transform.CompileStatic
import groovy.transform.ToString

@ToString
@CompileStatic
class UserNotFoundEvent implements NotFoundEvent {
    String username
}
