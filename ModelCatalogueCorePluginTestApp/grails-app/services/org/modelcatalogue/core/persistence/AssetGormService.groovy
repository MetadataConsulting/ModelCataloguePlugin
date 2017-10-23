package org.modelcatalogue.core.persistence

import org.modelcatalogue.core.Asset
import org.springframework.transaction.annotation.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class AssetGormService {

    @Transactional
    Asset findById(long id) {
        Asset.get(id)
    }
}
