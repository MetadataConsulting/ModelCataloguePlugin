package org.modelcatalogue.core.gel

import org.modelcatalogue.core.Model
import org.modelcatalogue.gel.exporter.ModelToDocxExporterRareDisease


class ModelController extends org.modelcatalogue.core.ModelController {

    def rareDiseasesInventoryDoc() {
        Model model = Model.get(params.id)

        Long modelId = model.id
        def assetId= assetService.storeReportAsAsset(
                name: "${model.name} report as MS Word Document",
                originalFileName: "${model.name}-${model.status}-${model.version}.docx",
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )  { OutputStream out ->
            new ModelToDocxExporterRareDisease(Model.get(modelId), modelService).export(out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

}
