package org.modelcatalogue.core

import com.bertramlabs.plugins.karman.CloudFile
import com.bertramlabs.plugins.karman.Directory
import com.bertramlabs.plugins.karman.StorageProvider
import com.bertramlabs.plugins.karman.local.LocalCloudFile
import com.bertramlabs.plugins.karman.local.LocalStorageProvider
import grails.util.Environment
import org.springframework.web.multipart.MultipartFile

class AssetController extends AbstractExtendibleElementController<Asset> {

    ModelCatalogueStorageService modelCatalogueStorageService

    static allowedMethods = [upload: 'POST', download: 'GET']

    AssetController() {
        super(Asset, false)
    }

    def upload() {
        MultipartFile file = request.getFile('asset')
        Asset asset = params.id ? Asset.get(params.id) : new Asset()

        asset.name              = params.name ?: file.originalFilename
        asset.description       = params.description
        asset.contentType       = file.contentType
        asset.size              = file.size
        asset.originalFileName  = file.originalFilename

        asset.validate()

        if (asset.hasErrors()) {
            respond asset.errors, view: 'create' // STATUS CODE 422
            return
        }

        asset.save()

        asset.uploaded = modelCatalogueStorageService.store('assets', asset.modelCatalogueId, file.contentType, file.bytes)

        if (asset.uploaded) {
            asset.save()
        }

        respond asset
    }

    def download() {
        Asset asset = Asset.get(params.id)
        if (!asset) {
            notFound()
            return
        }

        String servingUrl = modelCatalogueStorageService.getServingUrl('assets', asset.modelCatalogueId)

        if (servingUrl) {
            redirect servingUrl
        }


        if (!modelCatalogueStorageService.exists('assets', asset.modelCatalogueId)) {
            notFound()
            return
        }

        response.setHeader("Content-disposition", "filename=${asset.originalFileName}")

        response.contentType    = asset.contentType
        response.contentLength  = asset.size
        response.outputStream << modelCatalogueStorageService.fetch('assets', asset.modelCatalogueId)
    }

}
