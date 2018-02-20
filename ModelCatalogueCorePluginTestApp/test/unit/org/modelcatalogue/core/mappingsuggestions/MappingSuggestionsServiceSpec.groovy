package org.modelcatalogue.core.mappingsuggestions

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionParameter
import org.modelcatalogue.core.actions.ActionState
import org.modelcatalogue.core.persistence.DataElementGormService
import spock.lang.Specification
import spock.lang.Unroll


@TestFor(MappingSuggestionsService)
@Mock([Action, ActionParameter])
class MappingSuggestionsServiceSpec extends Specification {


    void "mappingSuggestion is build of Action instance"() {
        given:
        Action actionInstance = new Action()
        actionInstance.addToExtensions(new ActionParameter(name: 'source', extensionValue: 'gorm://org.modelcatalogue.core.DataElement:1'))
        actionInstance.addToExtensions(new ActionParameter(name: 'destination', extensionValue: 'gorm://org.modelcatalogue.core.DataElement:2'))
        actionInstance.addToExtensions(new ActionParameter(name: 'matchScore', extensionValue: '60'))

        when:
        service.dataElementGormService = Stub(DataElementGormService) {
            findById(1l) >> new DataElement(name: 'Glicose', modelCatalogueId: '001')
            findById(2l) >> new DataElement(name: 'Glucose', modelCatalogueId: '002')
        }
        MappingSuggestion mappingSuggestion = service.mappingSuggestionOfAction(actionInstance)

        then:
        mappingSuggestion
        mappingSuggestion.source
        mappingSuggestion.source.name == 'Glicose'
        mappingSuggestion.source.code == '001'
        mappingSuggestion.destination
        mappingSuggestion.destination.name == 'Glucose'
        mappingSuggestion.destination.code == '002'
        mappingSuggestion.score == 60.0
    }
}
