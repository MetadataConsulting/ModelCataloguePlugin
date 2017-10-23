package org.modelcatalogue.core

import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.persistence.CsvTransformationGormService
import org.modelcatalogue.core.util.FriendlyErrors
import org.springframework.web.multipart.MultipartFile

class CsvTransformationController extends AbstractRestfulController<CsvTransformation>{

    def dataArchitectService

    CsvTransformationGormService csvTransformationGormService

    static allowedMethods = [transform: "POST"]

    CsvTransformationController() {
        super(CsvTransformation)
    }

    def transform() {
        if (!params.id) {
            notFound()
        }

        if (!modelCatalogueSecurityService.hasRole('USER', getDataModel())) {
            unauthorized()
            return
        }

        CsvTransformation transformation = csvTransformationGormService.findById(params.long('id'))

        if (!transformation) {
            notFound()
        }

        MultipartFile file = request.getFile('csv')

        params.separator = params.separator ?: ';'

        response.setHeader("Content-Disposition", "\"filename=${transformation.name}.csv\"")
        response.setHeader("Content-Type", "text/csv")
        file.inputStream.withReader {
            dataArchitectService.transformData(params, transformation, it, response.getWriter())
        }
    }

    @Override
    protected bindRelations(CsvTransformation instance, boolean newVersion, Object objectToBind) {
        if (objectToBind.columns != null) {
            for (definition in objectToBind.columns) {
                ColumnTransformationDefinition columnTransformationDefinition = new ColumnTransformationDefinition(
                        transformation: instance,
                        source: getByIdOrNull(definition.source),
                        destination: getByIdOrNull(definition.destination),
                        header: definition.header
                )
                FriendlyErrors.failFriendlySave(columnTransformationDefinition)
            }
        }
    }

    private static DataElement getByIdOrNull(sourceOrDestination) {
        if (!sourceOrDestination) return null
        if (sourceOrDestination instanceof JSONObject.Null) return null
        DataElement.get(sourceOrDestination.id)
    }

    // column definitions deleted on cascade
    protected checkAssociationsBeforeDelete(CsvTransformation instance) { }

    protected CsvTransformation findById(long id) {
        csvTransformationGormService.findById(id)
    }

    @Override
    protected cleanRelations(CsvTransformation instance) {
        if (instance.columnDefinitions) {
            def definitions = new ArrayList<ColumnTransformationDefinition>(instance.columnDefinitions)
            for (columnDefinition in definitions) {
                columnDefinition.transformation = null
                instance.removeFromColumnDefinitions columnDefinition
                if (columnDefinition.id) {
                    columnDefinition.delete(flush: true)
                }
            }
            instance.columnDefinitions.clear()
        }
    }
}
