package org.modelcatalogue.core

import org.springframework.util.DigestUtils
import org.springframework.web.multipart.MultipartFile

import java.security.DigestInputStream
import java.security.MessageDigest

class AssetController extends AbstractPublishedElementController<Asset> {

    StorageService modelCatalogueStorageService
    AssetService assetService

    static allowedMethods = [upload: 'POST', download: 'GET']

    AssetController() {
        super(Asset, false)
    }

    def upload() {
        MultipartFile file = request.getFile('asset')

        Asset asset = assetService.upload(params.long('id'), params.name, params.description, file)

        if (!asset) {
            notFound()
            return
        }

        if (asset.hasErrors()) {
            respond asset.errors, view: 'create' // STATUS CODE 422
            return
        }

        respond asset
    }

    def download() {
        Asset currentAsset = Asset.get(params.id)
        if (!currentAsset) {
            notFound()
            return
        }

        String servingUrl = modelCatalogueStorageService.getServingUrl('assets', currentAsset.modelCatalogueId)

        if (servingUrl) {
            redirect servingUrl
        }

        String assetName = null
        for (int i = currentAsset.versionNumber ; i > 0 ; i--) {
            String testedName = "${currentAsset.bareModelCatalogueId}_${i}"
            if (modelCatalogueStorageService.exists('assets', testedName)) {
                assetName = testedName
                break
            }
        }

        if (!assetName) {
            notFound()
            return
        }

        Asset asset = currentAsset.modelCatalogueId == assetName ? currentAsset : Asset.findByModelCatalogueId(assetName)

        if (!asset) {
            notFound()
            return
        }

        response.setHeader("Content-disposition", "filename=${asset.originalFileName}")

        response.contentType    = asset.contentType
        response.contentLength  = asset.size
        response.outputStream << modelCatalogueStorageService.fetch('assets', asset.modelCatalogueId)
    }

}
