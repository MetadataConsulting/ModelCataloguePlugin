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
            items = modelCatalogueSearchService.search(DataModel, searchParams)
        }else {
            items = dataModelService.getAllEffectiveItems(max, params)
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





}

