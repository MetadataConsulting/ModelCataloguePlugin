package org.modelcatalogue.core

import org.hibernate.SessionFactory
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.catalogueelement.reorder.AbstractReorderInternalService
import org.modelcatalogue.core.catalogueelement.reorder.DataTypeReorderInternalService
import org.modelcatalogue.core.persistence.DataTypeGormService
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists

class DataTypeController<T> extends AbstractCatalogueElementController<DataType> {

    DataTypeService dataTypeService
    SessionFactory sessionFactory
    DataTypeGormService dataTypeGormService
    DataTypeReorderInternalService dataTypeReorderInternalService

    DataTypeController() {
        super(DataType, false)
    }

    DataTypeController(Class<? extends DataType> resource) {
        super(resource, false)
    }

    def dataElements(Integer max){
        handleParams(max)
        DataType dataType = findById(params.long('id'))
        if (!dataType) {
            notFound()
            return
        }

        respond dataModelService.classified(Lists.fromCriteria(params, DataElement, "/${resourceName}/${params.id}/dataElement") {
            eq "dataType", dataType
            if (!dataType.attach().archived) {
                ne 'status', ElementStatus.DEPRECATED
                ne 'status', ElementStatus.UPDATED
                ne 'status', ElementStatus.REMOVED
            }
        })
    }

    def convert() {
        DataType dataType = findById(params.long('id'))
        if (!dataType) {
            notFound()
            return
        }

        DataType other = findById(params.destination)
        if (!other) {
            notFound()
            return
        }

        Mapping mapping = Mapping.findBySourceAndDestination(dataType, other)
        if (!mapping) {
            respond result: "Mapping is missing. Don't know how to convert value."
            return
        }

        if (!params.value) {
            respond result: "Please, enter value."
            return
        }

        def valid = dataType.validateRule(params.value)

        if (!(valid instanceof Boolean && valid)) {
            respond result: "INVALID: Please, enter valid value"
            return
        }

        def result = mapping.map(params.value)

        if (result instanceof Exception) {
            respond result: "ERROR: ${result.class.simpleName}: $result.message"
            return
        }

        respond result: result
    }


    def validateValue() {
        DataType dataType = findById(params.long('id'))
        if (!dataType) {
            notFound()
            return
        }

        if (!dataType.rule && !(dataType.instanceOf(EnumeratedType)) && dataType.countIsBasedOn() == 0) {
            respond result: "Don't know how to validate value."
            return
        }

        if (!params.value) {
            respond result: "Please, enter value."
            return
        }

        def result = dataType.validateRule(params.value)

        if (result instanceof Exception) {
            respond result: "ERROR: ${result.class.simpleName}: $result.message"
        }

        respond result: result
    }

    @Override
    protected AbstractReorderInternalService getReorderInternalService() {
        dataTypeReorderInternalService
    }

    @Override
    protected ListWrapper<DataType> getAllEffectiveItems(Integer max) {

       if (!params.long("dataModel") || sessionFactory.currentSession.connection().metaData.databaseProductName != 'MySQL'){
            return super.getAllEffectiveItems(max)
        }

       String resourceName = "/${resourceName}/"

       DataModel dataModel = dataModelGormService.findById(params.long('dataModel'))

       ListWithTotalAndType<DataType>  typeListing = dataTypeService.findAllDataTypesInModel(params, dataModel)

       ListWrapper<DataType> rapper = Lists.wrap(params, resourceName, typeListing )

       return rapper
    }

    protected DataType findById(long id) {
        dataTypeGormService.findById(id)
    }
}
