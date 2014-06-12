package org.modelcatalogue.core

import org.springframework.web.multipart.MultipartFile

class AssetController extends AbstractExtendibleElementController<Asset> {

    static allowedMethods = [upload: 'POST']

    AssetController() {
        super(Asset, false)
    }

    def upload() {
        MultipartFile file = request.getFile('asset')
        Asset asset = params.id ? Asset.get(params.id) : new Asset()

        asset.name          = params.name ?: file.originalFilename
        asset.description   = params.description

        asset.validate()

        if (asset.hasErrors()) {
            respond asset.errors, view: 'create' // STATUS CODE 422
            return
        }

        asset.save flush: true

        asset.ext.contentType    = file.contentType
        asset.ext.size           = file.size.toString()

        respond asset
    }

}
