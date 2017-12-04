package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.context.MessageSource

class AssetGormService implements WarnGormErrors {

    MessageSource messageSource

    @Transactional(readOnly = true)
    Asset findById(long id) {
        Asset.get(id)
    }

    @Transactional
    Asset saveWithNameAndDescription(String name, String description) {
        save(new Asset(name: name, description: description))
    }

    @Transactional
    Asset saveWithNameAndDescriptionAndStatus(String name, String description, ElementStatus status) {
        save(new Asset(name: name, description: description, status: status))
    }

    @Transactional
    Asset save(Asset assetInstance) {
        if ( !assetInstance.save() ) {
            warnErrors(assetInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        assetInstance
    }
}
