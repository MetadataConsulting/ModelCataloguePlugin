package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation

class CsvTransformationController extends AbstractRestfulController<CsvTransformation>{

    CsvTransformationController() {
        super(CsvTransformation)
    }

    @Override
    protected bindRelations(CsvTransformation instance, Object objectToBind) {
        if (objectToBind.columnDefinitions != null) {
            for (definition in objectToBind.columnDefinitions) {
                ColumnTransformationDefinition columnTransformationDefinition = new ColumnTransformationDefinition(
                        transformation: instance,
                        source: definition.source?.id ? DataElement.get(definition.source.id) : null,
                        destination: definition.destination?.id ? DataElement.get(definition.destination.id) : null,
                        header: definition.header
                )
                columnTransformationDefinition.save(failOnError: true)
                instance.addToColumnDefinitions columnTransformationDefinition
            }
        }
    }

    // column definitions deleted on cascade
    protected checkAssociationsBeforeDelete(CsvTransformation instance) { }

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
