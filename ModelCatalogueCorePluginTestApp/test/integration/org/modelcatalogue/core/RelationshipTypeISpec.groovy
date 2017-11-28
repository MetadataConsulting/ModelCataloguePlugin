package org.modelcatalogue.core
/**
 * Created by ladin on 10.02.14.
 */
class RelationshipTypeISpec extends AbstractIntegrationSpec {

    def md1, de1, md2

    def setup() {
        loadFixtures()
        md1 = new DataClass(name: "book").save(failOnError: true)
        md2 = new DataClass(name: "chapter1").save(failOnError: true)
        de1 = new DataElement(name: "DE_author1").save(failOnError: true)
    }

    def "data elements can be contained in models, models can contain data elements"() {

        def model = new DataClass(name: "tester12343124").save()
        def element =  DataElement.get(de1.id)

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
        !model.contains.contains(element)
        !element.containedIn.contains(model)

        when:
        element.addToContainedIn(model)

        then:
        model.contains
        model.contains.contains(element)
        element.containedIn
        element.containedIn.contains(model)

        when:
        element.removeFromContainedIn(model)

        then:

        !model.contains.contains(element)
        !element.containedIn.contains(model)

        cleanup:
        model.delete()
    }

    def "model can be a parent of another model, model can be child of another model)"() {

        def book = DataClass.get(md1.id)
        def chapter = DataClass.get(md2.id)

        when:
        book.addToParentOf(chapter)

        then:
        book.parentOf
        book.parentOf.contains(chapter)
        chapter.childOf
        chapter.childOf.contains(book)

        when:
        book.removeFromParentOf(chapter)

        then:
        !book.parentOf.contains(chapter)
        !chapter.childOf.contains(book)

        when:
        chapter.addToChildOf(book)

        then:
        book.parentOf
        book.parentOf.contains(chapter)
        chapter.childOf
        chapter.childOf.contains(book)

        when:
        chapter.removeFromChildOf(book)


        then:
        !book.parentOf.contains(chapter)
        !chapter.childOf.contains(book)

    }

}
