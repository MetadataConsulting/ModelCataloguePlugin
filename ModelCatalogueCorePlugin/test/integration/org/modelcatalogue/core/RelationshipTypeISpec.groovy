package org.modelcatalogue.core

import spock.lang.Shared

/**
 * Created by ladin on 10.02.14.
 */
class RelationshipTypeISpec extends AbstractIntegrationSpec {

    @Shared
    def md1, de1, cd1, md2, vd

    def setupSpec(){
        loadFixtures()
        md1 = Model.findByName("book")
        md2 = Model.findByName("chapter1")
        de1 = DataElement.findByName("DE_author1")
        cd1 = ConceptualDomain.findByName("public libraries")
        vd = ValueDomain.findByName("value domain uni subjects 2")

    }

    def cleanupSpec(){

    }

    def "data elements can be contained in models, models can contain data elements"(){

        def model = new Model(name: "tester12343124").save()
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

    def "conceptual domains can provide context for model, models have context of conceptual domains"(){

        def model = Model.get(md1.id)
        def conceptualDomain =  ConceptualDomain.get(cd1.id)


        when:
        model.addToHasContextOf(conceptualDomain)

        then:
        model.hasContextOf
        model.hasContextOf.contains(conceptualDomain)
        conceptualDomain.isContextFor
        conceptualDomain.isContextFor.contains(model)

        when:
        model.removeFromHasContextOf(conceptualDomain)

        then:
        !model.hasContextOf.contains(conceptualDomain)
        !conceptualDomain.isContextFor.contains(model)

        when:
        conceptualDomain.addToIsContextFor(model)

        then:
        model.hasContextOf
        model.hasContextOf.contains(conceptualDomain)
        conceptualDomain.isContextFor
        conceptualDomain.isContextFor.contains(model)

        when:

        conceptualDomain.removeFromIsContextFor(model)

        then:
        !model.hasContextOf.contains(conceptualDomain)
        !conceptualDomain.isContextFor.contains(model)

    }

    def "model can be a parent of another model, model can be child of another model)"(){

        def book = Model.get(md1.id)
        def chapter = Model.get(md2.id)

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


    def "conceptualDomain can include valueDomain, valueDomains can be included in conceptual domains"(){

        def university = ConceptualDomain.get(cd1.id)
        def subjects = ValueDomain.get(vd.id)

        when:
        university.addToIncludes(subjects)

        then:
        university.includes
        university.includes.contains(subjects)
        subjects.includedIn
        subjects.includedIn.contains(university)

        when:
        university.removeFromIncludes(subjects)

        then:
        !university.includes
        !subjects.includedIn

        when:
        subjects.addToIncludedIn(university)

        then:
        university.includes
        university.includes.contains(subjects)
        subjects.includedIn
        subjects.includedIn.contains(university)

        when:
        subjects.removeFromIncludedIn(university)

        then:
        !university.includes
        !subjects.includedIn

    }

}
