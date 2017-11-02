package org.modelcatalogue.core.catalogueelement

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.events.CatalogueElementStatusNotDeprecatedEvent
import org.modelcatalogue.core.events.CatalogueElementStatusNotFinalizedEvent
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.events.NotFoundEvent
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.security.DataModelAclService
import spock.lang.Specification

@Mock([DataModel])
@TestFor(DataModelCatalogueElementService)
class DataModelCatalogueElementServiceSpec extends Specification {

    def "archive returns NotFound if data model does not exist"() {
        given:
        service.dataModelGormService = Stub(DataModelGormService) {
            findById(_) >> null
        }

        when:
        MetadataResponseEvent responseEvent = service.archive(1)

        then:
        responseEvent instanceof NotFoundEvent
    }

    def "archive returns CatalogueElementStatusNotInFinalizedEvent if data model is not finalized"() {
        given:
        DataModel dataModel = new DataModel(status: ElementStatus.PENDING)
        service.dataModelGormService = Stub(DataModelGormService) {
            findById(_) >> dataModel
        }
        service.dataModelAclService = Stub(DataModelAclService) {
            isAdminOrHasAdministratorPermission(_) >> true
        }

        when:
        MetadataResponseEvent responseEvent = service.archive(1)

        then:
        responseEvent instanceof CatalogueElementStatusNotFinalizedEvent
    }

    def "restore returns CatalogueElementStatusNotDeprecatedEvent if data model is not deprecated"() {
        given:
        DataModel dataModel = new DataModel(status: ElementStatus.FINALIZED)
        service.dataModelGormService = Stub(DataModelGormService) {
            findById(_) >> dataModel
        }
        service.dataModelAclService = Stub(DataModelAclService) {
            isAdminOrHasAdministratorPermission(_) >> true
        }

        when:
        MetadataResponseEvent responseEvent = service.restore(1)

        then:
        responseEvent instanceof CatalogueElementStatusNotDeprecatedEvent
    }
}
