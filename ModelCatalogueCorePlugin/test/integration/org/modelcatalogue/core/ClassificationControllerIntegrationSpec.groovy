package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import spock.lang.Unroll

/**
 * Created by adammilward on 27/02/2014.
 */
class ClassificationControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Unroll
    def "get json classifies: #no where max: #max offset: #offset"() {
        Classification first = loadItem
        createDataElementsUsingClassification(first, 12)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "json"
        controller.classifies(max)
        def json = controller.response.json
        recordResult "classifies$no", json

        then:
        checkJsonCorrectListValues(json, total, size, offset, max, next, previous)

        when:
        def item  = json.list[0]
        def classifies = first.classifies.find {it.id == item.id}

        then:
        item.id == classifies.id
        item.id == classifies.id
        resource.count() == totalCount

        cleanup:
        deleteDataElements(first, 12)

        where:
        [no, size, max, offset, total, next, previous] << getPaginationClassifiesParameters("/${resourceName}/${loadItem.id}/classifies")
    }


    @Unroll
    def "get value domains: #no where max: #max offset: #offset"() {
        Classification first = loadItem
        createDataElementsUsingClassification(first, 12)

        when:
        controller.params.id = first.id
        controller.params.offset = offset
        controller.params.max = max
        controller.response.format = "xml"
        controller.classifies(max)
        def xml = controller.response.xml
        recordResult "classifies$no", xml

        then:
        checkXmlCorrectListValues(xml, total, size, offset, max, next, previous)
        xml.children().size() - 2 == size

        when:
        def item  = xml.children().getAt(0)
        def classifies = first.classifies.find {it.id == item.@id.text() as Long}

        then:
        item.@id == classifies.id
        item.@id == classifies.id
        resource.count() == totalCount

        cleanup:
        deleteDataElements(first, 12)


        where:
        [no, size, max, offset, total, next, previous] << getPaginationClassifiesParameters("/${resourceName}/${loadItem.id}/classifies")
    }


    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description "]
    }

    @Override
    Map getNewInstance(){
       [name:"new classification", description: "the classification of the university2"]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }

    @Override
    String getBadXmlError(){
        "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttProperty [name] of class [class org.modelcatalogue.core.Classification] with value [tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt] does not fall within the valid size range from [1] to [255]"

        //"Property [name] of class [class org.ConceptualDomaincatalogue.core.${resourceName.capitalize()}] cannot be null"
    }

    @Override
    Class getResource() {
        Classification
    }

    @Override
    AbstractCatalogueElementController getController() {
        new ClassificationController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    Classification getLoadItem() {
        Classification.findByName("data set 1")
    }

    @Override
    Classification getAnotherLoadItem() {
        Classification.findByName("data set 2")
    }


    def getPaginationClassifiesParameters(baseLink){
        [
                // no,size, max , off. tot. next                           , previous
                [1, 10, 10, 0, 12, "${baseLink}?max=10&offset=10", ""],
                [2, 5, 5, 0, 12, "${baseLink}?max=5&offset=5", ""],
                [3, 5, 5, 5, 12, "${baseLink}?max=5&offset=10", "${baseLink}?max=5&offset=0"],
                [4, 4, 4, 8, 12, "", "${baseLink}?max=4&offset=4"],
                [5, 2, 10, 10, 12, "", "${baseLink}?max=10&offset=0"],
                [6, 2, 2, 10, 12, "", "${baseLink}?max=2&offset=8"]
        ]
    }



    private createDataElementsUsingClassification(Classification classification, Integer max){
        max.times {
            new DataElement(name: "classifiedDataElements${it}", description: "the ground speed of the moving vehicle").save().addToClassifications(classification)
        }
    }

    private deleteDataElements(Classification classification, Integer max) {
        max.times {
            classification.removeFromClassifies(DataElement.findByName("classifiedDataElements${it}"))
            DataElement.findByName("classifiedDataElements${it}").delete(flush: true)
        }
    }

}
