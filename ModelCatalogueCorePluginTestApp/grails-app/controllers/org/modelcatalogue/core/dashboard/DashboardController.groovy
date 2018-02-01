package org.modelcatalogue.core.dashboard

import grails.gorm.DetachedCriteria
import groovy.transform.CompileStatic
import org.modelcatalogue.core.AbstractCatalogueElementController
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.MaxOffsetSublistUtils
import org.modelcatalogue.core.SortParamsUtils
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.catalogueelement.DataModelCatalogueElementService
import org.modelcatalogue.core.catalogueelement.ManageCatalogueElementService
import org.modelcatalogue.core.util.SearchParams
import org.modelcatalogue.core.util.lists.CustomizableJsonListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeImpl
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeWrapper
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller


class DashboardController<T extends CatalogueElement> extends AbstractCatalogueElementController<DataModel> {

    DataModelCatalogueElementService dataModelCatalogueElementService

    static allowedMethods = [
            index: 'GET'
    ]

    DashboardController() {
        super(DataModel, false)
    }

    @Override
    def index(Integer max) {

        handleParams(max)

        //only want the active models
        if(!params.status) params.status = 'active'
        if(!params.elementType) params.elementType = 'data model'

        //before interceptor deals with this security - this is only applicable to data models and users

        boolean hasRoleViewer = modelCatalogueSecurityService.hasRole('VIEWER', getDataModel())
        if(params.status && !(params.status.toString().toLowerCase() in ['finalized', 'deprecated', 'active']) && !hasRoleViewer) {
            unauthorized()
            return
        }

        ListWithTotalAndType items

        // allows you to filter imports etc quickly and easily (if not data models then use elasticsearch)
        // if there is a search param in data models then use elasticsearch

        if(params?.search){
            SearchParams searchParams = getSearchParams(max)
            if(params.elementType){
                switch (params.elementType) {
                    case "data model":
                        items = modelCatalogueSearchService.search(DataModel, searchParams)
                        break
                    case "data class":
                        items = modelCatalogueSearchService.search(DataClass, searchParams)
                        break
                    case "data element":
                        items = modelCatalogueSearchService.search(DataElement, searchParams)
                        break
                    case "data type":
                        items = modelCatalogueSearchService.search(DataType, searchParams)
                        break
                    case "all":
                        items = modelCatalogueSearchService.search(CatalogueElement, searchParams)
                        break
                    default:
                        items = modelCatalogueSearchService.search(DataModel, searchParams)
                }
            }else{
                items = modelCatalogueSearchService.search(DataModel, searchParams)
            }

        }else {



            if(params.elementType){
                switch (params.elementType) {
                    case "data model":
                        items = items = getAllEffectiveItems(max)
                        break
                    case "data class":
                        items = items = getAllEffectiveItems(max)
                        break
                    case "data element":
                        items = items = getAllEffectiveItems(max)
                        break
                    case "data type":
                        items = items = getAllEffectiveItems(max)
                        break
                    case "all":
                        items = items = getAllEffectiveItems(max)
                        break
                    default:
                        items = getAllEffectiveItems(max)
                }
            }else{
                items = getAllEffectiveItems(max)
            }



        }

        if (params.total) {
            items.totalKnownAlready(params.total as Long) // should set the total.
        }


        if (params.boolean('minimal') && items instanceof ListWithTotalAndTypeWrapper) {
            ListWithTotalAndTypeWrapper<DataModel> listWrapper = items as ListWithTotalAndTypeWrapper<DataModel>

            if (listWrapper.list instanceof CustomizableJsonListWithTotalAndType) {
                CustomizableJsonListWithTotalAndType<DataModel> customizable = listWrapper.list as CustomizableJsonListWithTotalAndType<DataModel>
                customizable.customize {
                    it.collect { CatalogueElementMarshaller.minimalCatalogueElementJSON(it) }
                }
            }
        }
        [models: items?.items, total: items.total]
    }


    @Override
    protected DataModel findById(long id) {
        dataModelGormService.findById(id)
    }

    @Override
    protected ManageCatalogueElementService getManageCatalogueElementService() {
        dataModelCatalogueElementService
    }





    /*
    * TODO: TAKEN THIS FROM MODEL CONTROLLER - NEED TO REORGANISE ALL OF THIS INTO A SERVICE BEFORE CHECKIN
    * TODO !!!!!!!!!!!!!!!!!!!!!!!
    *    * TODO !!!!!!!!!!!!!!!!!!!!!!!
    *    * TODO !!!!!!!!!!!!!!!!!!!!!!!
    *    * TODO !!!!!!!!!!!!!!!!!!!!!!!
    * */


    /**
     * override the abstract controller method so all effective items for a user is a list of data models based on the general role - rather than a specific data model role
     * i.e. we can view the basic info of data models that we aren't subscribed to
     * @param id of data model
     */

    @Override
    protected ListWrapper<DataModel> getAllEffectiveItems(Integer max) {
        ListWrapper<DataModel> items = findUnfilteredEffectiveItems(max)
        filterUnauthorized(items)
    }

    protected ListWrapper<DataModel> findUnfilteredEffectiveItems(Integer max) {
        //if you only want the active data models (draft and finalised)
        if (params.status?.toLowerCase() == 'active') {
            //if you have the role viewer you can see drafts
            if (modelCatalogueSecurityService.hasRole('VIEWER')) {
                return dataModelService.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                    'in' 'status', [ElementStatus.FINALIZED, ElementStatus.DRAFT, ElementStatus.PENDING]
                }), overridableDataModelFilter)
            }
            //if not you can only see finalised models
            return dataModelService.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'eq' 'status', ElementStatus.FINALIZED
            }), overridableDataModelFilter)
        }

        //if you want models with a specific status
        //check that you can access drafts i.e. you have a viewer role
        //then return the models by the status - providing you have the correct role
        if (params.status) {
            return dataModelService.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'in' 'status', ElementService.getStatusFromParams(params, modelCatalogueSecurityService.hasRole('VIEWER'))
            }), overridableDataModelFilter)
        }

        return dataModelService.classified(withAdditionalIndexCriteria(Lists.all(params, resource, "/${resourceName}/")), overridableDataModelFilter)
    }


    protected ListWrapper<DataModel> filterUnauthorized(ListWrapper<DataModel> items) {
        if ( items instanceof ListWithTotalAndTypeWrapper ) {
            ListWithTotalAndTypeWrapper listWithTotalAndTypeWrapperInstance = (ListWithTotalAndTypeWrapper) items
            DetachedCriteria<DataModel> criteria = listWithTotalAndTypeWrapperInstance.list.criteria
            Map<String, Object> params = listWithTotalAndTypeWrapperInstance.list.params
            ListWithTotalAndType<DataModel> listWithTotalAndType = instantiateListWithTotalAndTypeWithCriteria(criteria, params)
            return ListWithTotalAndTypeWrapper.create(listWithTotalAndTypeWrapperInstance.params, listWithTotalAndTypeWrapperInstance.base, listWithTotalAndType)
        }
        items
    }

    protected ListWithTotalAndType<DataModel> instantiateListWithTotalAndTypeWithCriteria(DetachedCriteria<DataModel> criteria, Map<String, Object> params) {
        List<DataModel> dataModelList = dataModelGormService.findAllByCriteria(criteria)
        if ( !dataModelList ) {
            return new ListWithTotalAndTypeImpl<DataModel>(DataModel, [], 0L)
        }
        int total = dataModelList.size()
        dataModelList = MaxOffsetSublistUtils.subList(SortParamsUtils.sort(dataModelList, params), params)
        new ListWithTotalAndTypeImpl<DataModel>(DataModel, dataModelList, total as Long)
    }

}

