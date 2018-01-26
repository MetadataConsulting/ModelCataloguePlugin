package org.modelcatalogue.core

import groovy.transform.CompileStatic

@CompileStatic
class CatalogueElementDeleteBlocker {
    CatalogueElement elementTargetedToDeletion
    List<DeleteBlocker> deleteBlockerList
}
