package org.modelcatalogue.core.security

import groovy.transform.Canonical
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel

@Canonical
@CompileStatic
class DataModelAuthorities {
    Long id
    Set<String> authorities
}
