package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.SchemaValidatorService
import org.springframework.web.multipart.MultipartFile

class AssetController extends AbstractCatalogueElementController<Asset> {

    StorageService modelCatalogueStorageService
    SchemaValidatorService schemaValidatorService

    static allowedMethods = [upload: 'POST', download: 'GET']

    AssetController() {
        super(Asset, false)
    }

    def upload() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }
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

    def validateXml() {
        Asset currentAsset = Asset.get(params.id)
        if (!currentAsset) {
            respond(errors: [[message: "Current asset ${params.id} not found"]])
            return
        }

        Asset asset =  getAssetWithContent(currentAsset)

        if (!asset) {
            respond(errors: [[message: "Asset with content for ${params.id} not found"]])
            return
        }

        MultipartFile file = request.getFile('xml')

        String result = schemaValidatorService.validateSchema(modelCatalogueStorageService.fetch('assets', "${asset.id}"), file.inputStream)

        if (result && !result.contains('INVALID')) {
            respond(success: true)
        } else {
            respond(errors: [[message: result]])
        }

    }

    def content() {
        serveOrDownload(true)
    }

    def download() {
        serveOrDownload(false)
    }

    protected serveOrDownload(boolean serve) {
        Asset currentAsset = Asset.get(params.id)
        if (!currentAsset) {
            notFound()
            return
        }

        String servingUrl = modelCatalogueStorageService.getServingUrl('assets', "${currentAsset.id}")

        if (servingUrl) {
            redirect servingUrl
            return
        }

        Asset asset =  getAssetWithContent(currentAsset)

        if (!asset) {
            notFound()
            return
        }

        if (!serve) {
            response.setHeader("Content-disposition", "filename=${asset.originalFileName}")
        }

        response.contentType    = asset.contentType
        response.contentLength  = asset.size
        response.outputStream << modelCatalogueStorageService.fetch('assets',  "${asset.id}")
    }


    private Asset getAssetWithContent(Asset currentAsset) {
        if (currentAsset.countVersions() == 1) {
            if (modelCatalogueStorageService.exists('assets', "${currentAsset.id}")) {
                return currentAsset
            }
            return null
        }

        if (!currentAsset.latestVersionId) {
            return null
        }

        List<Asset> assets = Asset.where {
            latestVersionId == currentAsset.latestVersionId && versionNumber <= currentAsset.versionNumber
        }.list(sort: 'versionNumber', order: 'desc')

        for (Asset asset in assets) {
            if (modelCatalogueStorageService.exists('assets', "${asset.id}")) {
                return asset
            }
        }
        return null
    }

}
