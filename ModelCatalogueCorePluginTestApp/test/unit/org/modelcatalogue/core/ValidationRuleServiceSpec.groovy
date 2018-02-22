package org.modelcatalogue.core

import grails.test.mixin.TestFor
import org.modelcatalogue.core.persistence.RelationshipGormService
import org.modelcatalogue.core.util.MetadataDomainEntity
import org.modelcatalogue.core.validation.ValidationRuleJsonView
import org.modelcatalogue.core.validation.ValidationRulesJsonView
import spock.lang.Specification

@TestFor(ValidationRuleService)
class ValidationRuleServiceSpec extends Specification {

    def "if findByMetadataDomainEntity returns null "() {
        given:
        service.metadataDomainEntityService = Mock(MetadataDomainEntityService)

        when:
        ValidationRulesJsonView result = service.findValidationRulesByMetadataDomainEntity(MetadataDomainEntity.of('gorm://org.modelcatalogue.core.DataElement:178688'))

        then:
        !result
    }

    def "if validation and validationRules is null"() {
        given:
        service.metadataDomainEntityService = Stub(MetadataDomainEntityService) {
            findByMetadataDomainEntity(_ as MetadataDomainEntity) >> new DataElement(id: 178688)
        }
        service.relationshipGormService = Mock(RelationshipGormService)

        when:
        ValidationRulesJsonView result = service.findValidationRulesByMetadataDomainEntity(MetadataDomainEntity.of('gorm://org.modelcatalogue.core.DataElement:178688'))

        then:
        !result
    }

    def "validating of DataElement is extracted"() {
        when:
        DataElement dataElement = new DataElement(dataType: new DataType(rule: "x == null || x in ['red', 'blue']"))
        def result = service.validatingByCatalogueElement(dataElement)

        then:
        result != null
        result.implicitRule == "x == null || x in ['red', 'blue']"
        result.explicitRule == null
        result.bases.isEmpty()
    }
}

