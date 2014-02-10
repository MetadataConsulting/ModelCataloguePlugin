package uk.co.mc.core

import spock.lang.Specification

/**
 * Created by ladin on 10.02.14.
 */
class RelationshipTypeISpec extends Specification {

    def "read by name returns read only instance"() {
        RelationshipType.initDefaultRelationshipTypes()
        RelationshipType containment = RelationshipType.readByName("containment")

        expect:
        containment

        when:
        containment.name = "foo"
        containment.save()

        then:
        IllegalStateException e = thrown(IllegalStateException)
        e
        e.message == "Cannot make an immutable entity modifiable."

    }

    def "data elements can be contained in models, models can contain data elements"(){
        RelationshipType.initDefaultRelationshipTypes()

        Model model = new Model(name: "model")
        DataElement element = new DataElement(name: "element")

        expect:
        model.save()
        element.save()
        !model.contains
        !element.containedIn

        when:
        model.addToContains(element)

        then:
        model.contains
        model.contains.size()       == 1
        element.containedIn
        element.containedIn.size()  == 1

        when:
        model.removeFromContains(element)

        then:
        !model.contains
        !element.containedIn

        when:
        element.addToContainedIn(model)

        then:
        model.contains
        model.contains.size()       == 1
        element.containedIn
        element.containedIn.size()  == 1
    }

    def "conceptual domains can provide context for model, models have context of conceptual domains"(){
        RelationshipType.initDefaultRelationshipTypes()

        Model model = new Model(name: "model")
        ConceptualDomain conceptualDomain = new ConceptualDomain(name: "conceptualDomain")

        expect:
        model.save()
        conceptualDomain.save()
        !model.hasContextOf
        !conceptualDomain.isContextFor

        when:
        model.addToHasContextOf(conceptualDomain)

        then:
        model.hasContextOf
        model.hasContextOf.size()       == 1
        conceptualDomain.isContextFor
        conceptualDomain.isContextFor.size()  == 1

        when:
        model.removeFromHasContextOf(conceptualDomain)

        then:
        !model.hasContextOf
        !conceptualDomain.isContextFor

        when:
        conceptualDomain.addToIsContextFor(model)

        then:
        model.hasContextOf
        model.hasContextOf.size()       == 1
        conceptualDomain.isContextFor
        conceptualDomain.isContextFor.size()  == 1
    }

    def "model can be a parent of another model, model can be child of another model)"(){
        RelationshipType.initDefaultRelationshipTypes()

        Model book = new Model(name: "book")
        Model chapter = new Model(name: "chapter1")

        expect:
        book.save()
        chapter.save()
        !book.parentOf
        !chapter.childOf

        when:
        book.addToParentOf(chapter)

        then:
        book.parentOf
        book.parentOf.size()       == 1
        chapter.childOf
        chapter.childOf.size()  == 1

        when:
        book.removeFromParentOf(chapter)

        then:
        !book.parentOf
        !chapter.childOf

        when:
        chapter.addToChildOf(book)

        then:
        book.parentOf
        book.parentOf.size()       == 1
        chapter.childOf
        chapter.childOf.size()  == 1
    }


    def "conceptualDomain can include valueDomain, valueDomains can be included in conceptual domains"(){
        RelationshipType.initDefaultRelationshipTypes()

        ConceptualDomain university = new ConceptualDomain(name: "university")
        EnumeratedType enumeratedType = new EnumeratedType(name: "sub1", enumerations:['history', 'politics', 'science']).save()
        ValueDomain subjects = new ValueDomain(name: "subjects", description: "subject in the university", dataType: enumeratedType)

        expect:
        university.save()
        subjects.save()
        !university.includes
        !subjects.includedIn

        when:
        university.addToIncludes(subjects)

        then:
        university.includes
        university.includes.size()       == 1
        subjects.includedIn
        subjects.includedIn.size()  == 1

        when:
        university.removeFromIncludes(subjects)

        then:
        !university.includes
        !subjects.includedIn

        when:
        subjects.addToIncludedIn(university)

        then:
        university.includes
        university.includes.size()       == 1
        subjects.includedIn
        subjects.includedIn.size()  == 1
    }



}
