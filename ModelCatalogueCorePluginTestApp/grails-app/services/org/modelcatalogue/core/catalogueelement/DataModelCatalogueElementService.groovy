package org.modelcatalogue.core.catalogueelement

import grails.util.GrailsNameUtils
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.events.DataModelFinalizedEvent
import org.modelcatalogue.core.events.DataModelNotFoundEvent
import org.modelcatalogue.core.events.DataModelWithErrorsEvent
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.UnauthorizedEvent
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import java.util.concurrent.ExecutorService

@Slf4j
class DataModelCatalogueElementService extends AbstractCatalogueElementService {

    ExecutorService executorService

    @Override
    CatalogueElement findById(Long id) {
        dataModelGormService.findById(id)
    }

    @Override
    protected String resourceName() {
        GrailsNameUtils.getPropertyName(DataModel.class.name)
    }

    MetadataResponseEvent finalize(Long dataModelId, String semanticVersion, String revisionNotes) {

        DataModel instance = dataModelGormService.findById(dataModelId)
        if (instance == null) {
            return new DataModelNotFoundEvent()
        }

        if ( !dataModelAclService.isAdminOrHasAdministratorPermission(instance) ) {
            return new UnauthorizedEvent()
        }

        instance.checkFinalizeEligibility(semanticVersion, revisionNotes)

        if (instance.hasErrors()) {
            return new DataModelWithErrorsEvent(dataModel: instance)
        }

        try {
            elementService.finalizeDataModel(instance, semanticVersion, revisionNotes, BuildProgressMonitor.create("Finalizing $instance", instance?.id))
        } catch (e) {
            log.error "Exception finalizing element on the background", e
            catalogueElementGormService.updateCatalogueElementStatus(instance, ElementStatus.DRAFT)
        }

        new DataModelFinalizedEvent(dataModel: instance)

    }
}
