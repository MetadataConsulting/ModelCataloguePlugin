package org.modelcatalogue.core

import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.export.inventory.DataClassToDocxExporter
import org.modelcatalogue.core.export.inventory.DataClassToXlsxExporter
import org.modelcatalogue.core.publishing.changelog.ChangelogGenerator
import org.modelcatalogue.core.util.Lists

class DataClassController extends AbstractCatalogueElementController<DataClass> {

    def dataClassService

    DataClassController() {
        super(DataClass, false)
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

        respond Lists.wrap(params, "/${resourceName}/", dataClassService.getTopLevelDataClasses(overridableDataModelFilter, params))
    }

    def referenceTypes(Integer max){
        handleParams(max)

        Boolean all = params.boolean('all')

        DataClass dataClass = queryForResource(params.id)
        if (!dataClass) {
            notFound()
            return
        }

        respond dataModelService.classified(Lists.fromCriteria(params, ReferenceType, "/${resourceName}/${params.id}/referenceType") {
            eq "dataClass", dataClass
            if (!all && !dataClass.attach().archived) {
                ne 'status', ElementStatus.DEPRECATED
                ne 'status', ElementStatus.UPDATED
                ne 'status', ElementStatus.REMOVED
            }
        })

    }

    def inventoryDoc() {
        DataClass model = DataClass.get(params.id)

        Long modelId = model.id
        def assetId= assetService.storeReportAsAsset(
                name: "${model.name} report as MS Word Document",
                originalFileName: "${model.name}-${model.status}-${model.version}.docx",
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )  { OutputStream out ->
            new DataClassToDocxExporter(DataClass.get(modelId), dataClassService).export(out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }



    def inventorySpreadsheet() {
        DataClass model = DataClass.get(params.id)

        Long modelId = model.id
        def assetId= assetService.storeReportAsAsset(
                name: "${model.name} report as MS Excel Document",
                originalFileName: "${model.name}-${model.status}-${model.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )  { OutputStream out ->
            new DataClassToXlsxExporter(DataClass.get(modelId), dataClassService).export(out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    def changelogDoc() {
        DataClass model = DataClass.get(params.id)

        Long modelId = model.id
        def assetId = assetService.storeReportAsAsset(
                name: "${model.name} changelog as MS Word Document",
                originalFileName: "${model.name}-${model.status}-${model.version}-changelog.docx",
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ) { OutputStream out ->
            new ChangelogGenerator(auditService, dataClassService).generateChangelog(DataClass.get(modelId), out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

}
