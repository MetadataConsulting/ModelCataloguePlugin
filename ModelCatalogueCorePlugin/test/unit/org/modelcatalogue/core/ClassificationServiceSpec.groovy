package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.grails.datastore.mapping.query.Query
import org.modelcatalogue.core.security.User
import spock.lang.Ignore
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ClassificationService)
@Mock(Relationship)
@Ignore
class ClassificationServiceSpec extends Specification {

    ModelCatalogueSecurityService securityService = Mock(ModelCatalogueSecurityService)

    def setup() {
        _ * securityService.isUserLoggedIn() >> true
        _ * securityService.getCurrentUser() >> new User(classifications: [new Classification(id: 123)] as Set<Classification>)
    }

    void "classification filter is added to the detached criteria for relationships"() {
        DetachedCriteria<Relationship> criteria = service.classificationAware(new DetachedCriteria<Relationship>(Relationship))
        expect:
        criteria.criteria.contains { it instanceof Query.Equals && it.property == 'classification' }
    }
}
