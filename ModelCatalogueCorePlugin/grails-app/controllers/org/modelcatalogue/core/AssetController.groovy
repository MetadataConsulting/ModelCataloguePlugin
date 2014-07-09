package org.modelcatalogue.core

import org.springframework.util.DigestUtils
import org.springframework.web.multipart.MultipartFile

import java.security.DigestInputStream
import java.security.MessageDigest

class AssetController extends AbstractPublishedElementController<Asset> {

    StorageService modelCatalogueStorageService

    static allowedMethods = [upload: 'POST', download: 'GET']

    AssetController() {
        super(Asset, false)
    }

    def upload() {
        MultipartFile file = request.getFile('asset')

        if (file.size > modelCatalogueStorageService.maxFileSize) {
            Asset asset = new Asset()
            asset.errors.rejectValue('md5', 'asset.uploadfailed', "You cannot upload files greater than ${toBytes(modelCatalogueStorageService.maxFileSize)}")
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

        DigestInputStream dis = null

        try {
            MessageDigest md5 = MessageDigest.getInstance('MD5')
            dis = new DigestInputStream(file.inputStream, md5)
            modelCatalogueStorageService.store('assets', asset.modelCatalogueId, file.contentType, dis)
            asset.md5 = DigestUtils.md5DigestAsHex(md5.digest())
            asset.save()
        } catch (e) {
            log.error('Exception storing asset ' + asset.name, e)
            asset.errors.rejectValue('md5', 'asset.uploadfailed', "There were problems uploading file $file.originalFilename")
            respond asset.errors, view: 'create' // STATUS CODE 422
            return
        } finally {
            dis?.close()
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
