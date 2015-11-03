package org.modelcatalogue.core

import org.modelcatalogue.core.export.inventory.ModelToDocxExporter
import org.modelcatalogue.core.export.inventory.ModelToXlsxExporter
import org.modelcatalogue.core.publishing.changelog.ChangelogGenerator
import org.modelcatalogue.core.util.Lists

class ModelController extends AbstractCatalogueElementController<Model> {

    def modelService

    ModelController() {
        super(Model, false)
    }

    @Override
    def index(Integer max) {
        if (!params.boolean("toplevel")) {
            return super.index(max)
        }
        if(params.status && params.status.toLowerCase() != 'finalized' && !modelCatalogueSecurityService.hasRole('VIEWER')) {
            notAuthorized()
            return
        }
        handleParams(max)

        respond Lists.wrap(params, "/${resourceName}/", modelService.getTopLevelModels(params))
    }

    def inventoryDoc() {
        Model model = Model.get(params.id)

        Long modelId = model.id
        def assetId= assetService.storeReportAsAsset(
                name: "${model.name} report as MS Word Document",
                originalFileName: "${model.name}-${model.status}-${model.version}.docx",
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )  { OutputStream out ->
            new ModelToDocxExporter(Model.get(modelId), modelService).export(out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }



    def inventorySpreadsheet() {
        Model model = Model.get(params.id)

        Long modelId = model.id
        def assetId= assetService.storeReportAsAsset(
                name: "${model.name} report as MS Excel Document",
                originalFileName: "${model.name}-${model.status}-${model.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )  { OutputStream out ->
            new ModelToXlsxExporter(Model.get(modelId), modelService).export(out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    def changelogDoc() {
        Model model = Model.get(params.id)

        Long modelId = model.id
        def assetId = assetService.storeReportAsAsset(
                name: "${model.name} changelog as MS Word Document",
                originalFileName: "${model.name}-${model.status}-${model.version}-changelog.docx",
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ) { OutputStream out ->
            new ChangelogGenerator(auditService, modelService).generateChangelog(Model.get(modelId), out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

}
