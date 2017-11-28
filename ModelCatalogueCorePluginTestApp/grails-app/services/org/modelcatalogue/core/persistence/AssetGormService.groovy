package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.Asset
import groovy.transform.CompileStatic

@CompileStatic
class AssetGormService {

    @Transactional
    Asset findById(long id) {
        Asset.get(id)
    }
}
