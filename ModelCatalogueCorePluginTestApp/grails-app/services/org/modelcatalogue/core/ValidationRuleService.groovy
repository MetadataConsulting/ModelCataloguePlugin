package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.persistence.RelationshipGormService
import org.modelcatalogue.core.scripting.Validating
import org.modelcatalogue.core.scripting.ValidatingImpl
import org.modelcatalogue.core.util.MetadataDomainEntity
import org.modelcatalogue.core.validation.ValidationRuleJsonView
import org.modelcatalogue.core.validation.ValidationRulesJsonView

import javax.annotation.PostConstruct

@CompileStatic
class ValidationRuleService {

    MetadataDomainEntityService metadataDomainEntityService

    RelationshipGormService relationshipGormService

    String serverUrl

    GrailsApplication grailsApplication

    @CompileDynamic
    @PostConstruct
    void init() {
        serverUrl = grailsApplication.config.grails.serverURL
    }

    protected ValidatingImpl validatingByCatalogueElement(CatalogueElement catalogueElement) {
        ValidatingImpl validating = null
        if ( catalogueElement instanceof DataElement ) {
            DataType dataType = ((DataElement) catalogueElement).dataType
            if ( dataType ) {
                validating = ValidatingImpl.of(dataType)
            }
        }
        if ( catalogueElement instanceof Validating ) {
            validating = ValidatingImpl.of(catalogueElement)
        }
        validating
    }

    @CompileDynamic
    @Transactional(readOnly = true)
    List<ValidationRule> findAllValidationRuleByMetadataDomainEntity(MetadataDomainEntity metadataDomainEntity) {
        relationshipGormService.queryByDestinationIdAndRelationshipTypeSourceToDestination(metadataDomainEntity.id, 'involves')
                .join('source')
                .list()
                .collect { Relationship relationship ->
            relationship.source as ValidationRule
        }.findAll { ValidationRule validationRule ->
            validationRule.rule
        } as List<ValidationRule>
    }

    List<ValidationRuleJsonView> rulesOfValidationRuleList(List<ValidationRule> validationRuleList) {
        validationRuleList.collect { ValidationRule validationRule ->
            Map m = [:]
            validationRule.extensions.each { ExtensionValue extensionValue ->
                m[extensionValue.name] = extensionValue.extensionValue
            }
            new ValidationRuleJsonView(rule: validationRule.rule,
                    name: validationRule.name,
                    identifiersToGormUrls: m)
        }
    }

    @Transactional(readOnly = true)
    ValidationRulesJsonView findValidationRulesByMetadataDomainEntity(MetadataDomainEntity metadataDomainEntity) {

        CatalogueElement catalogueElement = metadataDomainEntityService.findByMetadataDomainEntity(metadataDomainEntity)
        if ( !catalogueElement ) {
            return null
        }

        List<ValidationRule> validationRuleList = findAllValidationRuleByMetadataDomainEntity(metadataDomainEntity)
        List<ValidationRuleJsonView> rules = rulesOfValidationRuleList(validationRuleList)

        ValidatingImpl validating = validatingByCatalogueElement(catalogueElement)

        if ( !rules && !validating ) {
            return null
        }
        new ValidationRulesJsonView(name: "${catalogueElement?.dataModel?.name ?: ''}:${catalogueElement?.dataModel?.modelCatalogueId ?: ''} - ${catalogueElement?.name ?: ''}",
                gormUrl: MetadataDomainEntity.stringRepresentation(metadataDomainEntity),
                url: MetadataDomainEntity.link(catalogueElement.dataModel.id, metadataDomainEntity, serverUrl),
                rules: rules,
                validating: validating)
    }
}
