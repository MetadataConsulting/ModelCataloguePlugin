package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.test.spock.IntegrationSpec

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
class ClassificationServiceSpec extends AbstractIntegrationSpec {

    def classificationService

    def setup() {
        loadFixtures()
    }

    def "all the models are returned if no classification is selected"(){
        DetachedCriteria<Model> criteria = classificationService.classified(Model)

        expect:
        new DetachedCriteria<Model>(Model).count()
        criteria.count()
        criteria.count() == new DetachedCriteria<Model>(Model).count()
    }

    def "all the finalized models are returned when classification is not selected"() {
        DetachedCriteria<Model> finalized = new DetachedCriteria<Model>(Model).build {
            eq 'status', ElementStatus.FINALIZED
        }

        DetachedCriteria<Model> criteria = classificationService.classified(finalized)

        expect:
        finalized.count()
        criteria.count()
        criteria.count() == finalized.count()
    }



}
