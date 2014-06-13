package org.modelcatalogue.core

import com.bertramlabs.plugins.karman.CloudFile
import com.bertramlabs.plugins.karman.Directory
import com.bertramlabs.plugins.karman.StorageProvider
import com.bertramlabs.plugins.karman.local.LocalCloudFile
import com.bertramlabs.plugins.karman.local.LocalStorageProvider
import grails.util.Environment
import org.springframework.web.multipart.MultipartFile

class AssetController extends AbstractExtendibleElementController<Asset> {

    static allowedMethods = [upload: 'POST']

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

        asset.save flush: true

        CloudFile stored = getCloudFile(asset.modelCatalogueId)

        stored.bytes = file.bytes
        stored.contentType(file.contentType)
        stored.save()

        asset.downloadUrl = createLink(controller: 'asset', action: 'download', id: asset.id, absolute: true)
        asset.save()

        respond asset
    }

    def download() {
        Asset asset = Asset.get(params.id)
        if (!asset) {
            notFound()
            return
        }

        CloudFile stored = getCloudFile(asset.modelCatalogueId)

        if (!stored.exists()) {
            notFound()
            return
        }

        response.setHeader("Content-disposition", "filename=${asset.originalFileName}")

        response.contentType    = asset.contentType
        response.contentLength  = asset.size
        response.outputStream << stored.bytes
        response.flushBuffer()
    }

    protected CloudFile getCloudFile(String assetFileName) {
        String providerName = grailsApplication.config.modelcatalogue.karman.provider ?: 'local'
        String directoryName = grailsApplication.config.modelcatalogue.karman.directory ?: 'assets'

        StorageProvider provider = StorageProvider.create(provider: providerName)

        if (provider instanceof LocalStorageProvider) {
            provider.basePath = grailsApplication.config.modelcatalogue.karman.basePath ? grailsApplication.config.modelcatalogue.karman.basePath : "${System.getProperty(Environment.currentEnvironment == Environment.DEVELOPMENT ? "java.io.tmpdir" : "user.dir")}/modelcatalogue/storage"
        }

        Directory directory = provider.getDirectory(directoryName)
        directory.mkdirs()
        directory.getFile(assetFileName)
    }

}
