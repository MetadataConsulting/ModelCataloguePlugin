package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.modelcatalogue.core.util.MetadataDomainEntity

@CompileStatic
interface ElementCompared {
    MetadataDomainEntity getMetadataDomainEntity()
    String getCode()
    String getName()
}