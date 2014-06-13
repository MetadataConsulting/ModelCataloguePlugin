package org.modelcatalogue.core

import org.springframework.web.multipart.MultipartFile

class AssetController extends AbstractExtendibleElementController<Asset> {

    StorageService modelCatalogueStorageService

    static allowedMethods = [upload: 'POST', download: 'GET']

    AssetController() {
        super(Asset, false)
    }

    def upload() {
        MultipartFile file = request.getFile('asset')

        if (file.size > modelCatalogueStorageService.maxFileSize) {
            Asset asset = new Asset()
            asset.errors.rejectValue('uploaded', 'asset.uploadfailed', "You cannot upload files greater than ${toBytes(modelCatalogueStorageService.maxFileSize)}")
            respond asset.errors, view: 'create' // STATUS CODE 422
            return
        }

        Asset asset = new Asset()

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

        if (params.id) {
            Asset existing = Asset.get(params.id)

            if (!existing) {
                notFound()
                return
            }

            publishedElementService.archiveAndIncreaseVersion(existing)

            existing.name              = asset.name
            existing.description       = asset.description
            existing.contentType       = asset.contentType
            existing.size              = asset.size
            existing.originalFileName  = asset.originalFileName

            asset = existing
        }

        asset.save()

        try {
            modelCatalogueStorageService.store('assets', asset.modelCatalogueId, file.contentType, file.inputStream)
            asset.uploaded = true
            asset.save()
        } catch (e) {
            log.error('Exception storing asset ' + asset.name, e)
            asset.errors.rejectValue('uploaded', 'asset.uploadfailed', "There were problems uploading file $file.originalFilename")
            respond asset.errors, view: 'create' // STATUS CODE 422
            return
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

    private static final long GIGA = 1024 * 1024 * 1024
    private static final long MEGA = 1024 * 1024
    private static final long KILO = 1024

    String toBytes(Long value) {
        if (!value) return "0 B"

        if (value > GIGA) return String.format("%.2f GB", value / GIGA)
        if (value > MEGA) return String.format("%.2f MB", value / MEGA)
        if (value > KILO) return String.format("%.2f KB", value / KILO)
        "$value B"
    }

}
