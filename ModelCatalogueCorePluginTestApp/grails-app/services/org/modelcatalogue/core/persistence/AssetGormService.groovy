package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.WarnGormErrors
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
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
    void update(Long assetId, ElementStatus status, String description) {
        DetachedCriteria<Asset> query = Asset.where { id == assetId }
        query.updateAll(status: status, description: description)
    }

    @Transactional
    void update(Long assetId, ElementStatus status, String name, String description) {
        DetachedCriteria<Asset> query = Asset.where { id == assetId }
        query.updateAll(status: status, name: name, description: description)
    }

    @Transactional
    void update(Long assetId, long size, String md5) {
        DetachedCriteria<Asset> query = Asset.where { id == assetId }
        query.updateAll(size: size, md5: md5)
    }

    @Transactional
    Asset save(Asset assetInstance) {
        if ( !assetInstance.save() ) {
            warnErrors(assetInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        assetInstance
    }

    @Transactional
    Asset finalizeAsset(Long id, DataModel dataModel, Long userId ){

        Asset assetInstance = Asset.get(id)

        if (!dataModel) {
            return assetInstance
        }
        assetInstance.dataModel = dataModel
        assetInstance.status = ElementStatus.FINALIZED
        assetInstance.description = "Your import has finished."
        assetInstance.save()
        if ( !assetInstance.save() ) {
            warnErrors(assetInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        assetInstance
    }

    @Transactional
    Asset finalizeAssetWithError(Long assetId, Exception e) {
        Asset assetInstance = Asset.get(assetId)
        assetInstance.refresh()
        assetInstance.status = ElementStatus.FINALIZED
        assetInstance.name = assetInstance.name + " - Error during upload"
        assetInstance.description = "Error importing file: ${e}"
        if ( !assetInstance.save() ) {
            warnErrors(assetInstance, messageSource)
            transactionStatus.setRollbackOnly()
        }
        assetInstance
    }
}
