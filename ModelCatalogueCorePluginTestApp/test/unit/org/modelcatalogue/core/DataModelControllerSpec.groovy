package org.modelcatalogue.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.security.DataModelAclService
import spock.lang.Ignore
import spock.lang.Specification
import org.springframework.http.HttpStatus

@TestFor(DataModelController)
@Mock([DataModel])
class DataModelControllerSpec extends Specification {

    void "Attempt to delete a finalized data model returns FORBIDDEN: 403 status"() {
        given:
        controller.catalogueElementService = Mock(CatalogueElementService)
        controller.dataModelAclService = Stub(DataModelAclService) {
            isAdminOrHasAdministratorPermission(_) >> true
        }

        when:
        controller.params.id = 1l
        controller.dataModelGormService = Stub(DataModelGormService) {
            findById(1l) >>  new DataModel(status: ElementStatus.FINALIZED)
        }
        controller.delete()

        then:
        response.status == HttpStatus.FORBIDDEN.value()
    }

    void "Attempt to delete a draft data model returns NO_CONTENT: 204 status"() {
        given:
        controller.manualDeleteRelationshipsService = Mock(ManualDeleteRelationshipsService)
        controller.catalogueElementService = Mock(CatalogueElementService)
        controller.dataModelAclService = Stub(DataModelAclService) {
            isAdminOrHasAdministratorPermission(_) >> true
        }

        when:
        controller.params.id = 1l
        controller.dataModelGormService = Stub(DataModelGormService) {
            findById(1l) >> new DataModel(status: ElementStatus.DRAFT)
        }
        controller.delete()

        then:
        response.status == HttpStatus.NO_CONTENT.value()
    }
}
