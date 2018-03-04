package org.modelcatalogue.core

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.persistence.DataModelPolicyGormService
import org.modelcatalogue.core.util.BeanMessage
import org.modelcatalogue.core.util.BeanMessageUtils
import org.modelcatalogue.core.util.IdName
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.MetadataDomainEntity
import org.springframework.context.MessageSource

@CompileStatic
class DataModelCreateController {

    DataModelCreateService dataModelCreateService
    FavouriteService favouriteService

    MessageSource messageSource

    static allowedMethods = [
            create: 'GET',
            save: 'POST',
    ]

    def create() {
        List<IdName> dataModelPolicyList = dataModelCreateService.findAllDataModelPolicyGormService()
        List<IdName> dataModelList = dataModelCreateService.findAllDataWhichMaybeImported()
        Map<String, Object> m = [
                dataModelList: dataModelList,
                dataModelPolicyList: dataModelPolicyList
        ] as Map<String, Object>
        m.putAll(new DataModelCreateCommand(dataModelPolicies: dataModelCreateService.findDefaultDataModelPolicyIds()).toMap())
        render view: '/dataModel/create', model: m
    }

    @CompileDynamic
    def save(DataModelCreateCommand cmd) {
        if ( cmd.hasErrors() ) {
            flash.error = BeanMessageUtils.beanMessages(cmd, messageSource, request.locale)
            render view: '/dataModel/create', model: cmd.toMap()
        }

        if ( dataModelCreateService.isDataModelUsedAlready(cmd.name) ) {
            flash.error = 'Name is already used in other data model.'
            render view: '/dataModel/create', model: cmd.toMap()
        }

        DataModel dataModel = dataModelCreateService.saveDataModel(cmd.toDataModel(), cmd.dataModelPolicies, cmd.dataModels)

        if ( dataModel.hasErrors() ) {
            flash.error = messageSource.getMessage('dataModel.save.failed', [] as Object[], 'Unable to save data Model', request.locale)
            render view: '/dataModel/create', model: cmd.toMap()
            return
        }

        dataModelCreateService.createRelationships(dataModel, cmd.dataModels)
        favouriteService.favouriteModel(dataModel)

        redirect(url: MetadataDomainEntity.link(dataModel.id, new MetadataDomainEntity(domain: MetadataDomain.DATA_MODEL, id: dataModel.id)))
    }
}
