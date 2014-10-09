package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.SchemaValidatorService
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile


class AssetController extends AbstractPublishedElementController<Asset> {

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
            reportCapableRespond asset.errors, view: 'create' // STATUS CODE 422
            return
        }

        reportCapableRespond asset
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

        String result = schemaValidatorService.validateSchema(modelCatalogueStorageService.fetch('assets', asset.modelCatalogueId), file.inputStream)

        if (result && !result.contains('INVALID')) {
            respond(success: true)
        } else {
            respond(errors: [[message: result]])
        }

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

        Asset asset =  getAssetWithContent(currentAsset)

        if (!asset) {
            notFound()
            return
        }

        response.setHeader("Content-disposition", "filename=${asset.originalFileName}")

        response.contentType    = asset.contentType
        response.contentLength  = asset.size
        response.outputStream << modelCatalogueStorageService.fetch('assets', asset.modelCatalogueId)
    }


    private Asset getAssetWithContent(Asset currentAsset) {
        String assetName = null
        for (int i = currentAsset.versionNumber ; i > 0 ; i--) {
            String testedName = "${currentAsset.bareModelCatalogueId}_${i}"
            if (modelCatalogueStorageService.exists('assets', testedName)) {
                assetName = testedName
                break
            }
        }

        if (!assetName) {
            return null
        }

        currentAsset.modelCatalogueId == assetName ? currentAsset : Asset.findByModelCatalogueId(assetName)
    }

}
