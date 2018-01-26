package org.modelcatalogue.core.dashboard


import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelController
import org.modelcatalogue.core.catalogueelement.DataModelCatalogueElementService
import org.modelcatalogue.core.catalogueelement.ManageCatalogueElementService
import org.modelcatalogue.core.util.lists.CustomizableJsonListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeWrapper
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller

@CompileStatic
class DashboardController <T extends CatalogueElement> extends DataModelController<DataModel> {

    DataModelCatalogueElementService dataModelCatalogueElementService

    static allowedMethods = [
            index: 'GET'
    ]

    @Override
    def index(Integer max) {
        handleParams(max)

        //only want the active models
        params.status = 'active'

        //before interceptor deals with this security - this is only applicable to data models and users

        boolean hasRoleViewer = modelCatalogueSecurityService.hasRole('VIEWER', getDataModel())
        if(params.status && !(params.status.toString().toLowerCase() in ['finalized', 'deprecated', 'active']) && !hasRoleViewer) {
            unauthorized()
            return
        }

        ListWithTotalAndType<DataModel> items

        //use elasticsearch for anything that isn't a data model or a user list
        // allows you to filter imports etc quickly and easilt

        items = getAllEffectiveItems(max)

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
        [models: items.items, total: items.total]
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

