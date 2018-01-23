package org.modelcatalogue.core

import groovy.transform.CompileStatic
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.asset.MicrosoftOfficeDocument

@CompileStatic
class AssetMetadataService {
    static transactional = false

    Asset instantiateAssetWithMetadata(AssetMetadata assetMetadata) {
        Asset asset = new Asset()
        asset.with {
            name = assetMetadata.name
            originalFileName = assetMetadata.originalFileName
            contentType = assetMetadata.contentType
            size = 0l
            status = ElementStatus.PENDING
            description = "Your report will be available in this asset soon. Use Refresh action to reload"
        }
        asset
    }

    Asset assetReportForDataModel(DataModel dataModel, String name, MicrosoftOfficeDocument officeDocument) {
        assetReport(name, dataModel.name, dataModel.status, dataModel.version, officeDocument)
    }

    Asset assetReportForDataClass(DataClass dataClass, String name, MicrosoftOfficeDocument officeDocument) {
        assetReport(name, dataClass.name, dataClass.status, dataClass.version, officeDocument)
    }

    Asset assetReport(String name, String elementName, ElementStatus status, Long version, MicrosoftOfficeDocument officeDocument) {
        String documentType = MicrosoftOfficeDocument.documentType(officeDocument)
        String suffix = MicrosoftOfficeDocument.suffix(officeDocument)
        String contentType = MicrosoftOfficeDocument.contentType(officeDocument)
        AssetMetadata assetMetadata = new AssetMetadata(
                name: name ? name : "${elementName} report as ${documentType} Document".toString(),
                originalFileName: "${elementName}-${status}-${version}.${suffix}",
                contentType: contentType
        )
        instantiateAssetWithMetadata(assetMetadata)
    }

}
