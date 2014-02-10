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

    def "models are contained in data elements"(){
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

    def "conceptual domains provide context for model"(){
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

    def "model1 can be a parent of model2, model2 can be child of model1"(){
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



}
