package org.modelcatalogue.core.forms

import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.export.inventory.CatalogueElementToXlsxExporter
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.crf.model.CaseReportForm
import org.modelcatalogue.crf.serializer.CaseReportFormSerializer
import org.modelcatalogue.crf.preview.CaseReportFormPreview
import org.springframework.http.HttpStatus
import org.springframework.validation.Errors

class FormGeneratorController {

    def modelCatalogueSecurityService
    def executorService
    def auditService
    def assetService
    def modelToFormExporterService

    def generateForm() {
        Long modelId = params.id as Long
        DataClass model = DataClass.findById(modelId)

        if (!model) {
            render status: HttpStatus.NOT_FOUND
            return
        }

        Asset asset = new Asset(
                dataModel: model.dataModel,
                name: "$model.name Case Report Form",
                originalFileName: "$model.name Case Report Form.xls",
                description: "Your form will be available in this asset soon. Use Refresh action to reload",
                status: ElementStatus.PENDING,
                contentType: CatalogueElementToXlsxExporter.EXCEL.name,
                size: 0
        )

        asset.save(flush: true, failOnError: true)

        Long id = asset.id

        Long authorId = modelCatalogueSecurityService.currentUser?.id

        executorService.submit {
            auditService.withDefaultAuthorId(authorId) {
                try {

                    DataClass theModel = DataClass.get(modelId)
                    log.info "Generating form for $theModel"
                    CaseReportForm form = modelToFormExporterService.convert(theModel)
                    Asset updated = Asset.get(id)

                    log.info "Validating form for $theModel"
                    Errors errors = modelToFormExporterService.validate(form)

                    if (errors.hasErrors()) {
                        updated.name = updated.name + " - Error during generation"
                        updated.status = ElementStatus.FINALIZED
                        updated.description = FriendlyErrors.printErrors("The form to be generated is not valid", errors)
                        updated.save(flush: true, failOnError: true)
                        return
                    }

                    log.info "Storing form for $theModel"
                    assetService.storeAssetWithStream(updated, CatalogueElementToXlsxExporter.EXCEL.name) {
                        CaseReportFormSerializer serializer = new CaseReportFormSerializer(form)
                        serializer.write(it)
                    }

                    log.info "Form for $theModel finished"
                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your form is ready. Use Download button to download it."
                    updated.save(flush: true, failOnError: true)
                } catch (e) {
                    log.error "Exception of type ${e.class} creating form ${id}", e
                    Asset updated = Asset.get(id)
                    updated.refresh()
                    updated.status = ElementStatus.FINALIZED
                    updated.name = updated.name + " - Error during generation"
                    updated.description = "Error generating form: ${e}"
                    updated.save(flush: true, failOnError: true)
                }
            }
        }

        response.setHeader("X-Asset-ID", asset.id.toString())

        redirect controller: 'asset', id: asset.id, action: 'show'
    }

    def previewForm() {
        Long modelId = params.id as Long
        DataClass model = DataClass.findById(modelId)

        if (!model) {
            render status: HttpStatus.NOT_FOUND
            return
        }


        CaseReportForm form = modelToFormExporterService.convert(model)

        response.contentType = 'text/html'

        CaseReportFormPreview preview = new CaseReportFormPreview(form)


        preview.write(response.outputStream)
        // response.outputStream.close()
    }

}
