package org.modelcatalogue.core

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.security.MetadataRolesUtils
import org.modelcatalogue.core.security.User
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.springframework.web.multipart.MultipartFile

import java.util.concurrent.Callable

abstract class AbstractDataImportService {

    AssetService assetService
    DataModelService dataModelService
    ElementService elementService
    SpringSecurityService springSecurityService
    AuditService auditService
    AssetGormService assetGormService
    UserGormService userGormService
    def executorService

    abstract String getContentType()

    abstract String getExecuteBackgroundMessage()

    abstract void loadInputStream(DefaultCatalogueBuilder defaultCatalogueBuilder, InputStream inputStream, String name)

    /**
     *
     * @param params request Grails Parameter Map
     * @param file the actual file to be imported.
     * @return stored asset id
     */
    Long importFile(GrailsParameterMap params, MultipartFile file) {
        boolean isAdmin = SpringSecurityUtils.ifAnyGranted(MetadataRolesUtils.getRolesFromAuthority('ADMIN').join(','))
        DefaultCatalogueBuilder defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService, isAdmin)
        Asset asset = assetService.storeAsset(params, file, contentType)
        final Long assetId = asset.id
        defaultCatalogueBuilder.monitor = BuildProgressMonitor.create("Importing $file.originalFilename", assetId)
        InputStream inputStream = file.inputStream
        String name = params.name
        Long userId = springSecurityService.principal?.id
        executeInBackground(assetId, executeBackgroundMessage) {
            try {
                //DataModel.withTransaction {
                loadInputStream(defaultCatalogueBuilder, inputStream, name)
                DataModel dataModelInstance = findCreatedDataModel(defaultCatalogueBuilder.created)
                finalizeAsset(assetId, dataModelInstance, userId)
                //}

            } catch (Exception e) {
                logError(assetId, e)
            }
        }
        assetId
    }

    DataModel findCreatedDataModel(Set<CatalogueElement> created) {
        DataModel dataModelInstance = (DataModel) created.find { it.instanceOf(DataModel) }
        if ( !dataModelInstance ) {
            return (DataModel) created.find { it.dataModel }?.dataModel
        }
        dataModelInstance
    }

    protected void logError(Long id,Exception e){
        BuildProgressMonitor.get(id)?.onError(e)
        log.error "Error importing Asset[$id]", e
        assetGormService.finalizeAssetWithError(e)
    }

    protected executeInBackground(Long assetId, String message, Closure code) {
        Long userId = springSecurityService.principal?.id
        executorService.submit {
            DataModel.withTransaction {
                auditService.logExternalChange(assetGormService.findById(assetId), userId, message, code)
            }
        }
    }

    protected Asset finalizeAsset(Long id, DataModel dataModel, Long userId){
        BuildProgressMonitor.get(id)?.onCompleted()

        Asset assetInstance = assetGormService.finalizeAsset(id, dataModel, userId)

        if ( userId && userGormService.exists(userId) ) {
            User userInstance = userGormService.findById(userId)
            userInstance.createLinkTo(dataModel, RelationshipType.favouriteType)
        }

        assetInstance
    }
}
