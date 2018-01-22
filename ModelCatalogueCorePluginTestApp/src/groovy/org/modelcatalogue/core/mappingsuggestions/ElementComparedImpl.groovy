package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.modelcatalogue.core.util.MetadataDomainEntity

@CompileStatic
class ElementComparedImpl implements ElementCompared {
    MetadataDomainEntity metadataDomainEntity
    String code
    String name
}
