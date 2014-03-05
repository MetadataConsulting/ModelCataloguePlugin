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
        RelationshipType.initDefaultRelationshipTypes()
        md1 = Model.findByName("book")
        md2 = Model.findByName("chapter1")
        de1 = DataElement.findByName("DE_author1")
        cd1 = ConceptualDomain.findByName("public libraries")
        vd = ValueDomain.findByName("value domain uni subjects 2")

    }

    /*def cleanupSpec(){

        de1.delete()
        md2.delete()
        md1.delete()
        cd1.delete()
        vd.delete()

    }*/

    def "read by name returns read only instance"() {

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

        def model = Model.get(md1.id)
        def element =  DataElement.get(de1.id)

        expect:
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

        when:
        element.removeFromContainedIn(model)

        then:

        !model.contains
        !element.containedIn

    }

    def "conceptual domains can provide context for model, models have context of conceptual domains"(){

        def model = md1
        def conceptualDomain = cd1

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

        when:

        conceptualDomain.removeFromIsContextFor(model)

        then:
        !model.hasContextOf
        !conceptualDomain.isContextFor

    }

    def "model can be a parent of another model, model can be child of another model)"(){

        def book = Model.get(md1.id)
        def chapter = Model.get(md2.id)

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

        when:
        chapter.removeFromChildOf(book)


        then:
        !book.parentOf
        !chapter.childOf

    }


    def "conceptualDomain can include valueDomain, valueDomains can be included in conceptual domains"(){

        def university = ConceptualDomain.get(cd1.id)
        def subjects = ValueDomain.get(vd.id)

        expect:
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

        when:
        subjects.removeFromIncludedIn(university)

        then:
        !university.includes
        !subjects.includedIn

    }


    def "data elements can be instantiated by valueDomain, valueDomains can instantiate in data elements"(){


        def course = DataElement.get(de1.id)
        def subjects = ValueDomain.get(vd.id)

        expect:

        !course.instantiatedBy
        !subjects.instantiates

        when:
        course.addToInstantiatedBy(subjects)

        then:
        course.instantiatedBy
        course.instantiatedBy.size()       == 1
        subjects.instantiates
        subjects.instantiates.size()  == 1

        when:
        course.removeFromInstantiatedBy(subjects)

        then:
        !course.instantiatedBy
        !subjects.instantiates

        when:
        subjects.addToInstantiates(course)

        then:
        course.instantiatedBy
        course.instantiatedBy.size()       == 1
        subjects.instantiates
        subjects.instantiates.size()  == 1

        when:
        subjects.removeFromInstantiates(course)

        then:
        !course.instantiatedBy
        !subjects.instantiates
    }


    /*def "data types can be mapped to other data types and mapped from other data types"(){

        EnumeratedType subjectsA = new EnumeratedType(name: "subA", enumerations:['history', 'politics', 'science']).save()
        EnumeratedType subjectsB = new EnumeratedType(name: "subB", enumerations:['HIS', 'POL', 'SCI']).save()

        expect:
        subjectsA.save()
        subjectsB.save()
        !subjectsA.mapsTo
        !subjectsB.mapsFrom

        when:
        subjectsA.addToMapsTo(subjectsB)

        then:
        subjectsA.mapsTo
        subjectsA.mapsTo.size()       == 1
        subjectsB.mapsFrom
        subjectsB.mapsFrom.size()  == 1

        when:
        subjectsA.removeFromMapsTo(subjectsB)

        then:
        !subjectsA.mapsTo
        !subjectsB.mapsFrom

        when:
        subjectsB.addToMapsFrom(subjectsA)

        then:
        subjectsA.mapsTo
        subjectsA.mapsTo.size()       == 1
        subjectsB.mapsFrom
        subjectsB.mapsFrom.size()  == 1
    }*/


}
