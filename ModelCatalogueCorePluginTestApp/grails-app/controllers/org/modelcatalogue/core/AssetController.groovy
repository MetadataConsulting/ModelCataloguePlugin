package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.SchemaValidatorService
import org.modelcatalogue.core.util.lists.Lists
import org.springframework.web.multipart.MultipartFile

class AssetController extends AbstractCatalogueElementController<Asset> {

    StorageService modelCatalogueStorageService
    SchemaValidatorService schemaValidatorService

    static allowedMethods = [upload: 'POST', download: 'GET']

    AssetController() {
        super(Asset, false)
    }

    protected boolean hasAdditionalIndexCriteria() {
        params.contentType
    }

    protected Closure buildAdditionalIndexCriteria() {
        if (!hasAdditionalIndexCriteria()) {
            return super.buildAdditionalIndexCriteria()
        }
        return {
            eq 'contentType', params.contentType
        }
    }

    def upload() {
        if (!modelCatalogueSecurityService.hasRole('CURATOR', getDataModel())) {
            unauthorized()
            return
        }
        MultipartFile file = request.getFile('asset')

        Asset asset = assetService.upload(params.long('id'), params.long('dataModel'), params.name, params.description, file, params.filename ?: file.originalFilename)

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

    @Override
    def delete() {
        def response = super.delete()

        modelCatalogueStorageService.delete('assets', "${params.id}")

        return response
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
            if (params.force) {
                response.setHeader("Content-disposition", "attachment; filename=\"${asset.originalFileName}\"")
            } else {
                response.setHeader("Content-disposition", "filename=\"${asset.originalFileName}\"")
            }
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

    def history(Integer max) {
        String name = getResourceName()
        Class type = resource

        params.max = Math.min(max ?: 10, 100)
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        Long id = element.id

        if (!element.latestVersionId) {
            respond Lists.wrap(params, "/${name}/${params.id}/history", Lists.lazy(params, type, {
                [type.get(id)]
            }, { 1 }))
            return
        }

        Long latestVersionId = element.latestVersionId

        def customParams = [:]
        customParams.putAll params

        customParams.sort = 'versionNumber'
        customParams.order = 'desc'

        respond Lists.fromCriteria(customParams, type, "/${name}/${params.id}/history") {
            eq 'latestVersionId', latestVersionId
        }
    }

}
