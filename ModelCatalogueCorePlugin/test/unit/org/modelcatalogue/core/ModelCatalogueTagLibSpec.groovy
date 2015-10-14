package org.modelcatalogue.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.util.CatalogueElementDynamicHelper
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.RelationshipDirection
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

@TestFor(ModelCatalogueTagLib)
@Mock([RelationshipType, Relationship, DataClass])
@ConfineMetaClassChanges(DataClass)
class ModelCatalogueTagLibSpec extends Specification {

    RelationshipService relationshipService
    AuditService auditService
    RelationshipType type

    def setup(){
        CatalogueElementDynamicHelper.addShortcuts(DataClass)

        type = [name: "hierarchy", sourceToDestination: "parent of", destinationToSource: "child of", sourceClass: CatalogueElement, destinationClass: CatalogueElement]
        type.relationshipTypeService = new RelationshipTypeService()
        type.save(failOnError: true)

        ModelCatalogueSecurityService securityService = new ModelCatalogueSecurityService()
        relationshipService = Mock(RelationshipService)
        relationshipService.modelCatalogueSecurityService = securityService
        tagLib.relationshipService = relationshipService

        auditService = Mock(AuditService)
    }


    def "relationships tag is iterating over relationships of the element"() {
        DataClass model = makeModel(name: 'Parent')

        ListWithTotal<DataClass> children = Lists.lazy([:], DataClass, {
            List<DataClass> ret = []

            3.times {
                ret << new Relationship(source: model, destination: makeModel(name: "Child ${it + 1}"), relationshipType: type)
            }
            ret
        })

        when:
        relationshipService.getRelationships([:],RelationshipDirection.OUTGOING, model, type).total
        applyTemplate('''
            <mc:relationships element="${model}" type="hierarchy" direction="outgoing" var="relationship">
                <p>${relationship.destination.name}</p>
            </mc:relationships>
        '''.stripIndent().trim(), [model: model]) == '''
            <p>Child 1</p>
            <p>Child 2</p>
            <p>Child 3</p>
        '''.stripIndent().trim()

        then:
        2 * relationshipService.getRelationships([:], RelationshipDirection.OUTGOING, model, type) >> children
    }


    private DataClass makeModel(Map params) {
        DataClass model = new DataClass(params)
        model.relationshipService = relationshipService
        model.auditService = auditService
        model.save(failOnError: true)
    }

}
